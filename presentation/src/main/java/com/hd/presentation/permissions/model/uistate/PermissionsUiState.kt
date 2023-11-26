package com.hd.presentation.permissions.model.uistate

import com.hd.presentation.permissions.model.PermissionsUI

sealed interface PermissionsUiState {
    data class Success(val permissionsUi: PermissionsUI) : PermissionsUiState
    data object Loading: PermissionsUiState
}