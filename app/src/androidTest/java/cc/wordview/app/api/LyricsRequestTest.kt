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

package cc.wordview.app.api

import cc.wordview.app.BuildConfig
import cc.wordview.app.ComposeTest
import cc.wordview.app.api.request.LyricsRequest
import cc.wordview.app.extensions.asURLEncoded
import com.android.volley.toolbox.Volley
import org.junit.Test

class LyricsRequestTest : ComposeTest() {
    private val endpoint get() = BuildConfig.API_BASE_URL

    @Test
    fun getLyrics() {
        val queue = Volley.newRequestQueue(context)

        val url = APIUrl("$endpoint/api/v1/lyrics")

        url.addRequestParam("id", "ZpT9VCUS54s")
        url.addRequestParam("lang", "ja")
        url.addRequestParam("trackName", "suisei no parade".asURLEncoded())
        url.addRequestParam("artistName", "majiko".asURLEncoded())

        val request = LyricsRequest(
            url.getURL(),
            { lyrics, dictionary ->
                run {
                    // The dictionary is always changing, so strictly comparing will not be done
                    assert(dictionary.isNotEmpty() && dictionary.isNotBlank())
                    assert(lyrics == "WEBVTT\n" +
                            "\n" +
                            "00:00:00.710 --> 00:00:06.270\n" +
                            "通り雨輝く路面\n" +
                            "\n" +
                            "00:00:06.270 --> 00:00:11.123\n" +
                            "要らなくなった傘が邪魔\n" +
                            "\n" +
                            "00:00:11.710 --> 00:00:17.190\n" +
                            "君の手に触れたいのに\n" +
                            "\n" +
                            "00:00:17.190 --> 00:00:22.766\n" +
                            "近付いたら壊れてしまいそう\n" +
                            "\n" +
                            "00:00:23.930 --> 00:00:32.880\n" +
                            "知らない花の種を蒔いた\n" +
                            "\n" +
                            "00:00:32.880 --> 00:00:38.070\n" +
                            "あの高架線茜に染まる\n" +
                            "\n" +
                            "00:00:38.070 --> 00:00:40.580\n" +
                            "向こうへ\n" +
                            "\n" +
                            "00:00:42.380 --> 00:00:47.930\n" +
                            "変わらないよこの想いは\n" +
                            "\n" +
                            "00:00:47.930 --> 00:00:53.330\n" +
                            "心が乱れても\n" +
                            "\n" +
                            "00:00:53.330 --> 00:00:58.740\n" +
                            "隠せないよこの痛みを\n" +
                            "\n" +
                            "00:00:58.740 --> 00:01:04.250\n" +
                            "月日が降り積もっても\n" +
                            "\n" +
                            "00:01:04.250 --> 00:01:09.720\n" +
                            "紙芝居はお終いだよ\n" +
                            "\n" +
                            "00:01:09.720 --> 00:01:15.030\n" +
                            "絵だけが残ったまま\n" +
                            "\n" +
                            "00:01:15.030 --> 00:01:20.490\n" +
                            "忘れないって\n" +
                            "\n" +
                            "00:01:20.490 --> 00:01:25.310\n" +
                            "だけ言って\n" +
                            "\n" +
                            "00:01:28.000 --> 00:01:33.530\n" +
                            "傾く影きらめく水面\n" +
                            "\n" +
                            "00:01:33.530 --> 00:01:38.970\n" +
                            "伸び過ぎた髪をマフラーにしまう\n" +
                            "\n" +
                            "00:01:38.970 --> 00:01:44.450\n" +
                            "君の目に映りたいのに\n" +
                            "\n" +
                            "00:01:44.450 --> 00:01:49.852\n" +
                            "気が付いたら逃げてしまうよ\n" +
                            "\n" +
                            "00:01:51.150 --> 00:02:00.060\n" +
                            "嫌いな色も好きになれた\n" +
                            "\n" +
                            "00:02:00.060 --> 00:02:05.270\n" +
                            "あの地平線陽炎に消える\n" +
                            "\n" +
                            "00:02:05.270 --> 00:02:08.700\n" +
                            "何処へ\n" +
                            "\n" +
                            "00:02:09.670 --> 00:02:15.140\n" +
                            "変わらないよこの願いは\n" +
                            "\n" +
                            "00:02:15.140 --> 00:02:20.490\n" +
                            "心が汚れても\n" +
                            "\n" +
                            "00:02:20.490 --> 00:02:25.950\n" +
                            "失くせないよこの悲しみは\n" +
                            "\n" +
                            "00:02:25.950 --> 00:02:31.400\n" +
                            "月日が入れ替わっても\n" +
                            "\n" +
                            "00:02:31.400 --> 00:02:36.860\n" +
                            "紙吹雪が街中を舞う\n" +
                            "\n" +
                            "00:02:36.860 --> 00:02:42.270\n" +
                            "パレードは続いたまま\n" +
                            "\n" +
                            "00:02:42.270 --> 00:02:47.720\n" +
                            "戻れないって\n" +
                            "\n" +
                            "00:02:47.720 --> 00:02:52.000\n" +
                            "わかっていた\n" +
                            "\n" +
                            "00:02:53.990 --> 00:03:01.890\n" +
                            "今は綺麗な嘘でもいい\n" +
                            "\n" +
                            "00:03:01.890 --> 00:03:05.240\n" +
                            "騙されていたいよ\n" +
                            "\n" +
                            "00:03:05.240 --> 00:03:10.990\n" +
                            "覚めない夢名もない彗星\n" +
                            "\n" +
                            "00:03:10.990 --> 00:03:13.740\n" +
                            "探して探して\n" +
                            "\n" +
                            "00:03:13.740 --> 00:03:19.170\n" +
                            "見つけて失って\n" +
                            "\n" +
                            "00:03:36.900 --> 00:03:42.440\n" +
                            "わからないよその未来は\n" +
                            "\n" +
                            "00:03:42.440 --> 00:03:47.800\n" +
                            "悲しみが残ったまま\n" +
                            "\n" +
                            "00:03:47.800 --> 00:03:53.557\n" +
                            "また会えるって\n" +
                            "\n" +
                            "00:03:53.557 --> 00:03:58.627\n" +
                            "信じて \n" +
                            "\n" +
                            "00:03:58.627 --> 00:04:05.210\n" +
                            "笑っていて\n")
                }
            },
            { _, _ -> run {
                throw FailedTestRequestException("Are you sure the API is running?")
            } }
        )

        queue.add(request)
    }
}