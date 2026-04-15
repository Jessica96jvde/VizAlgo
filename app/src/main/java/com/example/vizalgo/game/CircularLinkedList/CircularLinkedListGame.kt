package com.example.vizalgo.game.CircularLinkedList

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.vizalgo.R
import com.example.vizalgo.game.GameLevelsScreen
import com.example.vizalgo.game.ProgressManager
import com.example.vizalgo.utils.glassmorphic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

class CircularLinkedListGame : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progressManager = ProgressManager(this)
        setContent {
            var selectedLevel by remember { mutableIntStateOf(0) }
            var unlockedLevel by remember { mutableIntStateOf(progressManager.getUnlockedLevel("Circular LL")) }
            var levelStars by remember { mutableStateOf<Map<Int, Int>>(progressManager.getAllLevelStars("Circular LL")) }

            if (selectedLevel == 0) {
                GameLevelsScreen(
                    dsName = "Circular LL",
                    unlockedLevel = unlockedLevel,
                    levelStars = levelStars,
                    onLevelSelected = { selectedLevel = it },
                    onBack = { finish() }
                )
            } else {
                CircularLinkedListGamePlayScreen(
                    level = selectedLevel,
                    onComplete = { stars ->
                        progressManager.saveProgress("Circular LL", selectedLevel, stars)
                        unlockedLevel = progressManager.getUnlockedLevel("Circular LL")
                        levelStars = progressManager.getAllLevelStars("Circular LL")
                        selectedLevel = 0
                    },
                    onBack = { selectedLevel = 0 }
                )
            }
        }
    }
}

data class CLLInstruction(val text: String, val op: String, val value: Int? = null, val pos: Int? = null)

@Composable
fun CircularLinkedListGamePlayScreen(level: Int, onComplete: (Int) -> Unit, onBack: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val green4 = Color(0xFF81C784)
    val specialLinkColor = Color(0xFFFF9800) // Orange for Tail -> Head
    
    val list = remember { mutableStateListOf<Int>() }
    val instructions = remember {
        mutableStateListOf<CLLInstruction>().apply {
            when {
                level <= 5 -> { // Easy: 6 steps
                    add(CLLInstruction("Insert ${10 * level} at Head", "ADD_HEAD", 10 * level))
                    add(CLLInstruction("Insert ${20 * level} at Tail", "ADD_TAIL", 20 * level))
                    add(CLLInstruction("Insert ${15 * level} at Position 1", "INSERT_POS", 15 * level, 1))
                    add(CLLInstruction("Delete Front", "DELETE_FRONT"))
                    add(CLLInstruction("Insert ${25 * level} at Head", "ADD_HEAD", 25 * level))
                    add(CLLInstruction("Delete the element at Position 1", "DELETE_POS", null, 1))
                }
                level <= 10 -> { // Medium: 8 steps
                    add(CLLInstruction("Insert ${15 * level % 100} at Head", "ADD_HEAD", 15 * level % 100))
                    add(CLLInstruction("Insert ${25 * level % 100} at Tail", "ADD_TAIL", 25 * level % 100))
                    add(CLLInstruction("Insert ${35 * level % 100} at Position 1", "INSERT_POS", 35 * level % 100, 1))
                    add(CLLInstruction("Delete Back", "DELETE_BACK"))
                    add(CLLInstruction("Insert ${45 * level % 100} at Tail", "ADD_TAIL", 45 * level % 100))
                    add(CLLInstruction("Delete the element at Position 0", "DELETE_POS", null, 0))
                    add(CLLInstruction("Insert ${55 * level % 100} at Head", "ADD_HEAD", 55 * level % 100))
                    add(CLLInstruction("Insert ${65 * level % 100} at Position 2", "INSERT_POS", 65 * level % 100, 2))
                }
                else -> { // Hard: 10 steps
                    add(CLLInstruction("Insert ${10 * level % 100} at Head", "ADD_HEAD", 10 * level % 100))
                    add(CLLInstruction("Insert ${20 * level % 100} at Tail", "ADD_TAIL", 20 * level % 100))
                    add(CLLInstruction("Insert ${30 * level % 100} at Position 1", "INSERT_POS", 30 * level % 100, 1))
                    add(CLLInstruction("Delete Front", "DELETE_FRONT"))
                    add(CLLInstruction("Insert ${40 * level % 100} at Tail", "ADD_TAIL", 40 * level % 100))
                    add(CLLInstruction("Insert ${50 * level % 100} at Head", "ADD_HEAD", 50 * level % 100))
                    add(CLLInstruction("Delete the element at Position 1", "DELETE_POS", null, 1))
                    add(CLLInstruction("Insert ${60 * level % 100} at Position 2", "INSERT_POS", 60 * level % 100, 2))
                    add(CLLInstruction("Delete Back", "DELETE_BACK"))
                    add(CLLInstruction("Insert ${70 * level % 100} at Head", "ADD_HEAD", 70 * level % 100))
                }
            }
        }
    }

    var currentStep by remember { mutableIntStateOf(0) }
    var xp by remember { mutableFloatStateOf(100f) }
    var showErrorIndex by remember { mutableIntStateOf(-1) }
    var showResultDialog by remember { mutableStateOf(false) }
    var finalStars by remember { mutableIntStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var recentlyAddedIndex by remember { mutableIntStateOf(-1) }
    var recentlyDeletedIndex by remember { mutableIntStateOf(-1) }
    var searchedIndex by remember { mutableIntStateOf(-1) }

    var interactionType by remember { mutableStateOf("NONE") } // NONE, INSERT, DELETE, SEARCH
    var pendingValue by remember { mutableStateOf<Int?>(null) }

    val xpPerMistake = if (level <= 5) 16.67f else if (level <= 10) 12.5f else 10f

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

        val isButtonValid = when(op) {
            "INSERT" -> instr.op in listOf("ADD_HEAD", "ADD_TAIL", "INSERT_POS")
            "DELETE" -> instr.op in listOf("DELETE_POS", "DELETE_FRONT", "DELETE_BACK")
            "SEARCH" -> instr.op == "SEARCH"
            else -> false
        }

        if (!isButtonValid) { handleMistake(); return }

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
                    val actualPos = index.coerceIn(0, list.size)
                    list.add(actualPos, pendingValue!!)
                    recentlyAddedIndex = actualPos
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
            scope.launch { delay(600); finalStars = calculateStars(xp); showResultDialog = true }
        }
    }

    val backgroundRes = when {
        level <= 5 -> R.drawable.easy
        level <= 10-> R.drawable.medium
        else -> R.drawable.hard
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = backgroundRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(modifier = Modifier.fillMaxSize().imePadding().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            CLLXPBar(xp = xp)
            Spacer(modifier = Modifier.height(20.dp))

            // Instructions Box with scrolling to prevent compression
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Instructions:", color = Color.White, fontFamily = cantoraFont, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    instructions.forEachIndexed { index, instr ->
                        val isDone = index < currentStep
                        val isCurrent = index == currentStep
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (showErrorIndex == index) Color.Red.copy(alpha = 0.3f)
                                else if (isCurrent) Color.White.copy(alpha = 0.1f)
                                else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(4.dp)
                        ) {
                            Text(
                                "${index + 1}) ${instr.text}",
                                color = if (isDone) Color.Gray else Color.White,
                                fontFamily = cantoraFont,
                                fontSize = 14.sp,
                                textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                        }
                    }
                }
            }

            // Visualizer - Circular Ring Layout
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (list.isEmpty() && interactionType != "INSERT") {
                    Text("List is empty", color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp, fontFamily = cantoraFont)
                } else {
                    val density = LocalDensity.current
                    BoxWithConstraints(modifier = Modifier.size(300.dp)) {
                        val fullWidthPx = with(density) { maxWidth.toPx() }
                        val fullHeightPx = with(density) { maxHeight.toPx() }
                        val centerX = fullWidthPx / 2f
                        val centerY = fullHeightPx / 2f
                        val ringRadius = fullWidthPx * 0.35f
                        
                        val dynamicNodeSize = if (list.size <= 6) 50.dp else 40.dp
                        val nodeRadiusPx = with(density) { (dynamicNodeSize / 2).toPx() }
                        
                        // 1. Draw Curved Arrows
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (list.size == 1) {
                                // Self-loop arrow
                                val nodeAngle = -90f
                                val nodeRad = Math.toRadians(nodeAngle.toDouble())
                                val nodeX = centerX + ringRadius * cos(nodeRad).toFloat()
                                val nodeY = centerY + ringRadius * sin(nodeRad).toFloat()
                                val loopRadius = 22.dp.toPx()
                                val loopCenterX = nodeX
                                val loopCenterY = nodeY - nodeRadiusPx - loopRadius + 8.dp.toPx()
                                
                                drawArc(
                                    color = specialLinkColor,
                                    startAngle = 45f,
                                    sweepAngle = 270f,
                                    useCenter = false,
                                    topLeft = Offset(loopCenterX - loopRadius, loopCenterY - loopRadius),
                                    size = Size(loopRadius * 2, loopRadius * 2),
                                    style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f))
                                )
                                // Arrow head
                                val endAngleRad = Math.toRadians(45.0)
                                val endX = loopCenterX + loopRadius * cos(endAngleRad).toFloat()
                                val endY = loopCenterY + loopRadius * sin(endAngleRad).toFloat()
                                val arrowDirAngle = Math.toRadians(45.0 + 90.0)
                                val arrowLength = 10.dp.toPx()
                                drawLine(color = specialLinkColor, start = Offset(endX, endY), end = Offset(endX - arrowLength * cos(arrowDirAngle - 0.5).toFloat(), endY - arrowLength * sin(arrowDirAngle - 0.5).toFloat()), strokeWidth = 3.dp.toPx())
                                drawLine(color = specialLinkColor, start = Offset(endX, endY), end = Offset(endX - arrowLength * cos(arrowDirAngle + 0.5).toFloat(), endY - arrowLength * sin(arrowDirAngle + 0.5).toFloat()), strokeWidth = 3.dp.toPx())
                            } else if (list.size > 1) {
                                list.forEachIndexed { index, _ ->
                                    val startAngle = (index * 360f / list.size) - 90f
                                    val endAngle = ((index + 1) * 360f / list.size) - 90f
                                    val isTailToHead = index == list.size - 1
                                    val color = if (isTailToHead) specialLinkColor else green4
                                    val strokeStyle = if (isTailToHead) Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)) else Stroke(width = 3.dp.toPx())

                                    val midAngle = (startAngle + endAngle) / 2f
                                    val midRad = Math.toRadians(midAngle.toDouble())
                                    val controlRadius = if (isTailToHead) ringRadius * 1.5f else ringRadius * 1.25f
                                    val controlX = centerX + controlRadius * cos(midRad).toFloat()
                                    val controlY = centerY + controlRadius * sin(midRad).toFloat()

                                    val angleGap = Math.toDegrees(asin(((nodeRadiusPx + 6.dp.toPx()) / ringRadius).toDouble())).toFloat()
                                    val adjStartAngle = startAngle + angleGap
                                    val adjEndAngle = endAngle - angleGap
                                    val adjStartRad = Math.toRadians(adjStartAngle.toDouble())
                                    val adjEndRad = Math.toRadians(adjEndAngle.toDouble())
                                    val adjStartX = centerX + ringRadius * cos(adjStartRad).toFloat()
                                    val adjStartY = centerY + ringRadius * sin(adjStartRad).toFloat()
                                    val adjEndX = centerX + ringRadius * cos(adjEndRad).toFloat()
                                    val adjEndY = centerY + ringRadius * sin(adjEndRad).toFloat()
                                    
                                    val path = Path().apply {
                                        moveTo(adjStartX, adjStartY)
                                        quadraticTo(controlX, controlY, adjEndX, adjEndY)
                                    }
                                    drawPath(path, color = color, style = strokeStyle)
                                    
                                    val angleRad = Math.atan2((adjEndY - controlY).toDouble(), (adjEndX - controlX).toDouble())
                                    val arrowLength = 15.dp.toPx()
                                    drawLine(color = color, start = Offset(adjEndX, adjEndY), end = Offset((adjEndX - arrowLength * cos(angleRad - 0.5)).toFloat(), (adjEndY - arrowLength * sin(angleRad - 0.5)).toFloat()), strokeWidth = 3.dp.toPx())
                                    drawLine(color = color, start = Offset(adjEndX, adjEndY), end = Offset((adjEndX - arrowLength * cos(angleRad + 0.5)).toFloat(), (adjEndY - arrowLength * sin(angleRad + 0.5)).toFloat()), strokeWidth = 3.dp.toPx())
                                }
                            }
                        }

                        // 2. Draw Nodes and Labels
                        list.forEachIndexed { index, value ->
                            val angle = (index * 360f / list.size) - 90f
                            val rad = Math.toRadians(angle.toDouble())
                            val xPx = centerX + ringRadius * cos(rad).toFloat()
                            val yPx = centerY + ringRadius * sin(rad).toFloat()

                            val glowColor = when {
                                index == recentlyDeletedIndex -> Color.Red
                                index == recentlyAddedIndex -> Color.Blue
                                index == searchedIndex -> Color.Yellow
                                else -> null
                            }

                            // Node
                            Box(
                                modifier = Modifier
                                    .size(dynamicNodeSize)
                                    .offset { IntOffset((xPx - nodeRadiusPx).toInt(), (yPx - nodeRadiusPx).toInt()) }
                                    .background(
                                        if (glowColor != null) glowColor.copy(alpha = 0.35f) 
                                        else Color.White.copy(alpha = 0.15f),
                                        CircleShape
                                    )
                                    .border(2.dp, glowColor ?: green4, CircleShape)
                                    .glassmorphic(CircleShape)
                                    .clickable(enabled = interactionType == "DELETE" || interactionType == "SEARCH") { completeAction(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = value.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = cantoraFont)
                            }

                            // Labels
                            val isHead = index == 0
                            val isTail = index == list.size - 1
                            if (isHead || isTail) {
                                val labelDistancePx = with(density) { 50.dp.toPx() }
                                val lxPx = centerX + (ringRadius + labelDistancePx) * cos(rad).toFloat()
                                val lyPx = centerY + (ringRadius + labelDistancePx) * sin(rad).toFloat()
                                
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.wrapContentSize().offset { IntOffset((lxPx - with(density) { 40.dp.toPx() }).toInt(), (lyPx - with(density) { 25.dp.toPx() }).toInt()) }
                                ) {
                                    val labelText = buildAnnotatedString {
                                        if (isHead && isTail) {
                                            withStyle(style = SpanStyle(color = Color.Cyan)) { append("Head") }
                                            withStyle(style = SpanStyle(color = Color.White)) { append("/") }
                                            withStyle(style = SpanStyle(color = Color.Yellow)) { append("Tail") }
                                        } else if (isHead) {
                                            withStyle(style = SpanStyle(color = Color.Cyan)) { append("Head") }
                                        } else {
                                            withStyle(style = SpanStyle(color = Color.Yellow)) { append("Tail") }
                                        }
                                    }

                                    Icon(
                                        imageVector = if (lyPx > yPx) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = if (isHead) Color.Cyan else Color.Yellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(text = labelText, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, fontFamily = cantoraFont)
                                }
                            }
                        }

                        // 3. Draw Ghost Nodes for Insertion
                        if (interactionType == "INSERT") {
                            if (list.isEmpty()) {
                                // One ghost node at the top
                                val angle = -90f
                                val rad = Math.toRadians(angle.toDouble())
                                val xPx = centerX + ringRadius * cos(rad).toFloat()
                                val yPx = centerY + ringRadius * sin(rad).toFloat()
                                Box(
                                    modifier = Modifier
                                        .size(dynamicNodeSize)
                                        .offset { IntOffset((xPx - nodeRadiusPx).toInt(), (yPx - nodeRadiusPx).toInt()) }
                                        .clickable { completeAction(0) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawCircle(color = Color.White.copy(alpha = 0.5f), radius = nodeRadiusPx, style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)))
                                    }
                                    Text("?", color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                // Ghost nodes at midpoints for each potential position
                                for (i in 0..list.size) {
                                    val startAngle = ((i - 1) * 360f / list.size) - 90f
                                    val endAngle = (i * 360f / list.size) - 90f
                                    
                                    // For Circular, index 0 and index list.size are on the same Tail->Head link
                                    val isTailToHeadSlot = (i == 0 || i == list.size)
                                    
                                    val midAngle = if (isTailToHeadSlot) {
                                        // Position them differently on the same link to distinguish
                                        if (i == 0) -110f else -70f
                                    } else {
                                        (startAngle + endAngle) / 2f
                                    }
                                    
                                    val midRad = Math.toRadians(midAngle.toDouble())
                                    val ghostRadius = if (isTailToHeadSlot) ringRadius * 1.4f else ringRadius * 1.2f
                                    val gxPx = centerX + ghostRadius * cos(midRad).toFloat()
                                    val gyPx = centerY + ghostRadius * sin(midRad).toFloat()
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(dynamicNodeSize)
                                            .offset { IntOffset((gxPx - nodeRadiusPx).toInt(), (gyPx - nodeRadiusPx).toInt()) }
                                            .clickable { completeAction(i) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            drawCircle(
                                                color = Color.White.copy(alpha = 0.5f),
                                                radius = nodeRadiusPx,
                                                style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                                            )
                                        }
                                        Text(
                                            if (i == list.size) "T" else if (i == 0) "H" else i.toString(),
                                            color = Color.White.copy(alpha = 0.5f),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = cantoraFont
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (interactionType != "NONE") {
                Text(
                    text = if (interactionType == "INSERT") "Click ghost node to insert $pendingValue" else "Click target node to action",
                    color = Color.Yellow,
                    fontFamily = cantoraFont,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Controls
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // INPUT FIELDS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = userInput,
                            onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 4) userInput = it },
                            placeholder = { Text("Value", color = Color.White.copy(alpha = 0.5f)) },
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                            textStyle = TextStyle(color = Color.White, fontSize = 16.sp, fontFamily = cantoraFont),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedIndicatorColor = green4,
                                unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
                            ),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // ACTION BUTTONS GRID
                    Column {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            CLLActionButton("Insert", green4, Modifier.weight(1f)) { onActionButtonClick("INSERT") }
                            Spacer(modifier = Modifier.width(8.dp))
                            CLLActionButton("Delete", Color(0xFFC54545), Modifier.weight(1f)) { onActionButtonClick("DELETE") }
                            Spacer(modifier = Modifier.width(8.dp))
                            CLLActionButton("Search", Color(0xFFFFA000), Modifier.weight(1f)) { onActionButtonClick("SEARCH") }
                        }
                    }
                }
            }
        }
        if (showResultDialog) CLLResultDialog(stars = finalStars, cantoraFont = cantoraFont) { onComplete(finalStars) }
    }
}

@Composable
private fun CLLActionButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, maxLines = 1)
    }
}

fun handleCLLInput(value: Int?, op: String, pos: Int?, instructions: List<CLLInstruction>, currentStep: Int, onError: () -> Unit, onSuccess: () -> Unit) {
    // Deprecated in favor of internal onActionButtonClick
}

fun undoCLL(instr: CLLInstruction, list: MutableList<Int>) {
    // This function is being replaced to match the new instruction structure
}

fun calculateStars(xp: Float): Int = when { xp >= 99.9f -> 3; xp >= 66.6f -> 2; xp >= 33.3f -> 1; else -> 0 }

@Composable
fun CLLXPBar(xp: Float) {
    val starColor1 = Color(0xFF2196F3)
    val starColor2 = Color(0xFF4CAF50)
    val starColor3 = Color(0xFFFF5252)

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(40.dp)) {
            Box(modifier = Modifier.fillMaxWidth(0.33f).fillMaxHeight(), contentAlignment = Alignment.BottomEnd) { CLLStarIcon(xp, 33.3f, starColor1) }
            Box(modifier = Modifier.fillMaxWidth(0.66f).fillMaxHeight(), contentAlignment = Alignment.BottomEnd) { CLLStarIcon(xp, 66.6f, starColor2) }
            Box(modifier = Modifier.fillMaxWidth(1.0f).fillMaxHeight(), contentAlignment = Alignment.BottomEnd) { CLLStarIcon(xp, 100f, starColor3) }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { xp.coerceAtLeast(0f) / 100f },
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
            color = Color(0xFF81C784),
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun CLLStarIcon(xp: Float, threshold: Float, color: Color) {
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
fun CLLResultDialog(stars: Int, cantoraFont: FontFamily, onDismiss: () -> Unit) {
    val message = when (stars) { 3 -> "EXCELLENT"; 2 -> "WELL DONE"; 1 -> "CHALLENGE DONE"; else -> "CHALLENGE FAILED" }
    AlertDialog(
        onDismissRequest = { },
        confirmButton = { Button(onClick = onDismiss) { Text("OK") } },
        title = { Text(text = message, fontFamily = cantoraFont, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                CLLStarIcon(if (stars >= 1) 33.3f else 0f, 33.3f, Color(0xFF2196F3))
                Spacer(modifier = Modifier.width(8.dp))
                CLLStarIcon(if (stars >= 2) 66.6f else 0f, 66.6f, Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                CLLStarIcon(if (stars >= 3) 100f else 0f, 100f, Color(0xFFFF5252))
            }
        }
    )
}
