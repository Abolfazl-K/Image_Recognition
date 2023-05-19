package com.example.imagerecognition

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.imagerecognition.databinding.ActivityResetPassBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPass : AppCompatActivity() {

    private lateinit var binding:ActivityResetPassBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPassBinding.inflate(layoutInflater)

        mAuth = FirebaseAuth.getInstance()

        binding.backToLogin.setOnClickListener{
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnResetPass.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val email = binding.emailInput.text.toString()
            if(email.isEmpty()){
                binding.emailInput.error = "Email cant be empty!"
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).equals(false)){
                binding.emailInput.error = "Please provide valid email!"
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
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

        setContentView(binding.root)
    }
}