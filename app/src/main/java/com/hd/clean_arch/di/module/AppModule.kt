package com.hd.clean_arch.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val appContext: Context) {

    @Singleton
    @Provides
    fun appContext() = appContext
}