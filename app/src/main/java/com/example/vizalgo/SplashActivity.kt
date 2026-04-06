package com.example.vizalgo
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vizalgo.authentication.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        val logo=findViewById<ImageView>(R.id.logo)
        val name=findViewById<TextView>(R.id.appname)
        
        logo.animate()
            .alpha(1f)
            .setDuration(1500)
            .withEndAction{
                logo.animate().alpha(0f)
                name.animate().alpha(1f).setDuration(2000)
                    .withEndAction{
                        val auth = FirebaseAuth.getInstance()
                        val currentUser = auth.currentUser
                        
                        if (currentUser != null) {
                            // User is already logged in, go to Home Screen (MainActivity)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // No user logged in, go to Auth Screen
                            val intent = Intent(this, AuthenticationActivity::class.java)
                            startActivity(intent)
                        }
                        finish()
                    }
            }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}