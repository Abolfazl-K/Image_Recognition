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

class ResetPass : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var btnReset: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var backToLogin: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)

        emailInput = findViewById(R.id.emailInput)
        btnReset = findViewById(R.id.btn_resetPass)
        progressBar = findViewById(R.id.progressBar)
        backToLogin = findViewById(R.id.backToLogin)

        mAuth = FirebaseAuth.getInstance()

        backToLogin.setOnClickListener{
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }

        btnReset.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = emailInput.text.toString()
            if(email.isEmpty()){
                emailInput.error = "Email cant be empty!"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).equals(false)){
                emailInput.error = "Please provide valid email!"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                progressBar.visibility = View.GONE
                if(task.isSuccessful){
                    Toast.makeText(
                        baseContext,
                        "Check your email to reset your password!",
                        Toast.LENGTH_SHORT,
                    ).show()
                }else{
                    Toast.makeText(
                        baseContext,
                        "Try again, Something wrong just happened!",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

        }

    }
}