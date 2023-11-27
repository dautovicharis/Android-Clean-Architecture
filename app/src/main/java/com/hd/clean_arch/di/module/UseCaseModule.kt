package com.hd.clean_arch.di.module

import com.hd.data.permissions.PermissionsUseCaseImpl
import com.hd.domain.permissions.usecase.PermissionsUseCase
import dagger.Binds
import dagger.Module

@Module
interface UseCaseModule {

    @Binds
    fun bindPermissionUseCase(permissionImpl: PermissionsUseCaseImpl): PermissionsUseCase
}