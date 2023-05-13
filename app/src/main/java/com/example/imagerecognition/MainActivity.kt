package com.example.imagerecognition

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val auth = FirebaseAuth.getInstance()
        val btnLogout: Button = findViewById(R.id.btn_logout)
        val userInfo:TextView = findViewById(R.id.userInfo)
        val user = auth.currentUser

        if(user == null){
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }else{
            userInfo.text = user.email.toString()
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}