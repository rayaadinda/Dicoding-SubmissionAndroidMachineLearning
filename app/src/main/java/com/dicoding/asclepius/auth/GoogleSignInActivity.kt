package com.dicoding.asclepius.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.R
import com.dicoding.asclepius.view.HomeActivity
import kotlinx.coroutines.launch

class GoogleSignInActivity : AppCompatActivity() {
    private lateinit var googleAuthClient: GoogleAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googleAuthClient = GoogleAuthClient(applicationContext)

        val signInButton: Button = findViewById(R.id.sign_in_button)
        val signInStatus: TextView = findViewById(R.id.sign_in_status)

        if (googleAuthClient.isSingedIn()) {
            // Navigate to HomeActivity if already signed in
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // Close the sign-in activity
        }

        signInButton.setOnClickListener {
            signInStatus.text = "Signing in..."
            lifecycleScope.launch {
                val success = googleAuthClient.signIn()
                if (success) {
                    // Navigate to HomeActivity after successful sign-in
                    startActivity(Intent(this@GoogleSignInActivity, HomeActivity::class.java))
                    finish() // Close the sign-in activity
                } else {
                    // Handle sign-in failure (e.g., show a message)
                    signInStatus.text = "Sign-in failed"
                    Log.e("GoogleSignInActivity", "Sign-in failed")
                }
            }
        }
    }
}