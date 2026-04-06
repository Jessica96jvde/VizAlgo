package com.example.vizalgo

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class OtpHelper(private val activity: Activity) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storedVerificationId: String? = null

    fun sendOtp(
        phoneNumber: String,
        onOtpSent: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Instant verification or auto-retrieval
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onFailure(e.message ?: "Verification failed")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                onOtpSent(verificationId)
            }
        }

        // Improved phone number formatting:
        // If user enters "+91...", use it as is.
        // If user enters "91...", prepend "+".
        // If user enters 10 digits, prepend "+91".
        val formattedPhone = when {
            phoneNumber.startsWith("+") -> phoneNumber
            phoneNumber.startsWith("91") && phoneNumber.length > 10 -> "+$phoneNumber"
            else -> "+91$phoneNumber"
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(
        otp: String,
        username: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val verificationId = storedVerificationId ?: return onFailure("Verification ID is missing")
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        saveOrFetchUser(user.uid, username, phoneNumber, onSuccess, onFailure)
                    }
                } else {
                    onFailure(task.exception?.message ?: "Verification failed")
                }
            }
    }

    private fun saveOrFetchUser(
        uid: String,
        username: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userRef = db.collection("users").document(uid)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                onSuccess()
            } else {
                val userData = hashMapOf(
                    "uid" to uid,
                    "username" to username,
                    "phone" to phoneNumber,
                    "stars" to 0 // Initialize for leaderboard
                )
                userRef.set(userData)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e.message ?: "Failed to save user") }
            }
        }.addOnFailureListener { e ->
            onFailure(e.message ?: "Error checking database")
        }
    }
}
