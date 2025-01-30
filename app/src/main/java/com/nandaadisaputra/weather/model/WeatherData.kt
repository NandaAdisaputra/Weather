package com.nandaadisaputra.weather.model

data class WeatherData(
    val temp: Float,              // Temperatur dalam Celsius
    val humidity: Int,            // Kelembaban dalam persen
    val windSpeed: Float,         // Kecepatan angin dalam km/h
    val pressure: Float,          // Tekanan udara dalam hPa
    val weatherCategory: String = "Tidak Terdefinisi"  // Kategori cuaca (Opsional)
) {
    // Validasi untuk memastikan data cuaca valid dan sesuai dengan batasan
    init {
        require(humidity in 0..100) { "Kelembaban harus antara 0 hingga 100" }
        require(temp >= -100 && temp <= 100) { "Suhu harus berada dalam rentang yang wajar" }
        require(windSpeed >= 0) { "Kecepatan angin tidak bisa negatif" }
        require(pressure >= 0) { "Tekanan udara tidak bisa negatif" }
    }

}
