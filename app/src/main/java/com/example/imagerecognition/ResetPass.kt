package com.example.imagerecognition

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.google.android.material.textfield.TextInputEditText

class ResetPass : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var btnReset: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)

        emailInput = findViewById(R.id.emailInput)
        btnReset = findViewById(R.id.btn_resetPass)
        progressBar = findViewById(R.id.progressBar)

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
            //TODO
        }

    }
}