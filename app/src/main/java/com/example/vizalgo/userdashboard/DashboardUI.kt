package com.example.vizalgo.userdashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

@Composable
fun UserDashboard(username: String, cantoraFont: FontFamily, onLogout: () -> Unit) {
    val poppins = FontFamily(Font(R.font.poppins_light))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // User Profile Section
        Spacer(modifier = Modifier.height(20.dp))
        
        Box(
            modifier = Modifier
                .size(110.dp)
                .border(3.dp, Color.White, CircleShape)
                .padding(6.dp)
                .border(2.dp, colorResource(id = R.color.green2).copy(alpha = 0.7f), CircleShape)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "User Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = username,
            fontFamily = cantoraFont,
            fontSize = 30.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Horizontal Bar Chart Leaderboard
        Text(
            text = "Star Leaderboard",
            fontFamily = cantoraFont,
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(20.dp))

        val leaderboardData = listOf(
            LeaderboardEntry("Alex", 150, Color(0xFFFFD700)),
            LeaderboardEntry(username, 120, Color(0xFFC0C0C0)),
            LeaderboardEntry("Sam", 100, Color(0xFFCD7F32)),
            LeaderboardEntry("Jordan", 80, Color(0xFFB8E3E9)),
            LeaderboardEntry("Taylor", 60, Color(0xFF93B1B5))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            leaderboardData.forEach { entry ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = entry.name,
                            color = Color.White,
                            fontFamily = poppins,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${entry.stars}",
                                color = Color.White,
                                fontFamily = poppins,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(entry.stars / 150f)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(entry.color.copy(alpha = 0.6f), entry.color)
                                    )
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DashboardMenuItem("Logout", cantoraFont, onClick = onLogout)
    }
}

data class LeaderboardEntry(val name: String, val stars: Int, val color: Color)

@Composable
fun DashboardMenuItem(text: String, font: FontFamily, onClick: () -> Unit) {
    Text(
        text = text,
        fontFamily = font,
        fontSize = 22.sp,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() }
    )
}
