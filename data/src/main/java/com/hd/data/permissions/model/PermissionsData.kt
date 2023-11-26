package com.hd.data.permissions.model

import android.content.Intent


data class PermissionsDTO (
    val permissions: List<PermissionDTO>,
    val shouldAskPermission: Boolean,
    val allGranted: Boolean
)

data class PermissionDTO(
    val permissionType: PermissionTypeDTO,
    val intent: Intent? = null,
    val granted: Boolean,
    val isOptional: Boolean
)

enum class PermissionTypeDTO {
    AUTO_START,
    ALARM,
    NOTIFICATIONS;
}

