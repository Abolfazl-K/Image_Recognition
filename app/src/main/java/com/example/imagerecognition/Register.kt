package com.example.imagerecognition

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.imagerecognition.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class Register : AppCompatActivity() {

    private val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding:ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        binding.loginNow.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_rev, R.anim.slide_out_rev)
        }

        binding.btnRegister.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            val email:String = binding.emailInput.text.toString()

            val pass1:String = binding.registerPass1.text.toString()
            val pass2:String = binding.registerPass2.text.toString()

            if(email.isEmpty()){
                binding.emailInput.error = "Email cant be empty!"
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailInput.error = "Please provide valid email!"
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(pass1.isEmpty() || pass2.isEmpty()){
                if(pass1.isEmpty() && pass2.isEmpty()){
                    binding.registerPass1.error = "Password cant be empty!"
                    binding.registerPass2.error = "Password cant be empty!"
                }else if(pass1.isEmpty()){
                    binding.registerPass1.error = "Password cant be empty!"
                }else{
                    binding.registerPass2.error = "Password cant be empty!"
                }
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(pass1 != pass2){
                binding.registerPass1.error = "Passwords are not the same!"
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(pass1.length < 6){
                binding.registerPass1.error = "Passwords must be at least 6 characters!"
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }


            mAuth.createUserWithEmailAndPassword(email, pass1)
                .addOnCompleteListener(this) { task ->
                    binding.progressBar.visibility = View.GONE

                    if (task.isSuccessful) {
                        val currentUser = mAuth.currentUser
                        val user = User(firstName = "firstName", lastName = "lastName", age = 18, address = "address", sex = "Choose", profilePicture = "")
                        val db = FirebaseFirestore.getInstance()
                        val userRef = db.collection("Users").document(currentUser!!.uid)
                        userRef.set(user).addOnCompleteListener {
                            currentUser.sendEmailVerification()
                            Toast.makeText(
                                this,
                                "User registered successfully!, please verify your email.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            val intent = Intent(this, Login::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_rev, R.anim.slide_out_rev)
                        }.addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Something bad happened!, Try again later",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
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
        setContentView(binding.root)
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_rev, R.anim.slide_out_rev)
    }
}