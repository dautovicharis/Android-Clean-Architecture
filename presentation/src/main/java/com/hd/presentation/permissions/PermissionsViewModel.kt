package com.hd.presentation.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hd.domain.permissions.usecase.PermissionsUseCase
import com.hd.presentation.permissions.mapper.PermissionTypeUI
import com.hd.presentation.permissions.mapper.PermissionUI
import com.hd.presentation.permissions.mapper.toUI
import com.hd.presentation.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val permissionsUseCase: PermissionsUseCase
) : ViewModel() {

    private val _permissionsState = MutableStateFlow<PermissionsUiState>(PermissionsUiState.Loading)
    val permissionsState: StateFlow<PermissionsUiState> = _permissionsState

    // LiveData that holds navigation events
    private val _navigationEvent = SingleLiveEvent<PermissionsNavigation>()
    val navigationEvent: SingleLiveEvent<PermissionsNavigation> = _navigationEvent

    fun checkAllPermissions() = viewModelScope.launch {
        permissionsUseCase.checkPermissions().collect {
            _permissionsState.value = PermissionsUiState.Success(it.toUI())
        }
    }

    fun handlePermissionToggle(permission: PermissionUI) = viewModelScope.launch {
        when (permission.permissionType) {
            PermissionTypeUI.AUTO_START -> {
                permissionsUseCase.toggleAutoStartPermission()
                openAutoStartActivity(permission)
            }

            PermissionTypeUI.ALARM -> {
                openAlarmActivity()
            }

            PermissionTypeUI.NOTIFICATIONS -> {
                if (permission.granted) {
                    openAppSettings()
                } else {
                    requestNotificationPermission()
                }
            }
        }
    }

    private fun openAutoStartActivity(permission: PermissionUI) {
        permission.intent?.let {
            _navigationEvent.value = PermissionsNavigation.OpenAutoStartActivity(it)
        }
    }

    private fun openAlarmActivity() {
        _navigationEvent.value = PermissionsNavigation.OpenAlarmActivity
    }

    private fun requestNotificationPermission() {
        _navigationEvent.value = PermissionsNavigation.RequestNotificationPermission
    }

    private fun openAppSettings() {
        _navigationEvent.value = PermissionsNavigation.OpenAppSettings
    }

    fun doNotAskMeAgain(value: Boolean) = viewModelScope.launch {
        permissionsUseCase.doNotAskMePermissions(value)
    }
}
