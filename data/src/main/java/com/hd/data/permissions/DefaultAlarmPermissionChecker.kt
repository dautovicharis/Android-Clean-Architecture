package com.hd.data.permissions

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import javax.inject.Inject

interface AlarmPermissionChecker {
    fun isPermissionGranted(appContext: Context): Boolean
}

class DefaultAlarmPermissionChecker @Inject internal constructor () : AlarmPermissionChecker {
    override fun isPermissionGranted(appContext: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager: AlarmManager =
                appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
}
