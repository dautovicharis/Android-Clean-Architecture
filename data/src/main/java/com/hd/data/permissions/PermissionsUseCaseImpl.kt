package com.hd.data.permissions

import android.content.Context
import android.os.Build
import com.hd.data.permissions.internal.DeviceType
import com.hd.data.permissions.internal.PermissionConfig
import com.hd.data.permissions.internal.PermissionsClient
import com.hd.data.permissions.internal.PermissionsConfiguration
import com.hd.data.permissions.model.PermissionTypeDTO
import com.hd.domain.permissions.model.Permissions
import com.hd.domain.permissions.usecase.PermissionsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PermissionsUseCaseImpl @Inject internal constructor(
    private val permissionsClient: PermissionsClient,
    private val appContext: Context
) : PermissionsUseCase {

    override suspend fun checkPermissions(): Flow<Permissions> {

        val autoStartDevices = setOf(
            DeviceType.XIAOMI,
            DeviceType.OPPO,
            DeviceType.VIVO,
            DeviceType.LETV,
            DeviceType.HONOR,
            DeviceType.ONEPLUS
        )

        val config = PermissionsConfiguration.Builder().addPermissionConfig(
            PermissionConfig(
                permissionType = PermissionTypeDTO.AUTO_START,
                supportedDevices = autoStartDevices,
                isOptional = true,
                isSupportedByDevice = {
                    permissionsClient.isAutoStartSupportedByDevice(
                        devices = autoStartDevices,
                        context = appContext
                    )
                }
            )
        ).addPermissionConfig(
            PermissionConfig(
                permissionType = PermissionTypeDTO.ALARM,
                minApiLevel = Build.VERSION_CODES.S
            )
        ).addPermissionConfig(
            PermissionConfig(
                permissionType = PermissionTypeDTO.NOTIFICATIONS,
                minApiLevel = Build.VERSION_CODES.TIRAMISU
            )
        ).build()

        return permissionsClient.getPermissions(config)
    }

    override suspend fun doNotAskMePermissions(value: Boolean) {
        permissionsClient.doNotAskMePermissions(value)
    }

    override suspend fun toggleAutoStartPermission() {
        permissionsClient.toggleAutoStartPermission()
    }
}
