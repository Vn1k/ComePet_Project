package com.example.comepet

import android.animation.ObjectAnimator
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.AnticipateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.comepet.databinding.ActivityMainBinding
import com.example.comepet.ui.post.PostFragment
import com.example.comepet.utils.SessionManager
import com.google.android.libraries.places.api.Places
import com.google.android.datatransport.runtime.BuildConfig
import com.google.android.libraries.places.api.net.PlacesClient

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var navView: BottomNavigationView
    private lateinit var likeButton: ImageView
    private var capturedImageUri: Uri? = null
    private val MainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.apply {
            setKeepOnScreenCondition{
                !MainViewModel.isReady.value
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        swipeRefreshLayout = binding.swipeRefreshLayout
        navView = binding.navView

        setupSwipeRefreshLayout()

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // List of destinations where refresh should be disabled
                R.id.navigation_login,
                R.id.navigation_register,
                R.id.navigation_boarding,
                R.id.navigation_welcome,
                R.id.navigation_upload,
                R.id.navigation_forgot,
                R.id.navigation_setting,
                R.id.navigation_chat,
                R.id.navigation_change_email,
                R.id.navigation_change_password,
                R.id.navigation_tagpet -> {
                    swipeRefreshLayout.isEnabled = false
                    hideBottomNavigation()
                }
                else -> {
                    swipeRefreshLayout.isEnabled = true
                    showBottomNavigation()
                }
            }
        }

        val token = SessionManager.getToken(this)
        if (token != null) {
            navigateToMainScreen()
        } else {
            navigateToLoginScreen()
        }

//        val apiKey = com.example.comepet.BuildConfig.PLACES_API_KEY
//        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
//            Log.d("Places test", "API key is missing: $apiKey")
//            finish()
//            return
//        }
//        Places.initialize(applicationContext, apiKey)
//        val placesClient = Places.createClient(this)

        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_search -> {
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_search)
                    true
                }
                R.id.navigation_post -> {
                    showBottomDialog()
                    true
                }
                R.id.navigation_profile -> {
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_profile)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            // Perform refresh operation based on current fragment
            refreshCurrentFragment()
        }

        // Customize refresh indicator colors
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    private fun refreshCurrentFragment() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val currentDestination = navController.currentDestination

        when (currentDestination?.id) {
            R.id.navigation_home -> {
                // Refresh home fragment data
                MainViewModel.refreshHomeData()
            }
            R.id.navigation_search -> {
                // Refresh search fragment data
                MainViewModel.refreshSearchData()
            }
            R.id.navigation_profile -> {
                // Refresh profile fragment data
                MainViewModel.refreshProfileData()
            }
            else -> {
                MainViewModel.refreshAllData()
            }
        }

        // Stop the refreshing animation after data is loaded
        swipeRefreshLayout.isRefreshing = false
    }

    private fun hideBottomNavigation() {
        navView.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        navView.visibility = View.VISIBLE
    }

    private fun navigateToMainScreen() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_home)
    }

    private fun navigateToLoginScreen() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_login)
    }

    private fun showBottomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_post)

        val cameraButton: TextView = dialog.findViewById(R.id.cameraButton)
        val galleryButton: TextView = dialog.findViewById(R.id.galleryButton)

        cameraButton.setOnClickListener {
            val currentDestination = findNavController(R.id.nav_host_fragment_activity_main).currentDestination?.id

            val bundle = Bundle().apply {
                putInt("sourceFragment", currentDestination ?: R.id.navigation_home)
            }

            findNavController(R.id.nav_host_fragment_activity_main).navigate(
                R.id.navigation_camera,
                bundle
            )
            dialog.dismiss()
        }

        galleryButton.setOnClickListener {
            val currentDestination = findNavController(R.id.nav_host_fragment_activity_main).currentDestination?.id

            val bundle = Bundle().apply {
                putInt("sourceFragment", currentDestination ?: R.id.navigation_home)
            }

            findNavController(R.id.nav_host_fragment_activity_main).navigate(
                R.id.navigation_upload,
                bundle
            )
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                capturedImageUri = it

                val bundle = Bundle().apply {
                    putString("capturedImageUri", capturedImageUri.toString())
                }

                findNavController(R.id.nav_host_fragment_activity_main).navigate(
                    R.id.navigation_upload,
                    bundle
                )
            }
        }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    fun navHome() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_home)
    }

    fun navSearch() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_search)
    }

    fun navProf() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_profile)
    }
}