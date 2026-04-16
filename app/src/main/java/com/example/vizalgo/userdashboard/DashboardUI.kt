package com.example.vizalgo.userdashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

@Composable
fun UserDashboard(username: String, cantoraFont: FontFamily, onLogout: () -> Unit) {
    val poppins = FontFamily(Font(R.font.poppins_light))
    var showAllLeaderboard by remember { mutableStateOf(false) }
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    
    var leaderboardData by remember { mutableStateOf<List<LeaderboardEntry>>(emptyList()) }
    var userXP by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch data from Firestore
    LaunchedEffect(Unit) {
        try {
            // We search for documents where 'xp' exists. If users don't have 'xp' field, we fallback to 'stars' or 0
            val snapshot = db.collection("users")
                .get()
                .await()
            
            leaderboardData = snapshot.documents.map { doc ->
                // Try to get 'xp', then 'stars', then default to 0
                val xpValue = doc.getLong("xp")?.toInt() 
                    ?: (doc.getLong("stars")?.toInt()?.times(100)) 
                    ?: 0
                    
                LeaderboardEntry(
                    name = doc.getString("username") ?: "Unknown",
                    points = xpValue,
                    color = if (doc.getString("username") == username) Color(0xFF81C784) else Color(0xFF64B5F6)
                )
            }.sortedByDescending { it.points }

            val currentUserDoc = db.collection("users").document(auth.currentUser?.uid ?: "").get().await()
            userXP = currentUserDoc.getLong("xp")?.toInt() 
                ?: (currentUserDoc.getLong("stars")?.toInt()?.times(100)) 
                ?: 0
            
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val displayedLeaderboard = if (showAllLeaderboard) leaderboardData else leaderboardData.take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        // Avatar Section
        Box(contentAlignment = Alignment.BottomEnd) {
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
            
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { },
                color = colorResource(id = R.color.green4),
                tonalElevation = 4.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Avatar",
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = username,
            fontFamily = cantoraFont,
            fontSize = 30.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "$userXP XP",
            fontFamily = poppins,
            fontSize = 18.sp,
            color = colorResource(id = R.color.green2),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Leaderboard Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Global Rankings",
                fontFamily = cantoraFont,
                fontSize = 24.sp,
                color = Color.White
            )
            
            if (leaderboardData.size > 5) {
                Text(
                    text = if (showAllLeaderboard) "Show Less" else "View More",
                    color = colorResource(id = R.color.green2),
                    fontFamily = poppins,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { showAllLeaderboard = !showAllLeaderboard }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(color = colorResource(id = R.color.green2))
        } else if (leaderboardData.isEmpty()) {
            Text("No rankings available", color = Color.White.copy(alpha = 0.5f), fontFamily = poppins)
        } else {
            val maxPoints = leaderboardData.firstOrNull()?.points?.coerceAtLeast(1) ?: 1

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                displayedLeaderboard.forEach { entry ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.name,
                                color = if (entry.name == username) colorResource(id = R.color.green2) else Color.White,
                                fontFamily = poppins,
                                fontSize = 15.sp,
                                fontWeight = if (entry.name == username) FontWeight.ExtraBold else FontWeight.Medium
                            )
                            Text(
                                text = "${entry.points} XP",
                                color = Color.White,
                                fontFamily = poppins,
                                fontSize = 13.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Horizontal XP Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(entry.points.toFloat() / maxPoints)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(entry.color.copy(alpha = 0.7f), entry.color)
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        DashboardMenuItem("Logout", cantoraFont, onClick = onLogout)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

data class LeaderboardEntry(val name: String, val points: Int, val color: Color)

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
