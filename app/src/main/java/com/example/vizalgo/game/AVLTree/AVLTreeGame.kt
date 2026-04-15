package com.example.vizalgo.game.AVLTree

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

class AVLNode(var value: Int) {
    var left: AVLNode? = null
    var right: AVLNode? = null
    var height: Int = 1
}

class AVLTree {
    var root: AVLNode? = null

    fun getHeight(node: AVLNode?): Int = node?.height ?: 0
    fun getBalance(node: AVLNode?): Int = if (node == null) 0 else getHeight(node.left) - getHeight(node.right)

    fun rightRotate(y: AVLNode): AVLNode {
        val x = y.left!!
        val t2 = x.right
        x.right = y
        y.left = t2
        y.height = maxOf(getHeight(y.left), getHeight(y.right)) + 1
        x.height = maxOf(getHeight(x.left), getHeight(x.right)) + 1
        return x
    }

    fun leftRotate(x: AVLNode): AVLNode {
        val y = x.right!!
        val t2 = y.left
        y.left = x
        x.right = t2
        x.height = maxOf(getHeight(x.left), getHeight(x.right)) + 1
        y.height = maxOf(getHeight(y.left), getHeight(y.right)) + 1
        return y
    }

    fun insert(node: AVLNode?, value: Int): AVLNode {
        if (node == null) return AVLNode(value)
        if (value < node.value) node.left = insert(node.left, value)
        else if (value > node.value) node.right = insert(node.right, value)
        else return node

        node.height = 1 + maxOf(getHeight(node.left), getHeight(node.right))
        val balance = getBalance(node)

        if (balance > 1 && value < node.left!!.value) return rightRotate(node)
        if (balance < -1 && value > node.right!!.value) return leftRotate(node)
        if (balance > 1 && value > node.left!!.value) {
            node.left = leftRotate(node.left!!)
            return rightRotate(node)
        }
        if (balance < -1 && value < node.right!!.value) {
            node.right = rightRotate(node.right!!)
            return leftRotate(node)
        }
        return node
    }

    fun bstInsert(node: AVLNode?, value: Int): AVLNode {
        if (node == null) return AVLNode(value)
        if (value < node.value) node.left = bstInsert(node.left, value)
        else if (value > node.value) node.right = bstInsert(node.right, value)
        else return node
        node.height = 1 + maxOf(getHeight(node.left), getHeight(node.right))
        return node
    }

    fun bstDelete(root: AVLNode?, value: Int): AVLNode? {
        if (root == null) return null
        var node: AVLNode? = root
        if (value < root.value) node!!.left = bstDelete(root.left, value)
        else if (value > root.value) node!!.right = bstDelete(root.right, value)
        else {
            if (root.left == null || root.right == null) {
                node = root.left ?: root.right
            } else {
                val temp = minValueNode(root.right!!)
                root.value = temp.value
                root.right = bstDelete(root.right, temp.value)
                node = root
            }
        }
        if (node == null) return null
        node.height = 1 + maxOf(getHeight(node.left), getHeight(node.right))
        return node
    }

    fun delete(root: AVLNode?, value: Int): AVLNode? {
        if (root == null) return null
        var node: AVLNode? = root
        if (value < root.value) node!!.left = delete(root.left, value)
        else if (value > root.value) node!!.right = delete(root.right, value)
        else {
            if (root.left == null || root.right == null) {
                node = root.left ?: root.right
            } else {
                val temp = minValueNode(root.right!!)
                root.value = temp.value
                root.right = delete(root.right, temp.value)
                node = root
            }
        }
        if (node == null) return null
        node.height = 1 + maxOf(getHeight(node.left), getHeight(node.right))
        val balance = getBalance(node)
        if (balance > 1 && getBalance(node.left) >= 0) return rightRotate(node)
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left!!)
            return rightRotate(node)
        }
        if (balance < -1 && getBalance(node.right) <= 0) return leftRotate(node)
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right!!)
            return leftRotate(node)
        }
        return node
    }

    fun findNode(root: AVLNode?, value: Int): AVLNode? {
        if (root == null || root.value == value) return root
        return if (value < root.value) findNode(root.left, value)
        else findNode(root.right, value)
    }

    fun minValueNode(node: AVLNode): AVLNode {
        var curr = node
        while (curr.left != null) curr = curr.left!!
        return curr
    }
}

class AVLTreeGame : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progressManager = ProgressManager(this)
        setContent {
            var selectedLevel by remember { mutableIntStateOf(0) }
            var unlockedLevel by remember { mutableIntStateOf(progressManager.getUnlockedLevel("AVL Tree")) }
            var levelStars by remember { mutableStateOf<Map<Int, Int>>(progressManager.getAllLevelStars("AVL Tree")) }

            if (selectedLevel == 0) {
                GameLevelsScreen(
                    dsName = "AVL Tree",
                    unlockedLevel = unlockedLevel,
                    levelStars = levelStars,
                    onLevelSelected = { selectedLevel = it },
                    onBack = { finish() }
                )
            } else {
                AVLTreeGamePlayScreen(
                    level = selectedLevel,
                    onComplete = { stars ->
                        progressManager.saveProgress("AVL Tree", selectedLevel, stars)
                        unlockedLevel = progressManager.getUnlockedLevel("AVL Tree")
                        levelStars = progressManager.getAllLevelStars("AVL Tree")
                        selectedLevel = 0
                    },
                    onBack = { selectedLevel = 0 }
                )
            }
        }
    }
}

data class AVLInstruction(val op: String, val value: Int, val text: String)

@Composable
fun AVLTreeGamePlayScreen(level: Int, onComplete: (Int) -> Unit, onBack: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val green4 = colorResource(id = R.color.green4)
    
    val tree = remember { AVLTree() }
    var rootState by remember { mutableStateOf<AVLNode?>(null) }
    
    val instructions = remember {
        mutableStateListOf<AVLInstruction>().apply {
            when {
                level <= 5 -> { // Easy: 6 steps
                    add(AVLInstruction("INSERT", 50, "Insert 50"))
                    add(AVLInstruction("INSERT", 30, "Insert 30"))
                    add(AVLInstruction("INSERT", 20, "Insert 20"))
                    add(AVLInstruction("INSERT", 40, "Insert 40"))
                    add(AVLInstruction("INSERT", 60, "Insert 60"))
                    add(AVLInstruction("DELETE", 30, "Delete 30"))
                }
                level <= 10 -> { // Medium: 8 steps
                    add(AVLInstruction("INSERT", 10, "Insert 10"))
                    add(AVLInstruction("INSERT", 20, "Insert 20"))
                    add(AVLInstruction("INSERT", 30, "Insert 30"))
                    add(AVLInstruction("INSERT", 40, "Insert 40"))
                    add(AVLInstruction("INSERT", 50, "Insert 50"))
                    add(AVLInstruction("INSERT", 25, "Insert 25"))
                    add(AVLInstruction("DELETE", 40, "Delete 40"))
                    add(AVLInstruction("INSERT", 35, "Insert 35"))
                }
                else -> { // Hard: 10 steps
                    add(AVLInstruction("INSERT", 100, "Insert 100"))
                    add(AVLInstruction("INSERT", 50, "Insert 50"))
                    add(AVLInstruction("INSERT", 150, "Insert 150"))
                    add(AVLInstruction("INSERT", 25, "Insert 25"))
                    add(AVLInstruction("INSERT", 75, "Insert 75"))
                    add(AVLInstruction("INSERT", 125, "Insert 125"))
                    add(AVLInstruction("INSERT", 175, "Insert 175"))
                    add(AVLInstruction("DELETE", 100, "Delete 100"))
                    add(AVLInstruction("INSERT", 110, "Insert 110"))
                    add(AVLInstruction("DELETE", 150, "Delete 150"))
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

    var interactionType by remember { mutableStateOf("NONE") } // NONE, INSERT, DELETE, ROTATE
    var pendingValue by remember { mutableIntStateOf(-1) }
    var rotationRequired by remember { mutableStateOf<String?>(null) } // LL, RR, LR, RL, NONE
    var balancedRoot by remember { mutableStateOf<AVLNode?>(null) }
    var rootBeforeOperation by remember { mutableStateOf<AVLNode?>(null) }

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
        interactionType = "NONE"
        rotationRequired = null
        // Do not reset currentStep; let user retry the same step
    }

    fun handleNodeDeletion(node: AVLNode) {
        if (interactionType != "DELETE") return
        if (node.value == pendingValue) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            rootBeforeOperation = copyNode(rootState)
            
            val expectedRotation = determineRequiredRotation(rootState, pendingValue, false)
            balancedRoot = tree.delete(copyNode(rootState), pendingValue)
            val unbalancedRoot = tree.bstDelete(copyNode(rootState), pendingValue)

            scope.launch {
                recentlyDeletedValue = pendingValue
                delay(500)
                
                if (expectedRotation != "NONE") {
                    rootState = unbalancedRoot
                    rotationRequired = expectedRotation
                    interactionType = "ROTATE"
                    recentlyDeletedValue = -1 
                } else {
                    rootState = balancedRoot
                    recentlyDeletedValue = -1
                    currentStep++
                    userInput = ""
                    interactionType = "NONE"
                    rootBeforeOperation = null
                    if (currentStep >= instructions.size) { 
                        finalStars = calculateStars(xp)
                        showResultDialog = true 
                    }
                }
            }
        } else {
            handleMistake()
        }
    }

    fun checkInsertion(parent: AVLNode?, isLeft: Boolean) {
        if (interactionType != "INSERT") return
        
        val correctPoint = getCorrectAVLInsertionPoint(rootState, pendingValue)
        if (correctPoint.first == parent && (parent == null || correctPoint.second == isLeft)) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            
            rootBeforeOperation = copyNode(rootState)
            val expectedRotation = determineRequiredRotation(rootState, pendingValue, true)
            balancedRoot = tree.insert(copyNode(rootState), pendingValue)
            val unbalancedRoot = tree.bstInsert(copyNode(rootState), pendingValue)

            if (expectedRotation != "NONE") {
                rootState = unbalancedRoot
                rotationRequired = expectedRotation
                interactionType = "ROTATE"
                recentlyAddedValue = pendingValue
            } else {
                rootState = balancedRoot
                recentlyAddedValue = pendingValue
                currentStep++
                userInput = ""
                interactionType = "NONE"
                rootBeforeOperation = null
                scope.launch { delay(500); recentlyAddedValue = -1 }
                if (currentStep >= instructions.size) {
                    finalStars = calculateStars(xp)
                    showResultDialog = true
                }
            }
        } else {
            handleMistake()
        }
    }

    fun handleRotationSelection(selected: String) {
        if (interactionType != "ROTATE") return
        
        if (selected == rotationRequired) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            rootState = balancedRoot
            rotationRequired = null
            interactionType = "NONE"
            currentStep++
            userInput = ""
            rootBeforeOperation = null
            scope.launch { delay(500); recentlyAddedValue = -1; recentlyDeletedValue = -1 }
            if (currentStep >= instructions.size) {
                finalStars = calculateStars(xp)
                showResultDialog = true
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
            AVLXPBar(xp = xp)
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
                            Text("${index + 1}) ${instr.text}", color = if (isDone) Color.Gray else Color.White, fontFamily = cantoraFont, fontSize = 18.sp, textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null)
                        }
                    }
                }
            }

            // Visual Area
            Box(modifier = Modifier.weight(1.5f).fillMaxWidth().animateContentSize()) {
                if (rootState == null && interactionType == "INSERT") {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val width = constraints.maxWidth.toFloat()
                        AVLGhostNode(width / 2, 100f, 44.dp, 14.sp) { checkInsertion(null, false) }
                    }
                } else {
                    rootState?.let {
                        AVLLayout(
                            it,
                            tree,
                            recentlyAddedValue,
                            recentlyDeletedValue,
                            interactionType == "INSERT",
                            interactionType == "DELETE",
                            null,
                            onNodeClick = { node ->
                                handleNodeDeletion(node)
                            },
                            onGhostClick = { parent, isLeft ->
                                checkInsertion(parent, isLeft)
                            }
                        )
                    }
                }

                if (interactionType != "NONE") {
                    Text(
                        text = when(interactionType) {
                            "INSERT" -> "Select Position for $pendingValue"
                            "ROTATE" -> "Select Rotation Type"
                            else -> ""
                        },
                        color = Color.Yellow,
                        fontFamily = cantoraFont,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                    )
                }
            }

            if (interactionType == "ROTATE") {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("LL", "RR", "LR", "RL", "NONE").forEach { type ->
                        Button(onClick = { handleRotationSelection(type) }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                            Text(type, color = Color.White)
                        }
                    }
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
                                    interactionType = "INSERT"
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
                                    pendingValue = valInt
                                    interactionType = "DELETE"
                                } else {
                                    handleMistake()
                                }
                            }
                        }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), shape = RoundedCornerShape(12.dp)) { Text("DELETE", fontFamily = cantoraFont) }
                    }
                }
            }
        }
        if (showResultDialog) AVLResultDialog(stars = finalStars, cantoraFont = cantoraFont) { onComplete(finalStars) }
    }
}

fun getCorrectAVLInsertionPoint(root: AVLNode?, value: Int): Pair<AVLNode?, Boolean> {
    if (root == null) return null to false
    var curr = root
    var parent: AVLNode? = null
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



fun determineRequiredRotation(root: AVLNode?, value: Int, isInsertion: Boolean): String {
    val tempTree = AVLTree()
    var tempRoot = copyNode(root)
    tempRoot = if (isInsertion) tempTree.bstInsert(tempRoot, value) else tempTree.bstDelete(tempRoot, value)
    
    fun findFirstUnbalanced(node: AVLNode?): String {
        if (node == null) return "NONE"
        val leftRes = findFirstUnbalanced(node.left)
        if (leftRes != "NONE") return leftRes
        val rightRes = findFirstUnbalanced(node.right)
        if (rightRes != "NONE") return rightRes
        
        val balance = tempTree.getBalance(node)
        if (balance > 1) {
            val leftBalance = tempTree.getBalance(node.left)
            return if (leftBalance >= 0) "LL" else "LR"
        } else if (balance < -1) {
            val rightBalance = tempTree.getBalance(node.right)
            return if (rightBalance <= 0) "RR" else "RL"
        }
        return "NONE"
    }
    
    return findFirstUnbalanced(tempRoot)
}

fun copyNode(node: AVLNode?): AVLNode? {
    if (node == null) return null
    val newNode = AVLNode(node.value)
    newNode.left = copyNode(node.left)
    newNode.right = copyNode(node.right)
    newNode.height = node.height
    return newNode
}

@Composable
fun AVLLayout(
    root: AVLNode,
    tree: AVLTree,
    addedVal: Int,
    deletedVal: Int,
    isSelectingPosition: Boolean,
    isSelectingReplacement: Boolean,
    replacementTargetNode: AVLNode?,
    onNodeClick: (AVLNode) -> Unit,
    onGhostClick: (AVLNode?, Boolean) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAVLConnections(this, root, width / 2, 100f, width / 4, 150f, 0)
        }
        DrawAVLNodes(
            root, tree, width / 2, 100f, width / 4, 150f, addedVal, deletedVal, 0,
            isSelectingPosition, isSelectingReplacement, replacementTargetNode,
            onNodeClick, onGhostClick
        )
    }
}

private fun drawAVLConnections(scope: DrawScope, node: AVLNode, x: Float, y: Float, xOff: Float, yOff: Float, level: Int) {
    node.left?.let { scope.drawLine(Color.White.copy(alpha = 0.3f), Offset(x, y), Offset(x - xOff, y + yOff), 3f); drawAVLConnections(scope, it, x - xOff, y + yOff, xOff / 1.8f, yOff, level + 1) }
    node.right?.let { scope.drawLine(Color.White.copy(alpha = 0.3f), Offset(x, y), Offset(x + xOff, y + yOff), 3f); drawAVLConnections(scope, it, x + xOff, y + yOff, xOff / 1.8f, yOff, level + 1) }
}

@Composable
fun DrawAVLNodes(
    node: AVLNode, tree: AVLTree, x: Float, y: Float, xOff: Float, yOff: Float, addedVal: Int, deletedVal: Int, level: Int,
    isSelectingPosition: Boolean,
    isSelectingReplacement: Boolean,
    replacementTargetNode: AVLNode?,
    onNodeClick: (AVLNode) -> Unit,
    onGhostClick: (AVLNode?, Boolean) -> Unit
) {
    val green4 = colorResource(id = R.color.green4)
    val density = LocalDensity.current
    val nodeSize = (44 - (level * 4)).coerceAtLeast(28).dp
    val fontSize = (14 - level).coerceAtLeast(10).sp
    val halfSize = with(density) { (nodeSize / 2).toPx() }
    
    val balance = tree.getBalance(node)
    val balanceColor = when {
        balance > 1 || balance < -1 -> Color.Red
        else -> Color.Cyan
    }

    val isBeingDeleted = replacementTargetNode?.value == node.value

    val glowColor = when {
        node.value == addedVal -> Color.Blue.copy(alpha = 0.5f)
        node.value == deletedVal || isBeingDeleted -> Color.Red.copy(alpha = 0.5f)
        isSelectingReplacement -> Color.Yellow.copy(alpha = 0.2f)
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
        
        // Balance Factor
        Text(
            text = balance.toString(),
            color = balanceColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 12.dp, y = (-4).dp)
        )
    }

    if (isSelectingPosition) {
        if (node.left == null) {
            AVLGhostNode(x - xOff, y + yOff, nodeSize, fontSize) { onGhostClick(node, true) }
        }
        if (node.right == null) {
            AVLGhostNode(x + xOff, y + yOff, nodeSize, fontSize) { onGhostClick(node, false) }
        }
    }

    node.left?.let { DrawAVLNodes(it, tree, x - xOff, y + yOff, xOff / 1.8f, yOff, addedVal, deletedVal, level + 1, isSelectingPosition, isSelectingReplacement, replacementTargetNode, onNodeClick, onGhostClick) }
    node.right?.let { DrawAVLNodes(it, tree, x + xOff, y + yOff, xOff / 1.8f, yOff, addedVal, deletedVal, level + 1, isSelectingPosition, isSelectingReplacement, replacementTargetNode, onNodeClick, onGhostClick) }
}

@Composable
fun AVLGhostNode(x: Float, y: Float, size: androidx.compose.ui.unit.Dp, fontSize: androidx.compose.ui.unit.TextUnit, onClick: () -> Unit) {
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
fun AVLXPBar(xp: Float) {
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
                AVLStarIcon(xp, 33.3f, starColor1)
            }
            // Star 2 at 66.6%
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.666f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                AVLStarIcon(xp, 66.6f, starColor2)
            }
            // Star 3 at 100%
            Box(
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                AVLStarIcon(xp, 100f, starColor3)
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
fun AVLStarIcon(xp: Float, threshold: Float, color: Color) {
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
fun AVLResultDialog(stars: Int, cantoraFont: FontFamily, onDismiss: () -> Unit) {
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
                AVLStarIcon(if (stars >= 1) 33.3f else 0f, 33.3f, Color(0xFF2196F3))
                Spacer(modifier = Modifier.width(8.dp))
                // Star 2 (66.6%) - Green
                AVLStarIcon(if (stars >= 2) 66.6f else 0f, 66.6f, Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                // Star 3 (100%) - Red
                AVLStarIcon(if (stars >= 3) 100f else 0f, 100f, Color(0xFFFF5252))
            }
        }
    )
}
