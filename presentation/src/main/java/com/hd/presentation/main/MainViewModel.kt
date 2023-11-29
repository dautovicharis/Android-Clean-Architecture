package com.hd.presentation.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hd.domain.permissions.usecase.PermissionsUseCase
import com.hd.presentation.R
import com.hd.presentation.permissions.mapper.toUI
import com.hd.presentation.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val permissionsUseCase: PermissionsUseCase
) : ViewModel() {

    private val _permissionsState = MutableStateFlow<MainUIState>(
        MainUIState.MissingPermissions(
            text = R.string.permissions_missing,
            icon = R.drawable.ic_warning
        )
    )

    val permissionsState: StateFlow<MainUIState> = _permissionsState

    // LiveData that holds navigation events
    private val _navigationEvent = SingleLiveEvent<MainNavigation>()
    val navigationEvent: LiveData<MainNavigation> = _navigationEvent

    // Value that represents the dismissed state of the permissions activity
    var permissionsDismissed: Boolean = false
        private set

    fun checkAllPermissions() = viewModelScope.launch {
        permissionsUseCase.checkPermissions().collect {
            val mapped = it.toUI()

            val dismissed = permissionsDismissed

            val permissionAction = when {
                mapped.allGranted -> MainUIState.Granted(
                    text = R.string.permissions_granted,
                    icon = R.drawable.ic_check
                )

                it.shouldAskPermission && !dismissed -> MainUIState.AskUser(
                    text = R.string.permissions_missing,
                    icon = R.drawable.ic_warning
                )

                else -> MainUIState.MissingPermissions(
                    text = R.string.permissions_missing,
                    icon = R.drawable.ic_warning
                )
            }

            _permissionsState.value = permissionAction
            handlePermissionAction(permissionAction)
        }
    }

    private fun handlePermissionAction(state: MainUIState) {
        when (state) {
            is MainUIState.AskUser -> {
                openPermissionActivity(View.VISIBLE)
            }

            is MainUIState.Granted -> {
            }

            is MainUIState.MissingPermissions -> {
            }
        }
    }

    fun openPermissionActivity(doNotAskVisibility: Int = View.GONE) {
        _navigationEvent.value = MainNavigation.ShowPermissionsActivity(doNotAskVisibility)
    }

    fun dismissPermissions() {
        permissionsDismissed = true
    }
}