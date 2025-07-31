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

package cc.wordview.app.ui.activities.player

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.SongViewModel
import cc.wordview.app.extensions.setOrientationSensorLandscape
import cc.wordview.app.extractor.VideoStream
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.ui.dtos.PlayerToLessonCommunicator
import cc.wordview.app.ui.activities.WordViewActivity
import cc.wordview.app.ui.activities.player.composables.ErrorScreen
import cc.wordview.app.ui.activities.player.composables.Player
import cc.wordview.app.ui.activities.player.viewmodel.PlayerState
import cc.wordview.app.ui.activities.player.viewmodel.PlayerViewModel
import cc.wordview.app.ui.components.CircularProgressIndicator
import cc.wordview.app.ui.components.OneTimeEffect
import cc.wordview.app.ui.theme.WordViewTheme
import cc.wordview.gengolex.Language
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.schabi.newpipe.extractor.exceptions.ExtractionException
import timber.log.Timber

@AndroidEntryPoint
class PlayerActivity : WordViewActivity() {
    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setOrientationSensorLandscape()
        setupWindowInsets()
        enableEdgeToEdge()
        setContent {
            ProvidePreferenceLocals {
                val state by viewModel.playerState.collectAsStateWithLifecycle()
                val videoId by SongViewModel.videoId.collectAsStateWithLifecycle()
                val videoStream by SongViewModel.videoStream.collectAsStateWithLifecycle()
                val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
                val statusCode by viewModel.statusCode.collectAsStateWithLifecycle()

                val langTag = AppSettings.language.get()

                val context = LocalContext.current

                fun start() {
                    val lang = Language.byTag(langTag)

                    Timber.i("Chosen language is ${lang.name.lowercase()}")

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            SongViewModel.videoStream.value.init(videoId, context)

                            viewModel.initAudio(videoStream.getStreamURL())
                            viewModel.getLyrics(videoId, lang, videoStream)
                            viewModel.getKnownWords(lang)
                            viewModel.getLessonTime()

                            PlayerToLessonCommunicator.initTts(context)
                        } catch (e: ExtractionException) {
                            Timber.e(e)
                            viewModel.setErrorMessage(e.message.toString())
                            viewModel.setPlayerState(PlayerState.ERROR)
                        }
                    }
                }

                OneTimeEffect { start() }

                WordViewTheme(darkTheme = true) {
                    Scaffold { innerPadding ->
                        when (state) {
                            PlayerState.READY -> Player(viewModel, innerPadding)

                            PlayerState.ERROR -> ErrorScreen(errorMessage, {
                                Timber.d("Refreshing player")
                                viewModel.setPlayerState(PlayerState.LOADING)
                                start()
                            }, statusCode)

                            PlayerState.LOADING -> Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(64.dp)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        val playerState = viewModel.playerState.value

        if (playerState == PlayerState.READY) {
            val player = viewModel.player.value
            player.pause()
        }
    }

    override fun onDestroy() {
        if (isFinishing) {
            SongViewModel.setVideoStream(VideoStream())
        }

        super.onDestroy()
    }

    private fun setupWindowInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.hide(WindowInsetsCompat.Type.displayCutout())

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}