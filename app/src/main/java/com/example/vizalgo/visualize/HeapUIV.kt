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

enum class HeapType { MAX, MIN }

class Heap(private val type: HeapType) {
    private val heap = mutableListOf<Int>()

    fun getHeap(): List<Int> = heap.toList()

    private fun compare(a: Int, b: Int): Boolean {
        return if (type == HeapType.MAX) a > b else a < b
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

    fun clear() {
        heap.clear()
    }
}

@Composable
fun HeapScreen() {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val green4 = colorResource(id = R.color.green4)
    
    var showDialog by remember { mutableStateOf(true) }
    var heapType by remember { mutableStateOf(HeapType.MAX) }
    val heap = remember(heapType) { Heap(heapType) }
    
    var heapState by remember { mutableStateOf(listOf<Int>()) }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    var recentlyAddedValue by remember { mutableStateOf(-1) }
    var recentlyDeletedValue by remember { mutableStateOf(-1) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Choose Heap Type", fontFamily = cantoraFont) },
            text = { Text("Select whether you want to visualize a Max Heap or a Min Heap.", fontFamily = cantoraFont) },
            confirmButton = {
                Button(onClick = { 
                    heapType = HeapType.MAX
                    showDialog = false 
                }) {
                    Text("Max Heap")
                }
            },
            dismissButton = {
                Button(onClick = { 
                    heapType = HeapType.MIN
                    showDialog = false 
                }) {
                    Text("Min Heap")
                }
            },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f)
        )
    }

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
                text = if (heapType == HeapType.MAX) "Max Heap Visualizer" else "Min Heap Visualizer",
                fontFamily = cantoraFont,
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (heapState.isNotEmpty()) {
                    HeapLayout(heapState, recentlyAddedValue, recentlyDeletedValue)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Heap is empty", color = Color.White.copy(alpha = 0.5f), fontFamily = cantoraFont, fontSize = 20.sp)
                    }
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
                                heap.insert(valToInsert)
                                heapState = heap.getHeap()
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
                            if (heapState.isNotEmpty()) {
                                val valToDelete = heapState[0]
                                recentlyDeletedValue = valToDelete
                                scope.launch {
                                    delay(500)
                                    heap.extract()
                                    heapState = heap.getHeap()
                                    recentlyDeletedValue = -1
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text(if (heapType == HeapType.MAX) "Extract Max" else "Extract Min")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { 
                            heap.clear()
                            heapState = emptyList()
                            input = "" 
                        },
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
fun HeapLayout(heap: List<Int>, addedVal: Int, deletedVal: Int) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawHeapConnections(this, heap, 0, width / 2, 100f, width / 4, 150f)
        }
        
        DrawHeapNodes(heap, 0, width / 2, 100f, width / 4, 150f, addedVal, deletedVal, 0)
    }
}

private fun drawHeapConnections(
    scope: DrawScope,
    heap: List<Int>,
    index: Int,
    x: Float,
    y: Float,
    xOffset: Float,
    yOffset: Float
) {
    val left = 2 * index + 1
    val right = 2 * index + 2

    if (left < heap.size) {
        scope.drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(x, y),
            end = Offset(x - xOffset, y + yOffset),
            strokeWidth = 3f
        )
        drawHeapConnections(scope, heap, left, x - xOffset, y + yOffset, xOffset / 1.8f, yOffset)
    }
    if (right < heap.size) {
        scope.drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(x, y),
            end = Offset(x + xOffset, y + yOffset),
            strokeWidth = 3f
        )
        drawHeapConnections(scope, heap, right, x + xOffset, y + yOffset, xOffset / 1.8f, yOffset)
    }
}

@Composable
fun DrawHeapNodes(
    heap: List<Int>,
    index: Int,
    x: Float,
    y: Float,
    xOffset: Float,
    yOffset: Float,
    addedVal: Int,
    deletedVal: Int,
    level: Int
) {
    val green4 = colorResource(id = R.color.green4)
    val density = LocalDensity.current
    
    val nodeSize = (40 - (level * 4)).coerceAtLeast(24).dp
    val fontSize = (12 - level).coerceAtLeast(8).sp
    val halfSize = with(density) { (nodeSize / 2).toPx() }
    
    val isRecentlyAdded = addedVal != -1 && index < heap.size && heap[index] == addedVal
    val isRecentlyDeleted = deletedVal != -1 && index == 0
    
    val nodeGlowColor = when {
        isRecentlyAdded -> Color.Blue.copy(alpha = 0.5f)
        isRecentlyDeleted -> Color.Red.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .offset(
                x = with(density) { (x - halfSize).toDp() },
                y = with(density) { (y - halfSize).toDp() }
            )
            .size(nodeSize)
            .background(nodeGlowColor, CircleShape)
            .border(2.dp, if (nodeGlowColor != Color.Transparent) nodeGlowColor else Color.White.copy(alpha = 0.8f), CircleShape)
            .background(green4.copy(alpha = 0.9f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = heap[index].toString(),
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }

    val left = 2 * index + 1
    val right = 2 * index + 2
    
    if (left < heap.size) {
        DrawHeapNodes(heap, left, x - xOffset, y + yOffset, xOffset / 1.8f, yOffset, addedVal, deletedVal, level + 1)
    }
    if (right < heap.size) {
        DrawHeapNodes(heap, right, x + xOffset, y + yOffset, xOffset / 1.8f, yOffset, addedVal, deletedVal, level + 1)
    }
}
