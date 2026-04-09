package com.example.vizalgo.visualize

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R
import com.example.vizalgo.utils.glassmorphic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LinkedListScreen(isDoubly: Boolean = false) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    var list by remember { mutableStateOf(listOf<Int>()) }
    var input by remember { mutableStateOf("") }
    var posInput by remember { mutableStateOf("") }
    val green4 = colorResource(id = R.color.green4)
    
    var recentlyAddedIndex by remember { mutableStateOf(-1) }
    var recentlyDeletedIndex by remember { mutableStateOf(-1) }
    var searchedIndex by remember { mutableStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.homebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                text = if (isDoubly) "Doubly Linked List" else "Singly Linked List",
                fontFamily = cantoraFont,
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // LIST DISPLAY AREA - Horizontal Scroll
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (list.isEmpty()) {
                    Text("List is empty", color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp, fontFamily = cantoraFont)
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 40.dp, horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        list.forEachIndexed { index, value ->
                            val isLastInTree = index == list.lastIndex

                            NodeItem(
                                value = value,
                                isFirst = index == 0,
                                isLast = isLastInTree,
                                isDoubly = isDoubly,
                                green4 = green4,
                                glowColor = when {
                                    index == recentlyAddedIndex -> Color.Blue
                                    index == recentlyDeletedIndex -> Color.Red
                                    index == searchedIndex -> Color.Yellow
                                    else -> null
                                },
                                isReversed = false
                            )

                            if (!isLastInTree) {
                                // Horizontal Connector
                                ConnectorArrow(
                                    isForward = true,
                                    isDoubly = isDoubly,
                                    green4 = green4
                                )
                            }
                        }
                    }
                }
            }

            // EDUCATIONAL NOTE
            Text(
                text = if (isDoubly) 
                    "Doubly: Each node knows its 'Next' AND 'Previous' neighbor. Fast to go backwards!" 
                    else "Singly: Each node only knows its 'Next'. The 'Tail' is an optimization to find the end instantly.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp, start = 20.dp, end = 20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontFamily = cantoraFont
            )

            val scope = rememberCoroutineScope()

            // INPUT + BUTTONS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .glassmorphic(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // INPUT FIELDS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = input,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 4) input = it },
                            placeholder = { Text("Value", color = Color.White.copy(alpha = 0.5f)) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
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

                        TextField(
                            value = posInput,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 2) posInput = it },
                            placeholder = { Text("Pos", color = Color.White.copy(alpha = 0.5f)) },
                            modifier = Modifier
                                .width(80.dp)
                                .padding(horizontal = 4.dp),
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
                                    scope.launch { delay(500); recentlyAddedIndex = -1 }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ActionButton("Add Tail", green4, Modifier.weight(1f)) {
                                if (input.isNotEmpty()) {
                                    val newVal = input.toInt()
                                    list = list + newVal
                                    input = ""
                                    recentlyAddedIndex = list.lastIndex
                                    scope.launch { delay(500); recentlyAddedIndex = -1 }
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
                                    scope.launch { delay(500); recentlyAddedIndex = -1 }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ActionButton("Delete Front", Color(0xFFC54545), Modifier.weight(1f)) {
                                if (list.isNotEmpty()) {
                                    recentlyDeletedIndex = 0
                                    scope.launch { delay(500); list = list.drop(1); recentlyDeletedIndex = -1 }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ActionButton("Delete Back", Color(0xFFD32F2F), Modifier.weight(1f)) {
                                if (list.isNotEmpty()) {
                                    recentlyDeletedIndex = list.lastIndex
                                    scope.launch { delay(500); list = list.dropLast(1); recentlyDeletedIndex = -1 }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ActionButton("Delete Pos", Color(0xFFD32F2F), Modifier.weight(1f)) {
                                if (posInput.isNotEmpty()) {
                                    val pos = posInput.toInt()
                                    if (pos in list.indices) {
                                        recentlyDeletedIndex = pos
                                        scope.launch { delay(500); val m = list.toMutableList(); m.removeAt(pos); list = m; posInput = ""; recentlyDeletedIndex = -1 }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ActionButton("Search", Color(0xFFFFA000), Modifier.weight(1f)) {
                                if (input.isNotEmpty()) {
                                    val target = input.toIntOrNull()
                                    if (target != null) {
                                        val index = list.indexOf(target)
                                        if (index != -1) {
                                            searchedIndex = index
                                            scope.launch {
                                                delay(1500)
                                                searchedIndex = -1
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.weight(2f)) // Placeholder
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectorArrow(isForward: Boolean, isDoubly: Boolean, green4: Color) {
    Column(
        modifier = Modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isForward) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = green4,
            modifier = Modifier.size(20.dp)
        )
        if (isDoubly) {
            Icon(
                imageVector = if (isForward) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = green4,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun NodeItem(
    value: Int,
    isFirst: Boolean,
    isLast: Boolean,
    isDoubly: Boolean,
    green4: Color,
    glowColor: Color? = null,
    isReversed: Boolean = false
) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val showNullOnLeft = (isLast && isReversed) || (isFirst && isDoubly && !isReversed)
            val showNullOnRight = (isLast && !isReversed) || (isFirst && isDoubly && isReversed)

            if (showNullOnLeft) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("null", color = Color.White, fontSize = 7.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            // Node Box
            Box(
                modifier = Modifier
                    .size(width = 75.dp, height = 45.dp)
                    .background(
                        if (glowColor != null) glowColor.copy(alpha = 0.25f)
                        else Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        2.dp,
                        glowColor ?: green4,
                        RoundedCornerShape(12.dp)
                    )
                    .glassmorphic(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showNullOnRight) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("null", color = Color.White, fontSize = 7.sp)
                }
            }
        }

        // Head/Tail Labels
        if (isFirst || isLast) {
            val labelText = buildAnnotatedString {
                if (isFirst && isLast) {
                    withStyle(style = SpanStyle(color = Color.Cyan)) { append("Head") }
                    withStyle(style = SpanStyle(color = Color.White)) { append("/") }
                    withStyle(style = SpanStyle(color = Color.Yellow)) { append("Tail") }
                } else if (isFirst) {
                    withStyle(style = SpanStyle(color = Color.Cyan)) { append("Head") }
                } else {
                    withStyle(style = SpanStyle(color = Color.Yellow)) { append("Tail") }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = if (isFirst) Color.Cyan else Color.Yellow,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = labelText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = cantoraFont
                )
            }
        } else {
            Spacer(modifier = Modifier.height(28.dp))
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
