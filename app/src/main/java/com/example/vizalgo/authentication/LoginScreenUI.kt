package com.example.vizalgo.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R

@Composable
fun LoginScreen(
    onLoginRequested: (String) -> Unit,
    onGoogleSignInRequested: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }

    val poppins = FontFamily(Font(R.font.poppins_light))
    val baloo = FontFamily(Font(R.font.baloo_2_semibold))

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.loginbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Log In",
                fontSize = 40.sp,
                fontFamily = baloo,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.green4)
            )

            Row {
                Text(
                    text = "New member? ",
                    fontSize = 14.sp,
                    fontFamily = poppins,
                    color = colorResource(id = R.color.green4)
                )
                Text(
                    text = "Sign Up",
                    color = Color.Blue,
                    fontSize = 14.sp,
                    fontFamily = poppins,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }

            Spacer(Modifier.height(48.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { 
                    username = it
                    usernameError = false
                },
                label = { Text("Username", fontFamily = poppins) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = usernameError,
                textStyle = TextStyle(fontFamily = poppins),
                shape = RoundedCornerShape(12.dp)
            )

            if (usernameError) {
                Text(
                    text = "Username cannot be empty",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontFamily = poppins,
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { 
                    if (username.isNotEmpty()) {
                        onLoginRequested(username)
                    } else {
                        usernameError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green4))
            ) {
                Text(
                    text = "Get OTP",
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = colorResource(R.color.green4))
                Text(
                    text = "or log in with",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 14.sp,
                    fontFamily = poppins,
                    color = colorResource(id = R.color.green4)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = colorResource(id = R.color.green4))
            }

            Spacer(Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.googlelogo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(48.dp).clickable { onGoogleSignInRequested() }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Continue as Guest",
                color = Color.Blue,
                fontSize = 16.sp,
                fontFamily = poppins,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { /* Guest access */ }
            )
        }
    }
}
