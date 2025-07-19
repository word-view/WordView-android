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

package cc.wordview.app.ui.screens.home

import cc.wordview.app.audio.Video
import cc.wordview.app.ui.activities.home.composables.home.HomeRepository
import com.android.volley.RequestQueue
import javax.inject.Inject

class MockHomeRepositoryImpl @Inject constructor() : HomeRepository {
    override var onSucceed: (ArrayList<Video>) -> Unit = { }
    override var onFail: (String, Int) -> Unit = { _: String, _: Int -> }

    override var endpoint: String = ""

    override lateinit var queue: RequestQueue

    override fun getHomeVideos() {
        onSucceed(arrayListOf(
            Video(
                id = "ZnUEeXpxBJ0",
                title =	"Aquarela",
                artist = "Toquinho" ,
                cover =	"https://i.ytimg.com/vi_webp/ZnUEeXpxBJ0/maxresdefault.webp"
            ),
            Video(
                id = "ZpT9VCUS54s",
                title =	"Suisei no parade",
                artist = "majiko",
                cover =	"https://i.ytimg.com/vi_webp/ZpT9VCUS54s/maxresdefault.webp"
            ),
            Video(
                id = "HCTunqv1Xt4",
                title = "When im sixty four",
                artist = "The Beatles",
                cover = "https://i.ytimg.com/vi_webp/HCTunqv1Xt4/maxresdefault.webp"
            ),
        ))
    }
}