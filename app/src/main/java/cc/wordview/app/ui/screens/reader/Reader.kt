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

package cc.wordview.app.ui.screens.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.GlobalViewModel
import cc.wordview.app.extensions.awaitEachGesture
import cc.wordview.app.extensions.enterImmersiveMode
import cc.wordview.app.extensions.getOrDefault
import cc.wordview.app.extensions.goBack
import cc.wordview.app.extensions.leaveImmersiveMode
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.theme.ptSerifFamily
import cc.wordview.assis.book.epub.ElementCategory
import cc.wordview.assis.parseEpub
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.zhanghai.compose.preference.LocalPreferenceFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reader(navController: NavHostController, viewModel: ReaderViewModel = hiltViewModel()) {
    val book by viewModel.book.collectAsStateWithLifecycle()
    val uiVisible by viewModel.uiVisible.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showBottomSheet by remember { mutableStateOf(false) }

    val currentPageNum by rememberSaveable { mutableIntStateOf(1) }

    val systemUiController = rememberSystemUiController()
    val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()

    OneTimeEffect {
        val bookInputStream = GlobalViewModel.bookInputStream.value
        if (bookInputStream != null) {
            viewModel.setBook(parseEpub(bookInputStream))
        }
    }

    LaunchedEffect(uiVisible) {
        if (uiVisible)
            systemUiController.leaveImmersiveMode()
        else
            systemUiController.enterImmersiveMode()
    }

    val page = book?.pages?.get(currentPageNum)

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        AnimatedVisibility(uiVisible) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = LocalContentColor.current
                ),
                title = {
                    book?.metadata?.title?.let { Text(it) }
                },
                actions = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.goBack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }
    }) { innerPadding ->
        var backgroundColor = Color.White
        var textColor = Color.Black

        when (preferences.getOrDefault<String>("reader_theme")) {
            "WHITE_ON_BLACK" -> {
                backgroundColor = Color.Black
                textColor = Color.White
            }
            "SEPIA" -> {
                backgroundColor = Color(0xFFF5F5DC)
                textColor = Color.Black
            }

            "BLACK_ON_WHITE" -> {}
            else -> {}
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
                // Solution borrowed from Myne (https://github.com/Pool-Of-Tears/Myne/blob/bf2339e0f0e2c3077f344c90fb7f2634dcdbf448/app/src/main/java/com/starry/myne/ui/screens/reader/main/composables/ChaptersContent.kt#L190)
                .awaitEachGesture {
                    val down = awaitFirstDown()
                    val up = waitForUpOrCancellation()
                    if (up != null && down.id == up.id) {
                        viewModel.toggleUi()
                    }
                }
        ) {
            page?.body?.let {
                LazyColumn(Modifier.padding(horizontal = 8.dp)) {
                    for (element in it) {
                        item {
                            when (element.category) {
                                ElementCategory.PARAGRAPH -> Text(
                                    modifier = Modifier.padding(bottom = 6.dp),
                                    text = element.value,
                                    color = textColor,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Justify,
                                    fontFamily = ptSerifFamily
                                )

                                ElementCategory.HEADER1 -> Text(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    text = element.value,
                                    color = textColor,
                                    fontSize = 42.sp,
                                    lineHeight = 36.sp,
                                    fontFamily = ptSerifFamily
                                )

                                ElementCategory.HEADER2 -> Text(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    text = element.value,
                                    color = textColor,
                                    fontSize = 36.sp,
                                    lineHeight = 36.sp,
                                    fontFamily = ptSerifFamily
                                )

                                ElementCategory.HEADER3 -> Text(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    text = element.value,
                                    color = textColor,
                                    fontSize = 28.sp,
                                    lineHeight = 36.sp,
                                    fontFamily = ptSerifFamily
                                )

                                ElementCategory.HORIZONTAL_RULE -> HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 24.dp),
                                    color = textColor,
                                    thickness = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                ReaderSettings()
            }
        }
    }
}