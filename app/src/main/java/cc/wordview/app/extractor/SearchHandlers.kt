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

package cc.wordview.app.extractor

import android.util.Log
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.StreamingService
import org.schabi.newpipe.extractor.search.SearchInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import java.net.UnknownHostException
import kotlin.concurrent.thread

var YTService: StreamingService = NewPipe.getService(0);

fun search(query: String, onError: () -> Unit,  onRequestCompleted: (items: List<StreamInfoItem>) -> Unit) {
    thread {
        try {
            val search = SearchInfo.getInfo(YTService, YTService.searchQHFactory.fromQuery(query))

            val items = ArrayList<StreamInfoItem>()

            for (item in search.relatedItems) {
                if (item is StreamInfoItem) items.add(item)
            }

            onRequestCompleted(items)
        } catch (e: UnknownHostException) {
            Log.e("SearchHandlers", "$e", e)
            onError()
        }

    }
}