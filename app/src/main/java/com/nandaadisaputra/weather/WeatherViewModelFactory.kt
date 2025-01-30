package com.nandaadisaputra.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WeatherViewModelFactory(
    private val repository: WeatherRepository,
    private val mlModel: WeatherMLModel
) : ViewModelProvider.Factory {

    // Fungsi untuk membuat instance dari WeatherViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Mengecek apakah kelas ViewModel yang diminta adalah WeatherViewModel
        return when {
            modelClass.isAssignableFrom(WeatherViewModel::class.java) -> {
                WeatherViewModel(repository, mlModel) as T // Mengembalikan instance WeatherViewModel
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
