package com.hd.presentation.permissions

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hd.presentation.utils.SingleLiveEvent
import com.hd.domain.permissions.usecase.PermissionsUseCase
import com.hd.present.permissions.mapper.toUI
import com.hd.presentation.permissions.model.event.NavigationEvent
import com.hd.presentation.permissions.model.event.PermissionEvent
import com.hd.presentation.permissions.model.event.TaskEvent
import com.hd.presentation.permissions.model.event.UiVisibilityEvent
import com.hd.presentation.permissions.model.uistate.CombinedUiState
import com.hd.presentation.permissions.model.PermissionTypeUI
import com.hd.presentation.permissions.model.PermissionUI
import com.hd.presentation.permissions.model.uistate.PermissionsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PermissionsViewModel @Inject constructor(
    private val permissionsUseCase: PermissionsUseCase
) : ViewModel() {

    // Flow that holds the combined UI state for permissions
    private val _permissionsFlow = MutableStateFlow(
        CombinedUiState(
            permissionsState = PermissionsUiState.Loading,
            permissionAction = PermissionEvent.MissingPermissions
        )
    )
    val permissionsFlow: StateFlow<CombinedUiState> = _permissionsFlow

    // LiveData that holds navigation events
    private val _navigationEvent = SingleLiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    // LiveData that holds UI visibility events
    private val _uiVisibilityEvent = SingleLiveEvent<UiVisibilityEvent>()
    val uiVisibilityEvent: LiveData<UiVisibilityEvent> = _uiVisibilityEvent

    // LiveData that holds task events
    private val _taskEvent = SingleLiveEvent<TaskEvent>()
    val taskEvent: LiveData<TaskEvent> = _taskEvent

    // Flow that represents the dismissed state of the permissions activity
    private val _permissionsActivityDismissed = MutableStateFlow(false)
    val permissionsActivityDismissed: Flow<Boolean> get() = _permissionsActivityDismissed

    fun checkAllPermissions() = viewModelScope.launch {
        permissionsUseCase.checkPermissions().collect {
            val mapped = it.toUI()

            permissionsActivityDismissed.collect { dismissed ->
                val permissionAction = when {
                    mapped.allGranted -> PermissionEvent.Granted
                    it.shouldAskPermission && !dismissed -> PermissionEvent.AskUser
                    else -> PermissionEvent.MissingPermissions
                }

                val combinedUiState = CombinedUiState(
                    permissionsState = PermissionsUiState.Success(mapped),
                    permissionAction = permissionAction
                )
                _permissionsFlow.value = combinedUiState
                handlePermissionAction(combinedUiState)
            }
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
            _navigationEvent.value = NavigationEvent.OpenAutoStartActivity(it)
        }
    }

    private fun openAlarmActivity() {
        _navigationEvent.value = NavigationEvent.OpenAlarmActivity
    }

    private fun requestNotificationPermission() {
        _navigationEvent.value = NavigationEvent.RequestNotificationPermission
    }

    private fun openAppSettings() {
        _navigationEvent.value = NavigationEvent.OpenAppSettings
    }

    fun showDoNotRemindMeAgain() {
        _uiVisibilityEvent.value = UiVisibilityEvent.DoNotRemindMe(View.VISIBLE)
    }

    fun doNotAskMeAgain(value: Boolean) = viewModelScope.launch {
        permissionsUseCase.doNotAskMePermissions(value)
    }

    private fun handlePermissionAction (state: CombinedUiState){
        when(state.permissionAction){
            is PermissionEvent.AskUser -> {
                _navigationEvent.value = NavigationEvent.ShowPermissionsActivity
            }
            is PermissionEvent.Granted -> {
                _taskEvent.value = TaskEvent.ScheduleNotifications
                _uiVisibilityEvent.value = UiVisibilityEvent.MissingPermissionWarning(View.GONE)
            }
            is PermissionEvent.MissingPermissions -> {
               _uiVisibilityEvent.value = UiVisibilityEvent.MissingPermissionWarning(View.VISIBLE)
            }
        }
    }

    fun dismiss() {
        _permissionsActivityDismissed.value = true
    }
}
