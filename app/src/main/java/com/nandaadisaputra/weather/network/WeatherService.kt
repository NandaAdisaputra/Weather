package com.nandaadisaputra.weather.network

// Mengimpor BuildConfig untuk mengakses konstanta BASE_URL
import com.nandaadisaputra.weather.BuildConfig.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Interface untuk mendefinisikan endpoint yang akan dipanggil oleh Retrofit
interface WeatherService {

    // Mendefinisikan endpoint API untuk mendapatkan data cuaca berdasarkan nama kota
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String, // Parameter query untuk nama kota
        @Query("appid") apiKey: String, // API key untuk otentikasi
        @Query("units") units: String // Parameter untuk menentukan satuan suhu (misalnya "metric" untuk Celcius)
    ): WeatherResponse? // Fungsi ini mengembalikan response dalam bentuk WeatherResponse, bisa null jika gagal

    // Companion object untuk membuat instance Retrofit dan mengonfigurasi WeatherService
    companion object {
        // Fungsi untuk membuat dan mengonfigurasi instance Retrofit
        fun create(): WeatherService {
            val retrofit = Retrofit.Builder()
                // Menggunakan BASE_URL yang disediakan oleh BuildConfig, diambil dari file build.gradle
                .baseUrl(BASE_URL)
                // Menambahkan converter Gson untuk parsing response API menjadi objek Kotlin
                .addConverterFactory(GsonConverterFactory.create())
                .build() // Membangun objek Retrofit
            // Mengembalikan instance WeatherService yang siap digunakan untuk panggilan API
            return retrofit.create(WeatherService::class.java)
        }
    }
}
