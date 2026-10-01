package com.example.anhughes_rapidrecall

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun StartScreen(state: GameState, modifier: Modifier = Modifier) {

    // No horizontal centering is a design choice (inconsistent with later screens but i like it)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        val digits = state.digits

        Text(
            text = "Can you remember $digits digits?"
        )

        Slider(
            value = digits.toFloat(),
            onValueChange = { state.digits = it.roundToInt() },
            valueRange = 1f..10f,
            steps = 8
        )

        Button(
            onClick = {
                state.generateNewSequence()
                state.stage = GameStages.MEMORIZATION
            }
        ) {
            Text("Let's go!")
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Button(
            onClick = {
                state.stage = GameStages.ATTEMPT_SUMMARY
            }
        ) {
            Text("Attempt summary")
        }

        Button(
            onClick = {
                state.stage = GameStages.LOGS
            }
        ) {
            Text("Logs")
        }

    }

}

@Composable
fun SequenceScreen(state: GameState) {

    val sequence = state.currentSequence?.numericalSequence
    val digits = state.digits
    var index by remember { mutableIntStateOf(0) }

    val alpha = remember { Animatable(1f) }

    // there has got to be a better way to implement the opacity effect

    LaunchedEffect(Unit) {
        while (index < digits) {
            alpha.snapTo(1f)

            // let opacity be at 100% for 700 ms

            delay(700.milliseconds)

            // decrease opacity over the span of 300 ms

            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )

            index++
        }

        state.stage = GameStages.RECALL
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        val color = Color.Black.copy(alpha = alpha.value)

        if (index < digits) {
            Text(
                text = sequence?.get(index).toString(),
                fontSize = 72.sp,
                color = color
            )
        }

    }
}

@Composable
fun RecallScreen(state: GameState) {

    var recallInput by remember { mutableStateOf("") }
    var validInput by remember { mutableStateOf(true) }    // this language will never feel normal to me lol
    val digits = state.digits

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = recallInput,
            onValueChange = { recallInput = it },
            label = { Text("Recall the $digits digits..") }
        )

        Button(
            onClick = {
                validInput = !(recallInput.any { it.isLetter() }) && recallInput.length == digits // i hate this language

                if (validInput) {

                    val userSequence = state.currentSequence?.inputSequence

                    for (digitChr in recallInput) {
                        userSequence?.add(digitChr.digitToInt())

                    }

                    state.stage = GameStages.REFLECT
                }
            }
        ) {
            Text(
                "Submit"
            )
        }

        if (!validInput)
            Text(
                color = Color.Red,
                text="Invalid Input!"
            )

    }

}

@Composable
fun ReflectScreen(state: GameState) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var sequencesMatch = true

        val digits = state.digits

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            var idx: Int = 0

            while (idx < digits) {

                val curDigit = state.currentSequence?.inputSequence?.get(idx)
                val correctDigit = state.currentSequence?.numericalSequence?.get(idx)

                var color = Color.Green
                if (curDigit != correctDigit) {
                    color = Color.Red
                    sequencesMatch = false
                }

                Text(
                    text="$curDigit",
                    color=color,
                    fontSize=48.sp
                )

                idx++

            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            var idx: Int = 0

            while (idx < digits) {

                val curDigit = state.currentSequence?.numericalSequence?.get(idx)

                Text(
                    text="$curDigit",
                    fontSize=48.sp
                )

                idx++

            }

        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (sequencesMatch)

            Text(
                text = "Good work! You remembered\nall $digits digits correctly.",
                textAlign = TextAlign.Center
            )

        else

            Text(
                text = "Not quite!\nClick the button below to try again.",
                textAlign = TextAlign.Center
            )

        Spacer(
            modifier = Modifier.height(96.dp)
        )

        Button(
            onClick = {
                state.currentSequence?.correct = sequencesMatch
                state.logSequence()

                state.stage = GameStages.MENU_SCREEN
            }
        ) {
            Text("Go again!")
        }

    }

}

@Composable
fun AttemptLogScreen(state: GameState) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Total Attempts: ${state.totalAttempts}"
        )

        Text(
            text = "Total Correct Attempts: ${state.totalCorrect}"
        )

        // assuming necessary for int division
        var accuracy = state.totalCorrect.toFloat() / state.totalAttempts.toFloat() * 100f
        if (state.totalAttempts == 0)
            accuracy = 0f

        Text(
            text = "Total Accuracy: ${"%.1f".format(accuracy)}%"
        )

        // some arbitrary height that i don't use in any other spacers (i need to get better at this)

        Spacer(
            modifier = Modifier.height(72.dp)
        )

        Button(
            onClick = {
                state.stage = GameStages.MENU_SCREEN
            }
        ) {
            Text("Back")
        }

    }

}

@Composable
fun LogScreen(state: GameState) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "${state.totalAttempts} attempts logged",
            fontSize = 30.sp
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .padding(72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(state.loggedSequences) { sequence ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp)
                        .background(Color(0.878f, 0.867f, 0.867f, 1.0f))
                ) {
                    Text(
                        text = "Target: ${sequence.getTargetString()}"
                    )
                    Text(
                        text = "Input: ${sequence.getInputString()}"
                    )
                    Text(
                        text = "Correct: ${sequence.correct}"
                    )
                }

            }

        }

        Button(
            onClick = {
                state.stage = GameStages.MENU_SCREEN
            }
        ) {
            Text("Back")
        }

    }

}