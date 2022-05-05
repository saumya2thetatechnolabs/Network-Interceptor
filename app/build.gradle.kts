plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.thetatechnolabs.networkinterceptorexample"
        minSdk = 24
        targetSdk = 32
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation(project(":networkInterceptor"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("android.arch.lifecycle:livedata:1.1.1")
    implementation("android.arch.lifecycle:viewmodel:1.1.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")

    implementation("androidx.activity:activity-ktx:1.4.0")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
}