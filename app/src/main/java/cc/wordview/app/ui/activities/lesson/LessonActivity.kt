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

package cc.wordview.app.ui.activities.lesson

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.components.extensions.openActivity
import cc.wordview.app.components.extensions.setOrientationSensorPortrait
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.ui.activities.WordViewActivity
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.activities.statistics.StatisticsActivity
import cc.wordview.app.ui.components.BackTopAppBar
import cc.wordview.app.ui.components.Icon
import cc.wordview.app.ui.components.LessonQuitDialog
import cc.wordview.app.components.ui.OneTimeEffect
import cc.wordview.app.ui.dtos.LessonToStatisticsCommunicator
import cc.wordview.app.ui.theme.WordViewTheme
import cc.wordview.gengolex.Language
import dagger.hilt.android.AndroidEntryPoint
import me.zhanghai.compose.preference.ProvidePreferenceLocals

@AndroidEntryPoint
class LessonActivity : WordViewActivity() {
    private val viewModel: LessonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.load()

        setOrientationSensorPortrait()
        enableEdgeToEdge()
        setContent {
            WordViewTheme {
                ProvidePreferenceLocals {
                    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                    val timer by viewModel.timer.collectAsStateWithLifecycle()
                    val timerFinished by viewModel.timerFinished.collectAsStateWithLifecycle()
                    val translations by viewModel.translations.collectAsStateWithLifecycle()

                    val activity = LocalActivity.current!!
                    val context = LocalContext.current

                    val langTag = AppSettings.language.get()
                    val language = Language.byTag(langTag)

                    var openQuitConfirm by remember { mutableStateOf(false) }

                    OneTimeEffect {
                        viewModel.getTranslations()
                        viewModel.nextWord()

                        ReviseTimer.start(
                            context = context,
                            onFinish = {
                                viewModel.finishTimer(language)
                            },
                            onTick = {
                                viewModel.setFormattedTime(it)
                            }
                        )
                    }

                    fun leave() {
                        ReviseTimer.pause()
                        viewModel.cleanWords()
                        activity.finish()
                    }

                    fun goToStatistics() {
                        ReviseTimer.pause()
                        LessonToStatisticsCommunicator.wordsLearnedAmount = viewModel.getKnownWordsAmount()
                        LessonToStatisticsCommunicator.translations = translations
                        viewModel.cleanWords()
                        context.openActivity<StatisticsActivity>()
                        activity.finish()
                    }

                    if (openQuitConfirm) {
                        ReviseTimer.pause()
                        LessonQuitDialog(
                            onDismiss = {
                                openQuitConfirm = false
                                ReviseTimer.start(
                                    context = context,
                                    onFinish = {
                                        viewModel.finishTimer(language)
                                    },
                                    onTick = {
                                        viewModel.setFormattedTime(it)
                                    }
                                )
                            },
                            onConfirm = { leave() }
                        )
                    }

                    LaunchedEffect(timerFinished) {
                        if (timerFinished) {
                            goToStatistics()
                        }
                    }

                    Scaffold(topBar = {
                        BackHandler { openQuitConfirm = true }
                        BackTopAppBar(title = {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = timer, fontSize = 20.sp)
                                Icon(
                                    modifier = Modifier.padding(end = 12.dp, start = 6.dp),
                                    imageVector = Icons.Filled.Timelapse,
                                )
                            }
                        }) { openQuitConfirm = true }
                    }) { innerPadding ->
                        Crossfade(
                            targetState = currentScreen,
                            label = "Screen switch cross fade",
                            animationSpec = tween(250)
                        ) {
                            Box(Modifier.fillMaxSize().testTag("lesson-exercise")) {
                                LessonNav.getByRoute(it)?.Composable(innerPadding)
                            }
                        }
                    }
                }
            }
        }
    }
}