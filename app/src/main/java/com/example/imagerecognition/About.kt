package com.example.imagerecognition

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.imagerecognition.databinding.ActivityAboutBinding

@Suppress("DEPRECATION")
class About : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        binding.btnBackToMain.setOnClickListener {
            onBackPressed()
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