plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") // ok manter mesmo com código Java
}

android {
    namespace = "com.example.performind"       // ✅ padrão único
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.performind"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // BuildConfig com Supabase (puxado de gradle.properties)
        buildConfigField("String", "SUPABASE_URL", "\"${project.findProperty("SUPABASE_URL") ?: ""}\"")
        buildConfigField("String", "SUPABASE_ANON", "\"${project.findProperty("SUPABASE_ANON") ?: ""}\"")

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
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        buildConfig = true       // ✅ resolve seu erro anterior
        viewBinding = true
    }

    packaging {
        resources.excludes += "META-INF/DEPENDENCIES"
    }
}

dependencies {
    // AndroidX / Material
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Retrofit + OkHttp + Moshi
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ExoPlayer (se usar vídeo)
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // Coroutines (opcional)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
