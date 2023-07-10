package com.uogames.remembercards.broadcast

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.uogames.remembercards.ui.games.notification.NotificationViewModel
import com.uogames.remembercards.ui.games.notification.type.NotificationTypeChoice
import com.uogames.remembercards.ui.games.notification.type.NotificationTypeShow
import com.uogames.remembercards.viewmodel.GlobalViewModel
import kotlinx.coroutines.runBlocking

class NotificationReceiver : BroadcastReceiver() {

	enum class NotificationType {
		SHOW, CHOICE
	}

	interface Model {
		suspend fun getType(): NotificationType?

		suspend fun setType(type: NotificationType)
	}

	companion object {
		const val ACTION_NEW_NOTIFICATION = "NotificationReceiver.ACTION_NEW_NOTIFICATION"
	}

	override fun onReceive(context: Context?, intent: Intent?): Unit = runBlocking {
		Log.e("TAG", "onReceive: ${intent?.action}", )
		if (context == null || intent == null) return@runBlocking
		val model = NotificationViewModel(GlobalViewModel(context))
		val builder = when (intent.action) {
			ACTION_NEW_NOTIFICATION -> newNotification(context, model)
			NotificationTypeShow.ACTION_FRONT_SIDE -> NotificationTypeShow.frontSideNotification(context, model, intent)
			NotificationTypeShow.ACTION_BACK_SIDE -> NotificationTypeShow.backSideNotification(context, model, intent)
			NotificationTypeChoice.ACTION_CHECK -> NotificationTypeChoice.actionCheck(context, model, intent)
			DownloadForeground.ACTION_START -> startForeground(context, DownloadForeground.ACTION_START)
			DownloadForeground.ACTION_STOP -> startForeground(context, DownloadForeground.ACTION_STOP)
			else -> null
		}
		builder?.let {
			val manager = context.getSystemService(NotificationManager::class.java)
			manager.notify(0, it.build())
		}
	}

	private suspend fun newNotification(context: Context, model: NotificationViewModel): NotificationCompat.Builder? {
		return when(model.getType()){
			NotificationType.SHOW -> NotificationTypeShow.createNotificationBuilder(context, model)
			NotificationType.CHOICE -> NotificationTypeChoice.newNotification(context, model)
			else -> { NotificationTypeShow.newNotification(context, model) }
		}
	}

	private fun startForeground(context: Context, action: String = DownloadForeground.ACTION_STOP): NotificationCompat.Builder? {
		context.startService(Intent(context, DownloadForeground::class.java).apply { this.action = action })
		return null
	}

}