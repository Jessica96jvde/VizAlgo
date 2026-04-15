package com.example.vizalgo.game.BPlusTree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.vizalgo.R
import com.example.vizalgo.game.GameLevelsScreen
import com.example.vizalgo.game.ProgressManager
import com.example.vizalgo.utils.glassmorphic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

class BPlusNode(val m: Int, var isLeaf: Boolean) {
    var keys = mutableListOf<Int>()
    var children = mutableListOf<BPlusNode>()
    var next: BPlusNode? = null

    fun insert(k: Int): Pair<Int, BPlusNode>? {
        if (isLeaf) {
            val idx = keys.binarySearch(k).let { if (it < 0) -it - 1 else it }
            if (idx < keys.size && keys[idx] == k) return null
            keys.add(idx, k)
            if (keys.size == m) return splitLeaf()
        } else {
            var i = 0
            while (i < keys.size && k >= keys[i]) i++
            val split = children[i].insert(k)
            if (split != null) {
                val (midKey, newNode) = split
                val insertIdx = keys.binarySearch(midKey).let { if (it < 0) -it - 1 else it }
                keys.add(insertIdx, midKey)
                children.add(insertIdx + 1, newNode)
                if (keys.size == m) return splitInternal()
            }
        }
        return null
    }

    private fun splitLeaf(): Pair<Int, BPlusNode> {
        val mid = m / 2
        val newNode = BPlusNode(m, true)
        newNode.keys.addAll(keys.subList(mid, keys.size))
        keys.subList(mid, keys.size).clear()
        newNode.next = this.next
        this.next = newNode
        return Pair(newNode.keys[0], newNode)
    }

    private fun splitInternal(): Pair<Int, BPlusNode> {
        val mid = m / 2
        val midKey = keys[mid]
        val newNode = BPlusNode(m, false)
        newNode.keys.addAll(keys.subList(mid + 1, keys.size))
        newNode.children.addAll(children.subList(mid + 1, children.size))
        keys.subList(mid, keys.size).clear()
        children.subList(mid + 1, children.size).clear()
        return Pair(midKey, newNode)
    }

    fun remove(k: Int) {
        if (isLeaf) {
            val idx = keys.indexOf(k)
            if (idx != -1) {
                keys.removeAt(idx)
            }
        } else {
            var i = 0
            while (i < keys.size && k >= keys[i]) i++
            children[i].remove(k)

            if (i < keys.size && children[i+1].isLeaf && children[i+1].keys.isNotEmpty()) {
                keys[i] = children[i+1].keys[0]
            }
        }
    }
}

class BPlusTree(val m: Int) {
    var root: BPlusNode? = null

    fun insert(k: Int) {
        if (root == null) {
            root = BPlusNode(m, true)
            root!!.keys.add(k)
        } else {
            val split = root!!.insert(k)
            if (split != null) {
                val newRoot = BPlusNode(m, false)
                newRoot.keys.add(split.first)
                newRoot.children.add(root!!)
                newRoot.children.add(split.second)
                root = newRoot
            }
        }
    }

    fun remove(k: Int) {
        root?.remove(k)
        if (root?.keys?.isEmpty() == true) {
            root = if (root!!.isLeaf) null else root!!.children[0]
        }
    }
}

class BPlusTreeGame : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progressManager = ProgressManager(this)
        setContent {
            var selectedLevel by remember { mutableIntStateOf(0) }
            var unlockedLevel by remember { mutableIntStateOf(progressManager.getUnlockedLevel("B+ Tree")) }
            var levelStars by remember { mutableStateOf<Map<Int, Int>>(progressManager.getAllLevelStars("B+ Tree")) }

            if (selectedLevel == 0) {
                GameLevelsScreen(
                    dsName = "B+ Tree",
                    unlockedLevel = unlockedLevel,
                    levelStars = levelStars,
                    onLevelSelected = { selectedLevel = it },
                    onBack = { finish() }
                )
            } else {
                BPlusTreeGamePlayScreen(
                    level = selectedLevel,
                    onComplete = { stars ->
                        progressManager.saveProgress("B+ Tree", selectedLevel, stars)
                        unlockedLevel = progressManager.getUnlockedLevel("B+ Tree")
                        levelStars = progressManager.getAllLevelStars("B+ Tree")
                        selectedLevel = 0
                    },
                    onBack = { selectedLevel = 0 }
                )
            }
        }
    }
}

data class BPlusInstruction(val op: String, val value: Int)

@Composable
fun BPlusTreeGamePlayScreen(level: Int, onComplete: (Int) -> Unit, onBack: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val green4 = colorResource(id = R.color.green4)

    var bplusState by remember { mutableStateOf<BPlusNode?>(null) }

    val instructions = remember {
        mutableStateListOf<BPlusInstruction>().apply {
            when {
                level <= 5 -> { // Easy: 6 steps
                    add(BPlusInstruction("INSERT", 10))
                    add(BPlusInstruction("INSERT", 20))
                    add(BPlusInstruction("INSERT", 5))
                    add(BPlusInstruction("INSERT", 15))
                    add(BPlusInstruction("INSERT", 25))
                    add(BPlusInstruction("DELETE", 15))
                }
                level <= 10 -> { // Medium: 8 steps
                    add(BPlusInstruction("INSERT", 40))
                    add(BPlusInstruction("INSERT", 10))
                    add(BPlusInstruction("INSERT", 50))
                    add(BPlusInstruction("INSERT", 20))
                    add(BPlusInstruction("INSERT", 60))
                    add(BPlusInstruction("DELETE", 40))
                    add(BPlusInstruction("INSERT", 30))
                    add(BPlusInstruction("DELETE", 10))
                }
                else -> { // Hard: 10 steps
                    add(BPlusInstruction("INSERT", 100))
                    add(BPlusInstruction("INSERT", 50))
                    add(BPlusInstruction("INSERT", 150))
                    add(BPlusInstruction("INSERT", 25))
                    add(BPlusInstruction("DELETE", 100))
                    add(BPlusInstruction("INSERT", 75))
                    add(BPlusInstruction("DELETE", 50))
                    add(BPlusInstruction("INSERT", 125))
                    add(BPlusInstruction("INSERT", 175))
                    add(BPlusInstruction("INSERT", 60))
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

    val xpPerMistake = when {
        instructions.size <= 6 -> 16.67f
        instructions.size <= 8 -> 12.5f
        else -> 10f
    }

    val backgroundRes = when {
        level <= 5 -> R.drawable.easy
        level <= 10 -> R.drawable.medium
        else -> R.drawable.hard
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = backgroundRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            BPlusXPBar(xp = xp)
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).glassmorphic(RoundedCornerShape(16.dp)).animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Instructions:", color = Color.White, fontFamily = cantoraFont, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    instructions.forEachIndexed { index, instr ->
                        val isDone = index < currentStep
                        val isCurrent = index == currentStep
                        Row(modifier = Modifier.fillMaxWidth().background(if (showErrorIndex == index) Color.Red.copy(alpha = 0.3f) else if (isCurrent) Color.White.copy(alpha = 0.1f) else Color.Transparent, RoundedCornerShape(4.dp)).padding(4.dp)) {
                            Text("${index + 1}) ${instr.op} ${instr.value}", color = if (isDone) Color.Gray else Color.White, fontFamily = cantoraFont, fontSize = 18.sp, textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null)
                        }
                    }
                }
            }

            // Visual Area
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                bplusState?.let {
                    BPlusTreeVisualizer(it, recentlyAddedValue, recentlyDeletedValue, cantoraFont)
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
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White.copy(alpha = 0.1f), unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color.White, focusedIndicatorColor = green4),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            if (userInput.isNotEmpty()) {
                                val valInt = userInput.toInt()
                                if (currentStep < instructions.size && instructions[currentStep].op == "INSERT" && instructions[currentStep].value == valInt) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    val newTree = BPlusTree(3)
                                    for (i in 0 until currentStep) {
                                        if (instructions[i].op == "INSERT") newTree.insert(instructions[i].value)
                                        else newTree.remove(instructions[i].value)
                                    }
                                    newTree.insert(valInt)
                                    bplusState = newTree.root
                                    recentlyAddedValue = valInt
                                    currentStep++
                                    userInput = ""
                                    scope.launch { delay(500); recentlyAddedValue = -1 }
                                    if (currentStep >= instructions.size) { finalStars = calculateBPlusStars(xp); showResultDialog = true }
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    scope.launch {
                                        xp -= xpPerMistake; recentlyError = true; showErrorIndex = currentStep
                                        val prev = currentStep; currentStep = maxOf(0, currentStep - 1)
                                        val newTree = BPlusTree(3)
                                        for (i in 0 until currentStep) {
                                            if (instructions[i].op == "INSERT") newTree.insert(instructions[i].value)
                                            else newTree.remove(instructions[i].value)
                                        }
                                        bplusState = newTree.root
                                        delay(800); recentlyError = false; showErrorIndex = -1
                                    }
                                }
                            }
                        }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = green4), shape = RoundedCornerShape(12.dp)) { Text("INSERT") }

                        Button(onClick = {
                            if (userInput.isNotEmpty()) {
                                val valInt = userInput.toInt()
                                if (currentStep < instructions.size && instructions[currentStep].op == "DELETE" && instructions[currentStep].value == valInt) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    recentlyDeletedValue = valInt
                                    scope.launch {
                                        delay(500)
                                        val newTree = BPlusTree(3)
                                        for (i in 0 until currentStep) {
                                            if (instructions[i].op == "INSERT") newTree.insert(instructions[i].value)
                                            else newTree.remove(instructions[i].value)
                                        }
                                        newTree.remove(valInt)
                                        bplusState = newTree.root
                                        recentlyDeletedValue = -1
                                        currentStep++
                                        userInput = ""
                                        if (currentStep >= instructions.size) { finalStars = calculateBPlusStars(xp); showResultDialog = true }
                                    }
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    scope.launch {
                                        xp -= xpPerMistake; recentlyError = true; showErrorIndex = currentStep
                                        val prev = currentStep; currentStep = maxOf(0, currentStep - 1)
                                        val newTree = BPlusTree(3)
                                        for (i in 0 until currentStep) {
                                            if (instructions[i].op == "INSERT") newTree.insert(instructions[i].value)
                                            else newTree.remove(instructions[i].value)
                                        }
                                        bplusState = newTree.root
                                        delay(800); recentlyError = false; showErrorIndex = -1
                                    }
                                }
                            }
                        }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), shape = RoundedCornerShape(12.dp)) { Text("DELETE") }
                    }
                }
            }
        }
        if (showResultDialog) BPlusResultDialog(stars = finalStars, cantoraFont = cantoraFont) { onComplete(finalStars) }
    }
}

@Composable
fun BPlusTreeVisualizer(root: BPlusNode, addedVal: Int, deletedVal: Int, cantoraFont: FontFamily) {
    var userScale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(24.dp))
        .background(Color.White.copy(alpha = 0.05f))
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                userScale = (userScale * zoom).coerceIn(0.5f, 5f)
                offset += pan
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(onDoubleTap = {
                userScale = 1f
                offset = Offset.Zero
            })
        }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            
            // Calculate height of the tree for scaling
            fun getTreeHeight(node: BPlusNode?): Int {
                if (node == null) return 0
                if (node.isLeaf) return 1
                return 1 + getTreeHeight(node.children.firstOrNull())
            }
            val treeHeight = getTreeHeight(root)
            val m = 3.0 // Order is 3 in game
            val h = treeHeight.toDouble()
            val reqW = (m.pow(h - 1.0) * 100.0) + 200.0
            val reqH = (h * 170.0) + 100.0
            val autoScale = minOf((width / reqW).coerceIn(0.2, 1.0), (height / reqH).coerceIn(0.3, 1.0)).toFloat()
            val combinedScale = autoScale * userScale

            Box(modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = combinedScale,
                    scaleY = combinedScale,
                    translationX = offset.x,
                    translationY = offset.y + (1f - autoScale) * -100f * userScale,
                    transformOrigin = TransformOrigin(0.5f, 0.2f)
                )
            ) {
                val initialX = width / 2
                val initialY = 120f
                val reductionFactor = 2.4f
                val yOffset = 170f
                val initialXOffset = (width * 0.35f * (1f / autoScale).coerceIn(1f, 3f)).coerceAtMost(width * 1.5f)

                val leafPositions = remember { mutableStateListOf<Offset>() }
                LaunchedEffect(root) { leafPositions.clear() }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawBPlusConnections(this, root, initialX, initialY, initialXOffset, yOffset, reductionFactor)
                    
                    if (leafPositions.size > 1) {
                        for (i in 0 until leafPositions.size - 1) {
                            val start = leafPositions[i]
                            val end = leafPositions[i+1]
                            drawPath(
                                path = Path().apply {
                                    moveTo(start.x + 30f, start.y + 15f)
                                    quadraticTo((start.x + end.x)/2, start.y + 60f, end.x - 30f, end.y + 15f)
                                },
                                color = Color.Yellow.copy(alpha = 0.5f),
                                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                            )
                        }
                    }
                }
                DrawBPlusNodes(root, initialX, initialY, initialXOffset, yOffset, addedVal, deletedVal, 0, reductionFactor, cantoraFont) { pos ->
                    leafPositions.add(pos)
                }
            }
        }
    }
}

private fun drawBPlusConnections(scope: DrawScope, node: BPlusNode, x: Float, y: Float, xOffset: Float, yOffset: Float, reductionFactor: Float) {
    if (!node.isLeaf && node.children.isNotEmpty()) {
        val startX = x - (xOffset * (node.children.size - 1) / 2)
        node.children.forEachIndexed { i, child ->
            val childX = startX + i * xOffset
            scope.drawLine(color = Color.White.copy(alpha = 0.25f), start = Offset(x, y), end = Offset(childX, y + yOffset), strokeWidth = 2.2f)
            drawBPlusConnections(scope, child, childX, y + yOffset, xOffset / reductionFactor, yOffset, reductionFactor)
        }
    }
}

@Composable
fun DrawBPlusNodes(node: BPlusNode, x: Float, y: Float, xOffset: Float, yOffset: Float, addedVal: Int, deletedVal: Int, level: Int, reductionFactor: Float, cantoraFont: FontFamily, onLeafPosition: (Offset) -> Unit) {
    val density = LocalDensity.current
    val fontSize = (14.0 - (level * 1.2)).coerceAtLeast(10.0).sp
    val keyPadding = (6 - level).coerceAtLeast(3).dp
    
    if (node.isLeaf) {
        SideEffect { onLeafPosition(Offset(x, y)) }
    }

    Box(modifier = Modifier.offset(x = with(density) { x.toDp() }, y = with(density) { y.toDp() })) {
        androidx.compose.ui.layout.Layout(content = {
            Row(modifier = Modifier.background(if (node.isLeaf) Color(0xFF2E7D32).copy(alpha = 0.95f) else Color(0xFF1565C0).copy(alpha = 0.95f), RoundedCornerShape(4.dp)).border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(4.dp)), verticalAlignment = Alignment.CenterVertically) {
                node.keys.forEachIndexed { idx, key ->
                    Box(modifier = Modifier.padding(keyPadding).background(when (key) { addedVal -> Color.Blue.copy(alpha = 0.4f); deletedVal -> Color.Red.copy(alpha = 0.4f); else -> Color.Transparent }, RoundedCornerShape(2.dp)).padding(horizontal = 6.dp, vertical = 3.dp), contentAlignment = Alignment.Center) {
                        Text(text = key.toString(), color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Bold, fontFamily = cantoraFont)
                    }
                    if (idx < node.keys.size - 1) Box(modifier = Modifier.height(18.dp).width(1.dp).background(Color.White.copy(alpha = 0.3f)))
                }
            }
        }) { measurables, constraints -> val p = measurables[0].measure(constraints); layout(p.width, p.height) { p.placeRelative(-p.width / 2, -p.height / 2) } }
    }
    if (!node.isLeaf && node.children.isNotEmpty()) {
        val startX = x - (xOffset * (node.children.size - 1) / 2)
        node.children.forEachIndexed { i, child ->
            val childX = startX + i * xOffset
            DrawBPlusNodes(child, childX, y + yOffset, xOffset / reductionFactor, yOffset, addedVal, deletedVal, level + 1, reductionFactor, cantoraFont, onLeafPosition)
        }
    }
}

fun calculateBPlusStars(xp: Float): Int = when { xp >= 99.9f -> 3; xp >= 66.6f -> 2; xp >= 33.3f -> 1; else -> 0 }

@Composable
fun BPlusXPBar(xp: Float) {
    val green4 = colorResource(id = R.color.green4)

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
                BPlusStarIcon(xp, 33.3f, green4)
            }
            // Star 2 at 66.6%
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.666f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                BPlusStarIcon(xp, 66.6f, green4)
            }
            // Star 3 at 100%
            Box(
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                BPlusStarIcon(xp, 100f, green4)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (xp / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = green4,
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun BPlusStarIcon(xp: Float, threshold: Float, color: Color) {
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
fun BPlusResultDialog(stars: Int, cantoraFont: FontFamily, onDismiss: () -> Unit) {
    val green4 = colorResource(id = R.color.green4)
    val message = when (stars) {
        3 -> "EXCELLENT"
        2 -> "WELL DONE"
        1 -> "CHALLENGE DONE"
        else -> "CHALLENGE FAILED"
    }
    AlertDialog(
        onDismissRequest = { },
        confirmButton = { Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = green4)) { Text("OK", fontFamily = cantoraFont) } },
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
                BPlusStarIcon(if (stars >= 1) 33.3f else 0f, 33.3f, green4)
                Spacer(modifier = Modifier.width(8.dp))
                BPlusStarIcon(if (stars >= 2) 66.6f else 0f, 66.6f, green4)
                Spacer(modifier = Modifier.width(8.dp))
                BPlusStarIcon(if (stars >= 3) 100f else 0f, 100f, green4)
            }
        }
    )
}
