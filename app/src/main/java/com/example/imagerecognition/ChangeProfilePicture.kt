package com.example.imagerecognition

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.imagerecognition.databinding.ActivityChangeProfilePictureBinding
import com.google.firebase.storage.FirebaseStorage

class ChangeProfilePicture : AppCompatActivity() {
    private val GALLERY_REQUEST_CODE = 1
    private val CAMERA_REQUEST_CODE = 2

    private lateinit var binding: ActivityChangeProfilePictureBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var selectedImg: Uri
    private lateinit var dialog: AlertDialog.Builder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile_picture)
        binding = ActivityChangeProfilePictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()

        binding.changePhotoButton.setOnClickListener {
            showImagePickerDialog()
        }

        binding.saveButton.setOnClickListener {

        }
    }
    private fun showImagePickerDialog() {
        val options = arrayOf("Gallery", "Camera")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog: DialogInterface, which: Int ->
            when (which) {
                0 -> openGallery()
                1 -> openCamera()
            }
            dialog.dismiss()
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data != null){
            selectedImg = data.data!!
            binding.profileImageView.setImageURI(selectedImg)
        }
    }
}