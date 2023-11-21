package com.hd.data.permissions.model

import android.content.Intent
import android.os.Build


data class PermissionsDTO (
    val permissions: List<PermissionDTO>,
    val shouldAskPermission: Boolean,
    val allGranted: Boolean
)

data class PermissionDTO(
    val permissionType: PermissionTypeDTO,
    val intent: Intent? = null,
    val granted: Boolean
)

enum class PermissionTypeDTO(private val minApiLevel: Int = Build.VERSION_CODES.BASE) {
    AUTO_START,
    ALARM(Build.VERSION_CODES.S),
    NOTIFICATIONS(Build.VERSION_CODES.TIRAMISU);

    fun isRelevant(): Boolean {
        return Build.VERSION.SDK_INT >= this.minApiLevel
    }
}

