package com.uogames.remembercards.ui.games.notification.type

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.uogames.dto.local.LocalCardView
import com.uogames.remembercards.App
import com.uogames.remembercards.R
import com.uogames.remembercards.ui.games.notification.NotificationReceiver

object NotificationTypeChoice {

	interface Model {
		suspend fun getRandomCard(): LocalCardView?

		suspend fun getRandomWithout(cardId: Int): LocalCardView?

		suspend fun getCardById(cardId: Int): LocalCardView?

		suspend fun saveResult(firstCardId: Int, secondCardId: Int, result: Boolean)

	}

	const val ACTION_CHECK = "NotificationTypeChoice.ACTION_CHECK"

	private const val FIRST_CARD_ID = "NotificationTypeChoice.FIRST_CARD_ID"
	private const val SECOND_CARD_ID = "NotificationTypeChoice.SECOND_CARD_ID"
	private const val RESULT = "NotificationTypeChoice.RESULT"

	suspend fun newNotification(context: Context, model: Model): NotificationCompat.Builder? {
		val first = model.getRandomCard() ?: return null
		val second = model.getRandomWithout(first.id) ?: return null
		return createNotificationBuilder(context, first, second, ACTION_CHECK)
	}

	suspend fun actionCheck(context: Context, model: Model, intent: Intent): NotificationCompat.Builder? {
		val first = intent.getIntExtra(FIRST_CARD_ID, 0)
		val second = intent.getIntExtra(SECOND_CARD_ID, 0)
		val result = intent.getBooleanExtra(RESULT, false)
		if (first == 0 || second == 0) return null
		model.saveResult(first, second, result)
		val card = model.getCardById(first) ?: return null
		val notification = NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) R.drawable.ic_logo_trans else R.drawable.ic_launcher_round)
			.setOnlyAlertOnce(true)
			.setAutoCancel(true)

		val views = RemoteViews(context.packageName, if (result) R.layout.card_notification_yes else R.layout.card_notification_no).apply {
			setTextViewText(R.id.text_answer, card.translate.phrase)
		}
		notification.setContent(views)
		return notification
	}

	private suspend fun createNotificationBuilder(
		context: Context,
		firstCard: LocalCardView,
		secondCard: LocalCardView,
		cardAction: String = ACTION_CHECK
	): NotificationCompat.Builder {
		val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
		val notification = NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) R.drawable.ic_logo_trans else R.drawable.ic_launcher_round)
			.setContentTitle(firstCard.phrase.phrase)
			.setOnlyAlertOnce(true)
			.setSubText(firstCard.reason)

		val trueIntent = Intent(context, NotificationReceiver::class.java).apply {
			action = cardAction
			putExtra(RESULT, true)
			putExtra(FIRST_CARD_ID, firstCard.id)
			putExtra(SECOND_CARD_ID, secondCard.id)
		}.let { PendingIntent.getBroadcast(context, 1, it, flag) }

		val falseIntent = Intent(context, NotificationReceiver::class.java).apply {
			action = cardAction
			putExtra(RESULT, false)
			putExtra(FIRST_CARD_ID, firstCard.id)
			putExtra(SECOND_CARD_ID, secondCard.id)
		}.let { PendingIntent.getBroadcast(context, 0, it, flag) }

		if (Math.random() >= 0.5) {
			notification.addAction(0, firstCard.translate.phrase, trueIntent)
			notification.addAction(0, secondCard.translate.phrase, falseIntent)
		} else {
			notification.addAction(0, secondCard.translate.phrase, falseIntent)
			notification.addAction(0, firstCard.translate.phrase, trueIntent)
		}
		return notification
	}

}