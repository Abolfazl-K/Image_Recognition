package com.example.imagerecognition

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.imagerecognition.databinding.ActivityChangeProfilePictureBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

@Suppress("DEPRECATION")
class ChangeProfilePicture : AppCompatActivity() {
    private val GALLERY_REQUEST_CODE = 1
    private val CAMERA_REQUEST_CODE = 2

    private lateinit var binding: ActivityChangeProfilePictureBinding
    private lateinit var user: FirebaseUser
    private lateinit var imageUri: Uri
    private lateinit var downloadUrl: String


    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile_picture)
        binding = ActivityChangeProfilePictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = FirebaseAuth.getInstance().currentUser!!
        val database = FirebaseFirestore.getInstance()
        val userReference = database.collection("Users").document(user.uid)
        userReference.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val imageUrl = document.getString("profilePicture")
                    Glide.with(this)
                        .load(imageUrl)
                        .error(R.drawable.baseline_broken_image_24)
                        .placeholder(R.drawable.placeholder_image)
                        .circleCrop()
                        .into(binding.profileImageView)
                }
            }

        val storageRef = FirebaseStorage.getInstance().reference
        val profilePicturesRef = storageRef.child("profilePictures/${user.uid}.jpg")

        binding.changePhotoButton.setOnClickListener {
            showImagePickerDialog()
        }

        binding.saveButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            profilePicturesRef.putFile(imageUri)
                .addOnSuccessListener {
                    binding.progressBar.visibility = View.GONE
                    // Image uploaded successfully
                    // You can now save the image URL to the user's profile
                    profilePicturesRef.downloadUrl.addOnSuccessListener {
                        val db = FirebaseFirestore.getInstance()
                        val userRef = db.collection("Users").document(user.uid)
                        downloadUrl = it.toString()
                        val updateMap: MutableMap<String, Any> = HashMap()
                        updateMap["profilePicture"] = downloadUrl
                        userRef.update(updateMap).addOnCompleteListener{
                            Toast.makeText(this, "Picture uploaded successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Profile::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }.addOnFailureListener {
                            Toast.makeText(this, "Something happened when uploading the downloadUrl into fireStore", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Something happened to the imageUrl!, please try again later.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    // Handle any errors that occur during the upload
                    Toast.makeText(this, "An error has happened during the upload!", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
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


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    if(data!= null){
                        imageUri = data.data!!
                        Glide.with(this)
                            .load(imageUri)
                            .error(R.drawable.baseline_broken_image_24)
                            .placeholder(R.drawable.placeholder_image)
                            .into(binding.profileImageView)
                    }else{
                        Toast.makeText(this, "Please choose a picture!", Toast.LENGTH_SHORT).show()
                    }
                    // Continue with the upload process
                }
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imageUri = saveImageToInternalStorage(imageBitmap)
                    Glide.with(this)
                        .load(imageUri)
                        .error(R.drawable.baseline_broken_image_24)
                        .placeholder(R.drawable.placeholder_image)
                        .into(binding.profileImageView)
                    // Continue with the upload process
                }
            }
        }
    }
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "${System.currentTimeMillis()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.fromFile(file)
    }
}