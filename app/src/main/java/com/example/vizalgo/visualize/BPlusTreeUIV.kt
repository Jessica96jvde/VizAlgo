package com.example.vizalgo.visualize

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.res.Configuration
import com.example.vizalgo.R
import com.example.vizalgo.utils.glassmorphic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

// B+ Tree Logic
class BPlusTreeNode(val m: Int, var isLeaf: Boolean) {
    var keys = mutableListOf<Int>()
    var children = mutableListOf<BPlusTreeNode>()
    var next: BPlusTreeNode? = null // For leaf node linking

    fun insert(k: Int): Pair<Int, BPlusTreeNode>? {
        if (isLeaf) {
            val idx = keys.binarySearch(k).let { if (it < 0) -it - 1 else it }
            keys.add(idx, k)
            if (keys.size == m) {
                return splitLeaf()
            }
        } else {
            var i = 0
            while (i < keys.size && k >= keys[i]) i++
            val split = children[i].insert(k)
            if (split != null) {
                val (midKey, newNode) = split
                val insertIdx = keys.binarySearch(midKey).let { if (it < 0) -it - 1 else it }
                keys.add(insertIdx, midKey)
                children.add(insertIdx + 1, newNode)
                if (keys.size == m) {
                    return splitInternal()
                }
            }
        }
        return null
    }

    private fun splitLeaf(): Pair<Int, BPlusTreeNode> {
        val mid = m / 2
        val newNode = BPlusTreeNode(m, true)
        newNode.keys.addAll(keys.subList(mid, keys.size))
        keys.subList(mid, keys.size).clear()
        newNode.next = this.next
        this.next = newNode
        return Pair(newNode.keys[0], newNode)
    }

    private fun splitInternal(): Pair<Int, BPlusTreeNode> {
        val mid = m / 2
        val midKey = keys[mid]
        val newNode = BPlusTreeNode(m, false)
        newNode.keys.addAll(keys.subList(mid + 1, keys.size))
        newNode.children.addAll(children.subList(mid + 1, children.size))
        keys.subList(mid, keys.size).clear()
        children.subList(mid + 1, children.size).clear()
        return Pair(midKey, newNode)
    }
}

class BPlusTree(val m: Int) {
    var root: BPlusTreeNode? = null

    fun insert(k: Int) {
        if (root == null) {
            root = BPlusTreeNode(m, true)
            root!!.keys.add(k)
        } else {
            val split = root!!.insert(k)
            if (split != null) {
                val newRoot = BPlusTreeNode(m, false)
                newRoot.keys.add(split.first)
                newRoot.children.add(root!!)
                newRoot.children.add(split.second)
                root = newRoot
            }
        }
    }

    fun getHeight(node: BPlusTreeNode? = root): Int {
        if (node == null) return 0
        if (node.isLeaf) return 1
        return 1 + getHeight(node.children.firstOrNull())
    }
}

@Composable
fun BPlusTreeScreen() {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val green4 = colorResource(id = R.color.green4)

    var order by remember { mutableIntStateOf(3) }
    var tree by remember { mutableStateOf(BPlusTree(3)) }
    var treeVersion by remember { mutableIntStateOf(0) }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    var recentlyAddedValue by remember { mutableIntStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.homebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))

        if (isPortrait) {
            // Orientation Overlay for first-time or portrait
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)).padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_rotate), // Ensure you have this drawable
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Rotate to Landscape for a better B+ Tree view",
                        color = Color.White,
                        fontFamily = cantoraFont,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }
        }

        val contentModifier = Modifier.fillMaxSize().padding(16.dp)

        if (!isPortrait) {
            Row(modifier = contentModifier) {
                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                        .glassmorphic(RoundedCornerShape(24.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text("B+ Tree", fontFamily = cantoraFont, fontSize = 32.sp, color = Color.White)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextField(
                        value = input,
                        onValueChange = { if (it.all { c -> c.isDigit() }) input = it },
                        placeholder = { Text("Value") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (input.isNotEmpty()) {
                                tree.insert(input.toInt())
                                recentlyAddedValue = input.toInt()
                                treeVersion++
                                input = ""
                                scope.launch { delay(600); recentlyAddedValue = -1 }
                            }
                        })
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(onClick = {
                        if (input.isNotEmpty()) {
                            tree.insert(input.toInt())
                            recentlyAddedValue = input.toInt()
                            treeVersion++
                            input = ""
                            scope.launch { delay(600); recentlyAddedValue = -1 }
                        }
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = green4)) {
                        Text("Insert", fontFamily = cantoraFont)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(onClick = {
                        tree = BPlusTree(order)
                        treeVersion++
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                        Text("Clear", fontFamily = cantoraFont)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                BPlusTreeVisualizerPane(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    tree = tree,
                    treeVersion = treeVersion,
                    addedVal = recentlyAddedValue,
                    order = order,
                    cantoraFont = cantoraFont
                )
            }
        }
    }
}

@Composable
fun BPlusTreeVisualizerPane(
    modifier: Modifier,
    tree: BPlusTree,
    treeVersion: Int,
    addedVal: Int,
    order: Int,
    cantoraFont: FontFamily
) {
    var userScale by remember(treeVersion) { mutableFloatStateOf(1f) }
    var offset by remember(treeVersion) { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .pointerInput(treeVersion) {
                detectTransformGestures { _, pan, zoom, _ ->
                    userScale = (userScale * zoom).coerceIn(0.5f, 5f)
                    offset += pan
                }
            }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth.toFloat()
            val treeHeight = tree.getHeight()
            val autoScale = (width / (Math.pow(order.toDouble(), treeHeight.toDouble() - 1) * 120 + 200)).coerceIn(0.1, 1.0).toFloat()
            val combinedScale = autoScale * userScale

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = combinedScale, scaleY = combinedScale,
                        translationX = offset.x, translationY = offset.y,
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.1f)
                    )
            ) {
                tree.root?.let { root ->
                    DrawBPlusNodes(root, width / 2, 100f, width * 0.35f, 180f, addedVal, 0, order.toFloat() * 0.7f)
                }
            }
        }
    }
}

@Composable
fun DrawBPlusNodes(node: BPlusTreeNode, x: Float, y: Float, xOffset: Float, yOffset: Float, addedVal: Int, level: Int, reduction: Float) {
    val density = LocalDensity.current
    
    // Draw connections
    if (!node.isLeaf) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val startX = x - (xOffset * (node.children.size - 1) / 2)
            node.children.forEachIndexed { i, _ ->
                drawLine(Color.White.copy(0.3f), Offset(x, y), Offset(startX + i * xOffset, y + yOffset), 2f)
            }
        }
        val startX = x - (xOffset * (node.children.size - 1) / 2)
        node.children.forEachIndexed { i, child ->
            DrawBPlusNodes(child, startX + i * xOffset, y + yOffset, xOffset / reduction, yOffset, addedVal, level + 1, reduction)
        }
    } else if (node.next != null) {
        // Leaf links (the "+" in B+ Tree)
        Canvas(modifier = Modifier.fillMaxSize()) {
            // This is a simplified horizontal link representation
        }
    }

    // Draw node
    Box(modifier = Modifier.offset(x = with(density) { x.toDp() }, y = with(density) { y.toDp() })) {
        Row(
            modifier = Modifier
                .background(if (node.isLeaf) Color(0xFF1B5E20) else Color(0xFF0D47A1), RoundedCornerShape(4.dp))
                .border(1.dp, Color.White.copy(0.5f), RoundedCornerShape(4.dp))
                .padding(4.dp)
        ) {
            node.keys.forEach { key ->
                Text(
                    key.toString(),
                    color = if (key == addedVal) Color.Yellow else Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
