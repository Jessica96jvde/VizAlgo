package com.example.vizalgo.game.Stack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R
import com.example.vizalgo.game.GameLevelsScreen
import com.example.vizalgo.game.ProgressManager
import com.example.vizalgo.utils.glassmorphic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StackGame : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progressManager = ProgressManager(this)
        
        setContent {
            var selectedLevel by remember { mutableIntStateOf(0) }
            var unlockedLevel by remember { mutableIntStateOf(progressManager.getUnlockedLevel("Stack")) }
            var levelStars by remember { mutableStateOf(progressManager.getAllLevelStars("Stack")) }

            if (selectedLevel == 0) {
                GameLevelsScreen(
                    dsName = "Stack",
                    unlockedLevel = unlockedLevel,
                    levelStars = levelStars,
                    onLevelSelected = { selectedLevel = it },
                    onBack = { finish() }
                )
            } else {
                StackGamePlayScreen(
                    level = selectedLevel,
                    onComplete = { stars ->
                        progressManager.saveProgress("Stack", selectedLevel, stars)
                        unlockedLevel = progressManager.getUnlockedLevel("Stack")
                        levelStars = progressManager.getAllLevelStars("Stack")
                        selectedLevel = 0
                    },
                    onBack = { selectedLevel = 0 }
                )
            }
        }
    }
}

data class StackInstruction(val op: String, val value: Int? = null)

@Composable
fun StackGamePlayScreen(level: Int, onComplete: (Int) -> Unit, onBack: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val green4 = colorResource(id = R.color.green4)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    
    // Game State
    val stack = remember { mutableStateListOf<Int>() }
    val instructions = remember {
        mutableStateListOf<StackInstruction>().apply {
            when {
                level <= 5 -> { // Easy: 6 steps
                    add(StackInstruction("PUSH", 10))
                    add(StackInstruction("PUSH", 20))
                    add(StackInstruction("POP"))
                    add(StackInstruction("PUSH", 30))
                    add(StackInstruction("PUSH", 40))
                    add(StackInstruction("POP"))
                }
                level <= 10 -> { // Medium: 8 steps
                    add(StackInstruction("PUSH", 15))
                    add(StackInstruction("PUSH", 25))
                    add(StackInstruction("POP"))
                    add(StackInstruction("PUSH", 35))
                    add(StackInstruction("PUSH", 45))
                    add(StackInstruction("POP"))
                    add(StackInstruction("PUSH", 55))
                    add(StackInstruction("POP"))
                }
                else -> { // Hard: 10 steps
                    add(StackInstruction("PUSH", 5))
                    add(StackInstruction("PUSH", 10))
                    add(StackInstruction("POP"))
                    add(StackInstruction("PUSH", 15))
                    add(StackInstruction("PUSH", 20))
                    add(StackInstruction("POP"))
                    add(StackInstruction("PUSH", 25))
                    add(StackInstruction("PUSH", 30))
                    add(StackInstruction("POP"))
                    add(StackInstruction("PUSH", 35))
                }
            }
        }
    }
    var currentStep by remember { mutableIntStateOf(0) }
    var xp by remember { mutableFloatStateOf(100f) }
    var showErrorIndex by remember { mutableIntStateOf(-1) }
    var showStackResultDialog by remember { mutableStateOf(false) }
    var finalStars by remember { mutableIntStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var recentlyAddedIndex by remember { mutableIntStateOf(-1) }
    var recentlyDeletedIndex by remember { mutableIntStateOf(-1) }
    var recentlyError by remember { mutableStateOf(false) }

    var interactionType by remember { mutableStateOf("NONE") } // NONE, PUSH, POP
    var pendingValue by remember { mutableStateOf<Int?>(null) }

    val xpPerMistake = when {
        instructions.size <= 6 -> 16.67f
        instructions.size <= 8 -> 12.5f
        else -> 10f
    }

    fun handleMistake() {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        scope.launch {
            xp -= xpPerMistake
            showErrorIndex = currentStep
            recentlyError = true
            delay(800)
            showErrorIndex = -1
            recentlyError = false
        }
    }

    fun completeAction(isGhost: Boolean = false) {
        if (currentStep >= instructions.size) return
        val instr = instructions[currentStep]

        if (interactionType == "PUSH" && isGhost) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            stack.add(pendingValue!!)
            recentlyAddedIndex = stack.size - 1
            scope.launch { delay(500); recentlyAddedIndex = -1 }
            currentStep++
            userInput = ""
        } else if (interactionType == "POP" && !isGhost) {
            val isCorrectPop = instr.op == "POP"
            if (!isCorrectPop) {
                handleMistake()
                return
            }
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            recentlyDeletedIndex = stack.size - 1
            scope.launch {
                delay(500)
                if (stack.isNotEmpty()) stack.removeAt(stack.size - 1)
                recentlyDeletedIndex = -1
            }
            currentStep++
        } else {
            handleMistake()
        }
        interactionType = "NONE"
        if (currentStep >= instructions.size) {
            scope.launch { delay(600); finalStars = calculateStackStars(xp); showStackResultDialog = true }
        }
    }

    val backgroundRes = when {
        level <= 5 -> R.drawable.easy
        level <= 10 -> R.drawable.medium
        else -> R.drawable.hard
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(
            modifier = Modifier.fillMaxSize().imePadding().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // XP Bar and Stars
            StackXPBar(xp = xp)
            
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Stack Game",
                fontFamily = cantoraFont,
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Instructions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp).heightIn(max = 130.dp).verticalScroll(rememberScrollState())) {
                    Text(
                        "Instructions:",
                        color = Color.White,
                        fontFamily = cantoraFont,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    instructions.forEachIndexed { index, instruction ->
                        val isDone = index < currentStep
                        val isCurrent = index == currentStep
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 1.dp)
                                .background(
                                    if (showErrorIndex == index) Color.Red.copy(alpha = 0.3f) 
                                    else if (isCurrent) Color.White.copy(alpha = 0.1f) 
                                    else Color.Transparent,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(4.dp)
                        ) {
                            Text(
                                "${index + 1}) ${instruction.op}${if (instruction.value != null) " ${instruction.value}" else ""}",
                                color = if (isDone) Color.Gray else Color.White,
                                fontFamily = cantoraFont,
                                fontSize = 16.sp,
                                textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Visual Stack
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState(), reverseScrolling = true)
                        .animateContentSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (interactionType == "PUSH") {
                        StackGhostNode(onGhostClick = { completeAction(isGhost = true) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    val reversedStack = stack.reversed()
                    reversedStack.forEachIndexed { index, value ->
                        val originalIndex = stack.size - 1 - index
                        val isFirst = index == 0
                        val isLast = index == reversedStack.lastIndex
                        
                        val shape = when {
                            isFirst && isLast -> RoundedCornerShape(20.dp)
                            isFirst -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                            isLast -> RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                            else -> RoundedCornerShape(0.dp)
                        }

                        val glowColor = when {
                            originalIndex == recentlyAddedIndex -> Color.Blue.copy(alpha = 0.4f)
                            originalIndex == recentlyDeletedIndex -> Color.Red.copy(alpha = 0.6f)
                            recentlyError && isFirst -> Color.Red.copy(alpha = 0.6f)
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .height(60.dp)
                                .background(glowColor, shape)
                                .border(2.dp, if (glowColor != Color.Transparent) Color.White else Color.Transparent, shape)
                                .glassmorphic(shape = shape)
                                .clickable(enabled = interactionType == "POP" && isFirst) { completeAction(isGhost = false) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = value.toString(),
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = cantoraFont
                            )
                        }
                        if (!isLast) Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Controls with TextField
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = userInput,
                        onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 4) userInput = it },
                        placeholder = { Text("Value", color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        textStyle = TextStyle(color = Color.White, fontSize = 18.sp, fontFamily = cantoraFont),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = green4,
                            unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
                        ),
                        singleLine = true
                    )

                    // PUSH BUTTON
                    Button(
                        onClick = {
                            if (currentStep < instructions.size) {
                                val instr = instructions[currentStep]
                                if (instr.op == "PUSH" && userInput == instr.value?.toString()) {
                                    interactionType = "PUSH"
                                    pendingValue = instr.value
                                } else {
                                    handleMistake()
                                }
                            }
                        },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = green4)
                    ) {
                        Text("Push", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = cantoraFont)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // POP BUTTON
                    Button(
                        onClick = {
                            if (currentStep < instructions.size) {
                                val instr = instructions[currentStep]
                                if (instr.op == "POP") {
                                    interactionType = "POP"
                                } else {
                                    handleMistake()
                                }
                            }
                        },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Pop", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = cantoraFont)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // CLEAR BUTTON
                    Button(
                        onClick = {
                            stack.clear()
                            currentStep = 0
                            userInput = ""
                            interactionType = "NONE"
                        },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                    ) {
                        Text("Clear", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = cantoraFont)
                    }
                }
            }
        }

        if (showStackResultDialog) {
            StackResultDialog(stars = finalStars, cantoraFont = cantoraFont) {
                onComplete(finalStars)
            }
        }
    }
}

@Composable
fun StackGhostNode(onGhostClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(60.dp)
            .clickable { onGhostClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.5f),
                style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)),
                cornerRadius = CornerRadius(20.dp.toPx())
            )
        }
        Text("?", color = Color.White.copy(alpha = 0.5f), fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

fun handleStackGameInput(
    value: Int?,
    op: String,
    instructions: List<StackInstruction>,
    currentStep: Int,
    onError: () -> Unit,
    onSuccess: () -> Unit
) {
    if (currentStep >= instructions.size) return
    
    val target = instructions[currentStep]
    if (target.op == op && (target.value == null || target.value == value)) {
        onSuccess()
    } else {
        onError()
    }
}

fun calculateStackStars(xp: Float): Int = when { xp >= 99.9f -> 3; xp >= 66.6f -> 2; xp >= 33.3f -> 1; else -> 0 }

@Composable
fun StackXPBar(xp: Float) {
    val starColor1 = Color(0xFF2196F3) // Blue
    val starColor2 = Color(0xFF4CAF50) // Green
    val starColor3 = Color(0xFFFF5252) // Red

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            // Star 1 at 33.3%
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.333f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                StackStarIcon(xp, 33.3f, starColor1)
            }
            // Star 2 at 66.6%
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.666f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                StackStarIcon(xp, 66.6f, starColor2)
            }
            // Star 3 at 100%
            Box(
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                StackStarIcon(xp, 100f, starColor3)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (xp / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = Color(0xFF81C784),
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun StackStarIcon(xp: Float, threshold: Float, color: Color) {
    val isFull = xp >= threshold
    val isHalf = !isFull && xp >= (threshold - 16.6f)
    
    Icon(
        imageVector = when {
            isFull -> Icons.Filled.Star
            isHalf -> Icons.AutoMirrored.Filled.StarHalf
            else -> Icons.Outlined.Star
        },
        contentDescription = null,
        tint = if (isFull || isHalf) color else Color.Gray,
        modifier = Modifier.size(28.dp)
    )
}

@Composable
fun StackResultDialog(stars: Int, cantoraFont: FontFamily, onDismiss: () -> Unit) {
    val message = when (stars) {
        3 -> "EXCELLENT"
        2 -> "WELL DONE"
        1 -> "CHALLENGE DONE"
        else -> "CHALLENGE FAILED"
    }

    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK") }
        },
        title = {
            Text(
                text = message,
                fontFamily = cantoraFont,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                StackStarIcon(if (stars >= 1) 33.3f else 0f, 33.3f, Color(0xFF2196F3))
                Spacer(modifier = Modifier.width(8.dp))
                StackStarIcon(if (stars >= 2) 66.6f else 0f, 66.6f, Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                StackStarIcon(if (stars >= 3) 100f else 0f, 100f, Color(0xFFFF5252))
            }
        }
    )
}
