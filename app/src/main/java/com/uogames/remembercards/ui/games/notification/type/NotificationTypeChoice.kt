package com.uogames.remembercards.ui.games.notification.type

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
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

		suspend fun getRandomWithoutPhrases(card: LocalCardView, phraseIds: Array<Int>): LocalCardView?

		suspend fun getCardById(cardId: Int): LocalCardView?

		suspend fun saveResult(firstCardId: Int, secondCardId: Int, result: Boolean)

	}

	const val ACTION_CHECK = "NotificationTypeChoice.ACTION_CHECK"

	private const val FIRST_CARD_ID = "NotificationTypeChoice.FIRST_CARD_ID"
	private const val SECOND_CARD_ID = "NotificationTypeChoice.SECOND_CARD_ID"
	private const val THIRD_CARD_ID = "NotificationTypeChoice.THIRD_CARD_ID"
	private const val FORTH_CARD_ID = "NotificationTypeChoice.FORTH_CARD_ID"
	private const val RESULT = "NotificationTypeChoice.RESULT"

	suspend fun newNotification(context: Context, model: Model): NotificationCompat.Builder? {
		val first = model.getRandomCard() ?: return null
		val array = ArrayList<Int>().apply {
			add(first.phrase.id)
			add(first.translate.id)
		}
		val second = model.getRandomWithoutPhrases(first, array.toTypedArray()) ?: return null
		array.add(second.translate.id)
		val third = model.getRandomWithoutPhrases(first, array.toTypedArray()) ?: return null
		array.add(third.translate.id)
		val forth = model.getRandomWithoutPhrases(first, array.toTypedArray()) ?: return null
		return createNotificationBuilder(context, first, second, third, forth, ACTION_CHECK)
	}

	suspend fun actionCheck(context: Context, model: Model, intent: Intent): NotificationCompat.Builder? {
		val first = intent.getIntExtra(FIRST_CARD_ID, 0)
		val second = intent.getIntExtra(SECOND_CARD_ID, 0)
		val third = intent.getIntExtra(THIRD_CARD_ID, 0)
		val forth = intent.getIntExtra(FORTH_CARD_ID, 0)
		val result = intent.getBooleanExtra(RESULT, false)
		if (first == 0 || second == 0) return null
		model.saveResult(first, second, result)
		model.saveResult(first, third, result)
		model.saveResult(first, forth, result)
		val card = model.getCardById(first) ?: return null
		val notification = NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) R.drawable.ic_logo_trans else R.drawable.ic_launcher_round)
			.setOnlyAlertOnce(true)
			.setAutoCancel(true)

		val views = RemoteViews(context.packageName, if (result) R.layout.card_notification_yes else R.layout.card_notification_no).apply {
			setTextViewText(R.id.text_answer, card.translate.phrase)
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			views.setColor(R.id.container, "setBackgroundColor", R.color.transparent)
			notification.setStyle(NotificationCompat.DecoratedCustomViewStyle())
		}

		notification.setCustomBigContentView(views)
		notification.setColorized(true)


		return notification
	}

	private suspend fun createNotificationBuilder(
		context: Context,
		firstCard: LocalCardView,
		secondCard: LocalCardView,
		thirdCard: LocalCardView,
		forthCard: LocalCardView,
		cardAction: String = ACTION_CHECK
	): NotificationCompat.Builder {
		val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
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
			putExtra(THIRD_CARD_ID, thirdCard.id)
			putExtra(FORTH_CARD_ID, forthCard.id)
		}

		val falseIntent = Intent(context, NotificationReceiver::class.java).apply {
			action = cardAction
			putExtra(RESULT, false)
			putExtra(FIRST_CARD_ID, firstCard.id)
			putExtra(SECOND_CARD_ID, secondCard.id)
			putExtra(THIRD_CARD_ID, thirdCard.id)
			putExtra(FORTH_CARD_ID, forthCard.id)
		}
		//	.let { PendingIntent.getBroadcast(context, 0, it, flag) }

		val array = ArrayList<Pair<String, Intent>>().apply {
			add(firstCard.translate.phrase to trueIntent)
			add(secondCard.translate.phrase to falseIntent)
			add(thirdCard.translate.phrase to falseIntent)
			add(forthCard.translate.phrase to falseIntent)
		}.shuffled()

		val vies = RemoteViews(context.packageName, R.layout.fragment_notification_show)
		vies.setTextViewText(R.id.txt_title, firstCard.phrase.phrase)
		for (i in array.indices) {
			val id = when (i) {
				0 -> R.id.btn_first
				1 -> R.id.btn_second
				2 -> R.id.btn_third
				3 -> R.id.btn_forth
				else -> R.id.btn_first
			}
			vies.setOnClickPendingIntent(id, array[i].second.let { PendingIntent.getBroadcast(context, i, it, flag) })
			vies.setTextViewText(id, array[i].first)
		}
		notification.setCustomBigContentView(vies)
		notification.setStyle(NotificationCompat.DecoratedCustomViewStyle())

		return notification
	}

}