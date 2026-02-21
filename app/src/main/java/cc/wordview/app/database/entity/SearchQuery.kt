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
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
data class SearchQuery @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey val uid: String = Uuid.random().toString(),
    @ColumnInfo(name = "query") val query: String,
    @ColumnInfo(name = "times_searched") var timesSearched: Int
)

@Dao
interface SearchQueryDAO {
    @Query("SELECT * FROM searchquery")
    fun getAll(): List<SearchQuery>

    @Query("SELECT * FROM searchquery WHERE `query` LIKE :query LIMIT 1")
    fun findByQuery(query: String): SearchQuery?

    @Query("UPDATE searchquery SET times_searched = :value WHERE uid = :uid")
    fun updateTimesSearched(uid: String, value: Int)

    @Insert
    fun insertAll(vararg searchQueries: SearchQuery)

    @Delete
    fun delete(user: SearchQuery)
}