package com.example.mootraki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mootraki.ui.theme.MootrakiTheme
import java.util.Locale

/**
 * Main Activity class responsible for initializing the app and setting the UI content.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MootrakiTheme {
                // Remember the NavController for navigation between screens
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) } // Bottom navigation bar
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route, // Define the start destination
                        Modifier.padding(innerPadding)
                    ) {
                        // Define the composable destinations
                        composable(Screen.Home.route) {
                            val homeViewModel: SubmitEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
                            Home(navController, viewModel = homeViewModel)
                        }
                        composable(Screen.Calendar.route) {
                            val calendarViewModel: CalendarEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
                            Calendar(viewModel = calendarViewModel)
                        }
                        composable(Screen.Charts.route) {
                            val chartViewModel: ChartViewModel = viewModel(factory = AppViewModelProvider.Factory)
                            Charts(viewModel = chartViewModel)
                        }
                        composable(Screen.Breathing.route) {
                            VideoScreen()
                        }
                        composable(Screen.Affirmations.route) {
                            Affirmations()
                        }
                    }
                }
            }
        }
    }

    /**
     * Composable function to display the bottom navigation bar.
     *
     * @param navController Controller to handle navigation between screens.
     */
    @Composable
    fun BottomNavigationBar(navController: NavController) {
        // List of screens for navigation
        val screens = listOf(
            Screen.Home,
            Screen.Calendar,
            Screen.Charts,
            Screen.Breathing,
            Screen.Affirmations
        )

        // Observe the current back stack entry to highlight the selected screen
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination?.route

        NavigationBar {
            screens.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = when (screen) {
                                Screen.Home -> painterResource(id = R.drawable.ic_home)
                                Screen.Calendar -> painterResource(id = R.drawable.ic_calendar)
                                Screen.Charts -> painterResource(id = R.drawable.ic_charts)
                                Screen.Breathing -> painterResource(id = R.drawable.ic_breathing)
                                Screen.Affirmations -> painterResource(id = R.drawable.ic_affirmations)
                            },
                            contentDescription = screen.route
                        )
                    },
                    label = {
                        Text(
                            screen.route.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                            }
                        )
                    },
                    selected = currentDestination == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            // Ensure a single instance of each destination
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}









