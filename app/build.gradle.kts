// O bloco 'plugins' DEVE estar no topo do arquivo.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Adicione quaisquer outros plugins que você precise aqui, por exemplo:
    // id("kotlin-kapt")
    // id("com.google.dagger.hilt.android")
}

// Agora que o plugin foi aplicado, o bloco 'android' é reconhecido.
android {
    namespace = "com.example.performind" // Use o nome do seu pacote real
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.performind"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // Outras configurações como buildFeatures, etc.
}

dependencies {
    // Suas dependências vão aqui
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")   // pode atualizar do 1.6.1
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // 🔹 Retrofit + OkHttp + Moshi (necessário para AuthApi, SupabaseClient, etc.)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 🔹 (Opcional) ExoPlayer se você tiver vídeo
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // 🔹 Testes padrões
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    // ...
}
    