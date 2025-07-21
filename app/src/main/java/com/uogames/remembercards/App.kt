package com.uogames.remembercards

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID_klmdfdskjfnsdvgksdnfgvsd"
        const val NOTIFICATION_CHANNEL_NAME = "CARDS"

    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

//    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
//        return DaggerAppComponent.builder().bindApplication(this).build()
//        // return AndroidInjector {  }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {

        val manager = getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        manager.createNotificationChannel(channel)

    }

}
