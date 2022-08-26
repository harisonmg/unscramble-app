package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    init {
        Log.d(GameFragment.CLASS_NAME, "$CLASS_NAME created!")
        getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(GameFragment.CLASS_NAME, "$CLASS_NAME destroyed!")
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

    companion object {
        val CLASS_NAME: String = GameViewModel::class.java.simpleName
    }
}