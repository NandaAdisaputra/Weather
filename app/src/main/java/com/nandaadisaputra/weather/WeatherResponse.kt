package com.nandaadisaputra.weather

import com.google.gson.annotations.SerializedName

// Data kelas untuk respon cuaca
data class WeatherResponse(
    val name: String,    // Nama kota
    val main: Main,      // Data utama cuaca (temperatur, kelembapan, tekanan)
    val weather: List<Weather>,  // Daftar kondisi cuaca
    val wind: Wind       // Kecepatan angin
)

data class Main(
    val temp: Float,    // Suhu dalam derajat Celsius
    val humidity: Int,  // Kelembapan dalam persentase
    val pressure: Float // Tekanan udara dalam hPa
)

data class Weather(
    val main: String,       // Nama kondisi cuaca (misalnya, "Clear", "Rain")
    @SerializedName("description") val description: String // Deskripsi kondisi cuaca (misalnya, "clear sky")
)

data class Wind(
    val speed: Float // Kecepatan angin dalam m/s
)
