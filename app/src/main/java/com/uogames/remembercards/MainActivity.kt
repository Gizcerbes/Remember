package com.uogames.remembercards

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.text.layoutDirection
import com.uogames.remembercards.utils.Permission
import com.uogames.repository.DataProvider
import dagger.android.support.DaggerAppCompatActivity
import java.util.*

class MainActivity : DaggerAppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		DataProvider.get(applicationContext)
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		Permission.values()[requestCode].onRequestPermissionResult(grantResults)
	}




}

