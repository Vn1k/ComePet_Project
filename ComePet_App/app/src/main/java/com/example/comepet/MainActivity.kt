package com.example.comepet

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.comepet.databinding.ActivityMainBinding
import com.example.comepet.ui.post.PostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
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
//                R.id.navigation_post -> hideBottomNavigation()
                R.id.navigation_upload -> hideBottomNavigation()
                R.id.navigation_search -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }

//        navigationPostButton.setOnClickListener {
//            val postDialog = PostDialogFragment()
//            postDialog.show(supportFragmentManager, "PostDialog")
//        }


        // Tambahkan listener untuk item navbar
//        navView.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.navigation_post -> {
//                    // Tampilkan dialog untuk Post
//                    val postDialog = PostFragment()
//                    postDialog.show(supportFragmentManager, "PostDialog")
//                    true
//                }
//                else -> {
//                    navController.navigate(item.itemId)
//                    true
//                }
//            }
//        }
    }

    private fun hideBottomNavigation() {
        navView.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        navView.visibility = View.VISIBLE
    }


}