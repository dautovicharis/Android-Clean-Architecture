plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.hd.clean_arch"
    compileSdk = ProjectConfig.Android.compileSdk

    defaultConfig {
        applicationId = "com.hd.clean_arch"
        minSdk = ProjectConfig.Android.minSdk
        targetSdk = ProjectConfig.Android.targetSdk
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core
    implementation(Dependencies.Core.androidXCore)
    implementation(Dependencies.Core.appCompat)
    implementation(Dependencies.Core.material)
    implementation(Dependencies.Core.constraintLayout)

    // DI
    implementation(Dependencies.DI.hiltAndroid)
    kapt(Dependencies.DI.hiltCompiler)

    // Lifecycle
    implementation(Dependencies.Lifecycle.lifeCycleViewModel)
    implementation(Dependencies.Lifecycle.lifeCycleRuntime)
    implementation(Dependencies.Lifecycle.lifeCycleExtension)

    // Modules
    implementation(project(Dependencies.Modules.data))
    implementation(project(Dependencies.Modules.domain))
    implementation(project(Dependencies.Modules.presentation))
}