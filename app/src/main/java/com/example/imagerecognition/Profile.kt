package com.example.imagerecognition

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Profile : AppCompatActivity() {
    private lateinit var changeInfoButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        changeInfoButton = findViewById(R.id.changeInfoButton)
        changeInfoButton.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}