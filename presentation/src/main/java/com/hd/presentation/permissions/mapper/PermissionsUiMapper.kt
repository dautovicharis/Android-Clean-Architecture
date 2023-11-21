package com.hd.present.permissions.mapper

import com.hd.domain.permissions.model.Permission
import com.hd.domain.permissions.model.PermissionType
import com.hd.domain.permissions.model.Permissions
import com.hd.presentation.permissions.model.PermissionTypeUI
import com.hd.presentation.permissions.model.PermissionUI
import com.hd.presentation.permissions.model.PermissionsUI

fun PermissionType.toUI(): PermissionTypeUI {
    return when (this) {
        PermissionType.AUTO_START -> PermissionTypeUI.AUTO_START
        PermissionType.ALARM -> PermissionTypeUI.ALARM
        PermissionType.NOTIFICATIONS -> PermissionTypeUI.NOTIFICATIONS
    }
}

fun Permission.toUI() = PermissionUI(
    permissionType = permissionType.toUI(),
    intent = intent,
    granted = granted
)

fun Permissions.toUI() =  PermissionsUI(
    permissions = permissions.map { it.toUI() },
    shouldAskPermission = shouldAskPermission,
    allGranted = allGranted
)