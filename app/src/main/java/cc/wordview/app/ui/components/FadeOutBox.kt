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

package cc.wordview.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import cc.wordview.app.extensions.alpha
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FadeOutBox(
    modifier: Modifier = Modifier,
    duration: Int,
    stagnationTime: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val fade by animateFloatAsState(
        if (visible) 1f else 0f,
        tween(duration),
        label = "FadeOutBoxAnimation",
        finishedListener = {
            if (visible) {
                coroutineScope.launch {
                    delay(stagnationTime.toLong())
                    visible = false
                }
            }
        })

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            delay(500)
            visible = false
        }
    }

    Box(modifier = modifier
        .alpha(fade)
        .testTag("fade-box")
        .semantics { alpha = fade }
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }) { visible = !visible }) {
        content()
    }
}