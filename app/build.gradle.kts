plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

android {
    namespace = "moroshkinem.healthylife"
    compileSdk = 36

    defaultConfig {
        applicationId = "moroshkinem.test"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.i18n)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.databinding.adapters)
    implementation(libs.androidx.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)  // Для ViewModel
    implementation(libs.room.runtime)  // Room runtime
    implementation(libs.room.ktx)  // Room с Coroutines
    kapt(libs.room.compiler)  // Для генерации кода Room
    implementation(libs.hilt.android)  // Hilt
    kapt(libs.hilt.compiler)  // Для генерации кода Hilt
    implementation(libs.hilt.navigation.compose)  // Hilt с навигацией
    implementation(libs.navigation.compose)  // Navigation Compose
    implementation(libs.kotlinx.coroutines.android)  // Coroutines

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.metadata.jvm)
    implementation(libs.androidx.work.runtime)
    implementation(libs.mpandroidchart)
    implementation(libs.androidx.compose.material.icons.extended)

}