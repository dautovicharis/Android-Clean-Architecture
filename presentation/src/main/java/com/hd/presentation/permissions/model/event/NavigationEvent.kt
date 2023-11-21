package com.hd.presentation.permissions.model.event

import android.content.Intent

sealed class NavigationEvent {
    class OpenAutoStartActivity(val intent: Intent) : NavigationEvent()
    object OpenAlarmActivity : NavigationEvent()
    object OpenAppSettings : NavigationEvent()
    object RequestNotificationPermission : NavigationEvent()
    object ShowPermissionsActivity : NavigationEvent()
}