package com.nandaadisaputra.weather

data class WeatherResult(
    val city: String,       // Nama kota
    val temp: Float,        // Suhu dalam derajat Celsius
    val humidity: Int,      // Kelembapan dalam persentase
    val windSpeed: Float,   // Kecepatan angin dalam m/s
    val pressure: Float,    // Tekanan udara dalam hPa
    val weatherCategory: String // Kategori cuaca (misalnya: "Clear", "Rain")
)

// Fungsi untuk memformat cuaca menjadi string yang bisa ditampilkan
fun WeatherResult.getFormattedWeatherInfo(): String {
    return "Kota: $city\n" +
            "Suhu: $temp°C\n" +
            "Kelembaban: $humidity%\n" +
            "Kecepatan Angin: $windSpeed m/s\n" +
            "Tekanan: $pressure hPa\n" +
            "Kondisi: $weatherCategory"
}
