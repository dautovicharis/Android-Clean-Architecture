package com.hd.presentation.permissions.model.event

sealed class UiVisibilityEvent(val visibility: Int) {
    class DoNotRemindMe(visibility: Int) : UiVisibilityEvent(visibility)
    class MissingPermissionWarning(visibility: Int): UiVisibilityEvent(visibility)
}