package com.hd.data.permissions.internal

import android.content.Context
import com.hd.data.permissions.mapper.toPermissions
import com.hd.data.permissions.model.PermissionDTO
import com.hd.data.permissions.model.PermissionTypeDTO
import com.hd.data.permissions.model.PermissionsDTO
import com.hd.data.source.local.SharedPreferenceClient
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
        val permissionsResponse = mutableListOf<PermissionDTO>()

        config.permissionConfigs.forEach {
            when (it.permissionType) {
                PermissionTypeDTO.ALARM -> {
                    if (it.isRequired()) {
                        permissionsResponse.add(
                            PermissionDTO(
                                permissionType = it.permissionType,
                                granted = alarmPermissionChecker.hasPermission(appContext),
                                isOptional = it.isOptional
                            )
                        )
                    }
                }

                PermissionTypeDTO.AUTO_START -> {
                    if (it.isRequired()){
                        permissionsResponse.add(
                            PermissionDTO(
                                permissionType = PermissionTypeDTO.AUTO_START,
                                granted = autoStartPermissionChecker.hasPermission(),
                                intent = autoStartPermissionChecker.createIntent(),
                                isOptional = it.isOptional
                            )
                        )
                    }

                }

                PermissionTypeDTO.NOTIFICATIONS -> {
                    if (it.isRequired()) {
                        permissionsResponse.add(
                            PermissionDTO(
                                permissionType = PermissionTypeDTO.NOTIFICATIONS,
                                granted = notificationPermissionChecker.hasPermission(appContext),
                                isOptional = it.isOptional
                            )
                        )
                    }
                }
            }
        }

        val allGranted = permissionsResponse.filterNot { it.isOptional }.all { it.granted }

        val permissionResponse = PermissionsDTO(
            permissions = permissionsResponse,
            shouldAskPermission = shouldAskPermission(),
            allGranted = allGranted
        )

        return flowOf(
            permissionResponse.toPermissions()
        )
    }

    fun doNotAskMePermissions(value: Boolean) {
        preference.setPreference(DO_NOT_ASK_ME_PERMISSIONS, value)
    }

    fun toggleAutoStartPermission() {
        autoStartPermissionChecker.togglePermission()
    }

    fun isAutoStartSupportedByDevice(devices: Set<DeviceType>, context: Context): Boolean {
        return autoStartPermissionChecker.isSupportedByDevice(devices, context)
    }

    private fun shouldAskPermission(): Boolean {
        return preference.getPreference(DO_NOT_ASK_ME_PERMISSIONS, false) as Boolean
    }

    companion object {
        private const val DO_NOT_ASK_ME_PERMISSIONS = "do_not_ask_me_permissions"
    }
}