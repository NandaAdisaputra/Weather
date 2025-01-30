package com.nandaadisaputra.weather.model

import android.content.Context
import com.nandaadisaputra.weather.network.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.pow
import kotlin.math.sqrt

class WeatherMLModel(context: Context) {

    // Menggunakan lazy untuk menunda pemuatan model hingga diperlukan
    private val model: Interpreter by lazy { Interpreter(loadModelFile(context)) }

    // Memuat model dari file TensorFlow Lite dengan penanganan error
    private fun loadModelFile(context: Context): MappedByteBuffer {
        return try {
            context.assets.openFd("weather_model.tflite").use { assetFileDescriptor ->
                FileInputStream(assetFileDescriptor.fileDescriptor).use { fileInputStream ->
                    fileInputStream.channel.map(
                        FileChannel.MapMode.READ_ONLY,
                        assetFileDescriptor.startOffset,
                        assetFileDescriptor.declaredLength
                    )
                }
            }
        } catch (e: Exception) {
            // Mengembalikan exception dengan pesan yang jelas jika terjadi kesalahan
            throw RuntimeException("Gagal memuat model cuaca: ${e.message}")
        }
    }

    // Fungsi untuk menghitung jarak Euclidean antara dua data cuaca
    private fun euclideanDistance(data1: WeatherData, data2: WeatherData): Float {
        return sqrt(
            ((data1.temp - data2.temp).toDouble().pow(2) +
                    (data1.humidity - data2.humidity).toDouble().pow(2) +
                    (data1.windSpeed - data2.windSpeed).toDouble().pow(2) +
                    (data1.pressure - data2.pressure).toDouble().pow(2)).toFloat()
        )
    }

    // Fungsi untuk memberikan rekomendasi cuaca berdasarkan KNN
    private fun knnRecommendation(weatherData: WeatherData): String {
        // Data cuaca historis untuk digunakan dalam KNN
        val historicalWeatherData = listOf(
            WeatherData(30f, 75, 10f, 1010f, "Cerah"),
            WeatherData(25f, 80, 5f, 1020f, "Mendung"),
            WeatherData(35f, 60, 15f, 1005f, "Hujan Ringan"),
            WeatherData(28f, 70, 8f, 1015f, "Berawan"),
            WeatherData(22f, 85, 3f, 1018f, "Hujan Deras"),
            WeatherData(18f, 90, 12f, 1008f, "Angin Kencang"),
            WeatherData(20f, 95, 4f, 1022f, "Kabut")
        )

        // Menemukan 3 tetangga terdekat berdasarkan jarak Euclidean
        val nearestNeighbors = historicalWeatherData
            .map { it to euclideanDistance(weatherData, it) }
            .sortedBy { it.second } // Urutkan berdasarkan jarak terdekat
            .take(3)

        // Menghitung kategori cuaca yang paling sering muncul di antara tetangga terdekat
        val weatherCategoryCount = nearestNeighbors.groupingBy { it.first.weatherCategory }
            .eachCount()

        // Mengembalikan kategori cuaca terbanyak, atau "Tidak Terdefinisi" jika tidak ada
        return weatherCategoryCount.maxByOrNull { it.value }?.key ?: "Tidak Terdefinisi"
    }

    // Fungsi untuk mengambil data cuaca dari API dan memberikan rekomendasi
    suspend fun predictRecommendation(city: String, apiKey: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Mengambil data cuaca dari API
                val response = WeatherService.create().getWeather(city, apiKey, "metric")
                    ?: throw RuntimeException("Tidak ada respons dari API cuaca")

                response.main?.let {
                    // Membuat objek WeatherData untuk prediksi
                    val weatherData = WeatherData(
                        temp = it.temp,
                        humidity = it.humidity,
                        windSpeed = response.wind.speed,
                        pressure = it.pressure
                    )
                    // Menggunakan metode KNN untuk rekomendasi cuaca
                    knnRecommendation(weatherData)
                } ?: "Data cuaca tidak lengkap."
            } catch (e: Exception) {
                // Menangani error dan memberikan pesan yang jelas
                "Gagal mendapatkan data cuaca: ${e.message}"
            }
        }
    }

    // Fungsi untuk melakukan prediksi cuaca menggunakan model TensorFlow Lite
    fun predictWithModel(weatherData: WeatherData): String {
        return try {
            // Menyiapkan input untuk model (data cuaca)
            val input = floatArrayOf(
                weatherData.temp,
                weatherData.humidity.toFloat(),
                weatherData.windSpeed,
                weatherData.pressure.toFloat()
            )

            // Menyiapkan output untuk model
            val output = Array(1) { FloatArray(1) }
            model.run(input, output)

            // Mengembalikan kategori cuaca berdasarkan hasil output model
            when (output[0][0].toInt()) {
                0 -> "Cerah"
                1 -> "Mendung"
                2 -> "Hujan Ringan"
                3 -> "Hujan Deras"
                4 -> "Berawan"
                5 -> "Angin Kencang"
                6 -> "Kabut"
                else -> "Tidak Terdefinisi"
            }
        } catch (e: Exception) {
            // Menangani error dan memberikan pesan yang jelas
            "Error dalam prediksi dengan model: ${e.message}"
        }
    }
}
