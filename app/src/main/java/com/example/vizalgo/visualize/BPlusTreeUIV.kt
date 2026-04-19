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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
    var next: BPlusTreeNode? = null 

    fun insert(k: Int): Pair<Int, BPlusTreeNode>? {
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

    fun remove(k: Int) {
        root?.remove(k)
        if (root?.keys?.isEmpty() == true) {
            root = if (root!!.isLeaf) null else root!!.children[0]
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
    var rootState by remember { mutableStateOf<BPlusTreeNode?>(null) }
    var treeVersion by remember { mutableIntStateOf(0) }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var recentlyAddedValue by remember { mutableIntStateOf(-1) }
    var recentlyDeletedValue by remember { mutableIntStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.homebg), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        if (isPortrait) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)).padding(32.dp).clickable(enabled = false) {}, contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(id = R.drawable.ic_rotate), contentDescription = null, tint = Color.White, modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Rotate to Landscape for a better B+ Tree view", color = Color.White, fontFamily = cantoraFont, textAlign = TextAlign.Center, fontSize = 20.sp)
                }
            }
        }

        val contentModifier = Modifier.fillMaxSize().padding(16.dp)
        if (isPortrait) {
            Column(modifier = contentModifier) {
                Column(modifier = Modifier.fillMaxWidth().height(260.dp).glassmorphic(RoundedCornerShape(24.dp)).verticalScroll(rememberScrollState()).padding(16.dp)) {
                    BPlusTreeControls(order, { order = it; tree = BPlusTree(it); rootState = null; treeVersion++ }, input, { input = it }, {
                        if (input.isNotEmpty()) {
                            val v = input.toInt(); tree.insert(v); rootState = tree.root; treeVersion++; recentlyAddedValue = v; input = ""; scope.launch { delay(600); recentlyAddedValue = -1 }
                        }
                    }, {
                        if (input.isNotEmpty()) {
                            val v = input.toInt(); recentlyDeletedValue = v; scope.launch { delay(400); tree.remove(v); rootState = tree.root; treeVersion++; recentlyDeletedValue = -1 }; input = ""
                        }
                    }, { tree = BPlusTree(order); rootState = null; treeVersion++; input = "" }, green4, cantoraFont)
                }
                Spacer(modifier = Modifier.height(16.dp))
                BPlusTreeVisualizerPane(Modifier.weight(1f).fillMaxWidth(), tree, rootState, treeVersion, recentlyAddedValue, recentlyDeletedValue, order, cantoraFont)
            }
        } else {
            Row(modifier = contentModifier) {
                Column(modifier = Modifier.width(280.dp).fillMaxHeight().glassmorphic(RoundedCornerShape(24.dp)).verticalScroll(rememberScrollState()).padding(20.dp)) {
                    BPlusTreeControls(order, { order = it; tree = BPlusTree(it); rootState = null; treeVersion++ }, input, { input = it }, {
                        if (input.isNotEmpty()) {
                            val v = input.toInt(); tree.insert(v); rootState = tree.root; treeVersion++; recentlyAddedValue = v; input = "" ; scope.launch { delay(600); recentlyAddedValue = -1 }
                        }
                    }, {
                        if (input.isNotEmpty()) {
                            val v = input.toInt(); recentlyDeletedValue = v; scope.launch { delay(400); tree.remove(v); rootState = tree.root; treeVersion++; recentlyDeletedValue = -1 }; input = ""
                        }
                    }, { tree = BPlusTree(order); rootState = null; treeVersion++; input = "" }, green4, cantoraFont)
                }
                Spacer(modifier = Modifier.width(16.dp))
                BPlusTreeVisualizerPane(Modifier.weight(1f).fillMaxHeight(), tree, rootState, treeVersion, recentlyAddedValue, recentlyDeletedValue, order, cantoraFont)
            }
        }
    }
}

@Composable
fun BPlusTreeControls(order: Int, onOrderChange: (Int) -> Unit, input: String, onInputChange: (String) -> Unit, onInsert: () -> Unit, onDelete: () -> Unit, onClear: () -> Unit, green4: Color, cantoraFont: FontFamily) {
    Text("B+ Tree", fontFamily = cantoraFont, fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Order (m): ", fontFamily = cantoraFont, fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { if (order > 3) onOrderChange(order - 1) }) { Text("-", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
        Text(text = order.toString(), color = green4, fontFamily = cantoraFont, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        IconButton(onClick = { if (order < 6) onOrderChange(order + 1) }) { Text("+", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
    }
    Spacer(modifier = Modifier.height(16.dp))
    TextField(value = input, onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 3) onInputChange(it) }, placeholder = { Text("Value (0-999)", color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color.White.copy(alpha = 0.1f), unfocusedContainerColor = Color.White.copy(alpha = 0.05f), focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color.White, focusedIndicatorColor = green4), shape = RoundedCornerShape(12.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { onInsert() }))
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = onInsert, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = green4), shape = RoundedCornerShape(12.dp)) { Text("Insert Key", fontFamily = cantoraFont, fontSize = 16.sp) }
    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onDelete, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), shape = RoundedCornerShape(12.dp)) { Text("Delete", fontFamily = cantoraFont, fontSize = 14.sp) }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onClear, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)), shape = RoundedCornerShape(12.dp)) { Text("Clear", fontFamily = cantoraFont, fontSize = 14.sp) }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Internal Nodes (Blue): Routing Keys\nLeaf Nodes (Green): Actual Data\nDashed Yellow Line: Leaf Sequence", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontFamily = cantoraFont, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
}

@Composable
fun BPlusTreeVisualizerPane(modifier: Modifier, tree: BPlusTree, rootState: BPlusTreeNode?, treeVersion: Int, addedVal: Int, deletedVal: Int, order: Int, cantoraFont: FontFamily) {
    var userScale by remember(treeVersion) { mutableFloatStateOf(1f) }
    var offset by remember(treeVersion) { mutableStateOf(Offset.Zero) }
    Box(modifier = modifier.clip(RoundedCornerShape(24.dp)).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)).pointerInput(treeVersion) { detectTransformGestures { _, pan, zoom, _ -> userScale = (userScale * zoom).coerceIn(0.5f, 8f); offset += pan } }.pointerInput(treeVersion) { detectTapGestures(onDoubleTap = { userScale = 1f; offset = Offset.Zero }) }) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth.toFloat(); val height = constraints.maxHeight.toFloat(); val treeHeight = tree.getHeight()
            val m = order.toDouble(); val h = treeHeight.toDouble()
            val reqW = (m.pow(h - 1.0) * 100.0) + 200.0; val reqH = (h * 170.0) + 100.0
            val autoScale = minOf((width / reqW).coerceIn(0.2, 1.0), (height / reqH).coerceIn(0.3, 1.0)).toFloat()
            val combinedScale = autoScale * userScale
            key(treeVersion) {
                Box(modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = combinedScale, scaleY = combinedScale, translationX = offset.x, translationY = offset.y + (1f - autoScale) * -100f * userScale, transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.2f))) {
                    rootState?.let { root -> BPlusTreeLayoutLandscape(root, width, addedVal, deletedVal, order, autoScale) } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Insert values to build the B+ Tree", color = Color.White.copy(alpha = 0.3f), fontFamily = cantoraFont, fontSize = 20.sp) }
                }
            }
        }
    }
}

@Composable
fun BPlusTreeLayoutLandscape(root: BPlusTreeNode, actualWidth: Float, addedVal: Int, deletedVal: Int, order: Int, scale: Float) {
    val initialX = actualWidth / 2; val initialY = 120f
    val spreadFactor = (1.0f / scale).coerceIn(1.0f, 3.0f); val initialXOffset = (actualWidth * 0.35f * spreadFactor).coerceAtMost(actualWidth * 1.5f)
    val reductionFactor = (order * 0.75f).coerceAtLeast(2.4f); val yOffset = 170f
    
    val leafPositions = remember { mutableStateListOf<Offset>() }
    LaunchedEffect(root) { leafPositions.clear() }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawBPlusConnections(this, root, initialX, initialY, initialXOffset, yOffset, reductionFactor)
        
        // Leaf sequence links
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
    
    DrawBPlusNodesLandscape(root, initialX, initialY, initialXOffset, yOffset, addedVal, deletedVal, 0, reductionFactor) { pos ->
        leafPositions.add(pos)
    }
}

private fun drawBPlusConnections(scope: DrawScope, node: BPlusTreeNode, x: Float, y: Float, xOffset: Float, yOffset: Float, reductionFactor: Float) {
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
fun DrawBPlusNodesLandscape(node: BPlusTreeNode, x: Float, y: Float, xOffset: Float, yOffset: Float, addedVal: Int, deletedVal: Int, level: Int, reductionFactor: Float, onLeafPosition: (Offset) -> Unit) {
    val density = LocalDensity.current; val fontSize = (14.0 - (level * 1.2)).coerceAtLeast(10.0).sp; val keyPadding = (6 - level).coerceAtLeast(3).dp
    
    if (node.isLeaf) {
        SideEffect { onLeafPosition(Offset(x, y)) }
    }

    Box(modifier = Modifier.offset(x = with(density) { x.toDp() }, y = with(density) { y.toDp() })) {
        androidx.compose.ui.layout.Layout(content = {
            Row(modifier = Modifier.background(if (node.isLeaf) Color(0xFF2E7D32).copy(alpha = 0.95f) else Color(0xFF1565C0).copy(alpha = 0.95f), RoundedCornerShape(4.dp)).border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(4.dp)), verticalAlignment = Alignment.CenterVertically) {
                node.keys.forEachIndexed { idx, key ->
                    Box(modifier = Modifier.padding(keyPadding).background(when (key) { addedVal -> Color.Blue.copy(alpha = 0.4f); deletedVal -> Color.Red.copy(alpha = 0.4f); else -> Color.Transparent }, RoundedCornerShape(2.dp)).padding(horizontal = 6.dp, vertical = 3.dp), contentAlignment = Alignment.Center) {
                        Text(text = key.toString(), color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.cantora_one)))
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
            DrawBPlusNodesLandscape(child, childX, y + yOffset, xOffset / reductionFactor, yOffset, addedVal, deletedVal, level + 1, reductionFactor, onLeafPosition)
        }
    }
}
