package com.hd.mvi_clean_arch.di.module

import android.content.Context
import com.hd.data.source.local.SharedPreferenceClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
@Module
class DataModule {

    @Singleton
    @Provides
    fun providePreference (appContext: Context) = SharedPreferenceClient(appContext)
}