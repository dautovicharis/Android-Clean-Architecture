
object Dependencies {

    object Modules {
        const val data = ":data"
        const val domain = ":domain"
        const val presentation = ":presentation"
    }

    object Core {
        const val androidXCore = "androidx.core:core-ktx:${Versions.coreKtxVersion}"
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompatVersion}"
        const val material = "com.google.android.material:material:${Versions.materialVersion}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutVersion}"
        const val preferences = "androidx.preference:preference-ktx:${Versions.preferenceVersion}"
        const val javaxInject = "javax.inject:javax.inject:${Versions.javaxInjectVersion}"
    }

    object Lifecycle {
        const val lifeCycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleVersion}"
        const val lifeCycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleVersion}"
        const val lifeCycleExtension = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleExtensionsVersion}"
    }

    object DI {
        const val dagger = "com.google.dagger:dagger:${Versions.daggerVersion}"
        const val daggerKapt = "com.google.dagger:dagger-compiler:${Versions.daggerVersion}"
    }

    object Coroutines {
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}"
    }

    object Testing {
        const val junit = "junit:junit:${Versions.junitVersion}"
        const val googleTruth = "com.google.truth:truth:${Versions.googleTruthVersion}"
        const val mockk = "io.mockk:mockk:${Versions.mockkVersion}"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesTestVersion}"
        const val androidxArchCoreTest = "androidx.arch.core:core-testing:${Versions.androidxArchCoreTestVersion}"
    }
}