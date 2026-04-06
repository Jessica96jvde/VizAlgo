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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    val green4 = colorResource(id = R.color.green4)
    
    var recentlyAddedIndex by remember { mutableStateOf(-1) }
    var recentlyDeletedIndex by remember { mutableStateOf(-1) }

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
                    Text("List is empty", color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp)
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = input,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 4) input = it },
                            placeholder = { Text("Value", color = Color.White.copy(alpha = 0.4f)) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (input.isNotEmpty()) {
                                    val newVal = input.toInt()
                                    list = list + newVal
                                    input = ""
                                    recentlyAddedIndex = list.lastIndex
                                    scope.launch {
                                        delay(500)
                                        recentlyAddedIndex = -1
                                    }
                                }
                            },
                            modifier = Modifier.height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = green4)
                        ) {
                            Text("Add Tail", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (input.isNotEmpty()) {
                                    val newVal = input.toInt()
                                    list = listOf(newVal) + list
                                    input = ""
                                    recentlyAddedIndex = 0
                                    scope.launch {
                                        delay(500)
                                        recentlyAddedIndex = -1
                                    }
                                }
                            },
                            modifier = Modifier.height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = green4)
                        ) {
                            Text("Add Head", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = {
                                if (list.isNotEmpty()) {
                                    recentlyDeletedIndex = list.lastIndex
                                    scope.launch {
                                        delay(500)
                                        list = list.dropLast(1)
                                        recentlyDeletedIndex = -1
                                    }
                                }
                            },
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Text("Pop Back", color = Color.White, fontSize = 12.sp)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                if (list.isNotEmpty()) {
                                    recentlyDeletedIndex = 0
                                    scope.launch {
                                        delay(500)
                                        list = list.drop(1)
                                        recentlyDeletedIndex = -1
                                    }
                                }
                            },
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Text("Pop Front", color = Color.White, fontSize = 12.sp)
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Head/Tail Labels
        Text(
            text = when {
                isFirst && isLast -> "Head/Tail"
                isFirst -> "Head"
                isLast -> "Tail"
                else -> ""
            },
            color = green4,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )

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
                    .then(
                        if (glowColor != null) {
                            Modifier.border(2.dp, glowColor, RoundedCornerShape(12.dp))
                                .background(glowColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        } else Modifier
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
    }
}
