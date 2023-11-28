package com.hd.presentation.permissions.mapper

import com.hd.domain.permissions.model.Permission
import com.hd.domain.permissions.model.PermissionComponent
import com.hd.domain.permissions.model.PermissionType
import com.hd.domain.permissions.model.Permissions

fun PermissionType.toUI(): PermissionTypeUI {
    return when (this) {
        PermissionType.AUTO_START -> PermissionTypeUI.AUTO_START
        PermissionType.ALARM -> PermissionTypeUI.ALARM
        PermissionType.NOTIFICATIONS -> PermissionTypeUI.NOTIFICATIONS
    }
}

fun Permission.toUI() = PermissionUI(
    permissionType = permissionType.toUI(),
    intent = intent?.toUI(),
    granted = granted
)

fun Permissions.toUI() =  PermissionsUI(
    permissions = permissions.map { it.toUI() },
    shouldAskPermission = shouldAskPermission,
    allGranted = allGranted
)

fun PermissionComponent.toUI() = PermissionComponentUI(
    packageName = packageName,
    className = className
)