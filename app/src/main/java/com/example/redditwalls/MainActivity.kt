package com.example.redditwalls

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.redditwalls.databinding.ActivityMainBinding
import com.example.redditwalls.repositories.SettingsRepository
import com.example.redditwalls.viewmodels.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.elevation = 0.0F

        val navView: BottomNavigationView = binding.bottomNavView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_favorites,
                R.id.navigation_search,
                R.id.navigation_saved,
                R.id.navigation_settings,
                R.id.navigation_settings_compose
            )
        )
        val noBottomBar = setOf(R.id.navigation_search_images, R.id.navigation_history)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            val lastDestination = controller.previousBackStackEntry?.destination
            if (lastDestination != null && lastDestination.id != destination.id) {
                setToolbarSubtitle("")
            }

            binding.appbar.setExpanded(true, true)
            binding.bottomNavView.isVisible = !noBottomBar.contains(destination.id)

            if (destination.id == R.id.navigation_search_images) {
                val subreddit =
                    arguments?.getString("subreddit") ?: SettingsRepository.FALLBACK_SUBREDDIT

                setToolbarTitle(subreddit)
            }
        }

        binding.bottomNavView.setOnItemReselectedListener {
            mainViewModel.onBottomNavItemClicked(it.itemId)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setToolbarTitle(title: String) {
        binding.toolbar.toolbar.title = title
    }

    fun setToolbarSubtitle(subtitle: String) {
        binding.toolbar.toolbar.subtitle = subtitle
    }
}