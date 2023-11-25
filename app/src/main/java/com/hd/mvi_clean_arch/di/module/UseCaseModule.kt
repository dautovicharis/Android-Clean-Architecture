package com.hd.mvi_clean_arch.di.module

import com.hd.data.permissions.AlarmPermissionChecker
import com.hd.data.permissions.AutoStartPermissionChecker
import com.hd.data.permissions.DefaultAlarmPermissionChecker
import com.hd.data.permissions.DefaultAutoStartPermissionChecker
import com.hd.data.permissions.DefaultNotificationPermissionChecker
import com.hd.data.permissions.NotificationPermissionChecker
import com.hd.data.permissions.PermissionsUseCaseImpl
import com.hd.domain.permissions.usecase.PermissionsUseCase
import dagger.Binds
import dagger.Module

@Module
interface UseCaseModule {

    @Binds
    fun bindPermissionUseCase(permissionImpl: PermissionsUseCaseImpl): PermissionsUseCase

    @Binds
    fun bindAlarmPermissionChecker(alarmPermissionChecker: DefaultAlarmPermissionChecker): AlarmPermissionChecker

    @Binds
    fun bindNotificationPermissionChecker(notificationPermissionChecker: DefaultNotificationPermissionChecker): NotificationPermissionChecker

    @Binds
    fun bindAutoStartPermissionChecker(autoStartPermissionChecker: DefaultAutoStartPermissionChecker): AutoStartPermissionChecker
}