package com.uogames.remembercards.broadcast

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.uogames.remembercards.App
import com.uogames.remembercards.R
import com.uogames.remembercards.viewmodel.GlobalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DownloadForeground : Service() {

	companion object {
		const val ACTION_START = "DownloadForeground.ACTION_START"
		const val ACTION_STOP = "DownloadForeground.ACTION_STOP"
	}

	private var started: Boolean = false

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		when (intent?.action) {
			ACTION_START -> startSharing()
			ACTION_STOP -> stopSharing()
			else -> {}
		}
		return super.onStartCommand(intent, flags, startId)
	}

	private fun startSharing() {
		if (started) return
		started = true
		val provider = GlobalViewModel(this).provider
		val scope = CoroutineScope(Dispatchers.Main)
		var contentTitle = ""
		var message: String = ""
		var maxCount: Int = 0
		var position: Int = 0
		var retry = 0
		val c = this
		scope.launch {
			var countElements = provider.download.count()
			contentTitle =  "${c.getText(R.string.download)}($countElements)"
			while (countElements > 0 && started) {
				try {
					val element = provider.download.getFirst()
					val mId = element?.globalModuleId
					if (mId != null) {
						val localModule = provider.module.download(mId) ?: continue
						message = localModule.name
						maxCount = provider.moduleCard.getGlobalCount(mId).toInt()
						position = 0
						val limit = 50
						while (position < maxCount && started) {
							provider.moduleCard.getGlobalListView(localModule.globalId, position.toLong(), limit).forEach {
								provider.moduleCard.fastSave(it, localModule)
								val mPosition = provider.moduleCard.getCountByModule(localModule)
								message = localModule.name + " $mPosition/$maxCount"
							}
							position += limit
						}
						position = maxCount
					}
					element?.globalCardId?.let {
						maxCount = 1
						position = maxCount
						message = "some card"
						provider.cards.download(it)
					}
					element?.globalPhraseId?.let {
						maxCount = 1
						position = maxCount
						message = "some phrase"
						provider.phrase.download(it)
					}
					if (started) element?.let { provider.download.delete(it) }
					else provider.download.clean()
					countElements = provider.download.count()
				} catch (e: Exception) {
					retry++
					delay(1000)
				}
			}
			started = false
			stopForeground(STOP_FOREGROUND_REMOVE)
		}
		val context = this
		scope.launch {
			val intent = Intent(context, NotificationReceiver::class.java).apply {
				action = ACTION_STOP
			}
			val stopIntent =
				PendingIntent.getBroadcast(
					context,
					0,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT
				)

			val notificationBuilder = NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
				.setContentTitle(contentTitle)
				.setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) R.drawable.ic_logo_trans else R.drawable.ic_launcher_round)
				.setSilent(true)
				.setContentText(message)
				.setProgress(maxCount, position, false)
				.addAction(
					0,
					"stop",
					stopIntent
				).setAutoCancel(true)

			startForeground(1, notificationBuilder.build())
			val manager = getSystemService(NotificationManager::class.java)
			while (started) {
				notificationBuilder
					.setContentTitle(contentTitle)
					.setContentText(message)
					.setProgress(maxCount, position, false)
				manager.notify(1, notificationBuilder.build())
				delay(1000)
			}
		}
	}

	private fun stopSharing() {
		started = false
			//stopForeground(STOP_FOREGROUND_REMOVE)
	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}


}