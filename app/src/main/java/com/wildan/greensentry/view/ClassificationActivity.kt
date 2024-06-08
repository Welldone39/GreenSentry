package com.wildan.greensentry

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.yalantis.ucrop.UCrop
import com.wildan.greensentry.databinding.ActivityClassificationBinding
import com.wildan.greensentry.imageclassifier.ImageClassifierHelper
import com.wildan.greensentry.imageclassifier.ImageClassifierHelper.ClassifierListener
import org.tensorflow.lite.task.vision.detector.Detection
import java.io.File

class ClassificationActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {

    private lateinit var binding: ActivityClassificationBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

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

        // Setup ImageClassifierHelper
        imageClassifierHelper = ImageClassifierHelper(context = this, classifierListener = this)

        // Setup button click listeners
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }

        updateButtonStatus()
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

    private fun analyzeImage() {
        if (currentImageUri != null) {
            binding.progressIndicator.visibility = View.VISIBLE
            imageClassifierHelper.classifyStaticImage(currentImageUri!!)
        } else {
            showToast(getString(R.string.select_an_image_first))
        }
    }

    private fun updateButtonStatus() {
        binding.analyzeButton.isEnabled = currentImageUri != null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onError(message: String) {
        binding.progressIndicator.visibility = View.GONE
        showToast(message)
    }

    override fun onResults(results: List<Detection>?, inferenceTime: Long) {
        binding.progressIndicator.visibility = View.GONE
        results?.forEach { detection ->
            val category = detection.categories.firstOrNull()
            category?.let {
                showToast("Detected: ${it.label}, Confidence: ${it.score}")
            }
        }
        showToast("Inference time: $inferenceTime ms")
    }
}

