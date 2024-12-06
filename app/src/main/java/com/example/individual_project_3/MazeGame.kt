package com.example.individual_project_3

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MazeGame(context: Context) {
    var blockPosition by remember { mutableStateOf(Pair(0, 0)) }
    val commands = remember { mutableStateListOf<String>() }
    val gridSize = 5
    val goalPosition = Pair(4, 4)
    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MazeGrid(gridSize, blockPosition, goalPosition)
        Spacer(Modifier.height(16.dp))
        CommandArea(commands)
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            coroutineScope.launch {
                executeCommands(commands, blockPosition) { newPos ->
                    blockPosition = newPos
                }
                if (blockPosition == goalPosition) {
                    logProgress(context, "child", 100)
                }
            }
        }) {
            Text("Run")
        }
        Button(onClick = {
            commands.clear()
            blockPosition = Pair(0, 0)
        }, Modifier.padding(top = 8.dp)) {
            Text("Reset")
        }
    }
}

@Composable
fun MazeGrid(gridSize: Int, blockPosition: Pair<Int, Int>, goalPosition: Pair<Int, Int>) {
    Column(
        Modifier.background(Color.LightGray).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (y in 0 until gridSize) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                for (x in 0 until gridSize) {
                    val color = when {
                        blockPosition == Pair(x, y) -> Color.Blue
                        goalPosition == Pair(x, y) -> Color.Green
                        else -> Color.White
                    }
                    Box(
                        Modifier
                            .size(50.dp)
                            .background(color, RoundedCornerShape(4.dp))
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommandArea(commands: MutableList<String>) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        DirectionCommand("Up", commands)
        DirectionCommand("Down", commands)
        DirectionCommand("Left", commands)
        DirectionCommand("Right", commands)
    }
}

@Composable
fun DirectionCommand(direction: String, commands: MutableList<String>) {
    Button(onClick = { commands.add(direction) }) {
        Text(direction)
    }
}

suspend fun executeCommands(
    commands: List<String>,
    currentPosition: Pair<Int, Int>,
    updatePosition: (Pair<Int, Int>) -> Unit
) {
    var position = currentPosition
    for (command in commands) {
        when (command) {
            "Up" -> position = position.copy(second = (position.second - 1).coerceAtLeast(0))
            "Down" -> position = position.copy(second = (position.second + 1).coerceAtMost(4))
            "Left" -> position = position.copy(first = (position.first - 1).coerceAtLeast(0))
            "Right" -> position = position.copy(first = (position.first + 1).coerceAtMost(4))
        }
        updatePosition(position)
        delay(500)
    }
}

fun logProgress(context: Context, username: String, progress: Int) {
    val logFile = context.getFileStreamPath("$username-progress.txt")
    logFile.appendText("Progress: $progress%\n")
}
