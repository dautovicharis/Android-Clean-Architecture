plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.hd.presentation"
    compileSdk = ProjectConfig.Android.compileSdk

    defaultConfig {
        minSdk = ProjectConfig.Android.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    // Core
    implementation(Dependencies.Core.javaxInject)

    // Lifecycle
    implementation(Dependencies.Lifecycle.lifeCycleViewModel)
    implementation(Dependencies.Lifecycle.lifeCycleRuntime)
    implementation(Dependencies.Lifecycle.lifeCycleExtension)

    // Coroutines
    implementation(Dependencies.Coroutines.coroutinesCore)
    implementation(Dependencies.Coroutines.coroutinesAndroid)

    // DI
    implementation(Dependencies.DI.hiltAndroid)
    kapt(Dependencies.DI.hiltCompiler)

    // Testing
    testImplementation(Dependencies.Testing.junit)
    testImplementation(Dependencies.Testing.googleTruth)
    testImplementation(Dependencies.Testing.mockk)
    testImplementation(Dependencies.Testing.coroutinesTest)
    testImplementation(Dependencies.Testing.androidxArchCoreTest)

    // Modules
    implementation(project(Dependencies.Modules.domain))
    testImplementation(project(Dependencies.Modules.data))
}