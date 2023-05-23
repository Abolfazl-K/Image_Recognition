package com.example.imagerecognition

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imagerecognition.databinding.ActivityMainBinding
import com.example.imagerecognition.databinding.NavHeaderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderBinding: NavHeaderBinding
    private lateinit var user:FirebaseUser
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navHeaderBinding = NavHeaderBinding.inflate(layoutInflater)

        user = auth.currentUser!!
        val database = FirebaseFirestore.getInstance()
        database.collection("Users").document(user.uid)

        user = FirebaseAuth.getInstance().currentUser!!
        val userReference = database.collection("Users").document(user.uid)
        userReference.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val imageUrl = document.getString("profilePicture")
                    Glide.with(this)
                        .load(imageUrl)
                        .apply(RequestOptions.overrideOf(250, 250))
                        .apply(RequestOptions.centerCropTransform())
                        .into(navHeaderBinding.navProfilePic)
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    navHeaderBinding.navUserName.text = getFullName(firstName, lastName)
                }
            }

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
                R.id.settings -> {
                    val intent = Intent(this, Settings::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
                R.id.about -> {
                    val intent = Intent(this, About::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
            }
            true
        }
        binding.navView.addHeaderView(navHeaderBinding.root)

        binding.userInfo.text = user.email.toString()

        setContentView(binding.root)
    }

    private fun getFullName(firstName: String?, lastName: String?): String {
        return "$firstName $lastName"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}