package com.nandaadisaputra.weather

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

// MainActivity adalah kelas utama yang menjalankan aplikasi cuaca
class MainActivity : AppCompatActivity() {

    // Deklarasi variabel UI yang digunakan untuk menampilkan input dan hasil
    private lateinit var cityInput: EditText // Input untuk nama kota
    private lateinit var resultText: TextView // Menampilkan hasil cuaca
    private lateinit var fetchButton: Button // Tombol untuk mengambil data cuaca
    private lateinit var predictionText: TextView // Menampilkan prediksi cuaca

    // Inisialisasi ViewModel dengan Factory yang menerima WeatherRepository dan WeatherMLModel
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(
            WeatherRepository(WeatherService.create(), WeatherMLModel(applicationContext)),
            WeatherMLModel(applicationContext)
        )
    }

    // Fungsi onCreate dijalankan saat activity dimulai
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Menetapkan layout untuk activity

        // Inisialisasi komponen UI dari layout XML
        cityInput = findViewById(R.id.city_input)
        resultText = findViewById(R.id.result_text)
        fetchButton = findViewById(R.id.fetch_button)
        predictionText = findViewById(R.id.prediction_text)

        // Memulai observasi terhadap perubahan data cuaca dari ViewModel
        observeViewModel()

        // Menambahkan event listener untuk tombol "fetchButton" ketika diklik
        fetchButton.setOnClickListener {
            // Mendapatkan nama kota dari input pengguna
            val cityName = cityInput.text.toString()

            // Memeriksa apakah input nama kota tidak kosong
            if (cityName.isNotEmpty()) {
                // Mengambil API Key dari BuildConfig (diambil dari gradle.properties)
                val apiKey = BuildConfig.WEATHER_API_KEY

                // Menggunakan Coroutine untuk memanggil fungsi fetchWeatherWithRecommendation secara asinkron
                lifecycleScope.launch {
                    // Memanggil fungsi fetchWeatherWithRecommendation dan fetch recommendation
                    viewModel.fetchWeatherWithRecommendation(city = cityName, apiKey = apiKey)
                }
            } else {
                // Menampilkan pesan jika input nama kota kosong
                resultText.text = getString(R.string.hint_city)
            }
        }
    }

    // Fungsi untuk mengamati perubahan data yang diterima oleh ViewModel
    private fun observeViewModel() {
        // Mengamati data cuaca yang diterima dari ViewModel
        viewModel.weatherData.observe(this, Observer { weather ->
            // Menampilkan informasi cuaca terformat
            resultText.text = weather.getFormattedWeatherInfo()

            // Menampilkan prediksi cuaca berdasarkan model machine learning
            viewModel.weatherRecommendation.observe(this, Observer { recommendation ->
                predictionText.text = if (!recommendation.isNullOrEmpty()) {
                    getString(R.string.weather_prediction) + " " + recommendation
                } else {
                    getString(R.string.weather_prediction)  // Hanya "Prediksi Cuaca:" tanpa nilai
                }
            })
        })

        // Mengamati error yang mungkin terjadi selama proses pengambilan data cuaca
        viewModel.error.observe(this, Observer { errorMessage ->
            // Menampilkan pesan error jika terjadi masalah dalam mengambil data
            resultText.text = errorMessage
        })
    }
}
