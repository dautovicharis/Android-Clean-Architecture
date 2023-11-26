package com.hd.domain.permissions.usecase

import kotlinx.coroutines.flow.Flow
import com.hd.domain.permissions.model.Permissions

interface PermissionsUseCase {
    suspend fun checkPermissions(): Flow<Permissions>
    suspend fun doNotAskMePermissions(value: Boolean)
    suspend fun toggleAutoStartPermission()
}