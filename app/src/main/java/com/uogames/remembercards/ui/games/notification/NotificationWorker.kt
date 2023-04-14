package com.uogames.remembercards.ui.games.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uogames.remembercards.App
import com.uogames.remembercards.R
import com.uogames.remembercards.viewmodel.GlobalViewModel
import kotlinx.coroutines.runBlocking


class NotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORKER_UNIQUE_NAME = "NotificationWorker_NAME"
    }

    override suspend fun doWork(): Result {
        applicationContext.sendBroadcast(
            Intent(
                applicationContext,
                NotificationReceiver::class.java
            ).apply { action = NotificationReceiver.ACTION_NEW_NOTIFICATION }
        )

        return Result.success()
    }

}