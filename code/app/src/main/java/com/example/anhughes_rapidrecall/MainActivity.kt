package com.example.anhughes_rapidrecall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anhughes_rapidrecall.ui.theme.AnhughesRapidRecallTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState = remember { GameState() }
            AnhughesRapidRecallTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(
                        appState,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun App(state: GameState, modifier: Modifier = Modifier) {

    // Consider GameState.stage and render the appropriate screen

    when (state.stage) {
        GameStages.MENU_SCREEN -> StartScreen(state)
        GameStages.MEMORIZATION -> SequenceScreen(state)
        GameStages.RECALL -> RecallScreen(state)
        GameStages.REFLECT -> ReflectScreen(state)
        GameStages.ATTEMPT_SUMMARY -> AttemptLogScreen(state)
        GameStages.LOGS -> LogScreen(state)
    }

    // at first i had if/else for each case and then i came across
    // kotlin's "when" keyword

    // goated

}