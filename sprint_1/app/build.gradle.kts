plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Firebase plugin
}

android {
    namespace = "com.example.igraracunanja"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.igraracunanja"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // BCrypt za hesovanje
    implementation("org.mindrot:jbcrypt:0.4")

    // Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    // Firebase zavisnosti
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database") // Dodato za Realtime Database

    // Test zavisnosti
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
