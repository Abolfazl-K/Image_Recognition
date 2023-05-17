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
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var emailInput: TextInputEditText
    private lateinit var passInput: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var registerNow:TextView
    private lateinit var forgotPass:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passInput = findViewById(R.id.passwordInput)
        btnLogin = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progressBar)
        registerNow = findViewById(R.id.registerNow)
        forgotPass = findViewById(R.id.forgotPass)

        forgotPass.setOnClickListener {
            val intent = Intent(applicationContext, ResetPass::class.java)
            startActivity(intent)
        }

        registerNow.setOnClickListener{
            val intent = Intent(applicationContext, Register::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email: String = emailInput.text.toString().trim()
            val password: String = passInput.text.toString()

            if(email.isEmpty()){
                emailInput.error = "Email cant be empty!"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(password.isEmpty()){
                passInput.error = "Password cant be empty!"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        val user:FirebaseUser = FirebaseAuth.getInstance().currentUser!!
                        if(user.isEmailVerified){
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(
                                baseContext,
                                "Login successful!",
                                Toast.LENGTH_SHORT,
                            ).show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }else{
                            user.sendEmailVerification()
                            Toast.makeText(
                                baseContext,
                                "Please verify your email and try again!",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
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
        if (currentUser != null && mAuth.currentUser?.isEmailVerified  == true  ) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}