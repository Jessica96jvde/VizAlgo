package com.example.vizalgo.visualize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R
import kotlinx.coroutines.delay

class StackVisualize : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StackVisualizationDashboard()
        }
    }
}

@Composable
fun StackVisualizationDashboard() {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val poppins = FontFamily(Font(R.font.poppins_light))
    val green4 = colorResource(id = R.color.green4)
    val green1 = colorResource(id = R.color.green1)
    val green3 = colorResource(id = R.color.green3)
    
    var stackElements by remember { mutableStateOf(listOf<Int>()) }
    var inputValue by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isAnimating by remember { mutableStateOf(false) }
    var lastOperation by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(green4)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "Stack Visualizer",
                fontFamily = cantoraFont,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = green1,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Main Content - Two Columns Layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left: Stack Visualization
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    StackDisplayPanel(
                        elements = stackElements,
                        isAnimating = isAnimating,
                        lastOperation = lastOperation,
                        green4 = green4,
                        green1 = green1,
                        poppins = poppins
                    )
                }
                
                // Right: Controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    ControlsPanel(
                        inputValue = inputValue,
                        onInputChange = { inputValue = it },
                        onPush = {
                            val value = inputValue.toIntOrNull()
                            if (value != null && stackElements.size < 10) {
                                stackElements = stackElements + value
                                message = "✅ Pushed: $value"
                                lastOperation = "PUSH"
                                inputValue = ""
                                isAnimating = true
                            } else if (stackElements.size >= 10) {
                                message = "⚠️ Stack Overflow! Max 10 elements"
                            } else {
                                message = "❌ Invalid input"
                            }
                        },
                        onPop = {
                            if (stackElements.isNotEmpty()) {
                                val popped = stackElements.last()
                                stackElements = stackElements.dropLast(1)
                                message = "✅ Popped: $popped"
                                lastOperation = "POP"
                                isAnimating = true
                            } else {
                                message = "⚠️ Stack Underflow! Stack is empty"
                            }
                        },
                        onPeek = {
                            if (stackElements.isNotEmpty()) {
                                message = "👀 Top Element: ${stackElements.last()}"
                                lastOperation = "PEEK"
                            } else {
                                message = "⚠️ Stack is empty"
                            }
                        },
                        onClear = {
                            stackElements = emptyList()
                            message = "🗑️ Stack cleared"
                            lastOperation = "CLEAR"
                            isAnimating = true
                        },
                        green4 = green4,
                        green1 = green1,
                        poppins = poppins
                    )
                    
                    // Status Message
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .background(green3.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = message.ifEmpty { "Ready to perform operations..." },
                            fontFamily = poppins,
                            fontSize = 14.sp,
                            color = if (message.contains("❌") || message.contains("⚠️")) Color(0xFFFF6B6B) else green1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Statistics
                    StatisticsPanel(
                        size = stackElements.size,
                        capacity = 10,
                        isEmpty = stackElements.isEmpty(),
                        isFull = stackElements.size >= 10,
                        green4 = green4,
                        green1 = green1,
                        poppins = poppins
                    )
                }
            }
        }
    }
    
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(600)
            isAnimating = false
        }
    }
}

@Composable
fun StackDisplayPanel(
    elements: List<Int>,
    isAnimating: Boolean,
    lastOperation: String,
    green4: Color,
    green1: Color,
    poppins: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(green4.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .border(2.dp, green1.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📦 Stack Memory",
            fontFamily = poppins,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = green1,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (elements.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(green1.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .border(2.dp, green1, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[ EMPTY ]",
                    fontFamily = poppins,
                    fontSize = 16.sp,
                    color = green1.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "↓ TOP ↓",
                    fontFamily = poppins,
                    fontSize = 14.sp,
                    color = Color(0xFFFF6B6B),
                    fontWeight = FontWeight.Bold
                )
                
                elements.reversed().forEachIndexed { index, element ->
                    val animateScale = remember { Animatable(1f) }
                    val animateColor = animateColorAsState(
                        targetValue = if (lastOperation in listOf("PUSH", "POP") && index == 0) Color(0xFFFFD700) else green1,
                        animationSpec = tween(300)
                    )
                    
                    LaunchedEffect(isAnimating) {
                        if (isAnimating) {
                            animateScale.animateTo(1.15f, animationSpec = tween(150))
                            animateScale.animateTo(1f, animationSpec = tween(150))
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(60.dp)
                            .scale(animateScale.value)
                            .background(animateColor.value, RoundedCornerShape(12.dp))
                            .shadow(8.dp, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = element.toString(),
                            fontFamily = poppins,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = green4
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "↑ BOTTOM ↑",
                    fontFamily = poppins,
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(green1.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📊 Size: ${elements.size}/10",
                fontFamily = poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = green1
            )
        }
    }
}

@Composable
fun ControlsPanel(
    inputValue: String,
    onInputChange: (String) -> Unit,
    onPush: () -> Unit,
    onPop: () -> Unit,
    onPeek: () -> Unit,
    onClear: () -> Unit,
    green4: Color,
    green1: Color,
    poppins: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(green4.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .border(2.dp, green1.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🎮 Controls",
            fontFamily = poppins,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = green1,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        TextField(
            value = inputValue,
            onValueChange = onInputChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text("Enter value (0-100)", fontFamily = poppins, color = green1.copy(alpha = 0.5f))
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = green1.copy(alpha = 0.1f),
                unfocusedContainerColor = green1.copy(alpha = 0.05f),
                focusedTextColor = green1,
                unfocusedTextColor = green1,
                focusedIndicatorColor = green1,
                unfocusedIndicatorColor = green1.copy(alpha = 0.3f)
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        OperationButton(
            text = "🔼 PUSH",
            onClick = onPush,
            backgroundColor = Color(0xFF4CAF50),
            poppins = poppins
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OperationButton(
                text = "🔽 POP",
                onClick = onPop,
                backgroundColor = Color(0xFFFF6B6B),
                poppins = poppins,
                modifier = Modifier.weight(1f)
            )
            
            OperationButton(
                text = "👀 PEEK",
                onClick = onPeek,
                backgroundColor = Color(0xFF4ECDC4),
                poppins = poppins,
                modifier = Modifier.weight(1f)
            )
        }
        
        OperationButton(
            text = "🗑️ CLEAR",
            onClick = onClear,
            backgroundColor = Color(0xFFFF9800),
            poppins = poppins
        )
    }
}

@Composable
fun OperationButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    poppins: FontFamily,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontFamily = poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun StatisticsPanel(
    size: Int,
    capacity: Int,
    isEmpty: Boolean,
    isFull: Boolean,
    green4: Color,
    green1: Color,
    poppins: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "📈 Statistics",
            fontFamily = poppins,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = green1,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(green4.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Size", "$size", green1, poppins)
            StatItem("Status", if (isEmpty) "Empty" else if (isFull) "Full" else "Ready", green1, poppins)
            StatItem("Capacity", capacity.toString(), green1, poppins)
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color,
    poppins: FontFamily
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontFamily = poppins,
            fontSize = 12.sp,
            color = color.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontFamily = poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

