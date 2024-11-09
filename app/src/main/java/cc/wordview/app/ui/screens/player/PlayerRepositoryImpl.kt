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

package cc.wordview.app.ui.screens.player

import android.content.Context
import android.util.Log
import cc.wordview.app.api.Response
import cc.wordview.app.extractor.VideoStreamInterface
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor() : PlayerRepository {
    private val TAG = this::class.java.simpleName

    override lateinit var endpoint: String
    override var onGetLyricsSuccess: (String) -> Unit = {}

    private lateinit var queue: RequestQueue

    override fun init(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    override fun getLyrics(id: String, lang: String, video: VideoStreamInterface) {
        val url =
            "$endpoint/api/v1/lyrics?id=$id&lang=$lang&trackName=${video.cleanTrackName}&artistName=${video.cleanArtistName}"

        val response = Response({ onGetLyricsSuccess(it) }, { Log.e(TAG, "getLyrics: ", it) })

        val request = jsonRequest(url, response)

        request.setRetryPolicy(
            DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        queue.add(request)
    }
}