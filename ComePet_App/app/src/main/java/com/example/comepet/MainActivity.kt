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
import com.example.comepet.utils.SessionManager
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private val MainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
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

}