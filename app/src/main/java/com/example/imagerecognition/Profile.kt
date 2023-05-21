package com.example.imagerecognition

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.imagerecognition.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Profile : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("Users").document(currentUser!!.uid)

        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.profileImageView.setOnClickListener {
            val intent = Intent(this, ChangeProfilePicture::class.java)
            startActivity(intent)
        }


        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Get the firstName field from the document
                    val name = document.getString("fullName")
                    val email = FirebaseAuth.getInstance().currentUser!!.email
                    val age = document.get("age")
                    val address = document.getString("address")
                    val sex = document.getString("sex")
                    val imageUrl = document.getString("profileImageUrl")
                    binding.nameTextView.text = name.toString()
                    binding.emailTextView.text = email.toString()
                    binding.addressTextView.text = address.toString()
                    binding.ageTextView.text = age.toString()
                    binding.sexTextView.text = sex.toString()
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.profileImageView)
                } else {
                    Toast.makeText( this, "User document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText( this, "Error getting user document", Toast.LENGTH_SHORT).show()
            }

        binding.changeInfoButton.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
            finish()
        }

        binding.logoutButton.setOnClickListener {
            val customDialog = CustomDialog()
            customDialog.show(supportFragmentManager, "CustomDialog")
        }
        setContentView(binding.root)
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}