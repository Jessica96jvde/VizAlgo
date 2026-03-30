package com.example.vizalgo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.vizalgo.authentication.AuthenticationActivity
import com.example.vizalgo.dslist.DSListActivity
import com.example.vizalgo.userdashboard.UserDashboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen(
                onCategoryClick = { mode ->
                    val intent = Intent(this, DSListActivity::class.java).apply {
                        putExtra("MODE", mode)
                    }
                    startActivity(intent)
                },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}

@Composable
fun HomeScreen(onCategoryClick: (String) -> Unit, onLogout: () -> Unit) {
    val cantoraFont = FontFamily(Font(R.font.cantora_one))
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current
    
    // State for user data
    var username by remember { mutableStateOf("Loading...") }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Fetch user data from Firestore
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        username = document.getString("username") ?: "User"
                    } else {
                        username = "New User"
                    }
                }
                .addOnFailureListener {
                    username = "User"
                }
        } else {
            username = "Guest"
        }
    }

    // State for dashboard visibility
    var offsetX by remember { mutableStateOf(0f) }
    val maxOffset = with(density) { (screenWidth * 0.8f).toPx() }
    
    val animatedOffset by animateDpAsState(
        targetValue = with(density) { offsetX.toDp() },
        label = "DashboardAnimation",
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        val newOffset = (offsetX + dragAmount).coerceIn(0f, maxOffset)
                        offsetX = newOffset
                    },
                    onDragEnd = {
                        offsetX = if (offsetX > maxOffset / 2) maxOffset else 0f
                    }
                )
            }
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.homebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Main Content (Home Page)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "VizAlgo",
                    fontFamily = cantoraFont,
                    fontSize = 54.sp,
                    color = colorResource(id = R.color.green4)
                )
                Text(
                    text = stringResource(id = R.string.app_tagline),
                    fontFamily = cantoraFont,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.green4).copy(alpha = 0.8f)
                )
            }

            // Three Divisions with Video Backgrounds from raw folder
            HomeDivisionCard("Learn", R.raw.learn, cantoraFont) { onCategoryClick("Learn") }
            HomeDivisionCard("Visualize", R.raw.visualize, cantoraFont) { onCategoryClick("Visualize") }
            HomeDivisionCard("Game", R.raw.game, cantoraFont) { onCategoryClick("Game") }
            
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Dashboard Drawer - Semi-transparent background
        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffset.toPx().roundToInt() - maxOffset.roundToInt(), 0) }
                .fillMaxHeight()
                .width(screenWidth * 0.8f)
                .background(
                    color = colorResource(id = R.color.green4).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                )
        ) {
            UserDashboard(username = username, cantoraFont = cantoraFont, onLogout = onLogout)
        }

        // Draggable Tab
        Box(
            modifier = Modifier
                .offset { 
                    IntOffset(
                        animatedOffset.toPx().roundToInt(), 
                        80.dp.toPx().toInt()
                    ) 
                }
                .size(46.dp, 80.dp)
                .background(
                    color = colorResource(id = R.color.green4).copy(alpha = 0.9f),
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                )
                .clickable { 
                    offsetX = if (offsetX > 0) 0f else maxOffset
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Dashboard Tab",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun VideoBackground(resId: Int) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = Uri.parse("android.resource://${context.packageName}/$resId")
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun HomeDivisionCard(title: String, videoResId: Int, font: FontFamily, onClick: () -> Unit) {
    val green2 = colorResource(id = R.color.green2)
    val glowColor = colorResource(id = R.color.white) // Use a lighter color for the glow
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .glow(color = glowColor, alpha = 0.8f, borderRadius = 24.dp, glowRadius = 20.dp) // More visible and lighter glow
            .border(
                width = 3.dp,
                color = green2,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            VideoBackground(videoResId)

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)) // Less fading of the video
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontFamily = font,
                    fontSize = 36.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.drawBehind {
                        // Adding a subtle text shadow for better readability without heavy fading
                        drawIntoCanvas {
                            val paint = Paint()
                            val frameworkPaint = paint.asFrameworkPaint()
                            frameworkPaint.setShadowLayer(10f, 0f, 0f, android.graphics.Color.BLACK)
                        }
                    }
                )
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

fun Modifier.glow(
    color: Color,
    alpha: Float = 0.5f,
    borderRadius: Dp = 0.dp,
    glowRadius: Dp = 10.dp
) = this.drawBehind {
    val transparentColor = color.copy(alpha = 0.0f).toArgb()
    val shadowColor = color.copy(alpha = alpha).toArgb()
    this.drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            glowRadius.toPx(),
            0f,
            0f,
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}
