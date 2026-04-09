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
import androidx.compose.ui.graphics.drawscope.DrawScope
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

class BTNode(var value: Int) {
    var left: BTNode? = null
    var right: BTNode? = null
}

class BinaryTree {
    var root: BTNode? = null

    fun insert(root: BTNode?, value: Int): BTNode {
        if (root == null) return BTNode(value)
        if (value < root.value) {
            root.left = insert(root.left, value)
        } else if (value > root.value) {
            root.right = insert(root.right, value)
        }
        return root
    }

    fun delete(root: BTNode?, value: Int): BTNode? {
        if (root == null) return null
        if (value < root.value) {
            root.left = delete(root.left, value)
        } else if (value > root.value) {
            root.right = delete(root.right, value)
        } else {
            if (root.left == null) return root.right
            if (root.right == null) return root.left
            root.value = minValue(root.right!!)
            root.right = delete(root.right, root.value)
        }
        return root
    }

    private fun minValue(root: BTNode): Int {
        var minv = root.value
        var curr = root
        while (curr.left != null) {
            minv = curr.left!!.value
            curr = curr.left!!
        }
        return minv
    }
}

@Composable
fun BinaryTreeScreen() {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val green4 = colorResource(id = R.color.green4)
    val tree = remember { BinaryTree() }
    var rootState by remember { mutableStateOf<BTNode?>(null) }
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
                text = "Binary Search Tree",
                fontFamily = cantoraFont,
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                rootState?.let {
                    TreeLayout(it, recentlyAddedValue, recentlyDeletedValue)
                } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tree is empty", color = Color.White.copy(alpha = 0.5f), fontFamily = cantoraFont, fontSize = 20.sp)
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
fun TreeLayout(root: BTNode, addedVal: Int, deletedVal: Int) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val density = LocalDensity.current

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBTConnections(this, root, width / 2, 100f, width / 4, 150f, 0)
        }
        
        DrawBTNodes(root, width / 2, 100f, width / 4, 150f, addedVal, deletedVal, 0)
    }
}

private fun drawBTConnections(
    scope: DrawScope,
    node: BTNode,
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
        drawBTConnections(scope, it, x - xOffset, y + yOffset, xOffset / 1.8f, yOffset, level + 1)
    }
    node.right?.let {
        scope.drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(x, y),
            end = Offset(x + xOffset, y + yOffset),
            strokeWidth = 3f
        )
        drawBTConnections(scope, it, x + xOffset, y + yOffset, xOffset / 1.8f, yOffset, level + 1)
    }
}

@Composable
fun DrawBTNodes(node: BTNode, x: Float, y: Float, xOffset: Float, yOffset: Float, addedVal: Int, deletedVal: Int, level: Int) {
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

    node.left?.let { DrawBTNodes(it, x - xOffset, y + yOffset, xOffset / 1.8f, yOffset, addedVal, deletedVal, level + 1) }
    node.right?.let { DrawBTNodes(it, x + xOffset, y + yOffset, xOffset / 1.8f, yOffset, addedVal, deletedVal, level + 1) }
}
