package com.example.vizalgo.game.BTree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
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

class BTreeNode(val m: Int, var leaf: Boolean) {
    var keys = mutableListOf<Int>()
    var children = mutableListOf<BTreeNode>()

    fun insert(k: Int) {
        var i = keys.size - 1
        if (leaf) {
            while (i >= 0 && keys[i] > k) i--
            keys.add(i + 1, k)
        } else {
            while (i >= 0 && keys[i] > k) i--
            children[i + 1].insert(k)
            if (children[i + 1].keys.size == m) {
                splitChild(i + 1, children[i + 1])
            }
        }
    }

    fun splitChild(i: Int, y: BTreeNode) {
        val mid = m / 2
        val z = BTreeNode(m, y.leaf)
        val middleKey = y.keys[mid]

        for (j in mid + 1 until y.keys.size) {
            z.keys.add(y.keys[j])
        }

        if (!y.leaf) {
            for (j in mid + 1 until y.children.size) {
                z.children.add(y.children[j])
            }
            y.children.subList(mid + 1, y.children.size).clear()
        }

        y.keys.subList(mid, y.keys.size).clear()
        keys.add(i, middleKey)
        children.add(i + 1, z)
    }

    fun remove(k: Int) {
        val idx = keys.indexOf(k)
        if (idx != -1) {
            if (leaf) {
                keys.removeAt(idx)
            } else {
                val pred = getPred(idx)
                keys[idx] = pred
                children[idx].remove(pred)
                fix(idx)
            }
        } else if (!leaf) {
            var i = 0
            while (i < keys.size && keys[i] < k) i++
            children[i].remove(k)
            fix(i)
        }
    }

    private fun getPred(idx: Int): Int {
        var cur = children[idx]
        while (!cur.leaf) cur = cur.children.last()
        return cur.keys.last()
    }

    private fun fix(i: Int) {
        val minKeys = (m + 1) / 2 - 1
        if (children[i].keys.size < minKeys) {
            if (i > 0 && children[i - 1].keys.size > minKeys) {
                borrowFromPrev(i)
            } else if (i < children.size - 1 && children[i + 1].keys.size > minKeys) {
                borrowFromNext(i)
            } else {
                if (i > 0) merge(i - 1)
                else merge(i)
            }
        }
    }

    private fun borrowFromPrev(i: Int) {
        val child = children[i]
        val sibling = children[i - 1]
        child.keys.add(0, keys[i - 1])
        keys[i - 1] = sibling.keys.removeAt(sibling.keys.size - 1)
        if (!child.leaf) child.children.add(0, sibling.children.removeAt(sibling.children.size - 1))
    }

    private fun borrowFromNext(i: Int) {
        val child = children[i]
        val sibling = children[i + 1]
        child.keys.add(keys[i])
        keys[i] = sibling.keys.removeAt(0)
        if (!child.leaf) child.children.add(sibling.children.removeAt(0))
    }

    private fun merge(i: Int) {
        val child = children[i]
        val sibling = children[i + 1]
        child.keys.add(keys.removeAt(i))
        child.keys.addAll(sibling.keys)
        if (!child.leaf) child.children.addAll(sibling.children)
        children.removeAt(i + 1)
    }
}

class BTree(val m: Int) {
    var root: BTreeNode? = null

    fun insert(k: Int) {
        if (root == null) {
            root = BTreeNode(m, true)
            root!!.keys.add(k)
        } else {
            root!!.insert(k)
            if (root!!.keys.size == m) {
                val s = BTreeNode(m, false)
                s.children.add(root!!)
                s.splitChild(0, root!!)
                root = s
            }
        }
    }

    fun remove(k: Int) {
        root?.remove(k)
        if (root?.keys?.isEmpty() == true) {
            root = if (root!!.leaf) null else root!!.children[0]
        }
    }
}

class BTreeGame : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progressManager = ProgressManager(this)
        setContent {
            var selectedLevel by remember { mutableIntStateOf(0) }
            var unlockedLevel by remember { mutableIntStateOf(progressManager.getUnlockedLevel("B-Tree")) }
            var levelStars by remember { mutableStateOf<Map<Int, Int>>(progressManager.getAllLevelStars("B-Tree")) }

            if (selectedLevel == 0) {
                GameLevelsScreen(
                    dsName = "B-Tree",
                    unlockedLevel = unlockedLevel,
                    levelStars = levelStars,
                    onLevelSelected = { selectedLevel = it },
                    onBack = { finish() }
                )
            } else {
                BTreeGamePlayScreen(
                    level = selectedLevel,
                    onComplete = { stars ->
                        progressManager.saveProgress("B-Tree", selectedLevel, stars)
                        unlockedLevel = progressManager.getUnlockedLevel("B-Tree")
                        levelStars = progressManager.getAllLevelStars("B-Tree")
                        selectedLevel = 0
                    },
                    onBack = { selectedLevel = 0 }
                )
            }
        }
    }
}

data class BTInstruction(val op: String, val value: Int)

@Composable
fun BTreeGamePlayScreen(level: Int, onComplete: (Int) -> Unit, onBack: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val green4 = colorResource(id = R.color.green4)

    // B-Tree Order 3
    var btreeState by remember { mutableStateOf<BTreeNode?>(null) }

    val instructions = remember {
        mutableStateListOf<BTInstruction>().apply {
            when {
                level <= 5 -> { // Easy: 6 steps
                    add(BTInstruction("INSERT", 50))
                    add(BTInstruction("INSERT", 100))
                    add(BTInstruction("INSERT", 25))
                    add(BTInstruction("INSERT", 75))
                    add(BTInstruction("INSERT", 150))
                    add(BTInstruction("DELETE", 75))
                }
                level <= 10 -> { // Medium: 8 steps
                    add(BTInstruction("INSERT", 10))
                    add(BTInstruction("INSERT", 20))
                    add(BTInstruction("INSERT", 30))
                    add(BTInstruction("INSERT", 40))
                    add(BTInstruction("INSERT", 50))
                    add(BTInstruction("INSERT", 60))
                    add(BTInstruction("DELETE", 20))
                    add(BTInstruction("DELETE", 40))
                }
                else -> { // Hard: 10 steps
                    add(BTInstruction("INSERT", 80))
                    add(BTInstruction("INSERT", 40))
                    add(BTInstruction("INSERT", 120))
                    add(BTInstruction("INSERT", 20))
                    add(BTInstruction("INSERT", 60))
                    add(BTInstruction("INSERT", 100))
                    add(BTInstruction("DELETE", 40))
                    add(BTInstruction("INSERT", 140))
                    add(BTInstruction("DELETE", 80))
                    add(BTInstruction("INSERT", 50))
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
            BTXPBar(xp = xp)
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
                btreeState?.let {
                    BTreeVisualizer(it, recentlyAddedValue, recentlyDeletedValue)
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

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            if (userInput.isNotEmpty()) {
                                val valInt = userInput.toInt()
                                if (currentStep < instructions.size && instructions[currentStep].op == "INSERT" && instructions[currentStep].value == valInt) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    val newTree = BTree(3)
                                    for (i in 0 until currentStep) {
                                        if (instructions[i].op == "INSERT") newTree.insert(instructions[i].value)
                                        else newTree.remove(instructions[i].value)
                                    }
                                    newTree.insert(valInt)
                                    btreeState = newTree.root
                                    recentlyAddedValue = valInt
                                    currentStep++
                                    userInput = ""
                                    scope.launch { delay(500); recentlyAddedValue = -1 }
                                    if (currentStep >= instructions.size) { finalStars = calculateBTStars(xp); showResultDialog = true }
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    scope.launch {
                                        xp -= xpPerMistake; recentlyError = true; showErrorIndex = currentStep
                                        delay(800); recentlyError = false; showErrorIndex = -1
                                    }
                                }
                            }
                        }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = green4), shape = RoundedCornerShape(12.dp)) { Text("INSERT") }

                        Button(onClick = {
                            if (userInput.isNotEmpty()) {
                                val valInt = userInput.toInt()
                                if (currentStep < instructions.size && instructions[currentStep].op == "DELETE" && instructions[currentStep].value == valInt) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    recentlyDeletedValue = valInt
                                    scope.launch {
                                        delay(500)
                                        val newTree = BTree(3)
                                        for (i in 0 until currentStep) {
                                            if (instructions[i].op == "INSERT") newTree.insert(instructions[i].value)
                                            else newTree.remove(instructions[i].value)
                                        }
                                        newTree.remove(valInt)
                                        btreeState = newTree.root
                                        recentlyDeletedValue = -1
                                        currentStep++
                                        userInput = ""
                                        if (currentStep >= instructions.size) { finalStars = calculateBTStars(xp); showResultDialog = true }
                                    }
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    scope.launch {
                                        xp -= xpPerMistake; recentlyError = true; showErrorIndex = currentStep
                                        delay(800); recentlyError = false; showErrorIndex = -1
                                    }
                                }
                            }
                        }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), shape = RoundedCornerShape(12.dp)) { Text("DELETE") }
                    }
                }
            }
        }
        if (showResultDialog) BTResultDialog(stars = finalStars, cantoraFont = cantoraFont) { onComplete(finalStars) }
    }
}

@Composable
fun BTreeVisualizer(root: BTreeNode, addedVal: Int, deletedVal: Int) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val reductionFactor = 2.4f
        val yOffset = 170f
        val initialXOffset = width * 0.3f
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBTreeConnections(this, root, width / 2, 80f, initialXOffset, yOffset, reductionFactor)
        }
        DrawBTreeNodes(root, width / 2, 80f, initialXOffset, yOffset, addedVal, deletedVal, 0, reductionFactor)
    }
}

private fun drawBTreeConnections(scope: androidx.compose.ui.graphics.drawscope.DrawScope, node: BTreeNode, x: Float, y: Float, xOff: Float, yOff: Float, reductionFactor: Float) {
    if (node.children.isNotEmpty()) {
        val childCount = node.children.size
        val startX = x - (childCount - 1) * xOff / 2
        node.children.forEachIndexed { idx, child ->
            val childX = startX + idx * xOff
            scope.drawLine(Color.White.copy(alpha = 0.3f), Offset(x, y), Offset(childX, y + yOff), 3f)
            drawBTreeConnections(scope, child, childX, y + yOff, xOff / reductionFactor, yOff, reductionFactor)
        }
    }
}

@Composable
fun DrawBTreeNodes(node: BTreeNode, x: Float, y: Float, xOff: Float, yOff: Float, addedVal: Int, deletedVal: Int, level: Int, reductionFactor: Float) {
    val density = LocalDensity.current
    val green4 = colorResource(id = R.color.green4)
    val fontSize = (14.0 - (level * 1.2)).coerceAtLeast(10.0).sp
    val keyPadding = (6 - level).coerceAtLeast(3).dp

    Box(modifier = Modifier.offset(x = with(density) { x.toDp() }, y = with(density) { y.toDp() })) {
        androidx.compose.ui.layout.Layout(
            content = {
                Row(
                    modifier = Modifier
                        .background(green4.copy(alpha = 0.95f), RoundedCornerShape(4.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(4.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    node.keys.forEachIndexed { idx, key ->
                        Box(
                            modifier = Modifier
                                .padding(keyPadding)
                                .background(
                                    when (key) {
                                        addedVal -> Color.Blue.copy(alpha = 0.4f)
                                        deletedVal -> Color.Red.copy(alpha = 0.4f)
                                        else -> Color.Transparent
                                    },
                                    RoundedCornerShape(2.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = key.toString(), color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Bold)
                        }
                        if (idx < node.keys.size - 1) {
                            Box(modifier = Modifier.height(18.dp).width(1.dp).background(Color.White.copy(alpha = 0.3f)))
                        }
                    }
                }
            }
        ) { measurables, constraints ->
            val p = measurables[0].measure(constraints)
            layout(p.width, p.height) { p.placeRelative(-p.width / 2, -p.height / 2) }
        }
    }

    if (node.children.isNotEmpty()) {
        val childCount = node.children.size
        val startX = x - (childCount - 1) * xOff / 2
        node.children.forEachIndexed { idx, child ->
            val childX = startX + idx * xOff
            DrawBTreeNodes(child, childX, y + yOff, xOff / reductionFactor, yOff, addedVal, deletedVal, level + 1, reductionFactor)
        }
    }
}

fun calculateBTStars(xp: Float): Int = when { xp >= 99.9f -> 3; xp >= 66.6f -> 2; xp >= 33.3f -> 1; else -> 0 }

@Composable
fun BTXPBar(xp: Float) {
    val green4 = colorResource(id = R.color.green4)
    val starColor1 = Color(0xFF2196F3) // Blue
    val starColor2 = green4
    val starColor3 = Color(0xFFFF5252) // Red

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            // Star 1 (33.3%) - Blue
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.333f)
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.CenterEnd
            ) {
                BTStarIcon(xp, 33.3f, starColor1)
            }

            // Star 2 (66.6%) - green4
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.666f)
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.CenterEnd
            ) {
                BTStarIcon(xp, 66.6f, starColor2)
            }

            // Star 3 (100%) - Red
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.CenterEnd
            ) {
                BTStarIcon(xp, 100f, starColor3)
            }
        }

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
fun BTStarIcon(xp: Float, threshold: Float, color: Color) {
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
fun BTResultDialog(stars: Int, cantoraFont: FontFamily, onDismiss: () -> Unit) {
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
                BTStarIcon(if (stars >= 1) 33.3f else 0f, 33.3f, Color(0xFF2196F3))
                Spacer(modifier = Modifier.width(8.dp))
                // Star 2 (66.6%) - green4
                BTStarIcon(if (stars >= 2) 66.6f else 0f, 66.6f, green4)
                Spacer(modifier = Modifier.width(8.dp))
                // Star 3 (100%) - Red
                BTStarIcon(if (stars >= 3) 100f else 0f, 100f, Color(0xFFFF5252))
            }
        }
    )
}
