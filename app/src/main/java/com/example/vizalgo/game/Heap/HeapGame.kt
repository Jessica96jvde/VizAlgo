package com.example.vizalgo.game.Heap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import com.example.vizalgo.R
import com.example.vizalgo.game.GameLevelsScreen
import com.example.vizalgo.game.ProgressManager
import com.example.vizalgo.utils.glassmorphic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class HeapType { MAX, MIN }

class Heap(private val type: HeapType) {
    val heap = mutableListOf<Int>()

    private fun compare(a: Int, b: Int): Boolean {
        return if (type == HeapType.MAX) a > b else a < b
    }

    fun insertWithoutSift(value: Int) {
        heap.add(value)
    }

    fun needsSiftUp(index: Int): Boolean {
        if (index <= 0) return false
        val parent = (index - 1) / 2
        return compare(heap[index], heap[parent])
    }

    fun swap(i: Int, j: Int) {
        val temp = heap[i]
        heap[i] = heap[j]
        heap[j] = temp
    }

    fun insert(value: Int) {
        heap.add(value)
        siftUp(heap.size - 1)
    }

    private fun siftUp(index: Int) {
        var curr = index
        while (curr > 0) {
            val parent = (curr - 1) / 2
            if (compare(heap[curr], heap[parent])) {
                val temp = heap[curr]
                heap[curr] = heap[parent]
                heap[parent] = temp
                curr = parent
            } else break
        }
    }

    fun needsSiftDown(index: Int): Boolean {
        val left = 2 * index + 1
        val right = 2 * index + 2
        var target = index
        if (left < heap.size && compare(heap[left], heap[target])) target = left
        if (right < heap.size && compare(heap[right], heap[target])) target = right
        return target != index
    }

    fun getSiftDownTarget(index: Int): Int {
        val left = 2 * index + 1
        val right = 2 * index + 2
        var target = index
        if (left < heap.size && compare(heap[left], heap[target])) target = left
        if (right < heap.size && compare(heap[right], heap[target])) target = right
        return target
    }

    fun extractInitial(): Int? {
        if (heap.isEmpty()) return null
        val rootValue = heap[0]
        val last = heap.removeAt(heap.size - 1)
        if (heap.isNotEmpty()) {
            heap[0] = last
        }
        return rootValue
    }

    fun extract(): Int? {
        if (heap.isEmpty()) return null
        val rootValue = heap[0]
        val last = heap.removeAt(heap.size - 1)
        if (heap.isNotEmpty()) {
            heap[0] = last
            siftDown(0)
        }
        return rootValue
    }

    private fun siftDown(index: Int) {
        var curr = index
        while (true) {
            val left = 2 * curr + 1
            val right = 2 * curr + 2
            var target = curr
            if (left < heap.size && compare(heap[left], heap[target])) target = left
            if (right < heap.size && compare(heap[right], heap[target])) target = right
            if (target != curr) {
                val temp = heap[curr]
                heap[curr] = heap[target]
                heap[target] = temp
                curr = target
            } else break
        }
    }
}

class HeapGame : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progressManager = ProgressManager(this)
        setContent {
            var selectedLevel by remember { mutableIntStateOf(0) }
            var unlockedLevel by remember { mutableIntStateOf(progressManager.getUnlockedLevel("Heap")) }
            var levelStars by remember { mutableStateOf<Map<Int, Int>>(progressManager.getAllLevelStars("Heap")) }

            if (selectedLevel == 0) {
                GameLevelsScreen(
                    dsName = "Heap",
                    unlockedLevel = unlockedLevel,
                    levelStars = levelStars,
                    onLevelSelected = { selectedLevel = it },
                    onBack = { finish() }
                )
            } else {
                HeapGamePlayScreen(
                    level = selectedLevel,
                    onComplete = { stars ->
                        progressManager.saveProgress("Heap", selectedLevel, stars)
                        unlockedLevel = progressManager.getUnlockedLevel("Heap")
                        levelStars = progressManager.getAllLevelStars("Heap")
                        selectedLevel = 0
                    },
                    onBack = { selectedLevel = 0 }
                )
            }
        }
    }
}

data class HeapInstruction(val op: String, val value: Int, val text: String)

@Composable
fun HeapGamePlayScreen(level: Int, onComplete: (Int) -> Unit, onBack: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val green4 = colorResource(id = R.color.green4)

    val backgroundRes = when {
        level <= 5 -> R.drawable.easy
        level <= 10 -> R.drawable.medium
        else -> R.drawable.hard
    }
    
    val heapType = remember { if (level % 2 == 0) HeapType.MAX else HeapType.MIN }
    val typeStr = if (heapType == HeapType.MAX) "Max Heap" else "Min Heap"
    val heapObj = remember { Heap(heapType) }
    val heapState = remember { mutableStateListOf<Int>() }
    
    val instructions = remember {
        mutableStateListOf<HeapInstruction>().apply {
            when {
                level <= 5 -> { // Easy: 6 steps
                    add(HeapInstruction("INSERT", 50, "Insert 50 ($typeStr)"))
                    add(HeapInstruction("INSERT", 30, "Insert 30 ($typeStr)"))
                    add(HeapInstruction("INSERT", 70, "Insert 70 ($typeStr)"))
                    add(HeapInstruction("INSERT", 20, "Insert 20 ($typeStr)"))
                    add(HeapInstruction("EXTRACT", if(heapType==HeapType.MIN) 20 else 70, "Extract ${if(heapType==HeapType.MAX) "Max" else "Min"}"))
                    add(HeapInstruction("INSERT", 40, "Insert 40 ($typeStr)"))
                }
                level <= 10 -> { // Medium: 8 steps
                    add(HeapInstruction("INSERT", 80, "Insert 80 ($typeStr)"))
                    add(HeapInstruction("INSERT", 40, "Insert 40 ($typeStr)"))
                    add(HeapInstruction("INSERT", 90, "Insert 90 ($typeStr)"))
                    add(HeapInstruction("EXTRACT", if(heapType==HeapType.MIN) 40 else 90, "Extract ${if(heapType==HeapType.MAX) "Max" else "Min"}"))
                    add(HeapInstruction("INSERT", 10, "Insert 10 ($typeStr)"))
                    add(HeapInstruction("INSERT", 50, "Insert 50 ($typeStr)"))
                    add(HeapInstruction("EXTRACT", if(heapType==HeapType.MIN) 10 else 80, "Extract ${if(heapType==HeapType.MAX) "Max" else "Min"}"))
                    add(HeapInstruction("INSERT", 30, "Insert 30 ($typeStr)"))
                }
                else -> { // Hard: 10 steps
                    add(HeapInstruction("INSERT", 100, "Insert 100 ($typeStr)"))
                    add(HeapInstruction("INSERT", 50, "Insert 50 ($typeStr)"))
                    add(HeapInstruction("INSERT", 150, "Insert 150 ($typeStr)"))
                    add(HeapInstruction("EXTRACT", if(heapType==HeapType.MIN) 50 else 150, "Extract ${if(heapType==HeapType.MAX) "Max" else "Min"}"))
                    add(HeapInstruction("INSERT", 25, "Insert 25 ($typeStr)"))
                    add(HeapInstruction("INSERT", 75, "Insert 75 ($typeStr)"))
                    add(HeapInstruction("EXTRACT", if(heapType==HeapType.MIN) 25 else 100, "Extract ${if(heapType==HeapType.MAX) "Max" else "Min"}"))
                    add(HeapInstruction("INSERT", 10, "Insert 10 ($typeStr)"))
                    add(HeapInstruction("EXTRACT", if(heapType==HeapType.MIN) 10 else 75, "Extract ${if(heapType==HeapType.MAX) "Max" else "Min"}"))
                    add(HeapInstruction("INSERT", 60, "Insert 60 ($typeStr)"))
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
    var recentlyAddedValue by remember { mutableIntStateOf(-1) }
    var recentlyDeletedValue by remember { mutableIntStateOf(-1) }
    var recentlyError by remember { mutableStateOf(false) }
    
    var isSelectingPosition by remember { mutableStateOf(false) }
    var isExtracting by remember { mutableStateOf(false) }
    var isSiftingUp by remember { mutableStateOf(false) }
    var isSiftingDown by remember { mutableStateOf(false) }
    var siftingIdx by remember { mutableIntStateOf(-1) }
    var pendingValue by remember { mutableIntStateOf(-1) }

    val xpPerMistake = when {
        instructions.size <= 6 -> 16.67f
        instructions.size <= 8 -> 12.5f
        else -> 10f
    }

    fun handleMistake() {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        scope.launch {
            xp -= xpPerMistake
            recentlyError = true
            showErrorIndex = currentStep
            
            delay(800)
            recentlyError = false
            showErrorIndex = -1
        }
        isSelectingPosition = false
        isExtracting = false
        isSiftingUp = false
        isSiftingDown = false
        userInput = ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = backgroundRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(modifier = Modifier.fillMaxSize().padding(20.dp).imePadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            HeapXPBar(xp = xp)
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .weight(0.35f)
                    .glassmorphic(RoundedCornerShape(16.dp))
                    .animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text("Instructions:", color = Color.White, fontFamily = cantoraFont, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    instructions.forEachIndexed { index, instr ->
                        val isDone = index < currentStep
                        val isCurrent = index == currentStep
                        Row(modifier = Modifier.fillMaxWidth().background(if (showErrorIndex == index) Color.Red.copy(alpha = 0.3f) else if (isCurrent) Color.White.copy(alpha = 0.1f) else Color.Transparent, RoundedCornerShape(4.dp)).padding(4.dp)) {
                            Text("${index + 1}) ${instr.text}", color = if (isDone) Color.Gray else Color.White, fontFamily = cantoraFont, fontSize = 18.sp, textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null)
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(0.65f).fillMaxWidth().animateContentSize()) {
                HeapVisualizer(
                    heapState,
                    recentlyAddedValue,
                    recentlyDeletedValue,
                    isSelectingPosition,
                    isExtracting,
                    isSiftingUp || isSiftingDown,
                    siftingIdx,
                    onGhostClick = { clickedIdx ->
                        if (isSelectingPosition) {
                            if (clickedIdx == heapState.size) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                heapObj.insertWithoutSift(pendingValue)
                                heapState.clear()
                                heapState.addAll(heapObj.heap)
                                recentlyAddedValue = pendingValue
                                isSelectingPosition = false
                                
                                if (heapObj.needsSiftUp(heapState.size - 1)) {
                                    isSiftingUp = true
                                    siftingIdx = heapState.size - 1
                                } else {
                                    currentStep++
                                    userInput = ""
                                    scope.launch { delay(500); recentlyAddedValue = -1 }
                                    if (currentStep >= instructions.size) {
                                        finalStars = calculateHeapStars(xp)
                                        showResultDialog = true
                                    }
                                }
                            } else {
                                handleMistake()
                            }
                        }
                    },
                    onNodeClick = { clickedIdx ->
                        if (isExtracting) {
                            if (clickedIdx == 0) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                val valToDelete = instructions[currentStep].value
                                recentlyDeletedValue = valToDelete
                                
                                heapObj.extractInitial()
                                heapState.clear()
                                heapState.addAll(heapObj.heap)
                                isExtracting = false
                                
                                if (heapState.isNotEmpty() && heapObj.needsSiftDown(0)) {
                                    isSiftingDown = true
                                    siftingIdx = 0
                                } else {
                                    currentStep++
                                    userInput = ""
                                    scope.launch { delay(500); recentlyDeletedValue = -1 }
                                    if (currentStep >= instructions.size) { 
                                        finalStars = calculateHeapStars(xp)
                                        showResultDialog = true 
                                    }
                                }
                            } else {
                                handleMistake()
                            }
                        } else if (isSiftingUp && clickedIdx == siftingIdx) {
                            val parentIdx = (siftingIdx - 1) / 2
                            if (parentIdx >= 0) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                heapObj.swap(siftingIdx, parentIdx)
                                heapState.clear()
                                heapState.addAll(heapObj.heap)
                                siftingIdx = parentIdx
                                
                                if (!heapObj.needsSiftUp(siftingIdx)) {
                                    isSiftingUp = false
                                    siftingIdx = -1
                                    currentStep++
                                    userInput = ""
                                    scope.launch { delay(500); recentlyAddedValue = -1 }
                                    if (currentStep >= instructions.size) {
                                        finalStars = calculateHeapStars(xp)
                                        showResultDialog = true
                                    }
                                }
                            }
                        } else if (isSiftingDown && clickedIdx == siftingIdx) {
                            val targetIdx = heapObj.getSiftDownTarget(siftingIdx)
                            if (targetIdx != siftingIdx) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                heapObj.swap(siftingIdx, targetIdx)
                                heapState.clear()
                                heapState.addAll(heapObj.heap)
                                siftingIdx = targetIdx
                                
                                if (!heapObj.needsSiftDown(siftingIdx)) {
                                    isSiftingDown = false
                                    siftingIdx = -1
                                    currentStep++
                                    userInput = ""
                                    scope.launch { delay(500); recentlyDeletedValue = -1 }
                                    if (currentStep >= instructions.size) {
                                        finalStars = calculateHeapStars(xp)
                                        showResultDialog = true
                                    }
                                }
                            }
                        }
                    }
                )
                
                if (isSelectingPosition || isExtracting || isSiftingUp || isSiftingDown) {
                    Text(
                        text = when {
                            isSelectingPosition -> "Select Position for $pendingValue"
                            isExtracting -> "Select Node to Extract"
                            isSiftingUp -> "Click Node to Swap with Parent"
                            isSiftingDown -> "Click Node to Swap with Child"
                            else -> ""
                        },
                        color = Color.Yellow,
                        fontFamily = cantoraFont,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                    )
                }
            }

            // Controls
            Card(modifier = Modifier.fillMaxWidth().glassmorphic(RoundedCornerShape(24.dp)), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    TextField(
                        value = userInput,
                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 3) userInput = it },
                        placeholder = { Text("Value", color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        textStyle = TextStyle(color = Color.White, fontSize = 18.sp, fontFamily = cantoraFont),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White.copy(alpha = 0.1f), unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color.White),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = {
                            if (userInput.isNotEmpty()) {
                                val valInt = userInput.toInt()
                                if (currentStep < instructions.size && instructions[currentStep].op == "INSERT" && instructions[currentStep].value == valInt) {
                                    pendingValue = valInt
                                    isSelectingPosition = true
                                } else {
                                    handleMistake()
                                }
                            }
                        }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = green4), shape = RoundedCornerShape(12.dp)) { Text("INSERT", fontFamily = cantoraFont) }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = {
                            if (currentStep < instructions.size && instructions[currentStep].op == "EXTRACT") {
                                isExtracting = true
                            } else {
                                handleMistake()
                            }
                        }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), shape = RoundedCornerShape(12.dp)) { Text(if (heapType == HeapType.MAX) "EXTRACT MAX" else "EXTRACT MIN", fontFamily = cantoraFont) }
                    }
                }
            }
        }
        if (showResultDialog) HeapResultDialog(stars = finalStars, cantoraFont = cantoraFont) { onComplete(finalStars) }
    }
}


@Composable
fun HeapVisualizer(
    heap: List<Int>,
    addedVal: Int,
    deletedVal: Int,
    isSelectingPosition: Boolean,
    isExtracting: Boolean,
    isSifting: Boolean,
    siftingIdx: Int,
    onGhostClick: (Int) -> Unit,
    onNodeClick: (Int) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawHeapConnections(heap, 0, width / 2, 80f, width / 4f, 130f, isSelectingPosition)
        }
        DrawHeapNodes(
            heap, 0, width / 2, 80f, width / 4f, 130f, addedVal, deletedVal, 0,
            isSelectingPosition, isExtracting, isSifting, siftingIdx, onGhostClick, onNodeClick
        )
    }
}

private fun DrawScope.drawHeapConnections(heap: List<Int>, idx: Int, x: Float, y: Float, xOff: Float, yOff: Float, isSelectingPosition: Boolean) {
    val left = 2 * idx + 1
    val right = 2 * idx + 2
    if (left < heap.size) { 
        drawLine(Color.White.copy(alpha = 0.3f), Offset(x, y), Offset(x - xOff, y + yOff), 3f)
        drawHeapConnections(heap, left, x - xOff, y + yOff, xOff / 1.8f, yOff, isSelectingPosition) 
    } else if (isSelectingPosition && idx < heap.size) {
        drawLine(Color.White.copy(alpha = 0.15f), Offset(x, y), Offset(x - xOff, y + yOff), 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    }
    if (right < heap.size) { 
        drawLine(Color.White.copy(alpha = 0.3f), Offset(x, y), Offset(x + xOff, y + yOff), 3f)
        drawHeapConnections(heap, right, x + xOff, y + yOff, xOff / 1.8f, yOff, isSelectingPosition) 
    } else if (isSelectingPosition && idx < heap.size) {
        drawLine(Color.White.copy(alpha = 0.15f), Offset(x, y), Offset(x + xOff, y + yOff), 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    }
}

@Composable
fun DrawHeapNodes(
    heap: List<Int>,
    idx: Int,
    x: Float,
    y: Float,
    xOff: Float,
    yOff: Float,
    addedVal: Int,
    deletedVal: Int,
    level: Int,
    isSelectingPosition: Boolean,
    isExtracting: Boolean,
    isSifting: Boolean,
    siftingIdx: Int,
    onGhostClick: (Int) -> Unit,
    onNodeClick: (Int) -> Unit
) {
    if (level > 4) return
    
    val density = LocalDensity.current
    val green4 = colorResource(id = R.color.green4)
    val nodeSize = (44 - (level * 4)).coerceAtLeast(32).dp
    val fontSize = (14 - level).coerceAtLeast(10).sp
    val halfSize = with(density) { (nodeSize / 2).toPx() }
    
    if (idx < heap.size) {
        val isRecentlyAdded = addedVal != -1 && heap[idx] == addedVal
        val isRecentlyDeleted = deletedVal != -1 && idx == 0
        val isCurrentSifting = isSifting && idx == siftingIdx

        val glowColor = when {
            isCurrentSifting -> Color.Yellow.copy(alpha = 0.5f)
            isRecentlyAdded -> Color.Blue.copy(alpha = 0.5f)
            isRecentlyDeleted -> Color.Red.copy(alpha = 0.5f)
            isExtracting && idx == 0 -> Color.Red.copy(alpha = 0.3f)
            else -> Color.Transparent
        }

        Box(
            modifier = Modifier
                .offset(x = with(density) { (x - halfSize).toDp() }, y = with(density) { (y - halfSize).toDp() })
                .size(nodeSize)
                .background(glowColor, CircleShape)
                .border(2.dp, if (glowColor != Color.Transparent) glowColor else Color.White.copy(alpha = 0.8f), CircleShape)
                .background(green4.copy(alpha = 0.9f), CircleShape)
                .clickable { onNodeClick(idx) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = heap[idx].toString(), color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Bold)
        }
        DrawHeapNodes(heap, 2 * idx + 1, x - xOff, y + yOff, xOff / 1.8f, yOff, addedVal, deletedVal, level + 1, isSelectingPosition, isExtracting, isSifting, siftingIdx, onGhostClick, onNodeClick)
        DrawHeapNodes(heap, 2 * idx + 2, x + xOff, y + yOff, xOff / 1.8f, yOff, addedVal, deletedVal, level + 1, isSelectingPosition, isExtracting, isSifting, siftingIdx, onGhostClick, onNodeClick)
    } else if (isSelectingPosition) {
        val parentIdx = if (idx > 0) (idx - 1) / 2 else -1
        if (idx == 0 || (parentIdx != -1 && parentIdx < heap.size)) {
            GhostNode(x, y, nodeSize, fontSize) { onGhostClick(idx) }
        }
    }
}

@Composable
fun GhostNode(x: Float, y: Float, size: androidx.compose.ui.unit.Dp, fontSize: androidx.compose.ui.unit.TextUnit, onClick: () -> Unit) {
    val density = LocalDensity.current
    val halfSize = with(density) { (size / 2).toPx() }
    Box(
        modifier = Modifier
            .offset(x = with(density) { (x - halfSize).toDp() }, y = with(density) { (y - halfSize).toDp() })
            .size(size)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                style = Stroke(
                    width = 2.dp.toPx(), 
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            )
        }
        Text("?", color = Color.White.copy(alpha = 0.5f), fontSize = fontSize)
    }
}

fun calculateHeapStars(xp: Float): Int = when { xp >= 99.9f -> 3; xp >= 66.6f -> 2; xp >= 33.3f -> 1; else -> 0 }

@Composable
fun HeapXPBar(xp: Float) {
    val green4 = colorResource(id = R.color.green4)
    val starColor1 = Color(0xFF2196F3) // Blue
    val starColor2 = green4
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
                HeapStarIcon(xp, 33.3f, starColor1)
            }
            // Star 2 at 66.6%
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.666f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                HeapStarIcon(xp, 66.6f, starColor2)
            }
            // Star 3 at 100%
            Box(
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                HeapStarIcon(xp, 100f, starColor3)
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
fun HeapStarIcon(xp: Float, threshold: Float, color: Color) {
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
fun HeapResultDialog(stars: Int, cantoraFont: FontFamily, onDismiss: () -> Unit) {
    val message = when (stars) {
        3 -> "EXCELLENT"
        2 -> "WELL DONE"
        1 -> "CHALLENGE DONE"
        else -> "CHALLENGE FAILED"
    }
    AlertDialog(
        onDismissRequest = { },
        confirmButton = { Button(onClick = onDismiss) { Text("OK") } },
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
                val green4 = colorResource(id = R.color.green4)
                // Star 1 (33.3%) - Blue
                HeapStarIcon(if (stars >= 1) 33.3f else 0f, 33.3f, Color(0xFF2196F3))
                Spacer(modifier = Modifier.width(8.dp))
                // Star 2 (66.6%) - green4
                HeapStarIcon(if (stars >= 2) 66.6f else 0f, 66.6f, green4)
                Spacer(modifier = Modifier.width(8.dp))
                // Star 3 (100%) - Red
                HeapStarIcon(if (stars >= 3) 100f else 0f, 100f, Color(0xFFFF5252))
            }
        }
    )
}
