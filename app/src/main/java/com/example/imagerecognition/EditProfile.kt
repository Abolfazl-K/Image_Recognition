package com.example.imagerecognition

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.imagerecognition.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class EditProfile : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)

        val genderOptions = resources.getStringArray(R.array.gender_options)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sexSpinner.adapter = adapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("Users").document(currentUser!!.uid)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Get the firstName field from the document
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val age = document.get("age")
                    val address = document.getString("address")
                    val sexValueIndex = when(document.getString("sex")){
                        "Choose" -> 0
                        "Male" -> 1
                        "Female" -> 2
                        else -> null
                    }
                    binding.sexSpinner.setSelection(sexValueIndex!!)
                    binding.firstNameEditText.setText(firstName.toString())
                    binding.lastNameEditText.setText(lastName.toString())
                    binding.ageInputEditText.setText(age.toString())
                    binding.addressInputEditText.setText(address.toString())
                } else {
                    Toast.makeText( this, "User document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText( this, "Error getting user document", Toast.LENGTH_SHORT).show()
            }

        binding.saveButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString()
            val lastName = binding.lastNameEditText.text.toString()
            val age = binding.ageInputEditText.text.toString()
            val address = binding.addressInputEditText.text.toString()
            val sex = binding.sexSpinner.selectedItem.toString()

            binding.progressBar.visibility = View.VISIBLE

            if(firstName.isEmpty() || firstName == "firstName"){
                binding.firstNameEditText.error = "First name cant be empty!"
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(lastName.isEmpty() || lastName == "lastName"){
                binding.firstNameEditText.error = "First name cant be empty!"
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(age.equals(null)){
                binding.ageInputEditText.error = "Age name cant be empty!"
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(address.isEmpty() || address == "address"){
                binding.addressInputEditText.error = "Address name cant be empty!"
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(sex == "Choose"){
                binding.sexError.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            binding.sexError.visibility = View.GONE

            val updateMap: MutableMap<String, Any> = HashMap()
            updateMap["firstName"] = firstName
            updateMap["lastName"] = lastName
            updateMap["address"] = address
            updateMap["age"] = age
            updateMap["sex"] = sex

            userRef.update(updateMap).addOnCompleteListener {
                binding.sexError.visibility = View.GONE
                if(it.isSuccessful){
                    Toast.makeText(
                        this,
                        "User information's updated successfully!",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val intent = Intent(this, Profile::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in_rev, R.anim.slide_out_rev)
                }else{
                    Toast.makeText(
                        this,
                        "Something went wrong!",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }

        setContentView(binding.root)
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_rev, R.anim.slide_out_rev)
    }
}