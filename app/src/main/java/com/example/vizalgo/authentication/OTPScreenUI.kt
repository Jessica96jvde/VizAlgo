package com.example.vizalgo.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R

@Composable
fun OTPScreenUI(
    onVerify: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {

    var otp by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    val poppins = FontFamily(Font(R.font.poppins_light))
    val baloo = FontFamily(Font(R.font.baloo_2_semibold))

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(R.drawable.loginbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Back Arrow
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colorResource(id = R.color.green4),
                modifier = Modifier.size(32.dp)
            )
        }

        // Card container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp, bottom = 70.dp, start = 30.dp, end = 30.dp)
                .background(
                    Color.White.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "OTP Verification",
                fontSize = 32.sp,
                fontFamily = baloo,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.green4),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Enter the 6-digit code sent to your phone number",
                fontSize = 14.sp,
                fontFamily = poppins,
                color = colorResource(id = R.color.green4),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(48.dp))

            // OTP Input
            OutlinedTextField(
                value = otp,
                onValueChange = { 
                    if (it.length <= 6) {
                        otp = it
                        errorText = ""
                    }
                },
                label = { Text("Enter OTP", fontFamily = poppins, color = colorResource(id = R.color.green4)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorText.isNotEmpty(),
                textStyle = TextStyle(
                    fontFamily = poppins,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp,
                    color = Color.Black
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                shape = RoundedCornerShape(12.dp)
            )
            
            if (errorText.isNotEmpty()) {
                Text(
                    text = errorText,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontFamily = poppins,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(48.dp))

            // Verify Button
            Button(
                onClick = { 
                    if (otp.length == 6) {
                        onVerify(otp)
                    } else {
                        errorText = "Please enter a 6-digit OTP"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green4))
            ) {
                Text(
                    text = "Verify",
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))

            // Resend Code
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Didn't receive the code? ",
                    fontSize = 14.sp,
                    fontFamily = poppins,
                    color = Color.DarkGray
                )
                Text(
                    text = "Resend",
                    color = Color.Blue,
                    fontSize = 14.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { /* Resend logic */ }
                )
            }
        }
    }
}
