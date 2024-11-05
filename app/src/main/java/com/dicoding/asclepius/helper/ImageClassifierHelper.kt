package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class ImageClassifierHelper(
    private val context: Context,
    var threshold: Float = 0.1f,
    var maxResults: Int = 2,
    val modelName: String = "cancer_classification.tflite",
    private val imageClassifierListener: ClassifierListener?

) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)

        if (CompatibilityList().isDelegateSupportedOnThisDevice){
            baseOptionsBuilder.useGpu()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            baseOptionsBuilder.useNnapi()
        } else {

            baseOptionsBuilder.setNumThreads(4)
        }
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                 optionsBuilder.build()
            )
        } catch (e: Exception) {
            val errorMessage = "Error loading model: ${e.message}"
            imageClassifierListener?.onError(errorMessage)
            Log.e(TAG, errorMessage, e)
        }
    }

    fun classify(image: Bitmap) {
        try {
            if (imageClassifier == null) {
                setupImageClassifier()
            }

            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .build()

            val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

            imageClassifier?.classify(tensorImage)?.let {
                imageClassifierListener?.onResults(it, image.width, image.height)
            } ?: run {
                imageClassifierListener?.onError("Classifier returned no results")
            }
        } catch (e: Exception) {
            val errorMessage = "Error during classification: ${e.message}"
            imageClassifierListener?.onError(errorMessage)
            Log.e(TAG, errorMessage, e)
        }
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>,
            imageWidth: Int,
            imageHeight: Int
        )
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}