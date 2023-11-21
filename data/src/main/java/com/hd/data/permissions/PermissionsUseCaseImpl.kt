package com.hd.data.permissions

import android.Manifest
import android.app.AlarmManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.hd.data.permissions.mapper.toPermissions
import com.hd.data.permissions.model.PermissionDTO
import com.hd.data.permissions.model.PermissionTypeDTO
import com.hd.data.permissions.model.PermissionsDTO
import com.hd.data.source.local.SharedPreferenceClient
import com.hd.domain.permissions.model.Permissions
import com.hd.domain.permissions.usecase.PermissionsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Locale
import javax.inject.Inject

class PermissionsUseCaseImpl @Inject internal constructor(
    private val appContext: Context,
    private val preference: SharedPreferenceClient
) : PermissionsUseCase {

    override suspend fun checkPermissions(): Flow<Permissions> {
        val hasAutoStartPermission =
            preference.getPreference(AUTO_START_PERMISSION, false) as Boolean
        val permissionsResponse = mutableListOf<PermissionDTO>()

        if (isAutoStartRequired()) {
            permissionsResponse.add(
                PermissionDTO(
                    permissionType = PermissionTypeDTO.AUTO_START,
                    granted = hasAutoStartPermission,
                    intent = getAutoStartIntent()
                )
            )
        }

        if (PermissionTypeDTO.ALARM.isRelevant()) {
            permissionsResponse.add(
                PermissionDTO(
                    permissionType = PermissionTypeDTO.ALARM,
                    granted = hasAlarmPermission()
                )
            )
        }

        if (PermissionTypeDTO.NOTIFICATIONS.isRelevant()) {
            permissionsResponse.add(
                PermissionDTO(
                    permissionType = PermissionTypeDTO.NOTIFICATIONS,
                    granted = hasNotificationPermission()
                )
            )
        }

        // Filter out the AUTO_START permission, which is not fully required for notifications to work.
        val nonRequiredPermissions = setOf(
            PermissionTypeDTO.AUTO_START
        )

        val allGranted =
            permissionsResponse.filterNot { it.permissionType in nonRequiredPermissions }
                .all { it.granted }
        val shouldAskPermission =
            preference.getPreference(DO_NOT_ASK_ME_PERMISSIONS, false) as Boolean

        val permissionResponse = PermissionsDTO(
            permissions = permissionsResponse,
            shouldAskPermission = shouldAskPermission,
            allGranted = allGranted
        )

        return flowOf(
            permissionResponse.toPermissions()
        )
    }

    override suspend fun doNotAskMePermissions(value: Boolean) {
        preference.setPreference(DO_NOT_ASK_ME_PERMISSIONS, value)
    }

    override suspend fun toggleAutoStartPermission() {
        val currentValue = preference.getPreference(AUTO_START_PERMISSION, false) as Boolean
        preference.setPreference(AUTO_START_PERMISSION, !currentValue)
    }

    private fun hasAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager: AlarmManager =
                appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun isAutoStartRequired(): Boolean {
        val autoStartManufacturers = setOf(
            "xiaomi", "oppo", "vivo", "letv", "honor", "oneplus"
        )
        val manufacturer = Build.MANUFACTURER.lowercase(Locale.getDefault())

        return when {
            autoStartManufacturers.contains(manufacturer) -> {
                val intent = getAutoStartIntent()
                val activities = appContext.packageManager
                    .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

                activities.isNotEmpty()
            }

            else -> false
        }
    }

    private fun getAutoStartIntent(): Intent {
        val componentName = when (Build.MANUFACTURER.lowercase(Locale.getDefault())) {
            "xiaomi" -> ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )

            "oppo" -> ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            )

            "vivo" -> ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            )

            "letv" -> ComponentName(
                "com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity"
            )

            "honor" -> ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            )

            "oneplus" -> ComponentName(
                "com.oneplus.security",
                "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
            )

            else -> null
        }

        return Intent().apply {
            component = componentName
        }
    }

    companion object {
        private const val AUTO_START_PERMISSION = "auto_start_permission"
        private const val DO_NOT_ASK_ME_PERMISSIONS = "do_not_ask_me_permissions"
    }
}
