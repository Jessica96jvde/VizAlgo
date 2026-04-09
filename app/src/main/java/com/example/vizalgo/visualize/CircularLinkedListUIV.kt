package com.example.vizalgo.visualize

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R
import com.example.vizalgo.utils.glassmorphic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularLinkedListScreen() {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    var list by remember { mutableStateOf(listOf<Int>()) }
    var input by remember { mutableStateOf("") }
    var posInput by remember { mutableStateOf("") }
    val green4 = colorResource(id = R.color.green4)
    val specialLinkColor = Color(0xFFFF9800) // Orange for Tail -> Head
    
    var recentlyAddedIndex by remember { mutableStateOf(-1) }
    var recentlyDeletedIndex by remember { mutableStateOf(-1) }

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.homebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Lighter overlay for better visibility of nodes and arrows
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                text = "Circular Linked List",
                fontFamily = cantoraFont,
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // LIST DISPLAY AREA - Circular Ring Layout
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (list.isEmpty()) {
                    Text("List is empty", color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp, fontFamily = cantoraFont)
                } else {
                    val density = LocalDensity.current
                    BoxWithConstraints(modifier = Modifier.size(340.dp)) {
                        val fullWidthPx = with(density) { maxWidth.toPx() }
                        val fullHeightPx = with(density) { maxHeight.toPx() }
                        val centerX = fullWidthPx / 2f
                        val centerY = fullHeightPx / 2f
                        val ringRadius = fullWidthPx * 0.35f // Slightly larger ring to give more space
                        
                        // Dynamic node size based on list length
                        val dynamicNodeSize = when {
                            list.size <= 5 -> 50.dp
                            list.size <= 7 -> 42.dp
                            list.size <= 10 -> 36.dp
                            else -> 30.dp
                        }
                        val nodeRadiusPx = with(density) { (dynamicNodeSize / 2).toPx() }
                        
                        // 1. Draw Curved Arrows
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (list.size == 1) {
                                // Self-loop arrow (Tail points to Head)
                                val nodeAngle = -90f
                                val nodeRad = Math.toRadians(nodeAngle.toDouble())
                                val nodeX = centerX + ringRadius * cos(nodeRad).toFloat()
                                val nodeY = centerY + ringRadius * sin(nodeRad).toFloat()
                                
                                val loopRadius = 22.dp.toPx()
                                val loopCenterX = nodeX
                                val loopCenterY = nodeY - nodeRadiusPx - loopRadius + 8.dp.toPx()
                                
                                drawArc(
                                    color = specialLinkColor,
                                    startAngle = 45f,
                                    sweepAngle = 270f,
                                    useCenter = false,
                                    topLeft = Offset(loopCenterX - loopRadius, loopCenterY - loopRadius),
                                    size = Size(loopRadius * 2, loopRadius * 2),
                                    style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f))
                                )
                                
                                // Arrow head for self-loop
                                val endAngleRad = Math.toRadians(45.0)
                                val endX = loopCenterX + loopRadius * cos(endAngleRad).toFloat()
                                val endY = loopCenterY + loopRadius * sin(endAngleRad).toFloat()
                                val arrowDirAngle = Math.toRadians(45.0 + 90.0)
                                val arrowLength = 10.dp.toPx()
                                drawLine(color = specialLinkColor, start = Offset(endX, endY), end = Offset(endX - arrowLength * cos(arrowDirAngle - 0.5).toFloat(), endY - arrowLength * sin(arrowDirAngle - 0.5).toFloat()), strokeWidth = 3.dp.toPx())
                                drawLine(color = specialLinkColor, start = Offset(endX, endY), end = Offset(endX - arrowLength * cos(arrowDirAngle + 0.5).toFloat(), endY - arrowLength * sin(arrowDirAngle + 0.5).toFloat()), strokeWidth = 3.dp.toPx())
                            } else {
                                list.forEachIndexed { index, _ ->
                                    val startAngle = (index * 360f / list.size) - 90f
                                    val endAngle = ((index + 1) * 360f / list.size) - 90f
                                    
                                    val isTailToHead = index == list.size - 1
                                    val color = if (isTailToHead) specialLinkColor else green4
                                    val strokeStyle = if (isTailToHead) Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)) else Stroke(width = 3.dp.toPx())

                                    // Curve control point
                                    val midAngle = (startAngle + endAngle) / 2f
                                    val midRad = Math.toRadians(midAngle.toDouble())
                                    
                                    // Make arrows straight if more than 12 nodes (except potentially Tail-to-Head)
                                    val useStraightLine = list.size > 12
                                    
                                    val controlRadius = when {
                                        isTailToHead && !useStraightLine -> ringRadius * 1.6f
                                        isTailToHead && useStraightLine -> ringRadius * 1.15f // Slight curve for Tail-to-Head even when many nodes
                                        else -> ringRadius * 1.3f
                                    }
                                    
                                    val controlX = centerX + controlRadius * cos(midRad).toFloat()
                                    val controlY = centerY + controlRadius * sin(midRad).toFloat()

                                    // Angle offset to ensure arrows start/end at node edges
                                    val angleGap = Math.toDegrees(asin(((nodeRadiusPx + 6.dp.toPx()) / ringRadius).toDouble())).toFloat()
                                    val adjStartAngle = startAngle + angleGap
                                    val adjEndAngle = endAngle - angleGap
                                    
                                    val adjStartRad = Math.toRadians(adjStartAngle.toDouble())
                                    val adjEndRad = Math.toRadians(adjEndAngle.toDouble())
                                    val adjStartX = centerX + ringRadius * cos(adjStartRad).toFloat()
                                    val adjStartY = centerY + ringRadius * sin(adjStartRad).toFloat()
                                    val adjEndX = centerX + ringRadius * cos(adjEndRad).toFloat()
                                    val adjEndY = centerY + ringRadius * sin(adjEndRad).toFloat()
                                    
                                    val path = Path().apply {
                                        moveTo(adjStartX, adjStartY)
                                        if (useStraightLine && !isTailToHead) {
                                            lineTo(adjEndX, adjEndY)
                                        } else {
                                            quadraticTo(controlX, controlY, adjEndX, adjEndY)
                                        }
                                    }
                                    drawPath(path, color = color, style = strokeStyle)
                                    
                                    // Arrow head
                                    val refX = if (useStraightLine && !isTailToHead) adjStartX else controlX
                                    val refY = if (useStraightLine && !isTailToHead) adjStartY else controlY
                                    val angleRad = Math.atan2((adjEndY - refY).toDouble(), (adjEndX - refX).toDouble())
                                    val arrowLength = 15.dp.toPx()
                                    drawLine(color = color, start = Offset(adjEndX, adjEndY), end = Offset((adjEndX - arrowLength * cos(angleRad - 0.5)).toFloat(), (adjEndY - arrowLength * sin(angleRad - 0.5)).toFloat()), strokeWidth = 3.dp.toPx())
                                    drawLine(color = color, start = Offset(adjEndX, adjEndY), end = Offset((adjEndX - arrowLength * cos(angleRad + 0.5)).toFloat(), (adjEndY - arrowLength * sin(angleRad + 0.5)).toFloat()), strokeWidth = 3.dp.toPx())
                                }
                            }
                        }

                        // 2. Draw Nodes and Labels
                        list.forEachIndexed { index, value ->
                            val angle = (index * 360f / list.size) - 90f
                            val rad = Math.toRadians(angle.toDouble())
                            val xPx = centerX + ringRadius * cos(rad).toFloat()
                            val yPx = centerY + ringRadius * sin(rad).toFloat()

                            // Node
                            Box(
                                modifier = Modifier
                                    .size(dynamicNodeSize)
                                    .offset { IntOffset((xPx - nodeRadiusPx).toInt(), (yPx - nodeRadiusPx).toInt()) }
                                    .background(
                                        if (index == recentlyAddedIndex) Color.Blue.copy(alpha = 0.35f) 
                                        else if (index == recentlyDeletedIndex) Color.Red.copy(alpha = 0.35f)
                                        else Color.White.copy(alpha = 0.15f),
                                        CircleShape
                                    )
                                    .border(
                                        2.dp, 
                                        if (index == recentlyAddedIndex) Color.Blue 
                                        else if (index == recentlyDeletedIndex) Color.Red 
                                        else green4, 
                                        CircleShape
                                    )
                                    .glassmorphic(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = value.toString(), 
                                    color = Color.White, 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = if (list.size > 8) 12.sp else 16.sp
                                )
                            }

                            // Labels (Outside the circle)
                            val isHead = index == 0
                            val isTail = index == list.size - 1
                            if (isHead || isTail) {
                                val labelDistancePx = with(density) { 50.dp.toPx() }
                                val lxPx = centerX + (ringRadius + labelDistancePx) * cos(rad).toFloat()
                                val lyPx = centerY + (ringRadius + labelDistancePx) * sin(rad).toFloat()
                                
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .offset { 
                                            IntOffset(
                                                (lxPx - with(density) { 40.dp.toPx() }).toInt(), 
                                                (lyPx - with(density) { 25.dp.toPx() }).toInt()
                                            ) 
                                        }
                                ) {
                                    val labelText = buildAnnotatedString {
                                        if (isHead && isTail) {
                                            withStyle(style = SpanStyle(color = Color.Cyan)) { append("Head") }
                                            withStyle(style = SpanStyle(color = Color.White)) { append("/") }
                                            withStyle(style = SpanStyle(color = Color.Yellow)) { append("Tail") }
                                        } else if (isHead) {
                                            withStyle(style = SpanStyle(color = Color.Cyan)) { append("Head") }
                                        } else {
                                            withStyle(style = SpanStyle(color = Color.Yellow)) { append("Tail") }
                                        }
                                    }

                                    val pointingUp = lyPx > yPx
                                    Icon(
                                        imageVector = if (pointingUp) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = if (isHead) Color.Cyan else Color.Yellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = labelText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = cantoraFont
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // EDUCATIONAL NOTE
            Text(
                text = "Notice the special connection from Tail back to Head, completing the circular structure.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp).padding(horizontal = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontFamily = cantoraFont
            )

            // CONTROLS PANEL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .glassmorphic(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // INPUTS ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = input,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 4) input = it },
                            placeholder = { Text("Value", color = Color.White.copy(alpha = 0.4f)) },
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedIndicatorColor = green4
                            ),
                            singleLine = true
                        )

                        TextField(
                            value = posInput,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 2) posInput = it },
                            placeholder = { Text("Pos", color = Color.White.copy(alpha = 0.4f)) },
                            modifier = Modifier.width(70.dp).padding(horizontal = 4.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedIndicatorColor = green4
                            ),
                            singleLine = true
                        )

                        Button(
                            onClick = { list = emptyList(); input = ""; posInput = "" },
                            modifier = Modifier.padding(start = 4.dp).height(54.dp).width(75.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                        ) {
                            Text("Clear", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ACTION BUTTONS GRID (3x2)
                    Column {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ActionButton("Add Head", green4, Modifier.weight(1f)) {
                                if (input.isNotEmpty()) {
                                    val newVal = input.toInt()
                                    list = listOf(newVal) + list
                                    input = ""
                                    recentlyAddedIndex = 0
                                    scope.launch { delay(800); recentlyAddedIndex = -1 }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ActionButton("Add Tail", green4, Modifier.weight(1f)) {
                                if (input.isNotEmpty()) {
                                    val newVal = input.toInt()
                                    list = list + newVal
                                    input = ""
                                    recentlyAddedIndex = list.lastIndex
                                    scope.launch { delay(800); recentlyAddedIndex = -1 }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ActionButton("Insert Pos", green4, Modifier.weight(1f)) {
                                if (input.isNotEmpty() && posInput.isNotEmpty()) {
                                    val newVal = input.toInt()
                                    val pos = posInput.toInt().coerceIn(0, list.size)
                                    val mutableList = list.toMutableList()
                                    mutableList.add(pos, newVal)
                                    list = mutableList
                                    input = ""; posInput = ""; recentlyAddedIndex = pos
                                    scope.launch { delay(800); recentlyAddedIndex = -1 }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ActionButton("Delete Front", Color(0xFFD32F2F), Modifier.weight(1f)) {
                                if (list.isNotEmpty()) {
                                    recentlyDeletedIndex = 0
                                    scope.launch { delay(600); list = list.drop(1); recentlyDeletedIndex = -1 }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ActionButton("Delete Back", Color(0xFFD32F2F), Modifier.weight(1f)) {
                                if (list.isNotEmpty()) {
                                    recentlyDeletedIndex = list.lastIndex
                                    scope.launch { delay(600); list = list.dropLast(1); recentlyDeletedIndex = -1 }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ActionButton("Delete Pos", Color(0xFFD32F2F), Modifier.weight(1f)) {
                                if (posInput.isNotEmpty()) {
                                    val pos = posInput.toInt()
                                    if (pos in list.indices) {
                                        recentlyDeletedIndex = pos
                                        scope.launch { delay(600); val m = list.toMutableList(); m.removeAt(pos); list = m; posInput = ""; recentlyDeletedIndex = -1 }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1)
    }
}
