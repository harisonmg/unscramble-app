package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String
    private val _currentScrambledWord = MutableLiveData<String>()
    private var _score = MutableLiveData(0)
    private var _currentWordCount = MutableLiveData(0)

    // backing properties
    val score: LiveData<Int>
        get() = _score
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    init {
        getNextWord()
    }

    /**
     * Updates [currentWord] and [currentScrambledWord] with the next word.
     */
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        // shuffle until the order is different
        while (String(tempWord) == currentWord) {
            tempWord.shuffle()
        }

        // check if the word has been shown
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = _currentWordCount.value?.inc()
            wordsList.add(currentWord)
        }
    }

    /**
     * Checks if the word input by the user is correct
     */
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord == currentWord) {
            increaseScore()
            return true
        }
        return false
    }

    /**
     * Returns true if the current word count is less than [MAX_NO_OF_WORDS].
     * Updates the next word.
     */
    fun nextWord(): Boolean {
        return if (currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    /**
     * Increases the score by [SCORE_INCREASE]
     */
    private fun increaseScore() {
        _score.value = _score.value?.plus(SCORE_INCREASE)
    }

    /**
     * Re-initializes the game data to restart the game.
     */
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
}