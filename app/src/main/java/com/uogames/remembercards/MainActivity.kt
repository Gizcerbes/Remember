package com.uogames.remembercards

import android.os.Bundle
import com.uogames.repository.DataProvider
import com.uogames.repository.Provider
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		DataProvider.get(applicationContext)

	}
}

