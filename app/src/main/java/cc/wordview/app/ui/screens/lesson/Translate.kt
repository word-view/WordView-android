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

package cc.wordview.app.ui.screens.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import cc.wordview.app.ui.screens.lesson.model.TranslateViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.R
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.components.TranslateResultContainer
import cc.wordview.app.ui.components.WordCard
import cc.wordview.app.ui.screens.lesson.components.Answer
import cc.wordview.app.ui.screens.lesson.components.ReviseScreen
import cc.wordview.app.ui.theme.DefaultRoundedCornerShape
import cc.wordview.app.ui.theme.Typography

@Composable
fun Translate(
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: TranslateViewModel = hiltViewModel()
) {
    var checked by rememberSaveable { mutableStateOf(false) }

    val phrase by viewModel.phrase.collectAsStateWithLifecycle()

    val currentWord by LessonViewModel.currentWord.collectAsStateWithLifecycle()

    val answerWordPool = viewModel.answerWordPool
    val wordPool = viewModel.wordPool
    val originalPoolOrder = viewModel.originalPoolOrder
    val wrongOrderedWords = viewModel.wrongOrderedWords

    fun check() {
        checked = true

        for (origWord in originalPoolOrder) {
            val answerIndex = viewModel.answerWordPool.indexOf(origWord)
            val originalIndex = viewModel.originalPoolOrder.indexOf(origWord)

            if (answerIndex == originalIndex) continue

            wrongOrderedWords.add(answerIndex)
        }

        if (wrongOrderedWords.size > 0) {
            LessonViewModel.setAnswer(Answer.WRONG)
            currentWord.misses++
        } else {
            LessonViewModel.setAnswer(Answer.CORRECT)
            currentWord.corrects++
        }
    }

    OneTimeEffect {
        viewModel.getPhrase(LessonViewModel.currentWord.value.word.word)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            Modifier
                .align(Alignment.TopCenter)
                .testTag("explanation")
        ) {
            Text(
                text = stringResource(R.string.translate_the_phrase_bellow_using_the_words_at_the_bottom),
                modifier = Modifier.padding(horizontal = 20.dp),
                style = Typography.labelLarge
            )
            Text(
                text = phrase,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 10.dp),
                style = Typography.titleLarge
            )
        }
        Column(
            Modifier
                .align(Alignment.Center)
                .testTag("answer-area")
        ) {
            // if not animated the horizontal will just clip
            AnimatedVisibility(answerWordPool.size > 0) {
                LazyRow(Modifier.padding(horizontal = 5.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    answerWordPool.forEachIndexed { index, word ->
                        item {
                            WordCard(
                                text = word,
                                modifier = Modifier
                                    .padding(bottom = 20.dp)
                                    .padding(horizontal = 5.dp)
                                    .border(
                                        if (wrongOrderedWords.contains(index)) 2.dp else 0.dp,
                                        if (wrongOrderedWords.contains(index)) MaterialTheme.colorScheme.error else Color.Transparent,
                                        DefaultRoundedCornerShape
                                    )
                                    .animateItem(
                                        fadeInSpec = tween(250),
                                        fadeOutSpec = tween(250),
                                        placementSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )
                                    .testTag("$word-answer"),
                                onClick = { viewModel.removeFromAnswer(word) }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 2.dp)
            AnimatedVisibility(checked) {
                TranslateResultContainer(wrongOrderedWords.size <= 0, originalPoolOrder)
            }
        }
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .testTag("word-pool"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow(
                modifier = Modifier.padding(bottom = 15.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                wordPool.forEachIndexed { _, word ->
                    item {
                        WordCard(
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .padding(horizontal = 5.dp)
                                .animateItem(
                                    fadeInSpec = tween(250),
                                    fadeOutSpec = tween(250),
                                    placementSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy)
                                )
                                .testTag("$word-wordpool"),
                            text = word,
                            onClick = { viewModel.addToAnswer(word) }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .testTag("controls"),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { check() },
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
                        LessonViewModel.setScreen(ReviseScreen.Presenter.route)
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
}