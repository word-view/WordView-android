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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cc.wordview.app.R
import cc.wordview.app.extensions.getFlag
import cc.wordview.app.settings.AppSettings
import cc.wordview.app.ui.activities.home.composables.ProfileScreen
import cc.wordview.app.ui.activities.home.composables.SettingsScreen
import cc.wordview.app.ui.activities.home.composables.history.HistoryScreen
import cc.wordview.app.ui.activities.home.composables.home.tabs.BottomNavigationItem
import cc.wordview.app.ui.activities.home.composables.home.tabs.MusicTab
import cc.wordview.app.ui.activities.home.composables.home.tabs.Tabs
import cc.wordview.app.ui.activities.home.composables.home.tabs.VideoTab
import cc.wordview.app.ui.activities.home.composables.search.SearchScreen
import cc.wordview.app.ui.components.ProfilePicture
import cc.wordview.app.ui.theme.poppinsFamily
import cc.wordview.app.ui.theme.redhatFamily
import cc.wordview.gengolex.Language
import com.composegears.tiamat.compose.navController
import com.composegears.tiamat.compose.navDestination
import com.composegears.tiamat.compose.navigate
import com.composegears.tiamat.navigation.NavDestination
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
val HomeScreen: NavDestination<Unit> by navDestination {
    val navController = navController()
    val viewModel: HomeViewModel = hiltViewModel()

    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val message by viewModel.snackBarMessage.collectAsState(initial = "")

    val langTag = AppSettings.language.get()
    val lang = Language.byTag(langTag)

    LaunchedEffect(Unit) {
        viewModel.snackBarMessage.collect {
            scope.launch {
                snackBarHostState.showSnackbar(message, duration = SnackbarDuration.Long)
            }
        }
    }

    BackHandler {}
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize(), topBar = {
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
                        modifier = Modifier.testTag("lang-flag"),
                        onClick = {},
                        enabled = false
                    ) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = lang.getFlag(),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag("settings"),
                        onClick = { navController.navigate(SettingsScreen) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                    ProfilePicture(
                        modifier = Modifier.testTag("profile-picture"),
                        onNavigateToProfile = { navController.navigate(ProfileScreen) },
                        onOpenHistory = { navController.navigate(HistoryScreen) }
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.testTag("search"),
                text = { Text(text = stringResource(R.string.search)) },
                onClick = { navController.navigate(SearchScreen) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar() {
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
                                tabNavController.navigate(navigationItem.route) {
                                    popUpTo(tabNavController.graph.findStartDestination().id) {
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
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = Tabs.Music.route,
        ) {
            composable(Tabs.Music.route) {
                MusicTab(innerPadding)
            }
            composable(Tabs.Video.route) {
                VideoTab(innerPadding)
            }
        }
    }
}