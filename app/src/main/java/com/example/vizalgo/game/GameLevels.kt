package com.example.vizalgo.game

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R

@Composable
fun GameLevelsScreen(
    dsName: String,
    unlockedLevel: Int,
    levelStars: Map<Int, Int>,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    
    // Color Palettes
    val easyLight = Color(0xFF81C784)
    val easyDark = Color(0xFF2E7D32)
    
    val mediumLight = Color(0xFFFFD54F)
    val mediumDark = Color(0xFFF57C00)
    
    val hardLight = Color(0xFFE57373)
    val hardDark = Color(0xFFB71C1C)

    val scrollState = rememberScrollState()
    val bgImage = ImageBitmap.imageResource(id = R.drawable.gamebg)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val scale = size.width / bgImage.width
                    val scaledHeight = bgImage.height * scale
                    val scrollOffset = scrollState.value.toFloat()

                    var yOffset = -(scrollOffset % scaledHeight)
                    while (yOffset < size.height) {
                        drawImage(
                            image = bgImage,
                            dstSize = IntSize(size.width.toInt(), scaledHeight.toInt()),
                            dstOffset = IntOffset(0, yOffset.toInt())
                        )
                        yOffset += scaledHeight
                    }
                    drawRect(Color.Black.copy(alpha = 0.2f))
                }
                .verticalScroll(scrollState)
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_rotate),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Text(
                text = dsName,
                fontFamily = cantoraFont,
                fontSize = 36.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Challenges",
                fontFamily = cantoraFont,
                fontSize = 24.sp,
                color = easyLight,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(30.dp))

            val levels = (1..15).toList()
            
            levels.forEachIndexed { index, level ->
                val isUnlocked = level <= unlockedLevel
                val stars = levelStars[level] ?: 0
                
                // Determine palette based on level range
                val (bgL, bgD) = when {
                    level <= 5 -> easyLight to easyDark
                    level <= 10 -> mediumLight to mediumDark
                    else -> hardLight to hardDark
                }

                LevelNode(
                    level = level,
                    index = index,
                    isUnlocked = isUnlocked,
                    stars = stars,
                    lightPalette = bgL,
                    darkPalette = bgD,
                    cantoraFont = cantoraFont,
                    isLast = index == levels.size - 1,
                    onClick = { if (isUnlocked) onLevelSelected(level) }
                )
            }
        }
    }
}

@Composable
fun LevelNode(
    level: Int,
    index: Int,
    isUnlocked: Boolean,
    stars: Int,
    lightPalette: Color,
    darkPalette: Color,
    cantoraFont: FontFamily,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val circleSize = 85.dp
    val spacingHeight = 280.dp
    val starsHeight = 28.dp 

    val currentDark = if (isUnlocked) darkPalette else Color(0xFF263238)
    val currentLight = if (isUnlocked) lightPalette else Color(0xFF78909C)
    val connectorColor = if (isUnlocked) lightPalette.copy(alpha = 0.65f) else currentLight.copy(alpha = 0.5f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                if (!isLast) {
                    val centerX = size.width / 2
                    val radiusPx = (circleSize / 2).toPx()
                    val starsHeightPx = (starsHeight + 6.dp).toPx() 
                    
                    val startX = centerX + (radiusPx * 0.7f)
                    val endX = centerX - (radiusPx * 0.7f)
                    
                    val startY = starsHeightPx + radiusPx
                    val endY = size.height + starsHeightPx + radiusPx
                    
                    val wideSwing = size.width * 1.0f
                    
                    val path = Path().apply {
                        moveTo(startX, startY)
                        cubicTo(
                            centerX + wideSwing, startY + (endY - startY) * 0.25f,
                            centerX - wideSwing, startY + (endY - startY) * 0.75f,
                            endX, endY
                        )
                    }

                    drawPath(
                        path = path,
                        color = connectorColor,
                        style = Stroke(width = 24f)
                    )
                }
            }
    ) {
        Row(modifier = Modifier.height(starsHeight).padding(bottom = 6.dp)) {
            repeat(3) { starIndex ->
                val isFilled = starIndex < stars
                Icon(
                    imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (isFilled) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(currentDark)
                .border(5.dp, currentLight, CircleShape)
                .clickable(enabled = isUnlocked) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                Text(
                    text = level.toString(),
                    color = Color.White,
                    fontFamily = cantoraFont,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        if (!isLast) {
            Spacer(modifier = Modifier.height(spacingHeight))
        } else {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
