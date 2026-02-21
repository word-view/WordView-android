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

package cc.wordview.app.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cc.wordview.app.database.entity.SearchQuery
import cc.wordview.app.database.entity.SearchQueryDAO
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SearchQueryDatabaseTest {
    private lateinit var searchQueryDAO: SearchQueryDAO
    private lateinit var database: WordViewDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        RoomAccess.open(context, inMemory = true)

        database = RoomAccess.getDatabase()
        searchQueryDAO = database.searchQueryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun savesSearchQuery() {
        val query = SearchQuery(
            query = "Search query",
            timesSearched = 1
        )

        searchQueryDAO.insertAll(query)

        val savedQuery = searchQueryDAO.getAll().singleOrNull()

        assert(savedQuery?.query == query.query)
        assert(savedQuery?.timesSearched == 1)
    }

    @Test
    @Throws(Exception::class)
    fun getSearchQueryByQuery() {
        val query = SearchQuery(
            query = "Search query",
            timesSearched = 1
        )

        searchQueryDAO.insertAll(query)

        val savedQuery = searchQueryDAO.findByQuery(query.query)

        assert(savedQuery?.query == query.query)
        assert(savedQuery?.timesSearched == 1)
    }

    @Test
    @Throws(Exception::class)
    fun increaseTimesSearched() {
        val query = SearchQuery(
            query = "Search query",
            timesSearched = 1
        )

        searchQueryDAO.insertAll(query)

        val savedQuery = searchQueryDAO.getAll().singleOrNull()

        assert(savedQuery?.timesSearched == 1)

        searchQueryDAO.updateTimesSearched(savedQuery!!.uid, 2)

        val updatedQuery = searchQueryDAO.getAll().singleOrNull()

        assert(updatedQuery?.timesSearched == 2)
    }
}