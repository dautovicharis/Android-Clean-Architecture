package com.hd.mvi_clean_arch.di.module

import androidx.lifecycle.ViewModelProvider
import com.hd.mvi_clean_arch.utils.ViewModelProviderFactory
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface ViewModelFactoryModule {

    @Singleton
    @Binds
    fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}