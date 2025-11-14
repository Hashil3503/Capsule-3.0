plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.Capsule"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "openai_api_key", project.findProperty("OPENAI_API_KEY")?.toString() ?: "")
        resValue("string", "med_search_api_key", project.findProperty("MED_SEARCH_API_KEY")?.toString() ?: "")
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //아래는 SQLite 연동을 위한 Room 라이브러리 추가
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // ML Kit OCR 추가
    implementation("com.google.mlkit:text-recognition:16.0.0"){
        exclude(group = "androidx.lifecycle", module = "lifecycle-viewmodel-ktx")
    }
    implementation("com.google.mlkit:text-recognition-korean:16.0.0"){
        exclude(group = "androidx.lifecycle", module = "lifecycle-viewmodel-ktx")
    }

    // CameraX 라이브러리 추가
    val cameraxVersion = "1.3.1"

    implementation("androidx.camera:camera-core:$cameraxVersion") {
        exclude(group = "androidx.lifecycle", module = "lifecycle-viewmodel-ktx")
    }
    implementation("androidx.camera:camera-camera2:$cameraxVersion") {
        exclude(group = "androidx.lifecycle", module = "lifecycle-viewmodel-ktx")
    }
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion") {
        exclude(group = "androidx.lifecycle", module = "lifecycle-viewmodel-ktx")
    }
    implementation("androidx.camera:camera-view:$cameraxVersion") {
        exclude(group = "androidx.lifecycle", module = "lifecycle-viewmodel-ktx")
    }

    // Gson? 추가 (알람 설정, OCR에서 사용)
    implementation("com.google.code.gson:gson:2.10.1")

    // Network (OCR, LLM에서 사용?)
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // CSV
    implementation("com.opencsv:opencsv:5.7.1")

    // Tasks
    implementation("com.google.android.gms:play-services-tasks:18.0.2")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    //json 관련 라이브러리
    implementation ("org.json:json:20230227")

    //androidx.security:security-crypto 라이브러리 (로그인 기능 구현을 위한 패스워드 암호화)
    implementation ("androidx.security:security-crypto:1.1.0-alpha03")
}