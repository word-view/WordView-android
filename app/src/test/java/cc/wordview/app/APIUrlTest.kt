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

package cc.wordview.app

import cc.wordview.app.api.APIUrl
import junit.framework.TestCase.assertEquals
import org.junit.Test

class APIUrlTest {
    @Test
    fun addOneRequestParameter() {
        val url = APIUrl("http://localhost:8080/api/v1/test")
        url.addRequestParam("testing", "true")

        assertEquals("http://localhost:8080/api/v1/test?testing=true", url.getURL())
    }

    @Test
    fun addTwoRequestParameter() {
        val url = APIUrl("http://localhost:8080/api/v1/test")
        url.addRequestParam("testing", "true")
        url.addRequestParam("mocking", "true")

        assertEquals("http://localhost:8080/api/v1/test?testing=true&mocking=true", url.getURL())
    }
}