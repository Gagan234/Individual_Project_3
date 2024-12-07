package com.example.individual_project_3

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MazeGame(context: Context, difficulty: String?) {
    // Set grid size based on difficulty
    val gridSize = when (difficulty?.lowercase()) {
        "easy" -> 5
        "hard" -> 7
        else -> 5 // Default to easy if there's an invalid or null input
    }

    var blockPosition by remember { mutableStateOf(Pair(0, 0)) }
    val commands = remember { mutableStateListOf<String>() }
    val goalPosition = Pair(gridSize - 1, gridSize - 1)

    // Predefined Maze with a path
    val maze = generatePredefinedMaze(gridSize, Pair(0, 0), goalPosition)

    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == 2 // ORIENTATION_LANDSCAPE = 2

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                MazeGrid(gridSize, blockPosition, goalPosition, maze)
                DropArea(commands)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CommandDraggableArea(commands)
                ControlButtons(coroutineScope, context, commands, gridSize, blockPosition, maze) { newPos ->
                    blockPosition = newPos
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            MazeGrid(gridSize, blockPosition, goalPosition, maze)
            DropArea(commands)
            CommandDraggableArea(commands)
            ControlButtons(coroutineScope, context, commands, gridSize, blockPosition, maze) { newPos ->
                blockPosition = newPos
            }
        }
    }
}

@Composable
fun ControlButtons(
    coroutineScope: CoroutineScope,
    context: Context,
    commands: MutableList<String>,
    gridSize: Int,
    currentPosition: Pair<Int, Int>,
    maze: Array<Array<Boolean>>,
    updatePosition: (Pair<Int, Int>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    executeCommands(commands, currentPosition, gridSize, maze, updatePosition)
                    if (currentPosition == Pair(gridSize - 1, gridSize - 1)) {
                        logProgress(context, "child", 100)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Run")
        }

        Button(
            onClick = {
                commands.clear()
                updatePosition(Pair(0, 0))
            },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Reset")
        }
    }
}

@Composable
fun MazeGrid(gridSize: Int, blockPosition: Pair<Int, Int>, goalPosition: Pair<Int, Int>, maze: Array<Array<Boolean>>) {
    val boxSize = if (gridSize == 5) 50.dp else 35.dp

    Column(
        Modifier
            .background(Color.LightGray)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (y in 0 until gridSize) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                for (x in 0 until gridSize) {
                    val color = when {
                        blockPosition == Pair(x, y) -> Color.Blue
                        goalPosition == Pair(x, y) -> Color.Green
                        maze[y][x] -> Color.Black
                        else -> Color.White
                    }
                    Box(
                        Modifier
                            .size(boxSize)
                            .background(color, RoundedCornerShape(4.dp))
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DropArea(commands: MutableList<String>) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text("Drop Commands Here", modifier = Modifier.align(Alignment.CenterHorizontally))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                commands.forEach {
                    Text(it, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}

@Composable
fun CommandDraggableArea(commands: MutableList<String>) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        DraggableCommand(Icons.Default.ArrowUpward, "Up", commands)
        DraggableCommand(Icons.Default.ArrowDownward, "Down", commands)
        DraggableCommand(Icons.Default.ArrowBack, "Left", commands)
        DraggableCommand(Icons.Default.ArrowForward, "Right", commands)
    }
}

@Composable
fun DraggableCommand(icon: ImageVector, direction: String, commands: MutableList<String>) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .graphicsLayer {
                shadowElevation = 2.dp.toPx()
            }
            .background(Color.Cyan, RoundedCornerShape(4.dp))
            .padding(8.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        commands.add(direction)
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            }
    ) {
        Icon(imageVector = icon, contentDescription = direction)
    }
}

suspend fun executeCommands(
    commands: List<String>,
    currentPosition: Pair<Int, Int>,
    gridSize: Int,
    maze: Array<Array<Boolean>>,
    updatePosition: (Pair<Int, Int>) -> Unit
) {
    var position = currentPosition
    for (command in commands) {
        val newPosition = when (command) {
            "Up" -> position.copy(second = (position.second - 1).coerceAtLeast(0))
            "Down" -> position.copy(second = (position.second + 1).coerceAtMost(gridSize - 1))
            "Left" -> position.copy(first = (position.first - 1).coerceAtLeast(0))
            "Right" -> position.copy(first = (position.first + 1).coerceAtMost(gridSize - 1))
            else -> position
        }

        if (!maze[newPosition.second][newPosition.first]) {
            position = newPosition
            updatePosition(position)
            delay(400) // Delay to keep the movement visible
        }
    }
}

fun generatePredefinedMaze(gridSize: Int, start: Pair<Int, Int>, goal: Pair<Int, Int>): Array<Array<Boolean>> {
    val maze = Array(gridSize) { Array(gridSize) { false } }

    // Create a predefined path from start to goal
    for (i in 0..goal.second) {
        maze[i][0] = false // Vertical path
    }
    for (j in 0..goal.first) {
        maze[goal.second][j] = false // Horizontal path to goal
    }

    // Add obstacles while keeping the path clear
    maze[1][2] = true
    maze[2][3] = true
    maze[3][1] = true

    return maze
}

fun logProgress(context: Context, username: String, progress: Int) {
    val logFile = context.getFileStreamPath("$username-progress.txt")
    logFile.appendText("Progress: $progress%\n")
}
