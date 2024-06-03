package com.wildan.greensentry

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.yalantis.ucrop.UCrop
import com.wildan.greensentry.databinding.ActivityClassificationBinding
import java.io.File

class ClassificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassificationBinding

    private var currentImageUri: Uri? = null

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                startCrop(it)
            } else {
                showToast(getString(R.string.failed_to_pick_image))
            }
        }

    private val cropResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                currentImageUri = resultUri
                showImage()
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                showToast(cropError?.message ?: getString(R.string.crop_error))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup button click listeners
        binding.galleryButton.setOnClickListener { startGallery() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "IMG_CROPPED.jpg"))
        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(100)
            setToolbarColor(resources.getColor(R.color.forest_green))
            setStatusBarColor(resources.getColor(R.color.forest_green))
        }
        val cropIntent = UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withOptions(options)
            .getIntent(this)
        cropResultLauncher.launch(cropIntent)
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
        updateButtonStatus()
    }

    private fun updateButtonStatus() {
        binding.analyzeButton.isEnabled = currentImageUri != null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
