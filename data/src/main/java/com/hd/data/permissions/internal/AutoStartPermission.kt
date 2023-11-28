package com.hd.data.permissions.internal

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.hd.data.permissions.internal.client.SharedPreferenceClient
import com.hd.data.permissions.model.PermissionComponentDTO
import java.util.Locale
import javax.inject.Inject

internal class AutoStartPermission @Inject internal constructor(
    private val preference: SharedPreferenceClient
) {
    fun hasPermission() = preference.getPreference(AUTO_START_PERMISSION, false) as Boolean

    fun togglePermission() {
        val currentValue = preference.getPreference(AUTO_START_PERMISSION, false) as Boolean
        preference.setPreference(AUTO_START_PERMISSION, !currentValue)
    }

    fun isSupportedByDevice(devices: Set<DeviceType>, context: Context): Boolean {
        // If no supported devices are specified, then it is supported by all devices
        if (devices.isEmpty()) return true

        val componentDTO = getComponent()
        val intent = componentDTO?.let {
              Intent().apply {
                component = ComponentName(it.packageName, it.className)
            }
        } ?: return false

        val activities = context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        return activities.isNotEmpty()
    }

    fun getComponent(): PermissionComponentDTO? {
        val manufacturer = Build.MANUFACTURER.lowercase(Locale.getDefault())

        return when (DeviceType.entries.find { it.name.lowercase() == manufacturer }) {
            DeviceType.XIAOMI -> PermissionComponentDTO(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )

            DeviceType.OPPO -> PermissionComponentDTO(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            )

            DeviceType.VIVO -> PermissionComponentDTO(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            )

            DeviceType.LETV -> PermissionComponentDTO(
                "com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity"
            )

            DeviceType.HONOR -> PermissionComponentDTO(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            )

            DeviceType.ONEPLUS -> PermissionComponentDTO(
                "com.oneplus.security",
                "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
            )

            else -> null
        }
    }

    companion object {
        private const val AUTO_START_PERMISSION = "auto_start_permission"
    }
}
