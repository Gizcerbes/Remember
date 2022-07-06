package com.uogames.remembercards

import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import com.uogames.remembercards.utils.Permission
import com.uogames.repository.DataProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
	}

	override fun onStart() {
		super.onStart()
		val view = findViewById<FragmentContainerView>(R.id.nav_host_fragment)
		view.viewTreeObserver.addOnGlobalLayoutListener {
			globalViewModel.setShowKeyboard(view)
		}
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		Permission.values()[requestCode].onRequestPermissionResult(grantResults)
	}

	override fun onStop() {
		super.onStop()
	}

	override fun onDestroy() {
		cacheDir?.let {
			try {
				it.listFiles()?.forEach { file -> file.delete() }
			}catch (e:Exception){
				Log.e("TAG", "onDestroy: $e", )
			}
		}
		runBlocking { globalViewModel.clean().join() }
		super.onDestroy()
	}
}

