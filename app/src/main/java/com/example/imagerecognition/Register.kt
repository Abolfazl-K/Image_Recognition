package com.example.imagerecognition

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    private val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var emailInput: TextInputEditText
    private lateinit var pass1Input: TextInputEditText
    private lateinit var pass2Input: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var progressBar: TextInputEditText
    private lateinit var loginNow: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailInput = findViewById(R.id.emailInput)
        pass1Input = findViewById(R.id.registerPass1)
        pass2Input = findViewById(R.id.registerPass2)
        btnRegister = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.progressBar)
        loginNow = findViewById(R.id.loginNow)

        loginNow.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        btnRegister.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            val email:String = emailInput.text.toString()

            val pass1:String = pass1Input.text.toString()
            val pass2:String = pass2Input.text.toString()

            if(email.isEmpty()){
                Toast.makeText(this, "Email cant be empty!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(pass1.isEmpty() || pass2.isEmpty()){
                Toast.makeText(this, "Password cant be empty!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(pass1 != pass2){
                Toast.makeText(this, "Passwords are not the same!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, pass1)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(
                            this,
                            "Account created!",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}