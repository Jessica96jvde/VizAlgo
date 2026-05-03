package com.example.vizalgo.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.vizalgo.MainActivity
import com.example.vizalgo.OtpHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.example.vizalgo.R

class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val otpHelper = OtpHelper(this)

        setContent {
            AuthenticationApp(
                otpHelper = otpHelper,
                onAuthSuccess = {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}

@Composable
fun AuthenticationApp(otpHelper: OtpHelper, onAuthSuccess: () -> Unit) {
    var currentScreen by remember { mutableStateOf("signup") }
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Get Web Client ID from strings.xml
    val webClientId = stringResource(id = R.string.default_web_client_id)

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            
            auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid ?: ""
                    
                    db.collection("users").document(uid).get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            if (currentScreen == "signup") {
                                // STOP: User tried to Sign Up but account exists
                                Toast.makeText(context, "Account already exists. Logging you in...", Toast.LENGTH_LONG).show()
                                onAuthSuccess()
                            } else {
                                onAuthSuccess()
                            }
                        } else {
                            if (currentScreen == "login") {
                                // STOP: User tried to Log In but account doesn't exist
                                Toast.makeText(context, "No account found. Please sign up first.", Toast.LENGTH_LONG).show()
                                auth.signOut()
                                currentScreen = "signup"
                            } else {
                                // SUCCESS: New Google User
                                val userData = hashMapOf(
                                    "uid" to uid,
                                    "username" to (user?.displayName ?: "Google User"),
                                    "phone" to (user?.email ?: "Google Auth"),
                                    "stars" to 0
                                )
                                db.collection("users").document(uid).set(userData).addOnSuccessListener {
                                    onAuthSuccess()
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: ApiException) {
            // PROVIDE BETTER DEBUG INFO: 
            // 12500: Internal Error
            // 10: Developer Error (Usually SHA-1/SHA-256 mismatch or API not enabled)
            // 7: Network Error
            // 12501: Sign-in Cancelled (or configuration issue)
            val message = when(e.statusCode) {
                10 -> "Developer Error (10): Check SHA fingerprints and Google Sign-In API in Firebase."
                7 -> "Network Error (7): Check your internet connection."
                12500 -> "Internal Error (12500): Try clearing Google Play Services data."
                12501 -> "Sign-in Cancelled (12501): User cancelled or configuration mismatch."
                else -> "Error code: ${e.statusCode} - ${e.localizedMessage}"
            }
            Toast.makeText(context, "Google Login Failed: $message", Toast.LENGTH_LONG).show()
        }
    }

    fun startGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        
        // IMPORTANT: Sign out from Google Client first to force account picker
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    when (currentScreen) {
        "signup" -> SignUpScreen(
            onOtpRequested = { name, phone ->
                // Check if username/phone exists
                db.collection("users").whereEqualTo("username", name).get()
                    .addOnSuccessListener { q ->
                        if (!queryExists(q)) {
                            username = name
                            phoneNumber = phone
                            Toast.makeText(context, "Sending OTP...", Toast.LENGTH_SHORT).show()
                            otpHelper.sendOtp(phone, 
                                { currentScreen = "otp" }, 
                                { error -> Toast.makeText(context, "OTP Error: $error", Toast.LENGTH_LONG).show() }
                            )
                        } else {
                            Toast.makeText(context, "Username already exists. Please log in.", Toast.LENGTH_SHORT).show()
                            currentScreen = "login"
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Database check failed: ${e.message}. Check internet or Firestore rules.", Toast.LENGTH_LONG).show()
                    }
            },
            onGoogleSignInRequested = { startGoogleSignIn() },
            onLoginClick = { currentScreen = "login" }
        )
        "login" -> LoginScreen(
            onLoginRequested = { enteredUser ->
                db.collection("users").whereEqualTo("username", enteredUser).get()
                    .addOnSuccessListener { query ->
                        if (!query.isEmpty) {
                            val doc = query.documents[0]
                            username = enteredUser
                            phoneNumber = doc.getString("phone") ?: ""
                            Toast.makeText(context, "User found. Sending OTP...", Toast.LENGTH_SHORT).show()
                            otpHelper.sendOtp(phoneNumber, 
                                { currentScreen = "otp" }, 
                                { error -> Toast.makeText(context, "OTP Error: $error", Toast.LENGTH_LONG).show() }
                            )
                        } else {
                            Toast.makeText(context, "Username not found. Please sign up.", Toast.LENGTH_SHORT).show()
                            currentScreen = "signup"
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Login search failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            },
            onGoogleSignInRequested = { startGoogleSignIn() },
            onSignUpClick = { currentScreen = "signup" }
        )
        "otp" -> OTPScreenUI(
            onVerify = { otp ->
                otpHelper.verifyOtp(otp, username, phoneNumber, onAuthSuccess, { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
            },
            onBack = { currentScreen = "signup" }
        )
    }
}

// Helper to check query results
private fun queryExists(query: com.google.firebase.firestore.QuerySnapshot): Boolean {
    return !query.isEmpty
}
