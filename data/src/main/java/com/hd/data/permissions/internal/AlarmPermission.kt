package com.hd.data.permissions.internal

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import javax.inject.Inject

internal class AlarmPermission @Inject internal constructor() {
    fun hasPermission(appContext: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager: AlarmManager =
                appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
}
