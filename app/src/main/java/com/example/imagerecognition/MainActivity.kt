package com.example.imagerecognition

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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
        val userRef = database.collection("Users").document(user.uid)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userRef.get()
            .addOnSuccessListener { document ->
                if(document.exists()){
                    navHeaderBinding.navUserName.text = document.getString("fullName")
                }else{
                    Toast.makeText(this, "error getting the user information!", Toast.LENGTH_SHORT).show()
                }
            }

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                }
                R.id.settings -> {
                    val intent = Intent(this, Settings::class.java)
                    startActivity(intent)
                }
                R.id.about -> {
                    val intent = Intent(this, About::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        binding.navView.addHeaderView(navHeaderBinding.root)

        binding.userInfo.text = user.email.toString()

        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}