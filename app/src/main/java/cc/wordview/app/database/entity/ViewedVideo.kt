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

package cc.wordview.app.database.entity

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import cc.wordview.app.api.VideoSearchResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
data class ViewedVideo @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey val uid: String = Uuid.random().toString(),
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "artist") val artist: String,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "unix_watched_at") val unixWatchedAt: Long = System.currentTimeMillis(),
) {
    companion object {
        fun fromSearchResult(vsr: VideoSearchResult): ViewedVideo {
            return ViewedVideo(
                id = vsr.id,
                title = vsr.title,
                artist = vsr.artist,
                duration = vsr.duration,
                thumbnailUrl = vsr.thumbnails.first().url,
            )
        }
    }
}

@Dao
interface ViewedVideoDAO {
    @Query("SELECT * FROM viewedvideo")
    fun getAll(): List<ViewedVideo>

    @Insert
    fun insertAll(vararg viewedVideo: ViewedVideo)
}