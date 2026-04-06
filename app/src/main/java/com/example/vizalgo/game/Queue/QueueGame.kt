package com.example.vizalgo.game.Queue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

class QueueGame : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QueueGameScreen()
        }
    }
}

@Composable
fun QueueGameScreen() {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val poppins = FontFamily(Font(R.font.poppins_light))
    val green4 = colorResource(id = R.color.green4)
    
    var gameState by remember { mutableStateOf("CHOICE") } // CHOICE, QUIZ, CHALLENGE

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.homebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (gameState) {
                "CHOICE" -> {
                    Text(
                        text = "Queue Game",
                        fontFamily = cantoraFont,
                        fontSize = 36.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    GameModeButton("Quiz Mode", "Test your knowledge", green4, cantoraFont) {
                        gameState = "QUIZ"
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    GameModeButton("Challenge Mode", "Solve Queue puzzles", Color(0xFFE91E63), cantoraFont) {
                        gameState = "CHALLENGE"
                    }
                }
                "QUIZ" -> QuizView(onBack = { gameState = "CHOICE" }, cantoraFont, poppins)
                "CHALLENGE" -> ChallengeView(onBack = { gameState = "CHOICE" }, cantoraFont, poppins)
            }
        }
    }
}

@Composable
fun GameModeButton(title: String, desc: String, color: Color, font: FontFamily, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color.White, fontFamily = font, fontSize = 24.sp)
            Text(text = desc, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        }
    }
}

@Composable
fun QuizView(onBack: () -> Unit, font: FontFamily, poppins: FontFamily) {
    var currentQuestion by remember { mutableStateOf(0) }
    val questions = listOf(
        QuizData("What principle does a Queue follow?", listOf("LIFO", "FIFO", "LILO", "FILO"), 1),
        QuizData("Which operation adds an item to a Queue?", listOf("Push", "Pop", "Enqueue", "Dequeue"), 2)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Queue Quiz", fontFamily = font, fontSize = 28.sp, color = Color.White)
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = questions[currentQuestion].question, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                questions[currentQuestion].options.forEachIndexed { index, option ->
                    Button(
                        onClick = { if (currentQuestion < questions.size - 1) currentQuestion++ else onBack() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green3))
                    ) {
                        Text(option)
                    }
                }
            }
        }
        TextButton(onClick = onBack) { Text("Back to Menu", color = Color.White) }
    }
}

@Composable
fun ChallengeView(onBack: () -> Unit, font: FontFamily, poppins: FontFamily) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Queue Challenge", fontFamily = font, fontSize = 28.sp, color = Color.White)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Challenge: Reorder the queue to match the target sequence.",
            color = Color.White,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(64.dp))
        Button(onClick = onBack) { Text("Solve") }
        TextButton(onClick = onBack) { Text("Back to Menu", color = Color.White) }
    }
}

data class QuizData(val question: String, val options: List<String>, val correctIndex: Int)
