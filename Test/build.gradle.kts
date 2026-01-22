plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.test"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.test"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    implementation("com.github.getActivity:XXPermissions:16.6")
    implementation("com.github.markzhai:blockcanary-android:1.5.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(project(":BlockMoonlightTreasureBox"))
    implementation(libs.material)
}
