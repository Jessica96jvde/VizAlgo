package com.example.vizalgo.dslist

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
import com.example.vizalgo.utils.glow
import com.example.vizalgo.learn.LearnActivity
import com.example.vizalgo.game.GameChoice

class DSListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mode = intent.getStringExtra("MODE") ?: "Learn"
        
        setContent {
            DSListScreen(mode) { dsName ->
                val destinationActivity = when (mode) {
                    "Game" -> GameChoice::class.java
                    else -> LearnActivity::class.java
                }
                
                val intent = Intent(this, destinationActivity).apply {
                    putExtra("MODE", mode)
                    putExtra("DS_NAME", dsName)
                }
                startActivity(intent)
            }
        }
    }
}

@Composable
fun DSListScreen(mode: String, onDSSelected: (String) -> Unit) {
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
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "$mode Data Structures",
                fontFamily = cantoraFont,
                fontSize = 32.sp,
                color = green4
            )

            Spacer(modifier = Modifier.height(60.dp))

            DSItemCard("Stack", R.drawable.splashbackground, cantoraFont) { onDSSelected("Stack") }
            Spacer(modifier = Modifier.height(32.dp))
            DSItemCard("Queue", R.drawable.splashbackground, cantoraFont) { onDSSelected("Queue") }
        }
    }
}

@Composable
fun DSItemCard(title: String, imageRes: Int, font: FontFamily, onClick: () -> Unit) {
    val green4 = colorResource(id = R.color.green4)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
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
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontFamily = font,
                    fontSize = 30.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
