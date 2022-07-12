package com.uogames.remembercards

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentContainerView
import com.uogames.remembercards.utils.Permission
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

//		Locale.getAvailableLocales().forEach {
//			Log.e("TAG", "${it.toLanguageTag()} ", )
//		}
//
//		Locale.getDefault()

//		Locale.getISOLanguages().map { Locale(it) }.forEach {
//			Log.e("TAG", "${it.displayLanguage} ", )
//		}

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
		Log.e("TAG", "onDestroy:", )
		super.onDestroy()
	}

}

