package com.nandaadisaputra.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val weatherMLModel: WeatherMLModel
) : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResult>()
    val weatherData: LiveData<WeatherResult> get() = _weatherData

    private val _weatherRecommendation = MutableLiveData<String>()
    val weatherRecommendation: LiveData<String> get() = _weatherRecommendation

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    suspend fun fetchWeatherWithRecommendation(city: String, apiKey: String) {
        try {
            // Mendapatkan data cuaca dengan rekomendasi
            val weatherResult = weatherRepository.fetchWeather(city, apiKey)

            // Update LiveData dengan data cuaca
            _weatherData.postValue(weatherResult)

            // Mengambil rekomendasi cuaca dari model ML dan memperbarui LiveData
            val recommendation = fetchRecommendation(city, apiKey)
            _weatherRecommendation.postValue(recommendation)

        } catch (e: Exception) {
            // Menangani error jika terjadi masalah saat mengambil data
            _error.postValue("Error fetching weather data: ${e.message}")
        }
    }

    // Fungsi untuk mendapatkan rekomendasi cuaca dari model ML
    suspend fun fetchRecommendation(city: String, apiKey: String): String {
        return weatherMLModel.predictRecommendation(city, apiKey)
    }
}
