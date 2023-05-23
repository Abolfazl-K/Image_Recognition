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

@Suppress("DEPRECATION")
class Profile : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val database = FirebaseFirestore.getInstance()
    private val userRef = database.collection("Users").document(currentUser!!.uid)

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.profileImageView.setOnClickListener {
            val intent = Intent(this, ChangeProfilePicture::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        binding.changeInfoButton.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_rev, R.anim.slide_out_rev)
        super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
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
    }
}