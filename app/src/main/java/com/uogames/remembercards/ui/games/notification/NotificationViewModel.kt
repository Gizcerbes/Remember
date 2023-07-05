package com.uogames.remembercards.ui.games.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.ErrorCard
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalImageView
import com.uogames.remembercards.ui.games.notification.type.NotificationTypeChoice
import com.uogames.remembercards.ui.games.notification.type.NotificationTypeShow
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.viewmodel.GlobalViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationViewModel @Inject constructor(
	private val globalViewModel: GlobalViewModel
) : ViewModel(),
	NotificationTypeShow.Model,
	NotificationReceiver.Model,
	NotificationTypeChoice.Model {

	companion object {
		const val NOTIFICATION_TYPE = "NotificationViewModel.NOTIFICATION_TYPE"
	}

	private val provider = globalViewModel.provider

	val moduleID = MutableStateFlow<Int?>(null)

	val notificationModuleID get() = globalViewModel.notificationModuleId

	override suspend fun getRandomCard(): LocalCardView? {
		val moduleId = globalViewModel.provider.setting.get(GlobalViewModel.MODULE_ID_FOR_NOTIFICATION)?.toInt()
		return moduleId?.let {
			if (Math.random() > 0.25) provider.moduleCard.getRandomModuleView(it)?.card
			else provider.moduleCard.getUnknowable(it)?.card
		}.ifNull {
			if (Math.random() > 0.25) provider.cards.getRandomView()
			else provider.cards.getUnknowableView()
		}
	}

//	override suspend fun getRandomWithout(cardId: Int): LocalCardView? {
//		val moduleId = globalViewModel.provider.setting.get(GlobalViewModel.MODULE_ID_FOR_NOTIFICATION)?.toInt()
//		val card = provider.cards.getById(cardId) ?: return null
//		return if (moduleId != null) {
//			if (Math.random() > 0.5) provider.moduleCard.getConfusingViewWithout(
//				moduleId,
//				card.idPhrase,
//				arrayOf(card.idPhrase, card.idTranslate)
//			)?.card.ifNull {
//				provider.moduleCard.getRandomViewWithoutPhrases(moduleId, arrayOf(card.idPhrase, card.idTranslate))?.card
//			}
//			else provider.moduleCard.getRandomViewWithoutPhrases(moduleId, arrayOf(card.idPhrase, card.idTranslate))?.card
//		} else {
//			if (Math.random() > 0.5) provider.cards.getConfusingViewWithout(card.idPhrase, arrayOf(card.idPhrase, card.idTranslate))
//				?: provider.cards.getRandomViewWithoutPhrases(arrayOf(card.idPhrase, card.idTranslate))
//			else provider.cards.getRandomViewWithoutPhrases(arrayOf(card.idPhrase, card.idTranslate))
//		}
//	}

	override suspend fun getRandomWithoutPhrases(card: LocalCardView, phraseIds: Array<Int>): LocalCardView? {
		val moduleId = globalViewModel.provider.setting.get(GlobalViewModel.MODULE_ID_FOR_NOTIFICATION)?.toInt()
		return if (moduleId != null) {
			val r = if (Math.random() > 0.5) provider.moduleCard.getConfusingViewWithout(moduleId, card.phrase.id, phraseIds)?.card else null
			r ?: provider.moduleCard.getRandomViewWithoutPhrases(moduleId, phraseIds)?.card
		} else {
			val r = if (Math.random() > 0.5) provider.cards.getConfusingViewWithout(card.phrase.id, phraseIds) else null
			r	?: provider.cards.getRandomViewWithoutPhrases(phraseIds)
		}
	}

	override suspend fun getCardById(cardId: Int) = provider.cards.getViewByID(cardId)

	override suspend fun saveResult(firstCardId: Int, secondCardId: Int, result: Boolean) {
		val first = getCardById(firstCardId) ?: return
		val second = getCardById(secondCardId) ?: return
		val trues = provider.errorCardProvider.getByPhraseAndTranslate(first.phrase.id, first.translate.id)
		val err = provider.errorCardProvider.getByPhraseAndTranslate(first.phrase.id, second.translate.id)
		if (err != null) {
			provider.errorCardProvider.update(updateErrorCard(err, result))
		} else {
			val nc = updateErrorCard(ErrorCard(idPhrase = first.phrase.id, idTranslate = second.translate.id), result)
			if (nc.percentCorrect < 100) provider.errorCardProvider.add(nc)
		}
		if (trues != null) {
			provider.errorCardProvider.update(updateErrorCard(trues, result))
		} else {
			val nc = updateErrorCard(ErrorCard(idPhrase = first.phrase.id, idTranslate = first.translate.id), result)
			if (nc.percentCorrect < 100) provider.errorCardProvider.add(nc)
		}
	}

	private fun updateErrorCard(errorCard: ErrorCard, result: Boolean): ErrorCard {
		if (result) errorCard.correct++ else errorCard.incorrect++
		errorCard.percentCorrect = (errorCard.correct * 100 / (errorCard.correct + errorCard.incorrect)).toByte()
		return errorCard
	}

	override suspend fun getData(localImageView: LocalImageView) = globalViewModel.provider.images.load(localImageView)

	fun saveSelectedModule() = viewModelScope.launch { globalViewModel.saveNotificationModuleID(moduleID.value) }

	fun getModuleByIdAsync(id: Int) = viewModelScope.async { provider.module.getById(id) }
	override suspend fun getType() = provider.setting.get(NOTIFICATION_TYPE)?.let { NotificationReceiver.NotificationType.valueOf(it) }
	override suspend fun setType(type: NotificationReceiver.NotificationType) = provider.setting.save(NOTIFICATION_TYPE, type.toString())


}