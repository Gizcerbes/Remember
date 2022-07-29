package com.uogames.remembercards

import android.content.res.Configuration
import android.util.Log
import com.uogames.remembercards.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App: DaggerApplication() {
	override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
		return DaggerAppComponent.builder().bindApplication(this).build()
		//return AndroidInjector {  }
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
	}

	override fun onTrimMemory(level: Int) {
		super.onTrimMemory(level)
		cacheDir?.let {
			try {
				it.listFiles()?.forEach { file -> file.delete() }
			}catch (e:Exception){
				Log.e("TAG", "onDestroy: $e", )
			}
		}
	}

	override fun onTerminate() {
		super.onTerminate()
	}
}