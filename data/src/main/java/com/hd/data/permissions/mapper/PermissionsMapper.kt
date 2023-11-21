package com.hd.data.permissions.mapper

import com.hd.data.permissions.model.PermissionDTO
import com.hd.data.permissions.model.PermissionTypeDTO
import com.hd.data.permissions.model.PermissionsDTO
import com.hd.domain.permissions.model.Permission
import com.hd.domain.permissions.model.PermissionType
import com.hd.domain.permissions.model.Permissions

fun PermissionsDTO.toPermissions() = Permissions(
    permissions = permissions.map { it.toPermission() },
    shouldAskPermission = shouldAskPermission,
    allGranted = allGranted
)

fun PermissionDTO.toPermission() = Permission(
    permissionType = permissionType.toPermissionType(),
    intent = intent,
    granted = granted
)

fun PermissionTypeDTO.toPermissionType(): PermissionType = when (this) {
    PermissionTypeDTO.AUTO_START -> PermissionType.AUTO_START
    PermissionTypeDTO.ALARM -> PermissionType.ALARM
    PermissionTypeDTO.NOTIFICATIONS -> PermissionType.NOTIFICATIONS
}