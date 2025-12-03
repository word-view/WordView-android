package cc.wordview.app.ui.activities.lesson.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.wordview.app.components.extensions.random
import cc.wordview.app.misc.AppSettings
import cc.wordview.app.ui.activities.lesson.LessonNav
import cc.wordview.app.ui.activities.lesson.viewmodel.Answer
import cc.wordview.app.ui.activities.lesson.viewmodel.LessonViewModel
import cc.wordview.app.components.ui.OneTimeEffect
import cc.wordview.app.components.ui.Space
import cc.wordview.app.components.ui.WordCard
import cc.wordview.app.ui.theme.Typography
import cc.wordview.gengolex.Language
import cc.wordview.gengolex.word.Word
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Choose(lessonViewModel: LessonViewModel = hiltViewModel()) {
    val currentWord by lessonViewModel.currentWord.collectAsStateWithLifecycle()
    val words by lessonViewModel.wordsToRevise.collectAsStateWithLifecycle()

    val alternatives = remember { arrayListOf<Word>() }

    val langTag = AppSettings.language.get()
    val lang = remember { Language.byTag(langTag) }

    var mainText by remember { mutableStateOf("") }
    var revealedText by remember { mutableStateOf(false) }
    var buttonsEnabled by remember { mutableStateOf(true) }
    var selectedWord by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    val coroutineScope = rememberCoroutineScope()

    OneTimeEffect {
        val filteredWords = words
            .filter { w -> w.tokenWord.word != currentWord.tokenWord.word }
            .filter { w -> w.tokenWord.representable }

        val res = filteredWords.map { it.tokenWord }.random(3) + currentWord.tokenWord

        alternatives.addAll(res.shuffled())

        val wordLength = currentWord.tokenWord.word.length
        mainText = "_".repeat(wordLength)
    }

    fun correct() {
        lessonViewModel.setAnswer(Answer.CORRECT)
        currentWord.corrects++
    }

    fun wrong() {
        lessonViewModel.setAnswer(Answer.WRONG)
        currentWord.misses++
    }

    fun validate() {
        if (selectedWord?.word == currentWord.tokenWord.word) {
            correct()
            isCorrect = true
        } else {
            wrong()
            isCorrect = false
        }
        lessonViewModel.setScreen(LessonNav.Presenter.route)
    }

    // Animate main text reveal
    val mainTextScale by animateFloatAsState(
        targetValue = if (revealedText) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 350f),
        label = "mainTextScale"
    )
    val mainTextAlpha by animateFloatAsState(
        targetValue = if (revealedText) 1f else 0.85f,
        animationSpec = tween(durationMillis = 180),
        label = "mainTextAlpha"
    )

    // Animate WordCards fade/scale when disabled
    val wordCardAlpha: (Word) -> Float = { word ->
        if (buttonsEnabled) 1f
        else if (word == selectedWord) 1f
        else 0.3f
    }
    val wordCardScale: (Word) -> Float = { word ->
        if (buttonsEnabled) 1f
        else if (word == selectedWord) 1.10f
        else 0.95f
    }

    val resultScale by animateFloatAsState(
        targetValue = if (isCorrect != null) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "resultScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("choose"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconItem(currentWord.tokenWord, "icon-item")

        Text(
            text = if (revealedText && selectedWord != null) selectedWord!!.word else mainText,
            textAlign = TextAlign.Center,
            style = if (lang == Language.JAPANESE) Typography.displayLarge else Typography.displayMedium,
            modifier = Modifier
                .scale(if (isCorrect != null) resultScale else mainTextScale)
                .alpha(if (isCorrect != null) 1f else mainTextAlpha)
                .padding(bottom = 16.dp)
                .testTag("reveal-text"),
        )

        Row(Modifier.padding(top = 48.dp)) {
            for (word in alternatives) {
                WordCard(
                    text = word.word,
                    enabled = buttonsEnabled,
                    modifier = Modifier
                        .scale(wordCardScale(word))
                        .alpha(wordCardAlpha(word))
                        .testTag("alternative"),
                    onClick = {
                        buttonsEnabled = false
                        selectedWord = word
                        revealedText = true
                        // Delay for animation effect before validation
                        coroutineScope.launch {
                            delay(220)
                            validate()
                        }
                    }
                )
                Space(6.dp)
            }
        }
    }
}