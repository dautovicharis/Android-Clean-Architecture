package com.hd.data.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import javax.inject.Inject

interface NotificationPermissionChecker {
    fun isPermissionGranted(appContext: Context): Boolean
}

class DefaultNotificationPermissionChecker @Inject internal constructor (): NotificationPermissionChecker {
    override fun isPermissionGranted(appContext: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED)
    }
}
