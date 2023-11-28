package com.hd.data.permissions.internal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import javax.inject.Inject


internal class NotificationPermission @Inject internal constructor() {
    fun hasPermission(appContext: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED)
    }
}
