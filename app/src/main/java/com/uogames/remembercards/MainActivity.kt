package com.uogames.remembercards

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.uogames.remembercards.utils.Permission
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

		val navController = findNavController(R.id.nav_host_fragment)
		navController.currentBackStackEntryFlow.onEach {
			globalViewModel.setBackQueue(navController.backQueue)
		}.launchIn(lifecycleScope)

	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		Permission.values()[requestCode].onRequestPermissionResult(grantResults)
	}

	override fun onDestroy() {
		super.onDestroy()
		runBlocking { globalViewModel.clean().join() }
		cacheDir.listFiles()?.forEach {
			try {
				it.delete()
			}catch (e:Exception){
				Log.e("CLEAN_CACHE_ERROR", "$e", )
			}
		}
		lifecycleScope.cancel()
		globalViewModel.setBackQueue(null)
	}

}

