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
import com.example.comepet.databinding.ActivityMainBinding
import com.example.comepet.ui.post.PostFragment
import com.example.comepet.utils.SessionManager
import com.google.android.libraries.places.api.Places
import com.google.android.datatransport.runtime.BuildConfig
import com.google.android.libraries.places.api.net.PlacesClient

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
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

        navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_login -> hideBottomNavigation()
                R.id.navigation_register -> hideBottomNavigation()
                R.id.navigation_boarding -> hideBottomNavigation()
                R.id.navigation_welcome -> hideBottomNavigation()
                R.id.navigation_upload -> hideBottomNavigation()
                R.id.navigation_search -> hideBottomNavigation()
                R.id.navigation_forgot -> hideBottomNavigation()
                R.id.navigation_setting -> hideBottomNavigation()
                R.id.navigation_change_email -> hideBottomNavigation()
                R.id.navigation_change_password -> hideBottomNavigation()
                else -> showBottomNavigation()
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
                    navHome()
                    true
                }
                R.id.navigation_search -> {
                    navSearch()
                    true
                }
                R.id.navigation_post -> {
                    val currentDestination = findNavController(R.id.nav_host_fragment_activity_main).currentDestination?.id
                    if (currentDestination != R.id.navigation_upload) {
                        showBottomDialog()
                    }
                    true
                }
                R.id.navigation_profile -> {
                    navProf()
                    true
                }
                else -> false
            }
        }
    }

    private fun navHome() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_home)
    }

    private fun navSearch() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_search)
    }

    private fun navProf() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_profile)
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
            findNavController(R.id.nav_host_fragment_activity_main).navigate(
                R.id.navigation_camera,
                null,
                NavOptions.Builder().setPopUpTo(R.id.navigation_post, true).build()
            )
            dialog.dismiss()
        }


        galleryButton.setOnClickListener {
            dialog.dismiss()
            openGallery()
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
}