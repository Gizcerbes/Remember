package com.uogames.remembercards.ui.games.notification

import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uogames.remembercards.App
import com.uogames.remembercards.R
import com.uogames.remembercards.viewmodel.GlobalViewModel


class NotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORKER_UNIQUE_NAME = "NotificationWorker_NAME"
    }

    override suspend fun doWork(): Result {
        val model = NotificationViewModel(GlobalViewModel(applicationContext))
        val card = model.getRandomCard() ?: return Result.failure()
        val imageData = card.phrase.image?.let { model.getData(it) }
        val notification = NotificationCompat.Builder(applicationContext, App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(card.phrase.phrase)
            .setSubText(card.reason)
            .setStyle(NotificationCompat.BigTextStyle().bigText(card.phrase.definition))
            .apply { if (imageData != null) setLargeIcon(BitmapFactory.decodeByteArray(imageData, 0, imageData.size)) }
            .build()

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.notify(0, notification)

        return Result.success()
    }


}