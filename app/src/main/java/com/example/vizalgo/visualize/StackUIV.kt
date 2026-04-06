package com.example.vizalgo.visualize

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun StackScreen() {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    var stack by remember { mutableStateOf(listOf<Int>()) }
    var input by remember { mutableStateOf("") }
    val green4 = colorResource(id = R.color.green4)
    val scope = rememberCoroutineScope()
    
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                text = "Stack Visualizer",
                fontFamily = cantoraFont,
                fontSize = 42.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // STACK DISPLAY AREA
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val reversedStack = stack.reversed()
                    reversedStack.forEachIndexed { index, value ->
                        val originalIndex = stack.size - 1 - index
                        val isFirst = index == 0
                        val isLast = index == reversedStack.lastIndex
                        
                        val shape = when {
                            isFirst && isLast -> RoundedCornerShape(20.dp)
                            isFirst -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                            isLast -> RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                            else -> RoundedCornerShape(0.dp)
                        }

                        val glowColor = when {
                            originalIndex == recentlyAddedIndex -> Color.Blue.copy(alpha = 0.4f)
                            originalIndex == recentlyDeletedIndex -> Color.Red.copy(alpha = 0.4f)
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(70.dp)
                                .background(glowColor, shape)
                                .glassmorphic(shape = shape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = value.toString(),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // INPUT + BUTTONS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .glassmorphic(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
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
                                stack = stack + input.toInt()
                                recentlyAddedIndex = stack.size - 1
                                input = ""
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
                        Text("Push", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (stack.isNotEmpty()) {
                                recentlyDeletedIndex = stack.size - 1
                                scope.launch {
                                    delay(500)
                                    stack = stack.dropLast(1)
                                    recentlyDeletedIndex = -1
                                }
                            }
                        },
                        modifier = Modifier.height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Pop", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
