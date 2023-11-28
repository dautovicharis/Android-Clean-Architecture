package com.hd.presentation.permissions

import android.content.Intent
import com.hd.presentation.permissions.mapper.PermissionComponentUI

sealed class PermissionsNavigation {
    class OpenAutoStartActivity(val intent: PermissionComponentUI) : PermissionsNavigation()
    data object OpenAlarmActivity : PermissionsNavigation()
    data object OpenAppSettings : PermissionsNavigation()
    data object RequestNotificationPermission : PermissionsNavigation()
}