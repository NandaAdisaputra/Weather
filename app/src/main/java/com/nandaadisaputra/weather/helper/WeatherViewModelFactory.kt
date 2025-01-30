package com.nandaadisaputra.weather.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nandaadisaputra.weather.model.WeatherMLModel
import com.nandaadisaputra.weather.repository.WeatherRepository
import com.nandaadisaputra.weather.ui.WeatherViewModel

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
