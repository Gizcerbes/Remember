package com.uogames.remembercards.ui.games.notification

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uogames.remembercards.broadcast.NotificationReceiver


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