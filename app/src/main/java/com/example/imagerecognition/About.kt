package com.example.imagerecognition

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.imagerecognition.databinding.ActivityAboutBinding

class About : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        binding.btnBackToMain.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
            overridePendingTransition(R.anim.slide_out, R.anim.slide_in)
        }

        setContentView(binding.root)
    }
}