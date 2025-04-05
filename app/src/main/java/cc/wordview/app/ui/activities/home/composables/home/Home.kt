/*
 * Copyright (c) 2025 Arthur Araujo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package cc.wordview.app.ui.activities.home.composables.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.ui.activities.home.HomeNav
import cc.wordview.app.ui.theme.poppinsFamily
import cc.wordview.app.ui.theme.redhatFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navHostController: NavHostController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BackHandler {}
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(
                    text = "WordView",
                    fontWeight = FontWeight.Bold,
                    fontFamily = redhatFamily
                )
            },
            actions = {
                IconButton(
                    modifier = Modifier.testTag("search"),
                    onClick = { navHostController.navigate(HomeNav.Search.route) }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(
                    modifier = Modifier.testTag("settings"),
                    onClick = { navHostController.navigate(HomeNav.Settings.route) }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        )
    }, bottomBar = {
        NavigationBar {
            BottomNavigationItem().bottomNavigationItems(LocalContext.current)
                .forEachIndexed { _, navigationItem ->
                    NavigationBarItem(
                        modifier = Modifier.testTag("${navigationItem.route}-tab"),
                        selected = navigationItem.route == currentDestination?.route,
                        label = {
                            Text(
                                navigationItem.name,
                                fontFamily = poppinsFamily
                            )
                        },
                        icon = {
                            Icon(
                                navigationItem.icon,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            navController.navigate(navigationItem.route) {
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
    }) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Tabs.Learn.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(Tabs.Learn.route) {
                LearnTab(
                    navController,
                    navHostController
                )
            }
            composable(Tabs.Explore.route) {
                ExploreTab(
                    navController
                )
            }
            composable(Tabs.Profile.route) {
                ProfileTab(
                    navController
                )
            }
        }
    }
}