package com.example.anhughes_rapidrecall

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class GameStages {
    MENU_SCREEN,
    MEMORIZATION,
    RECALL,
    REFLECT,
    ATTEMPT_SUMMARY,
    LOGS
}

class GameState {

    var stage by mutableStateOf(GameStages.MENU_SCREEN)

    var digits by mutableIntStateOf(7)

    var totalAttempts by mutableIntStateOf(0)
    var totalCorrect by mutableIntStateOf(0)

    val loggedSequences = mutableStateListOf<Sequence>()

    // no reason to initialize on app startup
    var currentSequence: Sequence? = null

    fun generateNewSequence() {
        currentSequence = Sequence(digits)
    }

    fun logSequence() {

        val sequence = currentSequence ?: return
        totalAttempts++

        if (sequence.correct)
            totalCorrect++

        loggedSequences.add(sequence)
    }
}