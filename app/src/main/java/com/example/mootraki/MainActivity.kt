/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mootraki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.mootraki.ui.theme.MootrakiTheme

import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MootrakiTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) { Home(navController) }
                        composable(Screen.Calendar.route) {
                            val viewModel: CalendarEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
                            Calendar(viewModel = viewModel)
                        }
                        composable(Screen.Charts.route) {
                            val chartviewModel: ChartViewModel = viewModel(factory = AppViewModelProvider.Factory)
                            Charts(viewModel = chartviewModel)
                        }
                        composable(Screen.Breathing.route) { VideoScreen() }
                        composable(Screen.Affirmations.route) { Affirmations() }
                    }
                }
            }
        }

    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val screens = listOf(
            Screen.Home,
            Screen.Calendar,
            Screen.Charts,
            Screen.Breathing,
            Screen.Affirmations
        )

        // Observe the current back stack entry
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
                    label = { Text(screen.route.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }) },
                    selected = currentDestination == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            // Ensure only a single instance of each destination
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









