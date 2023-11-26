package com.hd.presentation.permissions.model.uistate

import com.hd.presentation.permissions.model.event.PermissionEvent

data class CombinedUiState(
    val permissionsState: PermissionsUiState,
    val permissionAction: PermissionEvent
)