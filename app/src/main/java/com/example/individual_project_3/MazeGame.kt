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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MazeGame(context: Context, difficulty: String?, onMainMenu: () -> Unit) {
    val gridSize = when (difficulty?.lowercase()) {
        "easy" -> 5
        "hard" -> 10
        else -> 5
    }
    var blockPosition by remember { mutableStateOf(Pair(0, 0)) }
    val commands = remember { mutableStateListOf<String>() }
    val gameIndex = remember { mutableStateOf(0) }
    val goalPosition = Pair(gridSize - 1, gridSize - 1)
    val coroutineScope = rememberCoroutineScope()

    val mazeList = when (difficulty?.lowercase()) {
        "easy" -> listOf(
            generateFixedMaze(gridSize, Pair(0, 0), goalPosition, "easy"),
            generateEasyMaze2(gridSize),
            generateEasyMaze3(gridSize)
        )
        "hard" -> listOf(
            generateFixedMaze(gridSize, Pair(0, 0), goalPosition, "hard"),
            generateHardMaze2(gridSize),
            generateHardMaze3(gridSize)
        )
        else -> listOf(generateFixedMaze(gridSize, Pair(0, 0), goalPosition, "easy"))
    }

    var isDialogVisible by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isFinalGame by remember { mutableStateOf(false) }

    if (isDialogVisible) {
        ShowCongratsDialog(
            message = dialogMessage,
            isVisible = isDialogVisible,
            onDismiss = { isDialogVisible = false },
            onNextGame = {
                isDialogVisible = false
                if (isFinalGame) {
                    onMainMenu()
                } else {
                    gameIndex.value++
                    blockPosition = Pair(0, 0)
                    commands.clear()
                }
            },
            onMainMenu = onMainMenu
        )
    }

    if (gameIndex.value < mazeList.size) {
        val maze = mazeList[gameIndex.value]
        isFinalGame = gameIndex.value == mazeList.size - 1

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == 2

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MazeGrid(gridSize, blockPosition, goalPosition, maze)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DropArea(commands)
                    CommandDraggableArea(commands)
                    ControlButtons(coroutineScope, commands, gridSize, blockPosition, maze, onMainMenu) { newPos ->
                        blockPosition = newPos
                        if (newPos == goalPosition) {
                            dialogMessage = if (isFinalGame) {
                                "Congrats! You completed all games!"
                            } else {
                                "Congrats! Moving to the next game!"
                            }
                            isDialogVisible = true
                        }
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
                ControlButtons(coroutineScope, commands, gridSize, blockPosition, maze, onMainMenu) { newPos ->
                    blockPosition = newPos
                    if (newPos == goalPosition) {
                        dialogMessage = if (isFinalGame) {
                            "Congrats! You completed all games!"
                        } else {
                            "Congrats! Moving to the next game!"
                        }
                        isDialogVisible = true
                    }
                }
            }
        }
    }
}


@Composable
fun MazeGrid(gridSize: Int, blockPosition: Pair<Int, Int>, goalPosition: Pair<Int, Int>, maze: Array<Array<Boolean>>) {
    val boxSize = 30.dp
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
                        blockPosition == Pair(x, y) && goalPosition == Pair(x, y) -> Color.Blue
                        blockPosition == Pair(x, y) -> Color.Blue
                        goalPosition == Pair(x, y) -> Color.Green
                        maze[y][x] -> Color.Black
                        else -> Color.White
                    }
                    Box(
                        Modifier
                            .size(boxSize)
                            .background(color, RoundedCornerShape(4.dp))
                            .padding(1.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun DropArea(commands: MutableList<String>) {
    val scrollState = rememberScrollState()
    LaunchedEffect(commands.size) {
        // Automatically scroll to the bottom when a new command is added
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(150.dp) // Increased height to accommodate more commands
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .verticalScroll(scrollState) // Enable scrolling
    ) {
        Column {
            Text("Drop Commands Here", modifier = Modifier.align(Alignment.CenterHorizontally))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) { // Stack commands vertically
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
        DraggableCommand("Up", commands)
        DraggableCommand("Down", commands)
        DraggableCommand("Left", commands)
        DraggableCommand("Right", commands)
    }
}

@Composable
fun DraggableCommand(direction: String, commands: MutableList<String>) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    Box(
        Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
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
            .background(Color.Cyan, RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Text(text = direction, color = Color.Black)
    }
}

@Composable
fun ControlButtons(
    coroutineScope: CoroutineScope,
    commands: MutableList<String>,
    gridSize: Int,
    currentPosition: Pair<Int, Int>,
    maze: Array<Array<Boolean>>,
    onMainMenu: () -> Unit,
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


suspend fun executeCommands(
    commands: List<String>,
    currentPosition: Pair<Int, Int>,
    gridSize: Int,
    maze: Array<Array<Boolean>>,
    updatePosition: (Pair<Int, Int>) -> Unit
) {
    var position = currentPosition
    for (command in commands) {
        val direction = when (command) {
            "Up" -> Pair(-1, 0)
            "Down" -> Pair(1, 0)
            "Left" -> Pair(0, -1)
            "Right" -> Pair(0, 1)
            else -> Pair(0, 0)
        }

        while (true) {
            val nextPosition = Pair(position.first + direction.second, position.second + direction.first)
            if (
                nextPosition.first !in 0 until gridSize ||
                nextPosition.second !in 0 until gridSize ||
                maze[nextPosition.second][nextPosition.first]
            ) {
                break
            }

            position = nextPosition
            updatePosition(position)
            delay(300) // Adjust the delay for slower or faster movement
        }
    }
}

fun moveUntilBoundary(
    position: Pair<Int, Int>,
    yStep: Int,
    xStep: Int,
    gridSize: Int,
    maze: Array<Array<Boolean>>
): Pair<Int, Int> {
    var current = position
    while (true) {
        val next = Pair(current.first + xStep, current.second + yStep)
        if (next.first !in 0 until gridSize || next.second !in 0 until gridSize || maze[next.second][next.first]) {
            break
        }
        current = next
    }
    return current
}




fun generateFixedMaze(gridSize: Int, start: Pair<Int, Int>, goal: Pair<Int, Int>, difficulty: String?): Array<Array<Boolean>> {
    val maze = Array(gridSize) { Array(gridSize) { true } }
    if (difficulty?.lowercase() == "easy") {
        maze[0][0] = false; maze[0][1] = false; maze[0][2] = false; maze[0][3] = false; maze[0][4] = false
        maze[1][4] = false; maze[2][4] = false; maze[3][4] = false; maze[4][4] = false
    } else {
        maze[0][0] = false; maze[0][1] = false; maze[0][2] = false
        maze[1][2] = false; maze[2][2] = false; maze[2][3] = false
        maze[2][4] = false; maze[3][4] = false; maze[4][4] = false
        maze[4][5] = false; maze[4][6] = false; maze[5][6] = false
        maze[6][6] = false; maze[6][7] = false; maze[7][7] = false
        maze[8][7] = false; maze[8][8] = false; maze[9][8] = false
        maze[9][9] = false
    }
    return maze
}

fun generateEasyMaze2(gridSize: Int): Array<Array<Boolean>> {
    val maze = Array(gridSize) { Array(gridSize) { true } }
    maze[0][0] = false; maze[0][1] = false; maze[1][1] = false; maze[2][1] = false; maze[2][2] = false
    maze[3][2] = false; maze[3][3] = false; maze[4][3] = false; maze[4][4] = false
    return maze
}

fun generateEasyMaze3(gridSize: Int): Array<Array<Boolean>> {
    val maze = Array(gridSize) { Array(gridSize) { true } }
    maze[0][0] = false; maze[1][0] = false; maze[2][0] = false; maze[2][1] = false; maze[2][2] = false
    maze[3][2] = false; maze[3][3] = false; maze[4][3] = false; maze[4][4] = false
    return maze
}

fun generateHardMaze2(gridSize: Int): Array<Array<Boolean>> {
    val maze = Array(gridSize) { Array(gridSize) { true } }
    maze[0][0] = false; maze[1][0] = false; maze[2][0] = false; maze[3][0] = false; maze[4][0] = false
    maze[4][1] = false; maze[4][2] = false; maze[4][3] = false; maze[5][3] = false; maze[6][3] = false
    maze[6][4] = false; maze[7][4] = false; maze[8][4] = false; maze[9][4] = false; maze[9][5] = false
    maze[9][6] = false; maze[9][7] = false; maze[9][8] = false; maze[9][9] = false
    return maze
}


fun generateHardMaze3(gridSize: Int): Array<Array<Boolean>> {
    val maze = Array(gridSize) { Array(gridSize) { true } }
    maze[0][0] = false; maze[0][1] = false; maze[0][2] = false; maze[1][2] = false; maze[2][2] = false
    maze[3][2] = false; maze[3][3] = false; maze[3][4] = false; maze[4][4] = false; maze[5][4] = false
    maze[6][4] = false; maze[6][5] = false; maze[6][6] = false; maze[7][6] = false; maze[8][6] = false
    maze[9][6] = false; maze[9][7] = false; maze[9][8] = false; maze[9][9] = false
    return maze
}

@Composable
fun ShowCongratsDialog(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onNextGame: () -> Unit,
    onMainMenu: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Congratulations!") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onNextGame) {
                    Text("Next Game")
                }
            },
            dismissButton = {
                TextButton(onClick = onMainMenu) {
                    Text("Main Menu")
                }
            }
        )
    }
}
