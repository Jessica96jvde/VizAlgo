package com.example.vizalgo.visualize

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R
import com.example.vizalgo.utils.glassmorphic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

class AVLNode(var value: Int) {
    var left: AVLNode? = null
    var right: AVLNode? = null
    var height: Int = 1
}

class AVLTree {
    var root: AVLNode? = null

    fun getHeight(node: AVLNode?): Int = node?.height ?: 0

    private fun getBalance(node: AVLNode?): Int = 
        if (node == null) 0 else getHeight(node.left) - getHeight(node.right)

    private fun rightRotate(y: AVLNode): AVLNode {
        val x = y.left!!
        val T2 = x.right
        x.right = y
        y.left = T2
        y.height = max(getHeight(y.left), getHeight(y.right)) + 1
        x.height = max(getHeight(x.left), getHeight(x.right)) + 1
        return x
    }

    private fun leftRotate(x: AVLNode): AVLNode {
        val y = x.right!!
        val T2 = y.left
        y.left = x
        x.right = T2
        x.height = max(getHeight(x.left), getHeight(x.right)) + 1
        y.height = max(getHeight(y.left), getHeight(y.right)) + 1
        return y
    }

    fun insert(root: AVLNode?, value: Int): AVLNode {
        if (root == null) return AVLNode(value)

        if (value < root.value)
            root.left = insert(root.left, value)
        else if (value > root.value)
            root.right = insert(root.right, value)
        else return root

        root.height = 1 + max(getHeight(root.left), getHeight(root.right))
        val balance = getBalance(root)

        // Left Left Case
        if (balance > 1 && value < root.left!!.value) return rightRotate(root)

        // Right Right Case
        if (balance < -1 && value > root.right!!.value) return leftRotate(root)

        // Left Right Case
        if (balance > 1 && value > root.left!!.value) {
            root.left = leftRotate(root.left!!)
            return rightRotate(root)
        }

        // Right Left Case
        if (balance < -1 && value < root.right!!.value) {
            root.right = rightRotate(root.right!!)
            return leftRotate(root)
        }

        return root
    }

    fun delete(root: AVLNode?, value: Int): AVLNode? {
        if (root == null) return root

        if (value < root.value)
            root.left = delete(root.left, value)
        else if (value > root.value)
            root.right = delete(root.right, value)
        else {
            if (root.left == null || root.right == null) {
                val temp = root.left ?: root.right
                if (temp == null) return null else return temp
            } else {
                val temp = minValueNode(root.right!!)
                root.value = temp.value
                root.right = delete(root.right, temp.value)
            }
        }

        root.height = 1 + max(getHeight(root.left), getHeight(root.right))
        val balance = getBalance(root)

        if (balance > 1 && getBalance(root.left) >= 0) return rightRotate(root)
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left!!)
            return rightRotate(root)
        }
        if (balance < -1 && getBalance(root.right) <= 0) return leftRotate(root)
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right!!)
            return leftRotate(root)
        }
        return root
    }

    private fun minValueNode(node: AVLNode): AVLNode {
        var current = node
        while (current.left != null) current = current.left!!
        return current
    }
}

@Composable
fun AVLTreeScreen() {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val green4 = colorResource(id = R.color.green4)
    val tree = remember { AVLTree() }
    var rootState by remember { mutableStateOf<AVLNode?>(null) }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    var recentlyAddedValue by remember { mutableStateOf(-1) }
    var recentlyDeletedValue by remember { mutableStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.homebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "AVL Tree Visualizer",
                fontFamily = cantoraFont,
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                rootState?.let {
                    TreeLayout(it, recentlyAddedValue, recentlyDeletedValue)
                } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tree is empty", color = Color.White.copy(alpha = 0.5f))
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp).glassmorphic(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = input,
                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 3) input = it },
                        placeholder = { Text("Val", color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
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
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (input.isNotEmpty()) {
                                val valToInsert = input.toInt()
                                rootState = tree.insert(rootState, valToInsert)
                                recentlyAddedValue = valToInsert
                                input = ""
                                scope.launch {
                                    delay(500)
                                    recentlyAddedValue = -1
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = green4)
                    ) {
                        Text("Insert")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (input.isNotEmpty()) {
                                val valToDelete = input.toInt()
                                recentlyDeletedValue = valToDelete
                                scope.launch {
                                    delay(500)
                                    rootState = tree.delete(rootState, valToDelete)
                                    recentlyDeletedValue = -1
                                }
                                input = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Delete")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { rootState = null; tree.root = null; input = "" },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                    ) {
                        Text("Clear")
                    }
                }
            }
        }
    }
}

@Composable
fun TreeLayout(root: AVLNode, addedVal: Int, deletedVal: Int) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val density = LocalDensity.current

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawConnections(this, root, width / 2, 100f, width / 4, 150f, 0)
        }
        
        DrawNodes(root, width / 2, 100f, width / 4, 150f, addedVal, deletedVal, 0)
    }
}

private fun drawConnections(
    scope: androidx.compose.ui.graphics.drawscope.DrawScope,
    node: AVLNode,
    x: Float,
    y: Float,
    xOffset: Float,
    yOffset: Float,
    level: Int
) {
    node.left?.let {
        scope.drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(x, y),
            end = Offset(x - xOffset, y + yOffset),
            strokeWidth = 3f
        )
        drawConnections(scope, it, x - xOffset, y + yOffset, xOffset / 1.8f, yOffset, level + 1)
    }
    node.right?.let {
        scope.drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(x, y),
            end = Offset(x + xOffset, y + yOffset),
            strokeWidth = 3f
        )
        drawConnections(scope, it, x + xOffset, y + yOffset, xOffset / 1.8f, yOffset, level + 1)
    }
}

@Composable
fun DrawNodes(node: AVLNode, x: Float, y: Float, xOffset: Float, yOffset: Float, addedVal: Int, deletedVal: Int, level: Int) {
    val green4 = colorResource(id = R.color.green4)
    val density = LocalDensity.current
    
    val nodeSize = (40 - (level * 4)).coerceAtLeast(24).dp
    val fontSize = (12 - level).coerceAtLeast(8).sp
    val halfSize = with(density) { (nodeSize / 2).toPx() }
    
    val glowColor = when (node.value) {
        addedVal -> Color.Blue.copy(alpha = 0.5f)
        deletedVal -> Color.Red.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .offset(
                x = with(density) { (x - halfSize).toDp() },
                y = with(density) { (y - halfSize).toDp() }
            )
            .size(nodeSize)
            .background(glowColor, CircleShape)
            .border(2.dp, if (glowColor != Color.Transparent) glowColor else Color.White.copy(alpha = 0.8f), CircleShape)
            .background(green4.copy(alpha = 0.9f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = node.value.toString(),
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }

    node.left?.let { DrawNodes(it, x - xOffset, y + yOffset, xOffset / 1.8f, yOffset, addedVal, deletedVal, level + 1) }
    node.right?.let { DrawNodes(it, x + xOffset, y + yOffset, xOffset / 1.8f, yOffset, addedVal, deletedVal, level + 1) }
}
