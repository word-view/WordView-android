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

package cc.wordview.app.ui.activities.lesson.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.misc.ImageCacheManager
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.word.Word
import coil3.compose.AsyncImage

@Composable
fun TextItem(word: Word, testTag: String) {
    val langTag = AppSettings.language.get()
    val lang = remember { Language.byTag(langTag) }

    Text(
        modifier = Modifier.testTag(testTag),
        text = word.word,
        textAlign = TextAlign.Center,
        style = if (lang == Language.JAPANESE) Typography.displayLarge else Typography.displayMedium,
    )
}

@Composable
fun IconItem(word: Word, testTag: String) {
    val image = ImageCacheManager.getCachedImage(word.parent)

    AsyncImage(
        modifier = Modifier
            .size(130.dp)
            .testTag(testTag),
        model = image,
        contentDescription = null
    )
}