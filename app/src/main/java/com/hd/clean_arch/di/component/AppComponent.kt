package com.hd.clean_arch.di.component

import com.hd.clean_arch.di.module.AppModule
import com.hd.clean_arch.di.module.DataModule
import com.hd.clean_arch.di.module.UseCaseModule
import com.hd.clean_arch.di.module.ViewModelFactoryModule
import com.hd.clean_arch.di.module.ViewModelModule
import com.hd.clean_arch.ui.main.MainActivity
import com.hd.clean_arch.ui.permissions.PermissionsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DataModule::class,
        ViewModelModule::class,
        UseCaseModule::class,
        ViewModelFactoryModule::class
    ]
)
interface AppComponent {
    fun inject(permissionsActivity: PermissionsActivity)
    fun inject(mainActivity: MainActivity)
}