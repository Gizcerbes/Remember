package com.uogames.remembercards.utils

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

enum class Permission constructor(private val permission: String) {
    ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
    READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),
    RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),

    @RequiresApi(Build.VERSION_CODES.Q)
    ACCESS_MEDIA_LOCATION(Manifest.permission.ACCESS_MEDIA_LOCATION),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATIONS(Manifest.permission.POST_NOTIFICATIONS)
    ;

    private var lastResult: Boolean = false
    private var listener: (Boolean) -> Unit = {}

    private fun hasPermission(application: Application): Boolean {
        return ContextCompat.checkSelfPermission(
            application,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity, requestListener: (permission: Boolean) -> Unit = {}) {
        listener = requestListener
        val res = hasPermission(activity.application)
        if (!res) activity.requestPermissions(arrayOf(permission), ordinal)
        lastResult = res
        listener(lastResult)
    }

    fun onRequestPermissionResult(grantResults: IntArray) {
        lastResult = grantResults[0] == PackageManager.PERMISSION_GRANTED
        listener(lastResult)
    }
}
