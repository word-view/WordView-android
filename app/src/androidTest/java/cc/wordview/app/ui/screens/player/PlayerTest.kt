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

package cc.wordview.app.ui.screens.player

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cc.wordview.app.SongViewModel
import cc.wordview.app.ui.activities.home.HomeActivity
import cc.wordview.app.ui.activities.lesson.ReviseTimer
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.ui.activities.player.viewmodel.KnownWordsRepository
import cc.wordview.app.ui.activities.player.viewmodel.PlayerRepository
import cc.wordview.gengolex.Language
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class PlayerTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HomeActivity>()

    @Inject
    lateinit var playerRepository: PlayerRepository

    @Inject
    lateinit var knownWordsRepository: KnownWordsRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun setLyrics(lyrics: String? = null) {
        mocklyrics = lyrics ?: "{\"lyrics\":\"WEBVTT\nKind: captions\nLanguage: ja\n\n00:00:00.500 --> 00:00:00.980\n \n\n00:00:03.900 --> 00:00:18.900\n消えそうな星を眺めて\n\n00:00:19.160 --> 00:00:24.860\n憂いを夜空に吐き出す\n\n00:00:25.420 --> 00:00:30.200\n落ちる一筋の願いは\n\n00:00:30.420 --> 00:00:35.780\n淡く溶けて消えた\n\n00:00:36.580 --> 00:00:41.840\n君のいつもの口癖も\n\n00:00:42.080 --> 00:00:47.520\n負けず嫌いな性格も\n\n00:00:47.700 --> 00:00:52.860\n隣で笑うその顔も\n\n00:00:53.560 --> 00:00:58.280\n大事にしてたのに\n\n00:00:59.440 --> 00:01:04.640\n気付けば何故か言えなくなった\n\n00:01:04.860 --> 00:01:10.740\n君に向けた「好き」という言葉\n\n00:01:11.140 --> 00:01:16.380\n心の何処かでは諦めてたのかな\n\n00:01:16.480 --> 00:01:22.000\n戻れないと知ってても\n\n00:01:22.248 --> 00:01:27.628\n二人の過去を全て壊して\n\n00:01:28.148 --> 00:01:33.228\nもう僕なんか嫌いになってよ\n\n00:01:33.528 --> 00:01:39.148\nそしたら君の事を今すぐにでも\n\n00:01:39.352 --> 00:01:44.732\n忘れてしまえるのに\n\n00:01:56.768 --> 00:02:01.828\n僕の最高の思い出を\n\n00:02:02.120 --> 00:02:07.420\n胸の中にまた隠して\n\n00:02:08.300 --> 00:02:13.460\n先の見えないこの道を\n\n00:02:13.696 --> 00:02:18.016\n今日も一人歩く\n\n00:02:19.556 --> 00:02:24.516\nいつしか何も信じられない\n\n00:02:25.100 --> 00:02:30.620\nそんな自分がこの場所にいた\n\n00:02:30.728 --> 00:02:36.188\n離れたくないけど僕はもう行かなきゃ\n\n00:02:36.360 --> 00:02:41.960\n帰れないと知ってても\n\n00:02:42.240 --> 00:02:47.660\n愛した日々もその温もりも\n\n\",\"dictionary\":[{\"parent\":\"say\",\"word\":\"言\",\"derivations\":[]},{\"parent\":\"star\",\"word\":\"星\",\"derivations\":[]}]}"
    }

    private fun enterPlayer() {
        SongViewModel.setVideoStream(MockVideoStream())
        composeTestRule.onNodeWithText("Aquarela").performClick()
    }

    @Test
    fun renders() {
        setLyrics()
        enterPlayer()

        composeTestRule.waitUntilNodeCount(hasTestTag("interface"), 1, 15_000)

        composeTestRule.onNodeWithTag("fade-out-box")
            .performClick()

        composeTestRule.onNodeWithText("Gran Vals").assertExists()
        composeTestRule.onNodeWithText("Francisco Tárrega").assertExists()
        composeTestRule.onNodeWithTag("back-button").assertExists()

        composeTestRule.onNodeWithTag("skip-back").assertExists()
        composeTestRule.onNodeWithTag("toggle-play").assertExists()
        composeTestRule.onNodeWithTag("skip-forward").assertExists()
        composeTestRule.onNodeWithTag("seekbar", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("progress-line", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("buffer-line", useUnmergedTree = true).assertExists()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-plain"), 5_000)
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-word"), 5_000)
    }

    @Test
    fun goBack() {
        setLyrics()
        enterPlayer()

        composeTestRule.waitUntilNodeCount(hasTestTag("interface"), 1, 5_000)

        composeTestRule.onNodeWithTag("fade-out-box").performClick()
        composeTestRule.onNodeWithTag("back-button").performClick()

        SongViewModel.setVideoStream(MockVideoStream())

        composeTestRule.onNodeWithText("Aquarela").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("interface"), 5_000)
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-plain"), 5_000)
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-word"), 5_000)
    }

    @Test
    fun pause() {
        setLyrics()
        enterPlayer()

        composeTestRule.onNodeWithTag("fade-out-box")
            .performClick()

        composeTestRule.waitUntilNodeCount(hasTestTag("toggle-play"), 1, 1_000)
        composeTestRule.onNodeWithTag("toggle-play").performClick().performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-plain"), 5_000)
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-word"), 5_000)
    }

    @Test
    fun skipForward() {
        setLyrics()
        enterPlayer()

        composeTestRule.onNodeWithTag("fade-out-box")
            .performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("skip-forward"), 2_000)
        composeTestRule.onNodeWithTag("skip-forward")
            .performClick()
            .performClick()
            .performClick()
            .performClick()

        composeTestRule.onNodeWithTag("toggle-play").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-plain"))
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-word"))
    }


    @Test
    fun skipBack() {
        setLyrics()
        enterPlayer()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("skip-forward"), 2_000)
        composeTestRule.onNodeWithTag("skip-forward")
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()

        composeTestRule.onNodeWithTag("skip-back")
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()
            .performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-plain"), 15_000)
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("text-cue-word"), 15_000)
    }

    @Test
    fun noWordsDialogShows() {
        setLyrics()
        enterPlayer()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("skip-forward"), 2_000)
        composeTestRule.onNodeWithTag("skip-forward")
            .performClick().performClick().performClick().performClick().performClick()
            .performClick().performClick().performClick().performClick().performClick()
            .performClick().performClick()


        composeTestRule.waitUntilAtLeastOneExists(
            hasTestTag("not-enough-words-alert-dialog"),
            15_000
        )
    }

    @Test
    fun noTimeLeftDialogShows() {
        setLyrics()
        enterPlayer()

        composeTestRule.onNodeWithTag("fade-out-box")
            .performClick()

        LessonViewModel.finishTimer(language = Language.ENGLISH)

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("skip-forward"), 2_000)
        composeTestRule.onNodeWithTag("skip-forward")
            .performClick().performClick().performClick().performClick().performClick()
            .performClick().performClick().performClick().performClick().performClick()
            .performClick().performClick()


        composeTestRule.waitUntilAtLeastOneExists(
            hasTestTag("no-time-left-dialog"),
            15_000
        )
    }

    @Test
    fun goesToLessonWhenFinished() {
        setLyrics("{\"lyrics\":\"WEBVTT\n\n00:00:00.000 --> 00:00:00.350\n作词:ホリエアツシ（エグジットチューンズ株式会社）\n\n00:00:00.350 --> 00:00:00.710\n作曲:ホリエアツシ（エグジットチューンズ株式会社）\n\n00:00:00.710 --> 00:00:06.270\n通り雨輝く路面\n\n00:00:06.270 --> 00:00:11.123\n要らなくなった傘が邪魔\n\n00:00:11.710 --> 00:00:17.190\n君の手に触れたいのに\n\n00:00:17.190 --> 00:00:22.766\n近付いたら壊れてしまいそう\n\n00:00:23.930 --> 00:00:32.880\n知らない花の種を蒔いた\n\n00:00:32.880 --> 00:00:38.070\nあの高架線茜に染まる\n\n00:00:38.070 --> 00:00:40.580\n向こうへ\n\n00:00:42.380 --> 00:00:47.930\n変わらないよこの想いは\n\n00:00:47.930 --> 00:00:53.330\n心が乱れても\n\n00:00:53.330 --> 00:00:58.740\n隠せないよこの痛みを\n\n00:00:58.740 --> 00:01:04.250\n月日が降り積もっても\n\n00:01:04.250 --> 00:01:09.720\n紙芝居はお終いだよ\n\n00:01:09.720 --> 00:01:15.030\n絵だけが残ったまま\n\n00:01:15.030 --> 00:01:20.490\n忘れないって\n\n00:01:20.490 --> 00:01:25.310\nだけ言って\n\n00:01:28.000 --> 00:01:33.530\n傾く影きらめく水面\n\n00:01:33.530 --> 00:01:38.970\n伸び過ぎた髪をマフラーにしまう\n\n00:01:38.970 --> 00:01:44.450\n君の目に映りたいのに\n\n00:01:44.450 --> 00:01:49.852\n気が付いたら逃げてしまうよ\n\n00:01:51.150 --> 00:02:00.060\n嫌いな色も好きになれた\n\n00:02:00.060 --> 00:02:05.270\nあの地平線陽炎に消える\n\n00:02:05.270 --> 00:02:08.700\n何処へ\n\n00:02:09.670 --> 00:02:15.140\n変わらないよこの願いは\n\n00:02:15.140 --> 00:02:20.490\n心が汚れても\n\n00:02:20.490 --> 00:02:25.950\n失くせないよこの悲しみは\n\n00:02:25.950 --> 00:02:31.400\n月日が入れ替わっても\n\n00:02:31.400 --> 00:02:36.860\n紙吹雪が街中を舞う\n\n00:02:36.860 --> 00:02:42.270\nパレードは続いたまま\n\n00:02:42.270 --> 00:02:47.720\n戻れないって\n\n00:02:47.720 --> 00:02:52.000\nわかっていた\n\n00:02:53.990 --> 00:03:01.890\n今は綺麗な嘘でもいい\n\n00:03:01.890 --> 00:03:05.240\n騙されていたいよ\n\n00:03:05.240 --> 00:03:10.990\n覚めない夢名もない彗星\n\n00:03:10.990 --> 00:03:13.740\n探して探して\n\n00:03:13.740 --> 00:03:19.170\n見つけて失って\n\n00:03:36.900 --> 00:03:42.440\nわからないよその未来は\n\n00:03:42.440 --> 00:03:47.800\n悲しみが残ったまま\n\n00:03:47.800 --> 00:03:53.557\nまた会えるって\n\n00:03:53.557 --> 00:03:58.627\n信じて \n\n00:03:58.627 --> 00:04:05.210\n笑っていて\n\",\"dictionary\":[{\"parent\":\"umbrella\",\"word\":\"傘\",\"representable\":true,\"derivations\":null},{\"parent\":\"star\",\"word\":\"星\",\"representable\":true,\"derivations\":null},{\"parent\":\"paper\",\"word\":\"紙\",\"representable\":true,\"derivations\":null},{\"parent\":\"hand\",\"word\":\"手\",\"representable\":true,\"derivations\":[{\"parent\":\"glove\",\"word\":\"手袋\",\"representable\":true}]},{\"parent\":\"flower\",\"word\":\"花\",\"representable\":true,\"derivations\":null},{\"parent\":\"eye\",\"word\":\"目\",\"representable\":true,\"derivations\":null},{\"parent\":\"heart\",\"word\":\"心\",\"representable\":true,\"derivations\":null},{\"parent\":\"rain\",\"word\":\"雨\",\"representable\":true,\"derivations\":null}]}")
        enterPlayer()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("skip-forward"), 2_000)

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("skip-forward"), 2_000)
        composeTestRule.onNodeWithTag("skip-forward")
            .performClick().performClick().performClick().performClick().performClick()
            .performClick().performClick().performClick().performClick().performClick()
            .performClick().performClick()


        composeTestRule.waitUntilExactlyOneExists(hasTestTag("lesson-exercise"), 10_000)
    }

    @Test
    fun errorScreenRenders() {
        setLyrics("fail_trigger")
        enterPlayer()

        composeTestRule.waitUntilExactlyOneExists(hasTestTag("error-screen"), 3_000)
    }

    @Test
    // BEWARE: this test takes about 2 minutes and 50 seconds
    fun allLyricsAreRendering() {
        // This one is stripped down to match the duration of the test song which is 2:55
        setLyrics("{\"lyrics\":\"WEBVTT\n\n00:00:00.710 --> 00:00:06.270\n通り雨輝く路面\n\n00:00:06.270 --> 00:00:11.123\n要らなくなった傘が邪魔\n\n00:00:11.710 --> 00:00:17.190\n君の手に触れたいのに\n\n00:00:17.190 --> 00:00:22.766\n近付いたら壊れてしまいそう\n\n00:00:23.930 --> 00:00:32.880\n知らない花の種を蒔いた\n\n00:00:32.880 --> 00:00:38.070\nあの高架線茜に染まる\n\n00:00:38.070 --> 00:00:40.580\n向こうへ\n\n00:00:42.380 --> 00:00:47.930\n変わらないよこの想いは\n\n00:00:47.930 --> 00:00:53.330\n心が乱れても\n\n00:00:53.330 --> 00:00:58.740\n隠せないよこの痛みを\n\n00:00:58.740 --> 00:01:04.250\n月日が降り積もっても\n\n00:01:04.250 --> 00:01:09.720\n紙芝居はお終いだよ\n\n00:01:09.720 --> 00:01:15.030\n絵だけが残ったまま\n\n00:01:15.030 --> 00:01:20.490\n忘れないって\n\n00:01:20.490 --> 00:01:25.310\nだけ言って\n\n00:01:28.000 --> 00:01:33.530\n傾く影きらめく水面\n\n00:01:33.530 --> 00:01:38.970\n伸び過ぎた髪をマフラーにしまう\n\n00:01:38.970 --> 00:01:44.450\n君の目に映りたいのに\n\n00:01:44.450 --> 00:01:49.852\n気が付いたら逃げてしまうよ\n\n00:01:51.150 --> 00:02:00.060\n嫌いな色も好きになれた\n\n00:02:00.060 --> 00:02:05.270\nあの地平線陽炎に消える\n\n00:02:05.270 --> 00:02:08.700\n何処へ\n\n00:02:09.670 --> 00:02:15.140\n変わらないよこの願いは\n\n00:02:15.140 --> 00:02:20.490\n心が汚れても\n\n00:02:20.490 --> 00:02:25.950\n失くせないよこの悲しみは\n\n00:02:25.950 --> 00:02:31.400\n月日が入れ替わっても\n\n00:02:31.400 --> 00:02:36.860\n紙吹雪が街中を舞う\n\n00:02:36.860 --> 00:02:42.270\nパレードは続いたまま\n\n00:02:42.270 --> 00:02:47.720\n戻れないって\",\"dictionary\":[{\"parent\":\"umbrella\",\"word\":\"傘\",\"representable\":true,\"derivations\":null},{\"parent\":\"star\",\"word\":\"星\",\"representable\":true,\"derivations\":null},{\"parent\":\"paper\",\"word\":\"紙\",\"representable\":true,\"derivations\":null},{\"parent\":\"hand\",\"word\":\"手\",\"representable\":true,\"derivations\":[{\"parent\":\"glove\",\"word\":\"手袋\",\"representable\":true}]},{\"parent\":\"flower\",\"word\":\"花\",\"representable\":true,\"derivations\":null},{\"parent\":\"eye\",\"word\":\"目\",\"representable\":true,\"derivations\":null},{\"parent\":\"heart\",\"word\":\"心\",\"representable\":true,\"derivations\":null},{\"parent\":\"rain\",\"word\":\"雨\",\"representable\":true,\"derivations\":null}]}")
        enterPlayer()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("skip-forward"), 2_000)

        assertFullCueExists("通り雨輝く路面")
        assertFullCueExists("要らなくなった傘が邪魔")
        assertFullCueExists("君の手に触れたいのに")
        assertFullCueExists("近付いたら壊れてしまいそう")
        assertFullCueExists("知らない花の種を蒔いた")
        assertFullCueExists("あの高架線茜に染まる")
        assertFullCueExists("向こうへ")
        assertFullCueExists("変わらないよこの想いは")
        assertFullCueExists("心が乱れても")
        assertFullCueExists("隠せないよこの痛みを")
        assertFullCueExists("月日が降り積もっても")
        assertFullCueExists("紙芝居はお終いだよ")
        assertFullCueExists("絵だけが残ったまま")
        assertFullCueExists("忘れないって")
        assertFullCueExists("だけ言って")
        assertFullCueExists("傾く影きらめく水面")
        assertFullCueExists("伸び過ぎた髪をマフラーにしまう")
        assertFullCueExists("君の目に映りたいのに")
        assertFullCueExists("気が付いたら逃げてしまうよ")
        assertFullCueExists("嫌いな色も好きになれた")
        assertFullCueExists("あの地平線陽炎に消える")
        assertFullCueExists("何処へ")
        assertFullCueExists("変わらないよこの願いは")
        assertFullCueExists("心が汚れても")
        assertFullCueExists("失くせないよこの悲しみは")
        assertFullCueExists("月日が入れ替わっても")
        assertFullCueExists("紙吹雪が街中を舞う")
        assertFullCueExists("パレードは続いたまま")
        assertFullCueExists("戻れないって")
    }

    private fun assertFullCueExists(cue: String) {
        for (char in cue) {
            composeTestRule.waitUntilAtLeastOneExists(hasText(char.toString()), 10_000)
        }
    }
}