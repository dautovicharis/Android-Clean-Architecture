package com.hd.data.permissions.internal

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.hd.data.source.local.SharedPreferenceClient
import java.util.Locale
import javax.inject.Inject

internal class DefaultAutoStartPermissionChecker @Inject internal constructor(
    private val preference: SharedPreferenceClient
) {
    fun isAutoStartPermissionGranted(context: Context): Boolean {
        val intent = createAutoStartIntent()
        val activities =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return activities.isNotEmpty()
    }

    fun hasAutoStartPermission() = preference.getPreference(AUTO_START_PERMISSION, false) as Boolean

    fun toggleAutoStartPermission() {
        val currentValue = preference.getPreference(AUTO_START_PERMISSION, false) as Boolean
        preference.setPreference(AUTO_START_PERMISSION, !currentValue)
    }

    fun isAutoStartSupportedByDevice(devices: Set<DeviceType>, context: Context): Boolean {
        // If no supported devices are specified, then it is supported by all devices
        if (devices.isEmpty()) return true

        val intent = createAutoStartIntent()
        val activities = context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        return activities.isNotEmpty()
    }

    fun createAutoStartIntent(): Intent {
        val manufacturer = Build.MANUFACTURER.lowercase(Locale.getDefault())
        val deviceType = DeviceType.entries.find { it.name.lowercase() == manufacturer }

        val componentName = when (deviceType) {
            DeviceType.XIAOMI -> ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )

            DeviceType.OPPO -> ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            )

            DeviceType.VIVO -> ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            )

            DeviceType.LETV -> ComponentName(
                "com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity"
            )

            DeviceType.HONOR -> ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            )

            DeviceType.ONEPLUS -> ComponentName(
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
    }
}
