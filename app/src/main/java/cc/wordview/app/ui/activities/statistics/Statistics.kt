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

package cc.wordview.app.ui.activities.statistics

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.R
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.components.Icon
import cc.wordview.app.components.ui.OneTimeEffect
import cc.wordview.app.ui.components.Space
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.ui.theme.Typography
import cc.wordview.app.ui.theme.poppinsFamily
import cc.wordview.gengolex.Language
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Statistics(viewModel: StatisticsViewModel = hiltViewModel()) {
    val wordsLearnedAmount by viewModel.wordsLearnedAmount.collectAsStateWithLifecycle()
    val accuracyPercentage by viewModel.accuracyPercentage.collectAsStateWithLifecycle()
    val wordsPracticedAmount by viewModel.wordsPracticedAmount.collectAsStateWithLifecycle()
    val words by viewModel.words.collectAsStateWithLifecycle()

    val langTag = AppSettings.language.get()
    val lang = remember { Language.byTag(langTag) }

    val activity = LocalActivity.current!!

    OneTimeEffect { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = LocalContentColor.current
                ),
                title = {
                    Text(
                        text = stringResource(R.string.lesson_results),
                        fontFamily = poppinsFamily,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Space(24.dp)
            Text(
                text = stringResource(R.string.statistics),
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )
            Space(12.dp)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = DefaultRoundedCornerShape,
                tonalElevation = 12.dp
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text(
                        text = "+$wordsLearnedAmount",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        style = Typography.headlineSmall,
                    )
                    Text(
                        text = stringResource(R.string.words_learned),
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        style = Typography.titleSmall,
                    )
                    Space(12.dp)
                    Text(
                        text = "$accuracyPercentage%",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        style = Typography.headlineSmall,
                    )
                    Text(
                        text = stringResource(R.string.accuracy),
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        style = Typography.titleSmall,
                    )
                    Space(12.dp)
                    Text(
                        text = "$wordsPracticedAmount",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        style = Typography.headlineSmall,
                    )
                    Text(
                        text = stringResource(R.string.praticed_words),
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        style = Typography.titleSmall,
                    )
                }
            }
            Space(24.dp)
            Text(
                text = stringResource(R.string.words),
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )
            Column(Modifier.fillMaxWidth()) {
                for (word in words.distinct()) {
                    Card(
                        modifier = Modifier
                            .testTag("result-item")
                            .fillMaxWidth()
                            .height(68.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                        shape = DefaultRoundedCornerShape,
                    ) {
                        Row(Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                            val image = ImageCacheManager.getCachedImage(word.tokenWord.parent)
                            AsyncImage(
                                modifier = Modifier.size(48.dp),
                                model = image,
                                contentDescription = null
                            )

                            Column(Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.5f)
                                .padding(start = 12.dp), verticalArrangement = Arrangement.Center) {
                                Text(
                                    text = word.tokenWord.word,
                                    style = Typography.bodyLarge,
                                )
                                Text(
                                    text = viewModel.getTranslation(word),
                                    style = Typography.bodySmall,
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(1f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.End
                            ) {
                                IconButton(
                                    modifier = Modifier.testTag("settings"),
                                    onClick = {
                                        viewModel.ttsSpeak(word.tokenWord.pronunciation ?: word.tokenWord.word, lang.locale)
                                    }
                                ) {
                                    Icon(Icons.Filled.VolumeUp)
                                }
                            }
                        }
                    }
                    Space(6.dp)
                }
            }
        }
    }
}