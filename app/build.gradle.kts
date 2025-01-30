plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.nandaadisaputra.weather"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nandaadisaputra.weather"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Menambahkan API key dari gradle.properties
        val weatherApiKey: String = project.findProperty("WEATHER_API_KEY") as? String ?: ""
        buildConfigField("String", "WEATHER_API_KEY", "\"$weatherApiKey\"")

        // Menambahkan BASE_URL
        buildConfigField("String", "BASE_URL", "\"https://api.openweathermap.org/data/2.5/\"")


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true // Mengaktifkan BuildConfig
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Retrofit dan Converter Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // TensorFlow Lite
    implementation(libs.tensorflow.lite)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle ViewModel & LiveData
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    // Material Design Components
    implementation(libs.material)

    // Unit Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
