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

// B-Tree Logic (Order m based)
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

    fun getHeight(node: BTreeNode? = root): Int {
        if (node == null) return 0
        if (node.leaf) return 1
        return 1 + getHeight(node.children.firstOrNull())
    }
}

@Composable
fun BTreeScreen() {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val green4 = colorResource(id = R.color.green4)

    var order by remember { mutableIntStateOf(3) }
    var bTree by remember { mutableStateOf(BTree(3)) }
    var rootState by remember { mutableStateOf<BTreeNode?>(null) }
    var treeVersion by remember { mutableIntStateOf(0) }
    
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    var recentlyAddedValue by remember { mutableIntStateOf(-1) }
    var recentlyDeletedValue by remember { mutableIntStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Shared Background
        Image(
            painter = painterResource(id = R.drawable.homebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        if (isPortrait) {
            // Orientation Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(32.dp)
                    .clickable(enabled = false) {}, // Block interaction
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_rotate),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Rotate to Landscape for a better B-Tree view",
                        color = Color.White,
                        fontFamily = cantoraFont,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }
        }

        // ADAPTIVE LAYOUT: Row for Landscape, Column for Portrait
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

        if (isPortrait) {
            Column(modifier = contentModifier) {
                // TOP PANEL: CONTROLS (Horizontal scroll for buttons)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .glassmorphic(RoundedCornerShape(24.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    BTreeControls(
                        order = order,
                        onOrderChange = { 
                            order = it
                            bTree = BTree(it)
                            rootState = null
                            treeVersion++
                        },
                        input = input,
                        onInputChange = { input = it },
                        onInsert = {
                            if (input.isNotEmpty()) {
                                val v = input.toInt()
                                bTree.insert(v)
                                rootState = bTree.root
                                treeVersion++
                                recentlyAddedValue = v
                                input = ""
                                scope.launch { delay(600); recentlyAddedValue = -1 }
                            }
                        },
                        onDelete = {
                            if (input.isNotEmpty()) {
                                val v = input.toInt()
                                recentlyDeletedValue = v
                                scope.launch {
                                    delay(400); bTree.remove(v); rootState = bTree.root; treeVersion++; recentlyDeletedValue = -1
                                }
                                input = ""
                            }
                        },
                        onClear = {
                            bTree = BTree(order); rootState = null; treeVersion++; input = ""
                        },
                        green4 = green4,
                        cantoraFont = cantoraFont
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BOTTOM PANEL: VISUALIZER
                BTreeVisualizerPane(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    bTree = bTree,
                    rootState = rootState,
                    treeVersion = treeVersion,
                    addedVal = recentlyAddedValue,
                    deletedVal = recentlyDeletedValue,
                    order = order,
                    cantoraFont = cantoraFont
                )
            }
        } else {
            Row(modifier = contentModifier) {
                // LEFT PANEL: CONTROLS
                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                        .glassmorphic(RoundedCornerShape(24.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    BTreeControls(
                        order = order,
                        onOrderChange = { 
                            order = it
                            bTree = BTree(it)
                            rootState = null
                            treeVersion++
                        },
                        input = input,
                        onInputChange = { input = it },
                        onInsert = {
                            if (input.isNotEmpty()) {
                                val v = input.toInt()
                                bTree.insert(v)
                                rootState = bTree.root
                                treeVersion++
                                recentlyAddedValue = v
                                input = ""
                                scope.launch { delay(600); recentlyAddedValue = -1 }
                            }
                        },
                        onDelete = {
                            if (input.isNotEmpty()) {
                                val v = input.toInt()
                                recentlyDeletedValue = v
                                scope.launch {
                                    delay(400); bTree.remove(v); rootState = bTree.root; treeVersion++; recentlyDeletedValue = -1
                                }
                                input = ""
                            }
                        },
                        onClear = {
                            bTree = BTree(order); rootState = null; treeVersion++; input = ""
                        },
                        green4 = green4,
                        cantoraFont = cantoraFont
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT PANEL: VISUALIZER
                BTreeVisualizerPane(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    bTree = bTree,
                    rootState = rootState,
                    treeVersion = treeVersion,
                    addedVal = recentlyAddedValue,
                    deletedVal = recentlyDeletedValue,
                    order = order,
                    cantoraFont = cantoraFont
                )
            }
        }
    }
}

@Composable
fun BTreeControls(
    order: Int,
    onOrderChange: (Int) -> Unit,
    input: String,
    onInputChange: (String) -> Unit,
    onInsert: () -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    green4: Color,
    cantoraFont: FontFamily
) {
    Text(
        text = "B-Tree",
        fontFamily = cantoraFont,
        fontSize = 32.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Order (m): ",
            fontFamily = cantoraFont,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { if (order > 3) onOrderChange(order - 1) }) {
            Text("-", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            text = order.toString(),
            color = green4,
            fontFamily = cantoraFont,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        IconButton(onClick = { if (order < 6) onOrderChange(order + 1) }) {
            Text("+", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    TextField(
        value = input,
        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 3) onInputChange(it) },
        placeholder = { Text("Value (0-999)", color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedIndicatorColor = green4,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onInsert() })
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onInsert,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = green4),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("Insert Key", fontFamily = cantoraFont, fontSize = 16.sp)
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onDelete,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Delete", fontFamily = cantoraFont, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onClear,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Clear", fontFamily = cantoraFont, fontSize = 14.sp)
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Text(
        text = "Optimized for B-Tree Order $order",
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 12.sp,
        fontFamily = cantoraFont,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun BTreeVisualizerPane(
    modifier: Modifier,
    bTree: BTree,
    rootState: BTreeNode?,
    treeVersion: Int,
    addedVal: Int,
    deletedVal: Int,
    order: Int,
    cantoraFont: FontFamily
) {
    var userScale by remember(treeVersion) { mutableFloatStateOf(1f) }
    var offset by remember(treeVersion) { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .pointerInput(treeVersion) {
                detectTransformGestures { _, pan, zoom, _ ->
                    userScale = (userScale * zoom).coerceIn(0.5f, 8f)
                    offset += pan
                }
            }
            .pointerInput(treeVersion) {
                detectTapGestures(onDoubleTap = {
                    userScale = 1f
                    offset = Offset.Zero
                })
            }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            val treeHeight = bTree.getHeight()
            
            // MATH FOR CENTERING AND SCALING
            val m = order.toDouble()
            val h = treeHeight.toDouble()
            
            // Estimate space needed to keep the structure centered correctly
            val reqW = (m.pow(h - 1.0) * 100.0) + 200.0
            val reqH = (h * 170.0) + 100.0
            
            val autoScale = minOf(
                (width / reqW).coerceIn(0.2, 1.0),
                (height / reqH).coerceIn(0.3, 1.0)
            ).toFloat()

            val combinedScale = autoScale * userScale

            key(treeVersion) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = combinedScale,
                            scaleY = combinedScale,
                            translationX = offset.x,
                            translationY = offset.y + (1f - autoScale) * -100f * userScale,
                            transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.2f)
                        )
                ) {
                    rootState?.let { root ->
                        // PASS AUTO SCALE for stable layout during zoom
                        BTreeLayoutLandscape(root, width, addedVal, deletedVal, order, autoScale)
                    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Insert values to build the tree", color = Color.White.copy(alpha = 0.3f), fontFamily = cantoraFont, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BTreeLayoutLandscape(root: BTreeNode, actualWidth: Float, addedVal: Int, deletedVal: Int, order: Int, scale: Float) {
    // initialX MUST be the center of the actual container to stay centered during scaling
    val initialX = actualWidth / 2
    val initialY = 120f
    
    // We increase the spacing based on how much we've scaled down to keep nodes distinct
    val spreadFactor = (1.0f / scale).coerceIn(1.0f, 3.0f)
    val initialXOffset = (actualWidth * 0.35f * spreadFactor).coerceAtMost(actualWidth * 1.5f)
    
    val reductionFactor = (order * 0.75f).coerceAtLeast(2.4f)
    val yOffset = 170f

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawBTreeConnectionsLandscape(this, root, initialX, initialY, initialXOffset, yOffset, reductionFactor)
    }
    DrawBTreeNodesLandscape(root, initialX, initialY, initialXOffset, yOffset, addedVal, deletedVal, 0, reductionFactor)
}

private fun drawBTreeConnectionsLandscape(scope: DrawScope, node: BTreeNode, x: Float, y: Float, xOffset: Float, yOffset: Float, reductionFactor: Float) {
    if (node.children.isNotEmpty()) {
        val startX = x - (xOffset * (node.children.size - 1) / 2)
        node.children.forEachIndexed { i, child ->
            val childX = startX + i * xOffset
            scope.drawLine(
                color = Color.White.copy(alpha = 0.25f),
                start = Offset(x, y),
                end = Offset(childX, y + yOffset),
                strokeWidth = 2.2f
            )
            drawBTreeConnectionsLandscape(scope, child, childX, y + yOffset, xOffset / reductionFactor, yOffset, reductionFactor)
        }
    }
}

@Composable
fun DrawBTreeNodesLandscape(node: BTreeNode, x: Float, y: Float, xOffset: Float, yOffset: Float, addedVal: Int, deletedVal: Int, level: Int, reductionFactor: Float) {
    val density = LocalDensity.current
    val fontSize = (14.0 - (level * 1.2)).coerceAtLeast(10.0).sp
    val keyPadding = (6 - level).coerceAtLeast(3).dp

    Box(modifier = Modifier.offset(x = with(density) { x.toDp() }, y = with(density) { y.toDp() })) {
        androidx.compose.ui.layout.Layout(
            content = {
                Row(
                    modifier = Modifier
                        .background(Color(0xFF2E7D32).copy(alpha = 0.95f), RoundedCornerShape(4.dp))
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
                            Text(text = key.toString(), color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.cantora_one)))
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
        val startX = x - (xOffset * (node.children.size - 1) / 2)
        node.children.forEachIndexed { i, child ->
            val childX = startX + i * xOffset
            DrawBTreeNodesLandscape(child, childX, y + yOffset, xOffset / reductionFactor, yOffset, addedVal, deletedVal, level + 1, reductionFactor)
        }
    }
}
