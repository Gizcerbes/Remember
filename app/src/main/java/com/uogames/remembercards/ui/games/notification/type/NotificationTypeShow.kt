package com.uogames.remembercards.ui.games.notification.type

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalImageView
import com.uogames.dto.local.LocalPhraseView
import com.uogames.remembercards.App
import com.uogames.remembercards.R
import com.uogames.remembercards.ui.games.notification.NotificationReceiver

object NotificationTypeShow {

	interface Model {

		suspend fun getRandomCard(): LocalCardView?

		suspend fun getCardById(cardId: Int): LocalCardView?

		suspend fun getData(localImageView: LocalImageView): ByteArray?

	}

	private const val CARD_ID = "NotificationTypeShow.CARD_ID"
	const val ACTION_FRONT_SIDE = "NotificationReceiver.ACTION_FRONT_SIDE"
	const val ACTION_BACK_SIDE = "NotificationReceiver.ACTION_BACK_SIDE"

	suspend fun newNotification(context: Context, model: Model): NotificationCompat.Builder? {
		val card = model.getRandomCard() ?: return null
		return createNotificationBuilder(context, model, card.phrase, card.id, ACTION_BACK_SIDE)
			.apply { setSubText(card.reason) }

	}

	suspend fun frontSideNotification(context: Context, model: Model, intent: Intent): NotificationCompat.Builder? {
		val cardId = intent.getIntExtra(CARD_ID, 0)
		val card = model.getCardById(cardId) ?: return null
		return createNotificationBuilder(context, model, card.phrase, cardId, ACTION_BACK_SIDE)
			.apply { setSubText(card.reason) }
	}

	suspend fun backSideNotification(context: Context, model: Model, intent: Intent): NotificationCompat.Builder? {
		val cardId = intent.getIntExtra(CARD_ID, 0)
		val card = model.getCardById(cardId) ?: return null
		return createNotificationBuilder(context, model, card.translate, cardId, ACTION_FRONT_SIDE)
			.apply { setSubText(card.reason) }
	}

	suspend fun createNotificationBuilder(context: Context, model: Model): NotificationCompat.Builder? {
		val card = model.getRandomCard() ?: return null
		return createNotificationBuilder(context, model, card.phrase, card.id, ACTION_BACK_SIDE)
			.apply { setSubText(card.reason) }
	}

	private suspend fun createNotificationBuilder(
		context: Context,
		model: Model,
		phrase: LocalPhraseView,
		cardId: Int? = null,
		cardAction: String = ACTION_BACK_SIDE
	): NotificationCompat.Builder {
		val imageData = phrase.image?.let { model.getData(it) }
		val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
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