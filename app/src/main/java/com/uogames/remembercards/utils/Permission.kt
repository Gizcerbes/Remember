package com.uogames.remembercards.utils

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Permission constructor(private val permission: String) {
	ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
	READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
	WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),

	@RequiresApi(Build.VERSION_CODES.Q)
	ACCESS_MEDIA_LOCATION(Manifest.permission.ACCESS_MEDIA_LOCATION);

	private val _isPermission = MutableStateFlow(false)
	val isPermission = _isPermission.asStateFlow()

	private fun hasPermission(application: Application): Boolean {
		return ContextCompat.checkSelfPermission(
			application,
			permission
		) == PackageManager.PERMISSION_GRANTED;
	}

	fun requestPermission(activity: Activity) {
		val res = hasPermission(activity.application)
		if (!res) activity.requestPermissions(arrayOf(permission), ordinal)
		_isPermission.value = res
	}

	fun onRequestPermissionResult(grantResults: IntArray) {
		_isPermission.value = grantResults[0] == PackageManager.PERMISSION_GRANTED
	}

}