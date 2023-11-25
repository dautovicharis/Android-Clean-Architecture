package com.hd.data.permissions

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

class PermissionsClient @Inject constructor(
    private val preference: SharedPreferenceClient,
    private val appContext: Context,
    private val autoStartPermissionChecker: AutoStartPermissionChecker,
    private val alarmPermissionChecker: AlarmPermissionChecker,
    private val notificationPermissionChecker: NotificationPermissionChecker
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
                                granted = alarmPermissionChecker.isPermissionGranted(appContext),
                                isOptional = it.isOptional
                            )
                        )
                    }
                }

                PermissionTypeDTO.AUTO_START -> {

                    val hasAutoStartPermission =
                        preference.getPreference(
                            AUTO_START_PERMISSION,
                            false
                        ) as Boolean

                    if (it.isRequired()){
                        permissionsResponse.add(
                            PermissionDTO(
                                permissionType = PermissionTypeDTO.AUTO_START,
                                granted = hasAutoStartPermission,
                                intent = autoStartPermissionChecker.createAutoStartIntent(),
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
                                granted = notificationPermissionChecker.isPermissionGranted(appContext),
                                isOptional = it.isOptional
                            )
                        )
                    }
                }
            }
        }

        val allGranted = permissionsResponse.filterNot { it.isOptional }.all { it.granted }
        val shouldAskPermission =
            preference.getPreference(
                DO_NOT_ASK_ME_PERMISSIONS,
                false
            ) as Boolean

        val permissionResponse = PermissionsDTO(
            permissions = permissionsResponse,
            shouldAskPermission = shouldAskPermission,
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
        val currentValue = preference.getPreference(AUTO_START_PERMISSION, false) as Boolean
        preference.setPreference(AUTO_START_PERMISSION, !currentValue)
    }

    fun isAutoStartSupportedByDevice(devices: Set<DeviceType>, context: Context): Boolean {
        return autoStartPermissionChecker.isAutoStartSupportedByDevice(devices, context)
    }

    companion object {
        private const val AUTO_START_PERMISSION = "auto_start_permission"
        private const val DO_NOT_ASK_ME_PERMISSIONS = "do_not_ask_me_permissions"
    }
}