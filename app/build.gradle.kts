plugins {
    id("com.android.application")
    // Se o projeto for 100% Java, pode remover esta linha
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.seu.pacote" // 🟢 ajuste para o package real do seu app
    compileSdk = 35

    defaultConfig {
        applicationId = "com.seu.pacote" // 🟢 mesmo package base
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // ✅ Supabase config injetada em BuildConfig
        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${project.findProperty("SUPABASE_URL") ?: ""}\""
        )
        buildConfigField(
            "String",
            "SUPABASE_ANON",
            "\"${project.findProperty("SUPABASE_ANON") ?: ""}\""
        )
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

    kotlinOptions {
        jvmTarget = "17"
    }

    // ✅ Ativa o BuildConfig (resolve o erro que você mencionou)
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
}

dependencies {
    // 🔹 AndroidX e Material
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // 🔹 Retrofit + OkHttp + Moshi (para Supabase REST)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 🔹 ExoPlayer (player de vídeo)
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // 🔹 (Opcional) JSON e utilidades
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // 🔹 Testes (padrão Android Studio)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
