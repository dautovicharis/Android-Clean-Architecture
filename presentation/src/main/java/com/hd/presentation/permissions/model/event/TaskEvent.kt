package com.hd.presentation.permissions.model.event

sealed class TaskEvent {
    object ScheduleNotifications: TaskEvent()
}