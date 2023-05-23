package com.example.imagerecognition

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.imagerecognition.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Profile : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var storage: FirebaseStorage
    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseFirestore.getInstance()
        val userRef = database.collection("Users").document(currentUser!!.uid)

        storage = FirebaseStorage.getInstance()

        binding.profileImageView.setOnClickListener {
            val intent = Intent(this, ChangeProfilePicture::class.java)
            startActivity(intent)
        }


        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Get the firstName field from the document
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val email = FirebaseAuth.getInstance().currentUser!!.email
                    val age = document.get("age")
                    val address = document.getString("address")
                    val sex = document.getString("sex")
                    val imageUrl = document.getString("profilePicture")
                    binding.nameTextView.text = getFullName(firstName, lastName)
                    binding.emailTextView.text = email.toString()
                    binding.addressTextView.text = address.toString()
                    binding.ageTextView.text = age.toString()
                    binding.sexTextView.text = sex.toString()
                    Glide.with(this)
                        .load(imageUrl)
                        .error(R.drawable.baseline_broken_image_24)
                        .placeholder(R.drawable.placeholder_image)
                        .circleCrop()
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
        }

        binding.logoutButton.setOnClickListener {
            val customDialog = CustomDialog()
            customDialog.show(supportFragmentManager, "CustomDialog")
        }
        setContentView(binding.root)
    }
    private fun getFullName(firstName: String?, lastName: String?): String {
        return "$firstName $lastName"
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        super.onBackPressed()
    }
}