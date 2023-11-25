package com.hd.data.permissions.internal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import javax.inject.Inject


internal class DefaultNotificationPermissionChecker @Inject internal constructor() {
    fun isPermissionGranted(appContext: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED)
    }
}
