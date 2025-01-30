package com.nandaadisaputra.weather.repository

import com.nandaadisaputra.weather.model.WeatherData
import com.nandaadisaputra.weather.model.WeatherMLModel
import com.nandaadisaputra.weather.network.WeatherResult
import com.nandaadisaputra.weather.network.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(private val api: WeatherService, private val mlModel: WeatherMLModel) {

    // Fungsi untuk mengambil data cuaca dari API dan memberikan rekomendasi dari model ML
    suspend fun fetchWeather(cityName: String, apiKey: String): WeatherResult {
        return withContext(Dispatchers.IO) {
            // Memastikan API key tidak kosong
            if (apiKey.isBlank()) {
                throw IllegalArgumentException("API key tidak boleh kosong")
            }

            try {
                // Mengambil data cuaca dari API
                val response = api.getWeather(cityName, apiKey, "metric")

                // Memastikan response valid dan memiliki data cuaca yang lengkap
                val main = response?.main ?: throw IllegalStateException("Data cuaca utama tidak tersedia")
                val wind = response.wind ?: throw IllegalStateException("Data kecepatan angin tidak tersedia")

                // Membuat objek WeatherData berdasarkan data dari response API
                val weatherData = WeatherData(
                    temp = main.temp,
                    humidity = main.humidity,
                    windSpeed = wind.speed,
                    pressure = main.pressure
                )

                // Menggunakan fungsi `predictWithModel` untuk mendapatkan rekomendasi dari model ML
                val recommendation = try {
                    mlModel.predictWithModel(weatherData)
                } catch (e: Exception) {
                    // Menangani kesalahan model ML dan memberikan rekomendasi default
                    "Tidak Terdefinisi"
                }

                // Mengembalikan hasil cuaca dengan rekomendasi cuaca berdasarkan model
                WeatherResult(
                    city = response.name,
                    temp = weatherData.temp,
                    humidity = weatherData.humidity,
                    windSpeed = weatherData.windSpeed,
                    pressure = weatherData.pressure,
                    weatherCategory = recommendation
                )

            } catch (e: Exception) {
                // Menangani kesalahan umum dan memberikan pesan kesalahan yang sesuai
                throw WeatherRepositoryException("Gagal mendapatkan data cuaca: ${e.message}")
            }!!
        }
    }
}

// Exception kustom untuk menangani kesalahan dalam repository
class WeatherRepositoryException(message: String) : Exception(message)
