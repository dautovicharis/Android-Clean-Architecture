package com.hd.domain.permissions.model

import android.content.Intent

data class Permission(
    val permissionType: PermissionType,
    val intent: Intent?,
    val granted: Boolean
)

data class Permissions(
    val permissions: List<Permission>,
    val shouldAskPermission: Boolean,
    val allGranted: Boolean
)

enum class PermissionType {
    AUTO_START,
    ALARM,
    NOTIFICATIONS;
}