package com.example.vizalgo.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R
import com.example.vizalgo.dslist.DSListActivity
import com.example.vizalgo.utils.glow

class GameModeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameModeScreen { mode ->
                val intent = Intent(this, DSListActivity::class.java).apply {
                    putExtra("MODE", mode)
                }
                startActivity(intent)
            }
        }
    }
}

@Composable
fun GameModeScreen(onModeSelected: (String) -> Unit) {
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
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                text = "Game Modes",
                fontFamily = cantoraFont,
                fontSize = 42.sp,
                color = green4,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            GameModeItemCard(
                title = "Quiz",
                subtitle = "Theoretical Knowledge",
                imageRes = R.drawable.splashbackground,
                font = cantoraFont
            ) {
                onModeSelected("Quiz")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            GameModeItemCard(
                title = "Challenge",
                subtitle = "Logic & Implementation",
                imageRes = R.drawable.splashbackground,
                font = cantoraFont
            ) {
                onModeSelected("Challenge")
            }
        }
    }
}

@Composable
fun GameModeItemCard(title: String, subtitle: String, imageRes: Int, font: FontFamily, onClick: () -> Unit) {
    val green4 = colorResource(id = R.color.green4)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .glow(color = green4, alpha = 0.5f, borderRadius = 24.dp, glowRadius = 15.dp)
            .border(
                width = 3.dp,
                color = green4,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = title,
                        fontFamily = font,
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Normal
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
