package com.dicoding.asclepius.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.dicoding.asclepius.data.database.AppDatabase
import com.dicoding.asclepius.data.model.ScanHistory
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.viewmodel.ResultViewModel
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {

    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val resultViewModel: ResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            threshold = 0.5f,
            maxResults = 2,
            imageClassifierListener = this
        )

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)


            try {
                val inputStream = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imageClassifierHelper.classify(bitmap)
            } catch (e: Exception) {
                Log.e("ResultActivity", "Error processing image", e)
                binding.resultText.text = "Error processing image: ${e.message}"
            }
        }
    }

    override fun onError(error: String) {
        runOnUiThread {
            Log.e("ResultActivity", "Classification error: $error")
            binding.resultText.text = "Error: $error"
        }
    }
    private fun getRecommendation(result: String): String {
        return when {
            result.contains("cancer", ignoreCase = true) -> {
                "REKOMENDASI:\n" +
                        "• Segera konsultasi ke dokter spesialis kulit\n" +
                        "• Hindari paparan sinar matahari berlebih\n" +
                        "• Dokumentasikan perubahan pada lesi kulit\n" +
                        "• Lakukan pemeriksaan lanjutan untuk konfirmasi diagnosis"
            }
            else -> {
                "REKOMENDASI:\n" +
                        "• Lakukan pemeriksaan kulit rutin\n" +
                        "• Gunakan tabir surya\n" +
                        "• Pantau perubahan pada kulit\n" +
                        "• Jaga kesehatan kulit dengan pola hidup sehat"
            }
        }
    }

//    private fun viewHistory() {
//        binding.viewHistoryButton.setOnClickListener {
//           this.startActivity(Intent(this, HistoryActivity::class.java))
//        }
//    }


    override fun onResults(results: List<Classifications>, imageWidth: Int, imageHeight: Int) {
        runOnUiThread {
            if (results.isNotEmpty() && results[0].categories.isNotEmpty()) {
                val builder = StringBuilder()
                builder.append("HASIL ANALISIS:\n\n")

                results[0].categories.forEach { category ->
                    val percentage = category.score * 100
                    builder.append("${category.label}: ")
                    builder.append(String.format("%.1f%%", percentage))
                    builder.append("\n\n")
                }


                builder.append(getRecommendation(results[0].categories[0].label))
            saveToHistory(builder.toString())

                binding.viewHistoryButton.setOnClickListener {
                    this.startActivity(Intent(this, HistoryActivity::class.java))
                }
                binding.resultText.text = builder.toString()
                binding.resultCard.visibility = View.VISIBLE
            } else {
                binding.resultText.text = "Tidak ada hasil klasifikasi"
                binding.resultCard.visibility = View.VISIBLE
            }
        }
    }


    private fun saveToHistory(result: String) {
        val timeStamp = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        val history = ScanHistory(
            imageUri = intent.getStringExtra(EXTRA_IMAGE_URI) ?: "",
            date = timeStamp,
            result = result,
            recommendation = getRecommendation(result)
        )
        // Save to database using ViewModel
        resultViewModel.saveToHistory(history)
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}