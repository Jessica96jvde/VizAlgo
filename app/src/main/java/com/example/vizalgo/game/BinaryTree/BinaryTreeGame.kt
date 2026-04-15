package com.example.vizalgo.game.BinaryTree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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

class BTNode(var value: Int) {
    var left: BTNode? = null
    var right: BTNode? = null
}

class BinarySearchTree {
    var root: BTNode? = null

    fun insert(root: BTNode?, value: Int): BTNode {
        if (root == null) return BTNode(value)
        if (value < root.value) root.left = insert(root.left, value)
        else if (value > root.value) root.right = insert(root.right, value)
        return root
    }

    fun delete(root: BTNode?, value: Int): BTNode? {
        if (root == null) return null
        if (value < root.value) root.left = delete(root.left, value)
        else if (value > root.value) root.right = delete(root.right, value)
        else {
            if (root.left == null) return root.right
            if (root.right == null) return root.left
            root.value = minValue(root.right!!)
            root.right = delete(root.right, root.value)
        }
        return root
    }

    fun findNode(root: BTNode?, value: Int): BTNode? {
        if (root == null || root.value == value) return root
        return if (value < root.value) findNode(root.left, value)
        else findNode(root.right, value)
    }

    fun minValue(root: BTNode): Int {
        var minv = root.value
        var curr = root
        while (curr.left != null) {
            minv = curr.left!!.value
            curr = curr.left!!
        }
        return minv
    }
}

class BinaryTreeGame : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progressManager = ProgressManager(this)
        setContent {
            var selectedLevel by remember { mutableIntStateOf(0) }
            var unlockedLevel by remember { mutableIntStateOf(progressManager.getUnlockedLevel("Binary Search Tree")) }
            var levelStars by remember { mutableStateOf(progressManager.getAllLevelStars("Binary Search Tree")) }

            if (selectedLevel == 0) {
                GameLevelsScreen(
                    dsName = "Binary Search Tree",
                    unlockedLevel = unlockedLevel,
                    levelStars = levelStars,
                    onLevelSelected = { selectedLevel = it },
                    onBack = { finish() }
                )
            } else {
                BinaryTreeGamePlayScreen(
                    level = selectedLevel,
                    onComplete = { stars ->
                        progressManager.saveProgress("Binary Search Tree", selectedLevel, stars)
                        unlockedLevel = progressManager.getUnlockedLevel("Binary Search Tree")
                        levelStars = progressManager.getAllLevelStars("Binary Search Tree")
                        selectedLevel = 0
                    },
                    onBack = { selectedLevel = 0 }
                )
            }
        }
    }
}

data class BTInstruction(val op: String, val value: Int, val text: String)

@Composable
fun BinaryTreeGamePlayScreen(level: Int, onComplete: (Int) -> Unit, onBack: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val green4 = colorResource(id = R.color.green4)
    
    val tree = remember { BinarySearchTree() }
    var rootState by remember { mutableStateOf<BTNode?>(null) }
    
    val instructions = remember {
        mutableStateListOf<BTInstruction>().apply {
            when {
                level <= 5 -> { // Easy: 6 steps
                    add(BTInstruction("INSERT", 50, "Insert 50"))
                    add(BTInstruction("INSERT", 30, "Insert 30"))
                    add(BTInstruction("INSERT", 70, "Insert 70"))
                    add(BTInstruction("INSERT", 20, "Insert 20"))
                    add(BTInstruction("INSERT", 40, "Insert 40"))
                    add(BTInstruction("DELETE", 30, "Delete 30"))
                }
                level <= 10 -> { // Medium: 8 steps
                    add(BTInstruction("INSERT", 100, "Insert 100"))
                    add(BTInstruction("INSERT", 50, "Insert 50"))
                    add(BTInstruction("INSERT", 150, "Insert 150"))
                    add(BTInstruction("INSERT", 25, "Insert 25"))
                    add(BTInstruction("INSERT", 75, "Insert 75"))
                    add(BTInstruction("INSERT", 125, "Insert 125"))
                    add(BTInstruction("DELETE", 50, "Delete 50"))
                    add(BTInstruction("INSERT", 60, "Insert 60"))
                }
                else -> { // Hard: 10 steps
                    add(BTInstruction("INSERT", 200, "Insert 200"))
                    add(BTInstruction("INSERT", 100, "Insert 100"))
                    add(BTInstruction("INSERT", 300, "Insert 300"))
                    add(BTInstruction("INSERT", 50, "Insert 50"))
                    add(BTInstruction("INSERT", 150, "Insert 150"))
                    add(BTInstruction("INSERT", 250, "Insert 250"))
                    add(BTInstruction("INSERT", 350, "Insert 350"))
                    add(BTInstruction("DELETE", 100, "Delete 100"))
                    add(BTInstruction("INSERT", 125, "Insert 125"))
                    add(BTInstruction("DELETE", 300, "Delete 300"))
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
    var isSelectingReplacement by remember { mutableStateOf(false) }
    var pendingValue by remember { mutableIntStateOf(-1) }
    var replacementTargetNode by remember { mutableStateOf<BTNode?>(null) }

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
        isSelectingReplacement = false
    }

    fun checkInsertion(parent: BTNode?, isLeft: Boolean) {
        if (!isSelectingPosition) return
        
        val correctPoint = getCorrectInsertionPoint(rootState, pendingValue)
        if (correctPoint.first == parent && (parent == null || correctPoint.second == isLeft)) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            rootState = tree.insert(rootState, pendingValue)
            recentlyAddedValue = pendingValue
            currentStep++
            userInput = ""
            isSelectingPosition = false
            scope.launch { delay(500); recentlyAddedValue = -1 }
            if (currentStep >= instructions.size) {
                finalStars = calculateStars(xp)
                showResultDialog = true
            }
        } else {
            handleMistake()
        }
    }

    fun checkReplacement(clickedNode: BTNode) {
        if (!isSelectingReplacement || replacementTargetNode == null) return
        
        val successorValue = tree.minValue(replacementTargetNode!!.right!!)
        if (clickedNode.value == successorValue) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            val valToDelete = replacementTargetNode!!.value
            scope.launch {
                recentlyDeletedValue = valToDelete
                delay(500)
                rootState = tree.delete(rootState, valToDelete)
                recentlyDeletedValue = -1
                currentStep++
                userInput = ""
                isSelectingReplacement = false
                replacementTargetNode = null
                if (currentStep >= instructions.size) {
                    finalStars = calculateStars(xp)
                    showResultDialog = true
                }
            }
        } else {
            handleMistake()
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

        Column(modifier = Modifier.fillMaxSize().padding(20.dp).imePadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            BTXPBar(xp = xp)
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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

            // Visual Area
            Box(modifier = Modifier.weight(1f).fillMaxWidth().animateContentSize()) {
                if (rootState == null && isSelectingPosition) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        GhostNode(0f, 0f, 44.dp, 14.sp) { checkInsertion(null, false) }
                    }
                } else {
                    rootState?.let {
                        TreeLayout(
                            it,
                            recentlyAddedValue,
                            recentlyDeletedValue,
                            isSelectingPosition,
                            isSelectingReplacement,
                            replacementTargetNode,
                            onNodeClick = { node ->
                                if (isSelectingReplacement) checkReplacement(node)
                            },
                            onGhostClick = { parent, isLeft ->
                                checkInsertion(parent, isLeft)
                            }
                        )
                    }
                }
                
                if (isSelectingPosition || isSelectingReplacement) {
                    Text(
                        text = if (isSelectingPosition) "Select Position for $pendingValue" else "Select Successor for ${replacementTargetNode?.value}",
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

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = {
                            if (userInput.isNotEmpty()) {
                                val valInt = userInput.toInt()
                                if (currentStep < instructions.size && instructions[currentStep].op == "INSERT" && instructions[currentStep].value == valInt) {
                                    pendingValue = valInt
                                    isSelectingPosition = true
                                    isSelectingReplacement = false
                                } else {
                                    handleMistake()
                                }
                            }
                        }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = green4), shape = RoundedCornerShape(12.dp)) { Text("INSERT", fontFamily = cantoraFont) }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = {
                            if (userInput.isNotEmpty()) {
                                val valInt = userInput.toInt()
                                if (currentStep < instructions.size && instructions[currentStep].op == "DELETE" && instructions[currentStep].value == valInt) {
                                    val nodeToDelete = tree.findNode(rootState, valInt)
                                    if (nodeToDelete != null) {
                                        if (nodeToDelete.left != null && nodeToDelete.right != null) {
                                            replacementTargetNode = nodeToDelete
                                            isSelectingReplacement = true
                                            isSelectingPosition = false
                                        } else {
                                            // 0 or 1 child, delete immediately
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            scope.launch {
                                                recentlyDeletedValue = valInt
                                                delay(500)
                                                rootState = tree.delete(rootState, valInt)
                                                recentlyDeletedValue = -1
                                                currentStep++
                                                userInput = ""
                                                if (currentStep >= instructions.size) { finalStars = calculateStars(xp); showResultDialog = true }
                                            }
                                        }
                                    } else {
                                        handleMistake()
                                    }
                                } else {
                                    handleMistake()
                                }
                            }
                        }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), shape = RoundedCornerShape(12.dp)) { Text("DELETE", fontFamily = cantoraFont) }
                    }
                }
            }
        }
        if (showResultDialog) BTResultDialog(stars = finalStars, cantoraFont = cantoraFont) { onComplete(finalStars) }
    }
}

fun getCorrectInsertionPoint(root: BTNode?, value: Int): Pair<BTNode?, Boolean> {
    if (root == null) return null to false
    var curr = root
    var parent: BTNode? = null
    var isLeft = false
    while (curr != null) {
        parent = curr
        if (value < curr.value) {
            curr = curr.left
            isLeft = true
        } else {
            curr = curr.right
            isLeft = false
        }
    }
    return parent to isLeft
}



@Composable
fun TreeLayout(
    root: BTNode,
    addedVal: Int,
    deletedVal: Int,
    isSelectingPosition: Boolean,
    isSelectingReplacement: Boolean,
    replacementTargetNode: BTNode?,
    onNodeClick: (BTNode) -> Unit,
    onGhostClick: (BTNode?, Boolean) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBTGameConnections(this, root, width / 2, 100f, width / 4, 150f)
        }
        DrawBTGameNodes(
            root, width / 2, 100f, width / 4, 150f, addedVal, deletedVal, 0,
            isSelectingPosition, isSelectingReplacement, replacementTargetNode,
            onNodeClick, onGhostClick
        )
    }
}

private fun drawBTGameConnections(scope: DrawScope, node: BTNode, x: Float, y: Float, xOff: Float, yOff: Float) {
    node.left?.let { scope.drawLine(Color.White.copy(alpha = 0.3f), Offset(x, y), Offset(x - xOff, y + yOff), 3f); drawBTGameConnections(scope, it, x - xOff, y + yOff, xOff / 1.8f, yOff) }
    node.right?.let { scope.drawLine(Color.White.copy(alpha = 0.3f), Offset(x, y), Offset(x + xOff, y + yOff), 3f); drawBTGameConnections(scope, it, x + xOff, y + yOff, xOff / 1.8f, yOff) }
}

@Composable
fun DrawBTGameNodes(
    node: BTNode, x: Float, y: Float, xOff: Float, yOff: Float, addedVal: Int, deletedVal: Int, level: Int,
    isSelectingPosition: Boolean,
    isSelectingReplacement: Boolean,
    replacementTargetNode: BTNode?,
    onNodeClick: (BTNode) -> Unit,
    onGhostClick: (BTNode?, Boolean) -> Unit
) {
    val green4 = colorResource(id = R.color.green4)
    val density = LocalDensity.current
    val nodeSize = (40 - (level * 4)).coerceAtLeast(24).dp
    val fontSize = (12 - level).coerceAtLeast(8).sp
    val halfSize = with(density) { (nodeSize / 2).toPx() }
    
    val isBeingDeleted = replacementTargetNode?.value == node.value

    val glowColor = when {
        node.value == addedVal -> Color.Blue.copy(alpha = 0.5f)
        node.value == deletedVal || isBeingDeleted -> Color.Red.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .offset(x = with(density) { (x - halfSize).toDp() }, y = with(density) { (y - halfSize).toDp() })
            .size(nodeSize)
            .background(glowColor, CircleShape)
            .border(2.dp, if (glowColor != Color.Transparent) glowColor else Color.White.copy(alpha = 0.8f), CircleShape)
            .background(if (isBeingDeleted) Color.Red.copy(alpha = 0.6f) else green4.copy(alpha = 0.9f), CircleShape)
            .clickable { onNodeClick(node) },
        contentAlignment = Alignment.Center
    ) {
        Text(text = node.value.toString(), color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Bold)
    }

    if (isSelectingPosition) {
        if (node.left == null) {
            GhostNode(x - xOff, y + yOff, nodeSize, fontSize) { onGhostClick(node, true) }
        }
        if (node.right == null) {
            GhostNode(x + xOff, y + yOff, nodeSize, fontSize) { onGhostClick(node, false) }
        }
    }

    node.left?.let { DrawBTGameNodes(it, x - xOff, y + yOff, xOff / 1.8f, yOff, addedVal, deletedVal, level + 1, isSelectingPosition, isSelectingReplacement, replacementTargetNode, onNodeClick, onGhostClick) }
    node.right?.let { DrawBTGameNodes(it, x + xOff, y + yOff, xOff / 1.8f, yOff, addedVal, deletedVal, level + 1, isSelectingPosition, isSelectingReplacement, replacementTargetNode, onNodeClick, onGhostClick) }
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
                style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
            )
        }
        Text("?", color = Color.White.copy(alpha = 0.5f), fontSize = fontSize)
    }
}

fun calculateStars(xp: Float): Int = when { xp >= 99.9f -> 3; xp >= 66.6f -> 2; xp >= 33.3f -> 1; else -> 0 }

@Composable
fun BTXPBar(xp: Float) {
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
                BTStarIcon(xp, 33.3f, starColor1)
            }
            // Star 2 at 66.6%
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.666f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                BTStarIcon(xp, 66.6f, starColor2)
            }
            // Star 3 at 100%
            Box(
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                BTStarIcon(xp, 100f, starColor3)
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
                // Star 1 (33.3%) - Blue
                BTStarIcon(if (stars >= 1) 33.3f else 0f, 33.3f, Color(0xFF2196F3))
                Spacer(modifier = Modifier.width(8.dp))
                // Star 2 (66.6%) - Green
                BTStarIcon(if (stars >= 2) 66.6f else 0f, 66.6f, Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                // Star 3 (100%) - Red
                BTStarIcon(if (stars >= 3) 100f else 0f, 100f, Color(0xFFFF5252))
            }
        }
    )
}
