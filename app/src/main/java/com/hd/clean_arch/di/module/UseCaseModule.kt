package com.hd.clean_arch.di.module

import com.hd.data.permissions.PermissionsUseCaseImpl
import com.hd.domain.permissions.usecase.PermissionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun providePermissionUseCase(permissionImpl: PermissionsUseCaseImpl): PermissionsUseCase =
        permissionImpl
}