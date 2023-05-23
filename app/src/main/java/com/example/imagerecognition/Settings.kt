package com.example.imagerecognition

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imagerecognition.databinding.ActivitySettingsBinding

@Suppress("DEPRECATION")
class Settings : AppCompatActivity() {
    private lateinit var binding:ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.logoutButton.setOnClickListener {
            val customDialog = CustomDialog()
            customDialog.show(supportFragmentManager, "CustomDialog")
        }
        binding.editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
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
        finish()
        overridePendingTransition(R.anim.slide_in_rev, R.anim.slide_out_rev)
        super.onBackPressed()
    }
}