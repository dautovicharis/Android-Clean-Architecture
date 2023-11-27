package com.hd.presentation.permissions

import com.hd.presentation.permissions.mapper.PermissionsUI

sealed interface PermissionsUiState {
    data class Success(val permissionsUi: PermissionsUI) : PermissionsUiState
    data object Loading: PermissionsUiState
}