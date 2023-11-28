package com.hd.data.permissions.internal.client

import android.content.Context
import com.hd.data.permissions.internal.AlarmPermission
import com.hd.data.permissions.internal.AutoStartPermission
import com.hd.data.permissions.internal.DeviceType
import com.hd.data.permissions.internal.NotificationPermission
import com.hd.data.permissions.internal.PermissionsConfiguration
import com.hd.data.permissions.mapper.toPermissions
import com.hd.data.permissions.model.PermissionDTO
import com.hd.data.permissions.model.PermissionTypeDTO
import com.hd.data.permissions.model.PermissionsDTO
import com.hd.domain.permissions.model.Permissions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class PermissionsClient @Inject internal constructor(
    private val preference: SharedPreferenceClient,
    private val appContext: Context,
    private val autoStartPermissionChecker: AutoStartPermission,
    private val alarmPermissionChecker: AlarmPermission,
    private val notificationPermissionChecker: NotificationPermission
) {

    fun getPermissions(config: PermissionsConfiguration): Flow<Permissions> {
        val permissions = config.permissionConfigs
            .filter { it.isRequired() }
            .map { permissionConfig ->
                val permissionDTO = when (val permissionType = permissionConfig.permissionType) {
                    PermissionTypeDTO.ALARM -> PermissionDTO(
                        permissionType = permissionType,
                        granted = alarmPermissionChecker.hasPermission(appContext),
                        isOptional = permissionConfig.isOptional
                    )

                    PermissionTypeDTO.AUTO_START -> PermissionDTO(
                        permissionType = permissionType,
                        granted = autoStartPermissionChecker.hasPermission(),
                        intent = autoStartPermissionChecker.getComponent(),
                        isOptional = permissionConfig.isOptional
                    )

                    PermissionTypeDTO.NOTIFICATIONS -> PermissionDTO(
                        permissionType = permissionType,
                        granted = notificationPermissionChecker.hasPermission(appContext),
                        isOptional = permissionConfig.isOptional
                    )
                }

                permissionDTO
            }

        val allGranted = permissions.filterNot { it.isOptional }.all { it.granted }

        val permissionsDTO = PermissionsDTO(
            permissions = permissions,
            shouldAskPermission = shouldAskPermissions(),
            allGranted = allGranted
        )

        return flowOf(permissionsDTO.toPermissions())
    }

    fun doNotAskMePermissions(value: Boolean) {
        preference.setPreference(DO_NOT_ASK_PERMISSIONS, value)
    }

    private fun shouldAskPermissions(): Boolean {
        return !(preference.getPreference(DO_NOT_ASK_PERMISSIONS, false) as Boolean)
    }

    fun toggleAutoStartPermission() {
        autoStartPermissionChecker.togglePermission()
    }

    fun isAutoStartSupportedByDevice(devices: Set<DeviceType>, context: Context): Boolean {
        return autoStartPermissionChecker.isSupportedByDevice(devices, context)
    }

    companion object {
        private const val DO_NOT_ASK_PERMISSIONS = "DO_NOT_ASK_PERMISSIONS"
    }
}