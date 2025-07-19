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

package cc.wordview.app.ui.activities.home.composables.home

import cc.wordview.app.api.request.HomeRequest
import cc.wordview.app.audio.Video
import com.android.volley.RequestQueue
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor() : HomeRepository {
    override var onSucceed: (ArrayList<Video>) -> Unit = { }
    override var onFail: (String, Int) -> Unit = { _: String, _: Int -> }

    override lateinit var queue: RequestQueue

    override fun getHomeVideos() {
        val request = HomeRequest(
            "$endpoint/api/v1/home",
            { onSucceed(it) },
            { message, status -> onFail(message, status) }
        )

        queue.add(request)
    }
}