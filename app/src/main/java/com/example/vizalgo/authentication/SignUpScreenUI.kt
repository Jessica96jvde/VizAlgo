package com.example.vizalgo.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vizalgo.R

@Composable
fun SignUpScreen(
    onOtpRequested: (String, String) -> Unit,
    onGoogleSignInRequested: () -> Unit,
    onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    
    var usernameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

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
                text = "Sign Up",
                fontSize = 40.sp,
                fontFamily = baloo,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.green4)
            )

            Row {
                Text(
                    text = "Already a member? ",
                    fontSize = 14.sp,
                    fontFamily = poppins,
                    color = colorResource(id = R.color.green4)
                )
                Text(
                    text = "Log in",
                    color = Color.Blue,
                    fontSize = 14.sp,
                    fontFamily = poppins,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            Spacer(Modifier.height(32.dp))

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

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { 
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        phoneNumber = it
                        phoneError = false
                    }
                },
                label = { Text("Phone Number", fontFamily = poppins) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = phoneError,
                textStyle = TextStyle(fontFamily = poppins),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { 
                    val isUserValid = username.isNotEmpty() && username.all { it.isLetter() }
                    val isPhoneValid = phoneNumber.length == 10
                    
                    usernameError = !isUserValid
                    phoneError = !isPhoneValid
                    
                    if (isUserValid && isPhoneValid) {
                        onOtpRequested(username, phoneNumber)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green4))
            ) {
                Text(
                    text = "Send OTP",
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = colorResource(R.color.green4))
                Text(
                    text = "or sign up with",
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
