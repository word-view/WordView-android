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
import cc.wordview.app.extractor.VideoStreamInterface
import javax.inject.Inject

class MockPlayerRepositoryImpl @Inject constructor() : PlayerRepository {
    override var onGetLyricsSuccess: (String) -> Unit = {}

    override var endpoint: String = ""

    override fun init(context: Context) {}

    override fun getLyrics(id: String, lang: String, video: VideoStreamInterface) {
        onGetLyricsSuccess(
            "{\"lyrics\":\"WEBVTT\nKind: captions\nLanguage: ja\n\n00:00:00.500 --> 00:00:00.980\n \n\n00:00:03.900 --> 00:00:18.900\n消えそうな星を眺めて\n\n00:00:19.160 --> 00:00:24.860\n憂いを夜空に吐き出す\n\n00:00:25.420 --> 00:00:30.200\n落ちる一筋の願いは\n\n00:00:30.420 --> 00:00:35.780\n淡く溶けて消えた\n\n00:00:36.580 --> 00:00:41.840\n君のいつもの口癖も\n\n00:00:42.080 --> 00:00:47.520\n負けず嫌いな性格も\n\n00:00:47.700 --> 00:00:52.860\n隣で笑うその顔も\n\n00:00:53.560 --> 00:00:58.280\n大事にしてたのに\n\n00:00:59.440 --> 00:01:04.640\n気付けば何故か言えなくなった\n\n00:01:04.860 --> 00:01:10.740\n君に向けた「好き」という言葉\n\n00:01:11.140 --> 00:01:16.380\n心の何処かでは諦めてたのかな\n\n00:01:16.480 --> 00:01:22.000\n戻れないと知ってても\n\n00:01:22.248 --> 00:01:27.628\n二人の過去を全て壊して\n\n00:01:28.148 --> 00:01:33.228\nもう僕なんか嫌いになってよ\n\n00:01:33.528 --> 00:01:39.148\nそしたら君の事を今すぐにでも\n\n00:01:39.352 --> 00:01:44.732\n忘れてしまえるのに\n\n00:01:56.768 --> 00:02:01.828\n僕の最高の思い出を\n\n00:02:02.120 --> 00:02:07.420\n胸の中にまた隠して\n\n00:02:08.300 --> 00:02:13.460\n先の見えないこの道を\n\n00:02:13.696 --> 00:02:18.016\n今日も一人歩く\n\n00:02:19.556 --> 00:02:24.516\nいつしか何も信じられない\n\n00:02:25.100 --> 00:02:30.620\nそんな自分がこの場所にいた\n\n00:02:30.728 --> 00:02:36.188\n離れたくないけど僕はもう行かなきゃ\n\n00:02:36.360 --> 00:02:41.960\n帰れないと知ってても\n\n00:02:42.240 --> 00:02:47.660\n愛した日々もその温もりも\n\n\",\"dictionary\":[{\"parent\":\"say\",\"word\":\"言\",\"derivations\":[{\"parent\":\"words\",\"word\":\"言葉\"}]},{\"parent\":\"break\",\"word\":\"壊\",\"derivations\":[{\"parent\":\"destroy\",\"word\":\"壊して\"}]},{\"parent\":\"star\",\"word\":\"星\",\"derivations\":[]},{\"parent\":\"sky\",\"word\":\"空\",\"derivations\":[]},{\"parent\":\"cant_see\",\"word\":\"見えない\"},{\"parent\":\"one\",\"word\":\"一\",\"derivations\":[{\"parent\":\"one_person\",\"word\":\"一人\"}]},{\"parent\":\"night\",\"word\":\"夜\",\"derivations\":[]},{\"parent\":\"two_people\",\"word\":\"二人\"},{\"parent\":\"two\",\"word\":\"二\",\"derivations\":[{\"parent\":\"two_people\",\"word\":\"二人\"}]},{\"parent\":\"i\",\"word\":\"僕\",\"derivations\":[]},{\"parent\":\"listen\",\"word\":\"聞\",\"derivations\":[{\"parent\":\"listen\",\"word\":\"聞いて\"}]},{\"parent\":\"road\",\"word\":\"道\",\"derivations\":[]},{\"parent\":\"destroy\",\"word\":\"壊して\"},{\"parent\":\"warmth\",\"word\":\"温もり\"},{\"parent\":\"tear\",\"word\":\"涙\",\"derivations\":[]},{\"parent\":\"walk\",\"word\":\"歩く\"}]}"
        )
    }
}