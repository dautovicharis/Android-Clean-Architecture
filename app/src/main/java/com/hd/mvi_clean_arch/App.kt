package com.hd.mvi_clean_arch

import android.app.Application
import com.hd.mvi_clean_arch.di.component.AppComponent
import com.hd.mvi_clean_arch.di.component.DaggerAppComponent
import com.hd.mvi_clean_arch.di.module.AppModule

class App : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(
                AppModule(this)
            ).build()
    }
}