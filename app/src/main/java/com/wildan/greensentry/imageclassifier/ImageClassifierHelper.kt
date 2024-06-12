package com.wildan.greensentry.imageclassifier

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException

class ImageClassifierHelper(
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var interpreter: Interpreter? = null
    private var inputImageBuffer: TensorImage? = null
    private var outputBuffer: TensorBuffer? = null

    // Define labels for two categories: Organik and Non Organik
    private val labels = listOf("Organik", "Non Organik")

    init {
        setupInterpreter()
    }

    private fun setupInterpreter() {
        try {
            val model = FileUtil.loadMappedFile(context, "model_waste.tflite")
            val options = Interpreter.Options()
            interpreter = Interpreter(model, options)

            // Initialize the input and output buffers
            inputImageBuffer = TensorImage(DataType.FLOAT32)
            outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, labels.size), DataType.FLOAT32)
        } catch (e: IOException) {
            Log.e(TAG, "Error initializing TensorFlow Lite interpreter: ${e.message}")
            classifierListener?.onError("Error initializing TensorFlow Lite interpreter: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error initializing TensorFlow Lite interpreter: ${e.message}")
            classifierListener?.onError("Unexpected error initializing TensorFlow Lite interpreter: ${e.message}")
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        if (interpreter == null) {
            classifierListener?.onError("TensorFlow Lite interpreter is not initialized.")
            return
        }

        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }.copy(Bitmap.Config.ARGB_8888, true)

        Log.d(TAG, "Bitmap width: ${bitmap.width}, height: ${bitmap.height}")
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(64, 64, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)) // Resize sesuai dengan model Anda
            .add(NormalizeOp(127.5f, 127.5f)) // Normalisasi dengan mean dan stddev yang sesuai
            .build()

        inputImageBuffer?.load(bitmap)
        inputImageBuffer = imageProcessor.process(inputImageBuffer)
        Log.d(TAG, "Input image buffer dimensions: ${inputImageBuffer?.width}, ${inputImageBuffer?.height}")

        val inferenceStartTime = SystemClock.uptimeMillis()
        interpreter?.run(inputImageBuffer?.buffer, outputBuffer?.buffer?.rewind())
        val inferenceTime = SystemClock.uptimeMillis() - inferenceStartTime

        val probabilities = outputBuffer?.floatArray
        Log.d(TAG, "Probabilities: ${probabilities?.contentToString()}")

        val maxIndex = probabilities?.let {
            it.indices.maxByOrNull { index -> it[index] }
        }

        val resultLabel = maxIndex?.let { labels.getOrNull(it) }
        Log.d(TAG, "Result label: $resultLabel")
        classifierListener?.onResults(resultLabel, inferenceTime)
    }

    interface ClassifierListener {
        fun onError(message: String)
        fun onResults(resultLabel: String?, inferenceTime: Long)
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}
