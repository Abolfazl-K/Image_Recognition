package com.example.imagerecognition

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput: TextInputEditText = findViewById(R.id.emailInput)
        val passInput: TextInputEditText = findViewById(R.id.passwordInput)
        val btnLogin: Button = findViewById(R.id.btn_login)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val registerNow:TextView = findViewById(R.id.registerNow)

        registerNow.setOnClickListener{
            val intent = Intent(applicationContext, Register::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email: String = emailInput.text.toString()
            val password: String = passInput.text.toString()

            if(email.isEmpty()){
                Toast.makeText(this, "Email cant be empty!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(password.isEmpty()){
                Toast.makeText(this, "Password cant be empty!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(
                            baseContext,
                            "Login successful!",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}