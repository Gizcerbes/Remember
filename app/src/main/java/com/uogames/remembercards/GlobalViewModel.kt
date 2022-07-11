package com.uogames.remembercards

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class GlobalViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

	companion object {
		const val USER_NAME = "USER_NAME"
		const val GLOBAL_NAME = "GLOBAL_NAME"
		const val USER_NATIVE_COUNTRY = "USER_NATIVE_COUNTRY"
		const val GAME_YES_OR_NO_COUNT = "GAME_YES_OR_NO_COUNT"
	}

	private val _isShowKey = MutableStateFlow(false)
	val isShowKey = _isShowKey.asStateFlow()

	private var job: Job? = null

	fun setShowKeyboard(view: View) {
		job?.cancel()
		job = viewModelScope.launch {
			delay(10)
			val r = Rect()
			view.getWindowVisibleDisplayFrame(r)
			_isShowKey.value = r.bottom / view.rootView.height.toDouble() < 0.8
		}
	}

	fun saveData(key: String, value: String?, finishCall: () -> Unit = {}) {
		viewModelScope.launch {
			provider.setting.saveAsync(key, value).await()
			finishCall()
		}
	}

	fun removeData(key: String) {
		viewModelScope.launch {
			provider.setting.removeAsync(key).await()
		}
	}

	fun getFlow(key: String) = provider.setting.getFlow(key)

	fun saveUserName(name: String) = viewModelScope.launch { provider.setting.saveAsync(USER_NAME, name).await() }

	fun getUserName() = provider.setting.getFlow(USER_NAME)

	fun saveGlobalName(name: String) = viewModelScope.launch { provider.setting.saveAsync(GLOBAL_NAME, name).await() }

	fun getGlobalName() = provider.setting.getFlow(GLOBAL_NAME)

	fun saveUserNativeCountry(name: String) = viewModelScope.launch { provider.setting.saveAsync(USER_NATIVE_COUNTRY, name).await() }

	fun getUserNativeCountry() = provider.setting.getFlow(USER_NATIVE_COUNTRY)

	fun getCountPhrases() = provider.phrase.countFlow()

	fun getCountCards() = provider.cards.getCountFlow()

	fun getCountModules() = provider.module.getCount()

	fun addGameYesOrNoGameCount() = viewModelScope.launch {
		val count = provider.setting.getFlow(GAME_YES_OR_NO_COUNT).first()?.toInt().ifNull { 0 }
		provider.setting.saveAsync(GAME_YES_OR_NO_COUNT, (count + 1).toString()).await()
	}

	fun setGameYesOrNotCount(count: Int) = viewModelScope.launch { provider.setting.saveAsync(GAME_YES_OR_NO_COUNT, count.toString()).await() }

	fun getGameYesOrNotGameCount() = provider.setting.getFlow(GAME_YES_OR_NO_COUNT)

	fun clean() = provider.clean()


}