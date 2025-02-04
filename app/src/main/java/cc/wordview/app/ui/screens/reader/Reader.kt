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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cc.wordview.app.GlobalViewModel
import cc.wordview.app.extensions.goBack
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.theme.ptSerifFamily
import cc.wordview.assis.book.epub.ElementCategory
import cc.wordview.assis.parseEpub

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reader(navController: NavHostController, viewModel: ReaderViewModel = hiltViewModel()) {
    val book by viewModel.book.collectAsStateWithLifecycle()

    val currentPageNum by rememberSaveable { mutableIntStateOf(1) }

    OneTimeEffect {
        val bookInputStream = GlobalViewModel.bookInputStream.value
        if (bookInputStream != null) {
            viewModel.setBook(parseEpub(bookInputStream))
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = LocalContentColor.current
            ),
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.goBack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }
        )
    }) { innerPadding ->
        val page = book?.pages?.get(currentPageNum)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            page?.body?.let {
                LazyColumn(Modifier.padding(horizontal = 8.dp)) {
                    for (element in it) {
                        item {
                            when (element.category) {
                                ElementCategory.PARAGRAPH -> Text(
                                    modifier = Modifier.padding(bottom = 6.dp),
                                    text = element.value,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Justify,
                                    fontFamily = ptSerifFamily
                                )

                                ElementCategory.HEADER1 -> Text(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    text = element.value,
                                    fontSize = 42.sp,
                                    lineHeight = 36.sp,
                                    fontFamily = ptSerifFamily
                                )

                                ElementCategory.HEADER2 -> Text(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    text = element.value,
                                    fontSize = 36.sp,
                                    lineHeight = 36.sp,
                                    fontFamily = ptSerifFamily
                                )

                                ElementCategory.HEADER3 -> Text(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    text = element.value,
                                    fontSize = 28.sp,
                                    lineHeight = 36.sp,
                                    fontFamily = ptSerifFamily
                                )

                                ElementCategory.HORIZONTAL_RULE -> HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 24.dp),
                                    thickness = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}