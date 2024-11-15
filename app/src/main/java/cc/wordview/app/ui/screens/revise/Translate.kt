/*
 * Copyright (c) 2024 Arthur Araujo
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

package cc.wordview.app.ui.screens.revise

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cc.wordview.app.ui.screens.revise.model.TranslateViewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.R
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.screens.revise.components.Answer
import cc.wordview.app.ui.screens.revise.components.ReviseScreen
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.ui.theme.Typography
import me.zhanghai.compose.preference.LocalPreferenceFlow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Translate(innerPadding: PaddingValues, viewModel: TranslateViewModel = hiltViewModel()) {
    val phrase by viewModel.phrase.collectAsStateWithLifecycle()

    val currentWord by WordReviseViewModel.currentWord.collectAsStateWithLifecycle()

    val answerWordPool = viewModel.answerWordPool
    val wordPool = viewModel.wordPool
    val originalPoolOrder = viewModel.originalPoolOrder
    val wrongOrderedWords = viewModel.wrongOrderedWords

    var checked by rememberSaveable { mutableStateOf(false) }

    OneTimeEffect {
        viewModel.getPhrase(WordReviseViewModel.currentWord.value.word.word)
    }

    fun compare() {
        checked = true

        for (origWord in originalPoolOrder) {
            val answerIndex = viewModel.answerWordPool.indexOf(origWord)
            val originalIndex = viewModel.originalPoolOrder.indexOf(origWord)

            if (answerIndex == originalIndex) continue

            wrongOrderedWords.add(answerIndex)
        }

        if (wrongOrderedWords.size > 0) {
            WordReviseViewModel.setAnswer(Answer.WRONG)
            currentWord.misses++
        } else {
            WordReviseViewModel.setAnswer(Answer.CORRECT)
            currentWord.corrects++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .testTag("explanation")
    ) {
        Text(
            text = stringResource(R.string.translate_the_phrase_bellow_using_the_words_at_the_bottom),
            modifier = Modifier.padding(horizontal = 20.dp),
            style = Typography.titleMedium
        )
        Spacer(Modifier.size(25.dp))
        Text(
            text = phrase,
            modifier = Modifier.padding(horizontal = 20.dp),
            style = Typography.titleLarge
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .testTag("answer-area"),
        verticalArrangement = Arrangement.Center,
    ) {
        FlowRow(Modifier.padding(horizontal = 5.dp)) {
            answerWordPool.forEachIndexed { index, word ->
                Card(
                    shape = DefaultRoundedCornerShape,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .padding(horizontal = 5.dp)
                        .border(
                            if (wrongOrderedWords.contains(index)) 2.dp else 0.dp,
                            if (wrongOrderedWords.contains(index)) MaterialTheme.colorScheme.error else Color.Transparent,
                            DefaultRoundedCornerShape
                        ),
                    onClick = { viewModel.removeFromAnswer(word) }
                ) {
                    Text(
                        text = word,
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                        style = Typography.titleLarge,
                        softWrap = false
                    )
                }
            }
        }
        HorizontalDivider(thickness = 2.dp)
        if (checked) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .padding(horizontal = 5.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = DefaultRoundedCornerShape
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.correct_answer),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        style = Typography.titleLarge
                    )
                    FlowRow(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                        originalPoolOrder.forEachIndexed { _, word ->
                            Surface(
                                modifier = Modifier.padding(5.dp),
                                shape = DefaultRoundedCornerShape
                            ) {
                                Text(
                                    text = word,
                                    modifier = Modifier.padding(
                                        horizontal = 15.dp,
                                        vertical = 10.dp
                                    ),
                                    style = Typography.titleLarge,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .testTag("word-pool"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        FlowRow(
            modifier = Modifier.padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            wordPool.forEachIndexed { _, word ->
                Card(
                    shape = DefaultRoundedCornerShape,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .padding(horizontal = 5.dp),
                    onClick = {
                        viewModel.addToAnswer(word)
                    }
                ) {
                    Text(
                        text = word,
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                        style = Typography.titleLarge,
                        softWrap = false
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { compare() },
                modifier = Modifier
                    .height(60.dp)
                    .width(220.dp)
                    .padding(bottom = 10.dp),
                enabled = viewModel.wordPool.isEmpty() && viewModel.answerWordPool.isNotEmpty() && !checked,
                shape = DefaultRoundedCornerShape
            ) {
                Text(text = stringResource(R.string.check))
            }
            Button(
                onClick = {
                    WordReviseViewModel.setScreen(ReviseScreen.Presenter.route)
                    viewModel.cleanup()
                },
                modifier = Modifier
                    .height(60.dp)
                    .padding(bottom = 10.dp),
                enabled = checked,
                shape = DefaultRoundedCornerShape,
            ) {
                Text(text = stringResource(R.string.proceed))
                Spacer(Modifier.size(10.dp))
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}