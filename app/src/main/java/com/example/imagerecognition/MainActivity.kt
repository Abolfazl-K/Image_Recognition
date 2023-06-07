package com.example.imagerecognition

import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imagerecognition.databinding.ActivityMainBinding
import com.example.imagerecognition.databinding.NavHeaderBinding
import com.example.imagerecognition.ml.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderBinding: NavHeaderBinding
    private var user = FirebaseAuth.getInstance().currentUser!!
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var imageUri: Uri
    private lateinit var image: Bitmap

    private val GALLERY_REQUEST_CODE = 1
    private val CAMERA_REQUEST_CODE = 2

    private val imageSize = 224

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navHeaderBinding = NavHeaderBinding.inflate(layoutInflater)

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

        binding.btnProcess.isEnabled = false

        binding.btnChooseImage.setOnClickListener {
            showImagePickerDialog()
        }

        binding.btnProcess.setOnClickListener {
            classify(image)
        }

        setContentView(binding.root)
    }

    private fun classify(imageUri: Bitmap) {
        val model = Model.newInstance(applicationContext)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)


        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val resizedImage: Bitmap = imageUri

        // Convert the Bitmap to input data and store it in the ByteBuffer
        val pixels = IntArray(imageSize * imageSize)
        resizedImage.getPixels(pixels, 0, resizedImage.width, 0, 0, resizedImage.width, resizedImage.height)

        var pixel = 0
        for (i in 0 until 224) {
            for (j in 0 until 224) {
                val pixelValue = pixels[pixel++]

                // Extract RGB channels from the pixel value
                val red = (pixelValue shr 16) and 0xFF
                val green = (pixelValue shr 8) and 0xFF
                val blue = pixelValue and 0xFF

                // Normalize pixel values and add them to the ByteBuffer
                val normalizedRed = (red.toFloat() / 255.0f)
                val normalizedGreen = (green.toFloat() / 255.0f)
                val normalizedBlue = (blue.toFloat() / 255.0f)

                byteBuffer.putFloat(normalizedRed)
                byteBuffer.putFloat(normalizedGreen)
                byteBuffer.putFloat(normalizedBlue)
            }
        }

        // Rewind the ByteBuffer to prepare for reading
        byteBuffer.rewind()

        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val outputValues = outputFeature0.floatArray
        val maxProbabilityIndex = outputValues.indices.maxByOrNull { outputValues[it] } ?: -1

        val classLabels = arrayOf("Apple___Apple_scab"
            , "Apple___Black_rot"
            , "Apple___Cedar_apple_rust"
            , "Apple___healthy"
            , "Blueberry___healthy"
            , "Cherry_(including_sour)___Powdery_mildew"
            , "Cherry_(including_sour)___healthy"
            , "Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot"
            , "Corn_(maize)___Common_rust_"
            , "Corn_(maize)___Northern_Leaf_Blight"
            , "Corn_(maize)___healthy"
            , "Grape___Black_rot"
            , "Grape___Esca_(Black_Measles)"
            , "Grape___Leaf_blight_(Isariopsis_Leaf_Spot)"
            , "Grape___healthy"
            , "Orange___Haunglongbing_(Citrus_greening)"
            , "Peach___Bacterial_spot"
            , "Peach___healthy"
            , "Pepper,_bell___Bacterial_spot"
            , "Pepper,_bell___healthy"
            , "Potato___Early_blight"
            , "Potato___Late_blight"
            , "Potato___healthy"
            , "Raspberry___healthy"
            , "Soybean___healthy"
            , "Squash___Powdery_mildew"
            , "Strawberry___Leaf_scorch"
            , "Strawberry___healthy"
            , "Tomato___Bacterial_spot"
            , "Tomato___Early_blight"
            , "Tomato___Late_blight"
            , "Tomato___Leaf_Mold"
            , "Tomato___Septoria_leaf_spot"
            , "Tomato___Spider_mites Two-spotted_spider_mite"
            , "Tomato___Target_Spot"
            , "Tomato___Tomato_Yellow_Leaf_Curl_Virus"
            , "Tomato___Tomato_mosaic_virus"
            , "Tomato___healthy")
        val predictedClassLabel = classLabels[maxProbabilityIndex]

        binding.resaultText.text = predictedClassLabel

        // Releases model resources if no longer used.
        model.close()
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

    override fun onStart() {
        super.onStart()

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

                        image = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                        image = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize)

//                        binding.pickedImageView.setImageBitmap(image)

                        Glide.with(this)
                            .load(imageUri)
                            .error(R.drawable.baseline_broken_image_24)
                            .apply(RequestOptions.overrideOf(500, 500))
                            .apply(RequestOptions.centerCropTransform())
                            .placeholder(R.drawable.placeholder_image)
                            .into(binding.pickedImageView)
                        binding.btnProcess.isEnabled = true
                    }else{
                        Toast.makeText(this, "Please choose a picture!", Toast.LENGTH_SHORT).show()
                    }
                    // Continue with the upload process
                }
                CAMERA_REQUEST_CODE -> {
                    image = data?.extras?.get("data") as Bitmap

                    image = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize)
                    imageUri = saveImageToInternalStorage(image)

                    Glide.with(this)
                        .load(imageUri)
                        .error(R.drawable.baseline_broken_image_24)
                        .apply(RequestOptions.overrideOf(500, 500))
                        .apply(RequestOptions.centerCropTransform())
                        .placeholder(R.drawable.placeholder_image)
                        .into(binding.pickedImageView)

                    binding.btnProcess.isEnabled = true
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