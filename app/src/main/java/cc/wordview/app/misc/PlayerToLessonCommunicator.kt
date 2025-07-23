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

package cc.wordview.app.misc

import cc.wordview.app.ui.activities.lesson.viewmodel.ReviseWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

/**
 * Holds the data that is prepared beforehand by the player that will be used in the lesson
 */
object PlayerToLessonCommunicator {
    val wordsToRevise = MutableStateFlow<ArrayList<ReviseWord>>(arrayListOf())

    fun appendWord(reviseWord: ReviseWord) {
        if (wordsToRevise.value.contains(reviseWord)) return
        Timber.d("Appending '${reviseWord.tokenWord.word}' to be revised")
        wordsToRevise.update { (it + reviseWord) as ArrayList<ReviseWord> }
    }
}