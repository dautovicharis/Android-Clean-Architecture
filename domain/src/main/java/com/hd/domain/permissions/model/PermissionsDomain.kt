package com.hd.domain.permissions.model

data class Permission(
    val permissionType: PermissionType,
    val intent: PermissionComponent?,
    val granted: Boolean
)

data class Permissions(
    val permissions: List<Permission>,
    val shouldAskPermission: Boolean,
    val allGranted: Boolean
)
data class PermissionComponent(
    val packageName: String,
    val className: String
)

enum class PermissionType {
    AUTO_START,
    ALARM,
    NOTIFICATIONS;
}