package com.example.vizalgo.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R
import com.example.vizalgo.game.Queue.QueueGame

class GameChoice : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dsName = intent.getStringExtra("DS_NAME") ?: "Queue"
        
        setContent {
            GameChoiceScreen(dsName) { mode ->
                if (dsName == "Queue") {
                    val intent = Intent(this, QueueGame::class.java).apply {
                        putExtra("GAME_MODE", mode)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun GameChoiceScreen(dsName: String, onModeSelected: (String) -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val green4 = colorResource(id = R.color.green4)

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
            Text(
                text = "$dsName Game",
                fontFamily = cantoraFont,
                fontSize = 36.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            GameOptionButton("Quiz", "Test your knowledge", green4, cantoraFont) {
                onModeSelected("QUIZ")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            GameOptionButton("Challenge", "Master the logic", Color(0xFFE91E63), cantoraFont) {
                onModeSelected("CHALLENGE")
            }
        }
    }
}

@Composable
fun GameOptionButton(title: String, desc: String, color: Color, font: FontFamily, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color.White, fontFamily = font, fontSize = 26.sp)
            Text(text = desc, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
        }
    }
}
