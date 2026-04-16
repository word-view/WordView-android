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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.components.extensions.setOrientationSensorLandscape
import cc.wordview.app.settings.AppSettings
import cc.wordview.app.ui.activities.WordViewActivity
import cc.wordview.app.ui.activities.player.composables.ErrorScreen
import cc.wordview.app.ui.activities.player.composables.AudioPlayer
import cc.wordview.app.ui.activities.player.viewmodel.Display
import cc.wordview.app.ui.activities.player.viewmodel.PlayerViewModel
import cc.wordview.app.components.ui.OneTimeEffect
import cc.wordview.app.ui.activities.player.composables.VideoPlayer
import cc.wordview.app.ui.activities.player.viewmodel.PlayerErrorState
import cc.wordview.app.ui.theme.WordViewTheme
import cc.wordview.gengolex.Language
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import timber.log.Timber

@AndroidEntryPoint
class PlayerActivity : WordViewActivity() {
    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val videoId: String = intent.getStringExtra("id")!!

        // Because having the title and artist empty seems weird in
        // the player we take these as temporary values from the place
        // that has opened the player to use while the stream is not ready yet
        val title: String = intent.getStringExtra("title")!!
        val artist: String = intent.getStringExtra("artist")!!

        // The mode that the player should open, video or audio only
        val mode: String = intent.getStringExtra("mode")!!

        setOrientationSensorLandscape()
        setupWindowInsets()
        enableEdgeToEdge()
        setContent {
            ProvidePreferenceLocals {
                val uiState by viewModel.state.collectAsStateWithLifecycle()

                val langTag = AppSettings.language.get()

                val context = LocalContext.current

                fun start() {
                    val lang = Language.byTag(langTag)

                    Timber.i("Chosen language is ${lang.name.lowercase()}")

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            uiState.videoStream.init(videoId, context)

                            when (mode) {
                                "audio" -> {
                                    viewModel.setDisplay(Display.AUDIO_PLAYER)
                                    viewModel.initAudio(uiState.videoStream.getAudioStreamURL())
                                    viewModel.getLyrics(videoId, lang, uiState.videoStream)
                                }
                                "video" -> {
                                    viewModel.setDisplay(Display.VIDEO_PLAYER)
                                    viewModel.initAudio(uiState.videoStream.getVideoStreamURL())
                                    viewModel.getSubtitle(videoId, lang)
                                }
                                else -> throw IllegalArgumentException("Player mode should be either 'audio' or 'video'")
                            }

                        } catch (e: Exception) {
                            Timber.e(e)
                            viewModel.declarePlayerError(PlayerErrorState(e.message.toString()))
                        }
                    }
                }

                OneTimeEffect { start() }

                WordViewTheme(darkTheme = true) {
                    Scaffold { innerPadding ->
                        when (uiState.display) {
                            Display.VIDEO_PLAYER -> VideoPlayer(
                                videoId,
                                viewModel,
                                title,
                                artist,
                                innerPadding
                            )

                            Display.AUDIO_PLAYER -> AudioPlayer(
                                videoId,
                                viewModel,
                                title,
                                artist,
                                innerPadding
                            )

                            Display.ERROR -> ErrorScreen(viewModel) {
                                Timber.d("Refreshing player")
                                viewModel.setDisplay(Display.AUDIO_PLAYER)
                                start()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        // For some reason, in some devices onPause seems to be called
        // when starting an activity, at that point the player is not available
        if (!viewModel.ready.value)
            return

        val playerState = viewModel.state.value.display

        if (playerState == Display.AUDIO_PLAYER) {
            val player = viewModel.state.value.player
            player.pause()
        }
    }

    override fun onStop() {
        viewModel.saveCurrentPosition()
        super.onStop()
    }

    override fun onDestroy() {
        if (isFinishing) {
            viewModel.cleanup()
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