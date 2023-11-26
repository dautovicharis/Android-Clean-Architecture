package com.hd.presentation.permissions.model

import android.content.Intent
import com.hd.presentation.R

data class PermissionsUI(
    val permissions: List<PermissionUI>,
    val shouldAskPermission: Boolean,
    val allGranted: Boolean
)

data class PermissionUI(
    val permissionType: PermissionTypeUI,
    val intent: Intent?,
    val granted: Boolean
)

enum class PermissionTypeUI {
    AUTO_START {
        override fun displayName() = R.string.permission_auto_start_title
        override fun displayDescription() = R.string.permission_auto_start_desc
    },
    ALARM {
        override fun displayName() = R.string.permission_alarm_title
        override fun displayDescription() = R.string.permission_alarm_desc
    },
    NOTIFICATIONS {
        override fun displayName() = R.string.permission_notification_title
        override fun displayDescription() = R.string.permission_notification_desc
    };

    abstract fun displayName(): Int
    abstract fun displayDescription(): Int
}

