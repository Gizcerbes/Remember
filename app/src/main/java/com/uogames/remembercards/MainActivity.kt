package com.uogames.remembercards

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.text.layoutDirection
import androidx.fragment.app.FragmentContainerView
import com.uogames.remembercards.utils.Permission
import com.uogames.repository.DataProvider
import dagger.android.support.DaggerAppCompatActivity
import java.util.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private val imm by lazy {
		getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		DataProvider.get(applicationContext)

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
}

