package com.example.vizalgo.game.SinglyLinkedList

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.vizalgo.R
import com.example.vizalgo.game.GameLevelsScreen
import com.example.vizalgo.game.ProgressManager
import com.example.vizalgo.utils.glassmorphic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SinglyLinkedListGame : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progressManager = ProgressManager(this)
        setContent {
            var selectedLevel by remember { mutableIntStateOf(0) }
            var unlockedLevel by remember { mutableIntStateOf(progressManager.getUnlockedLevel("Singly Linked List")) }
            var levelStars by remember { mutableStateOf(progressManager.getAllLevelStars("Singly Linked List")) }

            if (selectedLevel == 0) {
                GameLevelsScreen(
                    dsName = "Singly Linked List",
                    unlockedLevel = unlockedLevel,
                    levelStars = levelStars,
                    onLevelSelected = { selectedLevel = it },
                    onBack = { finish() }
                )
            } else {
                SinglyLinkedListGamePlayScreen(
                    level = selectedLevel,
                    onComplete = { stars ->
                        progressManager.saveProgress("Singly Linked List", selectedLevel, stars)
                        unlockedLevel = progressManager.getUnlockedLevel("Singly Linked List")
                        levelStars = progressManager.getAllLevelStars("Singly Linked List")
                        selectedLevel = 0
                    },
                    onBack = { selectedLevel = 0 }
                )
            }
        }
    }
}

data class SLLInstruction(val text: String, val op: String, val value: Int? = null, val pos: Int? = null)

@Composable
fun SinglyLinkedListGamePlayScreen(level: Int, onComplete: (Int) -> Unit, onBack: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val scope = rememberCoroutineScope()
    val green4 = colorResource(id = R.color.green4)
    val haptic = LocalHapticFeedback.current
    
    val list = remember { mutableStateListOf<Int>() }
    val instructions = remember {
        mutableStateListOf<SLLInstruction>().apply {
            when {
                level <= 5 -> {
                    add(SLLInstruction("Insert 10 at Head", "ADD_HEAD", 10))
                    add(SLLInstruction("Insert 20 at Tail", "ADD_TAIL", 20))
                    add(SLLInstruction("Insert 15 at Position 1", "INSERT_POS", 15, 1))
                    add(SLLInstruction("Delete the element at Position 2", "DELETE_POS", null, 2))
                    add(SLLInstruction("Delete the element at Position 0", "DELETE_POS", null, 0))
                    add(SLLInstruction("Insert 25 at Head", "ADD_HEAD", 25))
                }
                level <= 10 -> {
                    add(SLLInstruction("Insert 42 at Position 0", "ADD_HEAD", 42, 0))
                    add(SLLInstruction("Insert 84 at Tail", "ADD_TAIL", 84))
                    add(SLLInstruction("Insert 21 at Head", "ADD_HEAD", 21))
                    add(SLLInstruction("Delete the element at Position 2", "DELETE_POS", null, 2))
                    add(SLLInstruction("Delete the element at Position 1", "DELETE_POS", null, 1))
                    add(SLLInstruction("Insert 50 at Position 1", "INSERT_POS", 50, 1))
                    add(SLLInstruction("Delete the element at Position 0", "DELETE_POS", null, 0))
                    add(SLLInstruction("Insert 15 at Tail", "ADD_TAIL", 15))
                }
                else -> {
                    add(SLLInstruction("Insert 5 at Head", "ADD_HEAD", 5))
                    add(SLLInstruction("Insert 10 at Tail", "ADD_TAIL", 10))
                    add(SLLInstruction("Insert 15 at Position 1", "INSERT_POS", 15, 1))
                    add(SLLInstruction("Delete the element at Position 2", "DELETE_POS", null, 2))
                    add(SLLInstruction("Insert 20 at Position 2", "INSERT_POS", 20, 2))
                    add(SLLInstruction("Delete the element at Position 0", "DELETE_POS", null, 0))
                    add(SLLInstruction("Delete the element at Position 1", "DELETE_POS", null, 1))
                    add(SLLInstruction("Insert 30 at Tail", "ADD_TAIL", 30))
                    add(SLLInstruction("Insert 25 at Head", "ADD_HEAD", 25))
                    add(SLLInstruction("Delete the element at Position 1", "DELETE_POS", null, 1))
                }
            }
        }
    }

    var currentStep by remember { mutableIntStateOf(0) }
    var xp by remember { mutableFloatStateOf(100f) }
    var showErrorIndex by remember { mutableIntStateOf(-1) }
    var showSLLResultDialog by remember { mutableStateOf(false) }
    var finalStars by remember { mutableIntStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var recentlyAddedIndex by remember { mutableIntStateOf(-1) }
    var recentlyDeletedIndex by remember { mutableIntStateOf(-1) }
    var searchedIndex by remember { mutableIntStateOf(-1) }

    var interactionType by remember { mutableStateOf("NONE") } // NONE, INSERT, DELETE, SEARCH
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
            delay(800)
            showErrorIndex = -1
        }
    }

    fun onActionButtonClick(op: String) {
        if (currentStep >= instructions.size) return
        val instr = instructions[currentStep]

        // Map general ops to instruction types
        val isButtonValid = when(op) {
            "INSERT" -> instr.op in listOf("ADD_HEAD", "ADD_TAIL", "INSERT_POS")
            "DELETE" -> instr.op in listOf("DELETE_POS", "DELETE_FRONT", "DELETE_BACK")
            "SEARCH" -> instr.op == "SEARCH"
            else -> false
        }

        if (!isButtonValid) { handleMistake(); return }

        // Input validation
        val valMatch = when(op) {
            "INSERT", "SEARCH" -> userInput == (instr.value?.toString() ?: "")
            else -> true
        }

        if (!valMatch) { handleMistake(); return }

        interactionType = when (op) {
            "INSERT" -> "INSERT"
            "DELETE" -> "DELETE"
            "SEARCH" -> "SEARCH"
            else -> "NONE"
        }
        pendingValue = instr.value
    }

    fun completeAction(index: Int? = null) {
        if (currentStep >= instructions.size) return
        val instr = instructions[currentStep]
        
        val targetIndex = when (instr.op) {
            "ADD_HEAD" -> 0
            "ADD_TAIL" -> list.size
            "INSERT_POS" -> instr.pos ?: 0
            "DELETE_FRONT" -> 0
            "DELETE_BACK" -> if (list.isNotEmpty()) list.size - 1 else -1
            "DELETE_POS" -> instr.pos ?: -1
            "SEARCH" -> list.indexOf(instr.value ?: -1)
            else -> -1
        }

        if (index == targetIndex && index != -1) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            when (interactionType) {
                "INSERT" -> {
                    list.add(index, pendingValue!!)
                    recentlyAddedIndex = index
                    scope.launch { delay(500); recentlyAddedIndex = -1 }
                }
                "DELETE" -> {
                    recentlyDeletedIndex = index
                    scope.launch {
                        delay(500)
                        if (index in list.indices) list.removeAt(index)
                        recentlyDeletedIndex = -1
                    }
                }
                "SEARCH" -> {
                    searchedIndex = index
                    scope.launch { delay(1500); searchedIndex = -1 }
                }
            }
            currentStep++
            userInput = ""
            interactionType = "NONE"
        } else {
            handleMistake()
        }
        if (currentStep >= instructions.size) { 
            scope.launch { delay(600); finalStars = calculateSLLStars(xp); showSLLResultDialog = true }
        }
    }

    val backgroundRes = when {
        level <= 5 -> R.drawable.easy
        level <= 10 -> R.drawable.medium
        else -> R.drawable.hard
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = backgroundRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(modifier = Modifier.fillMaxSize().imePadding().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            SLLXPBar(xp = xp)
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Instructions:", color = Color.White, fontFamily = cantoraFont, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    instructions.forEachIndexed { index, instr ->
                        val isDone = index < currentStep
                        val isCurrent = index == currentStep
                        Row(modifier = Modifier.fillMaxWidth().background(if (showErrorIndex == index) Color.Red.copy(alpha = 0.3f) else if (isCurrent) Color.White.copy(alpha = 0.1f) else Color.Transparent, RoundedCornerShape(4.dp)).padding(2.dp)) {
                            Text("${index + 1}) ${instr.text}", color = if (isDone) Color.Gray else Color.White, fontFamily = cantoraFont, fontSize = 14.sp, textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Visualizer Area
            Box(
                modifier = Modifier.weight(2.5f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (list.isEmpty() && interactionType != "INSERT") {
                    Text("List is empty", color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp, fontFamily = cantoraFont)
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        // Ghost at 0
                        if (interactionType == "INSERT") {
                            SLLGhostNode(onGhostClick = { completeAction(0) })
                            if (list.isNotEmpty()) SLLConnectorArrow(green4 = green4.copy(alpha = 0.5f))
                        }

                        list.forEachIndexed { index, value ->
                            val isLastInList = index == list.lastIndex

                            SLLNodeItem(
                                value = value,
                                isFirst = index == 0,
                                isLast = isLastInList,
                                green4 = green4,
                                glowColor = when {
                                    index == recentlyAddedIndex -> Color.Blue
                                    index == recentlyDeletedIndex -> Color.Red
                                    index == searchedIndex -> Color.Yellow
                                    else -> null
                                },
                                isClickable = interactionType == "DELETE" || interactionType == "SEARCH",
                                onClick = { completeAction(index) }
                            )

                            if (interactionType == "INSERT") {
                                SLLConnectorArrow(green4 = green4.copy(alpha = 0.5f))
                                SLLGhostNode(onGhostClick = { completeAction(index + 1) })
                                if (!isLastInList) SLLConnectorArrow(green4 = green4.copy(alpha = 0.5f))
                            } else {
                                if (!isLastInList) SLLConnectorArrow(green4 = green4)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Controls
            Card(modifier = Modifier.fillMaxWidth().glassmorphic(RoundedCornerShape(24.dp)), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = userInput,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 4) userInput = it },
                            placeholder = { Text("Value", color = Color.White.copy(alpha = 0.5f)) },
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                            textStyle = TextStyle(color = Color.White, fontSize = 16.sp, fontFamily = cantoraFont),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White.copy(alpha = 0.1f), unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color.White, focusedIndicatorColor = green4, unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                        )
                        Button(
                            onClick = { list.clear(); currentStep = 0; userInput = ""; interactionType = "NONE" },
                            modifier = Modifier.padding(start = 4.dp).height(56.dp).width(75.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                        ) {
                            Text("Clear", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = cantoraFont)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Column {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SLLActionButton("Insert", green4, Modifier.weight(1f)) { onActionButtonClick("INSERT") }
                            Spacer(modifier = Modifier.width(8.dp))
                            SLLActionButton("Delete", Color(0xFFC54545), Modifier.weight(1f)) { onActionButtonClick("DELETE") }
                            Spacer(modifier = Modifier.width(8.dp))
                            SLLActionButton("Search", Color(0xFFFFA000), Modifier.weight(1f)) { onActionButtonClick("SEARCH") }
                        }
                    }
                }
            }
        }
        if (showSLLResultDialog) SLLResultDialog(stars = finalStars, cantoraFont = cantoraFont) { onComplete(finalStars) }
    }
}

@Composable
fun SLLNodeItem(value: Int, isFirst: Boolean, isLast: Boolean, green4: Color, glowColor: Color? = null, isClickable: Boolean = false, onClick: () -> Unit = {}) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Higher visibility label area (TOP)
        Box(modifier = Modifier.height(50.dp), contentAlignment = Alignment.BottomCenter) {
            if (isFirst || isLast) {
                val labelText = buildAnnotatedString {
                    if (isFirst && isLast) {
                        withStyle(style = SpanStyle(color = Color.Cyan, fontWeight = FontWeight.ExtraBold)) { append("Head") }
                        withStyle(style = SpanStyle(color = Color.White)) { append("/") }
                        withStyle(style = SpanStyle(color = Color.Yellow, fontWeight = FontWeight.ExtraBold)) { append("Tail") }
                    } else if (isFirst) {
                        withStyle(style = SpanStyle(color = Color.Cyan, fontWeight = FontWeight.ExtraBold)) { append("Head") }
                    } else {
                        withStyle(style = SpanStyle(color = Color.Yellow, fontWeight = FontWeight.ExtraBold)) { append("Tail") }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(color = Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(bottom = 2.dp)) {
                        Text(text = labelText, fontSize = 12.sp, fontFamily = cantoraFont, modifier = Modifier.padding(horizontal = 4.dp))
                    }
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = if (isFirst) Color.Cyan else Color.Yellow, modifier = Modifier.size(16.dp))
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(width = 80.dp, height = 50.dp).background(if (glowColor != null) glowColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)).border(2.dp, glowColor ?: green4, RoundedCornerShape(12.dp)).glassmorphic(RoundedCornerShape(12.dp)).clickable(enabled = isClickable) { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = value.toString(), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = cantoraFont)
            }
            if (isLast) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                    Text("null", color = Color.White, fontSize = 10.sp, fontFamily = cantoraFont)
                }
            }
        }
        Spacer(modifier = Modifier.height(50.dp)) // Bottom balance
    }
}

@Composable
fun SLLGhostNode(onGhostClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(50.dp))
        Box(
            modifier = Modifier.size(width = 70.dp, height = 50.dp).clickable { onGhostClick() },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(color = Color.White.copy(alpha = 0.5f), style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)), cornerRadius = CornerRadius(12.dp.toPx()))
            }
            Text("?", color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun SLLConnectorArrow(green4: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(50.dp))
        Box(modifier = Modifier.height(50.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = green4, modifier = Modifier.size(28.dp).padding(horizontal = 4.dp))
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun SLLActionButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = color), contentPadding = PaddingValues(horizontal = 4.dp)) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, maxLines = 1)
    }
}

fun calculateSLLStars(xp: Float): Int = when { xp >= 99.9f -> 3; xp >= 66.6f -> 2; xp >= 33.3f -> 1; else -> 0 }

@Composable
fun SLLXPBar(xp: Float) {
    val starColor1 = Color(0xFF2196F3); val starColor2 = Color(0xFF4CAF50); val starColor3 = Color(0xFFFF5252)
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().height(40.dp)) {
            Box(modifier = Modifier.fillMaxWidth(0.33f).fillMaxHeight(), contentAlignment = Alignment.BottomEnd) { SLLStarIcon(xp, 33.3f, starColor1) }
            Box(modifier = Modifier.fillMaxWidth(0.66f).fillMaxHeight(), contentAlignment = Alignment.BottomEnd) { SLLStarIcon(xp, 66.6f, starColor2) }
            Box(modifier = Modifier.fillMaxWidth(1.0f).fillMaxHeight(), contentAlignment = Alignment.BottomEnd) { SLLStarIcon(xp, 100f, starColor3) }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(progress = { (xp / 100f).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)), color = Color(0xFF81C784), trackColor = Color.White.copy(alpha = 0.2f))
    }
}

@Composable
fun SLLStarIcon(xp: Float, threshold: Float, color: Color) {
    val isFull = xp >= threshold; val isHalf = !isFull && xp >= (threshold - 16.6f)
    Icon(imageVector = when { isFull -> Icons.Filled.Star; isHalf -> Icons.AutoMirrored.Filled.StarHalf; else -> Icons.Outlined.Star }, contentDescription = null, tint = if (isFull || isHalf) color else Color.Gray, modifier = Modifier.size(28.dp))
}

@Composable
fun SLLResultDialog(stars: Int, cantoraFont: FontFamily, onDismiss: () -> Unit) {
    val message = when (stars) { 3 -> "EXCELLENT"; 2 -> "WELL DONE"; 1 -> "CHALLENGE DONE"; else -> "CHALLENGE FAILED" }
    AlertDialog(onDismissRequest = { }, confirmButton = { Button(onClick = onDismiss) { Text("OK") } }, title = { Text(text = message, fontFamily = cantoraFont, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }, text = { Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) { SLLStarIcon(if (stars >= 1) 33.3f else 0f, 33.3f, Color(0xFF2196F3)); Spacer(modifier = Modifier.width(8.dp)); SLLStarIcon(if (stars >= 2) 66.6f else 0f, 66.6f, Color(0xFF4CAF50)); Spacer(modifier = Modifier.width(8.dp)); SLLStarIcon(if (stars >= 3) 100f else 0f, 100f, Color(0xFFFF5252)) } })
}
