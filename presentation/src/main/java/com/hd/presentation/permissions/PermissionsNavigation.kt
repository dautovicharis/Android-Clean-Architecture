package com.hd.presentation.permissions

import android.content.Intent

sealed class PermissionsNavigation {
    class OpenAutoStartActivity(val intent: Intent) : PermissionsNavigation()
    data object OpenAlarmActivity : PermissionsNavigation()
    data object OpenAppSettings : PermissionsNavigation()
    data object RequestNotificationPermission : PermissionsNavigation()
}