package com.hd.presentation.permissions.model.event

sealed class PermissionEvent {
    object AskUser : PermissionEvent()
    object Granted : PermissionEvent()
    object MissingPermissions : PermissionEvent()
}