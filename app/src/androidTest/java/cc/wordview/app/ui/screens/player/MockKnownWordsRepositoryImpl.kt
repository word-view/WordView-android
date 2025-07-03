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

package cc.wordview.app.ui.screens.player

import cc.wordview.app.ui.activities.player.viewmodel.KnownWordsRepository
import com.android.volley.RequestQueue
import jakarta.inject.Inject

class MockKnownWordsRepositoryImpl @Inject constructor() : KnownWordsRepository {
    override var onSucceed: (List<String>) -> Unit = {}
    override var onFail: (String, Int) -> Unit = { s: String, i: Int -> }

    override var endpoint: String = ""

    override lateinit var queue: RequestQueue

    override fun getKnownWords(lang: String, jwt: String) {
        onSucceed(listOf("rain", "world"))
    }
}