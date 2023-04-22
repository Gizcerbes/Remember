package com.uogames.remembercards.ui.games.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.uogames.dto.local.LocalPhraseView
import com.uogames.remembercards.App
import com.uogames.remembercards.R
import com.uogames.remembercards.viewmodel.GlobalViewModel
import kotlinx.coroutines.runBlocking

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_NEW_NOTIFICATION = "NotificationReceiver_ACTION_NEW_NOTIFICATION"
        const val ACTION_FRONT_SIDE = "NotificationReceiver_ACTION_FRONT_SIDE"
        const val ACTION_BACK_SIDE = "NotificationReceiver_ACTION_BACK_SIDE"
        const val CARD_ID = "NotificationReceiver_CARD_ID"
    }


    override fun onReceive(context: Context?, intent: Intent?): Unit = runBlocking {
        if (context == null || intent == null) return@runBlocking
        val cardId = intent.getIntExtra(CARD_ID, 0)
        val model = NotificationViewModel(GlobalViewModel(context))
        val builder = when (intent.action) {
            ACTION_NEW_NOTIFICATION -> newNotification(context, model)
            ACTION_FRONT_SIDE -> frontSideNotification(context, model, cardId)
            ACTION_BACK_SIDE -> backSideNotification(context, model, cardId)
            else -> null
        }
        builder?.let {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.notify(0, it.build())
        }
    }

    private suspend fun newNotification(context: Context, model: NotificationViewModel): NotificationCompat.Builder? {
        val card = model.getRandomCard() ?: return null
        return createNotificationBuilder(context, model, card.phrase, card.id, ACTION_BACK_SIDE)
            .apply { setSubText(card.reason) }

    }

    private suspend fun frontSideNotification(context: Context, model: NotificationViewModel, cardId: Int): NotificationCompat.Builder? {
        val card = model.getCardById(cardId) ?: return null
        return createNotificationBuilder(context, model, card.phrase, cardId, ACTION_BACK_SIDE)
            .apply { setSubText(card.reason) }
    }

    private suspend fun backSideNotification(context: Context, model: NotificationViewModel, cardId: Int): NotificationCompat.Builder? {
        val card = model.getCardById(cardId) ?: return null
        return createNotificationBuilder(context, model, card.translate, cardId, ACTION_FRONT_SIDE)
            .apply { setSubText(card.reason) }
    }

    private suspend fun createNotificationBuilder(
        context: Context,
        model: NotificationViewModel,
        phrase: LocalPhraseView,
        cardId: Int? = null,
        cardAction: String = ACTION_NEW_NOTIFICATION
    ): NotificationCompat.Builder {
        val imageData = phrase.image?.let { model.getData(it) }
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        val notification = NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) R.drawable.ic_logo_trans else R.drawable.ic_launcher_round)
            .setContentTitle(phrase.phrase)
            .setOnlyAlertOnce(true)
            .apply { if (imageData != null) setLargeIcon(BitmapFactory.decodeByteArray(imageData, 0, imageData.size)) }
        notification.setStyle(NotificationCompat.BigTextStyle().bigText(phrase.definition))
        notification.addAction(
            0,
            context.getText(R.string.turn_over),
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, NotificationReceiver::class.java).apply {
                    action = cardAction
                    putExtra(CARD_ID, cardId)
                },
                flag
            )
        )

        return notification
    }


}