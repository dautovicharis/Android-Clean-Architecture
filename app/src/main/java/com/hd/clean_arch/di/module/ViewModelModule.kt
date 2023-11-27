package com.hd.clean_arch.di.module

import androidx.lifecycle.ViewModel
import com.hd.presentation.main.MainViewModel
import com.hd.presentation.permissions.PermissionsViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PermissionsViewModel::class)
    abstract fun bindPermissionsViewModel(viewModel: PermissionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}