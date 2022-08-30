package com.uogames.remembercards

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
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

	private val _screenDifferent = MutableStateFlow(0.0)
	val  screenDifferent = _screenDifferent.asStateFlow()

	private var lastDestination: NavDestination? = null
	private var _backSize: Int = 0
	val backSize get() = _backSize
	private var _shouldReset: Boolean = false
	val shouldReset get() = _shouldReset

	private var job: Job? = null

	fun setShowKeyboard(view: View) {
		job?.cancel()
		job = viewModelScope.launch(Dispatchers.IO) {
			delay(10)
			val r = Rect()
			view.getWindowVisibleDisplayFrame(r)
			_isShowKey.value = r.bottom / view.rootView.height.toDouble() < 0.8
		}
	}

	fun setBackQueue(arrBackStack: ArrayDeque<NavBackStackEntry>?) {
		arrBackStack?.let {
			val lastDest = it.last().destination
			_shouldReset = it.size > _backSize || (it.size == _backSize && lastDestination != lastDest)
			_backSize = it.size
			lastDestination = lastDest
		}.ifNull {
			_shouldReset = false
		}
	}

	fun saveData(key: String, value: String?, finishCall: () -> Unit = {}) {
		viewModelScope.launch {
			provider.setting.save(key, value)
			finishCall()
		}
	}

	fun removeData(key: String) {
		viewModelScope.launch {
			provider.setting.remove(key)
		}
	}

	fun getFlow(key: String) = provider.setting.getFlow(key)

	fun saveUserName(name: String) = viewModelScope.launch { provider.setting.save(USER_NAME, name) }

	fun getUserName() = provider.setting.getFlow(USER_NAME)

	fun saveGlobalName(name: String) = viewModelScope.launch { provider.setting.save(GLOBAL_NAME, name) }

	fun getGlobalName() = provider.setting.getFlow(GLOBAL_NAME)

	fun saveUserNativeCountry(name: String) = viewModelScope.launch { provider.setting.save(USER_NATIVE_COUNTRY, name) }

	fun getUserNativeCountry() = provider.setting.getFlow(USER_NATIVE_COUNTRY)

	fun getCountPhrases() = provider.phrase.countFlow()

	fun getCountCards() = provider.cards.getCountFlow()

	fun getCountModules() = provider.module.getCount()

	fun addGameYesOrNoGameCount() = viewModelScope.launch {
		val count = provider.setting.getFlow(GAME_YES_OR_NO_COUNT).first()?.toInt().ifNull { 0 }
		provider.setting.save(GAME_YES_OR_NO_COUNT, (count + 1).toString())
	}

	fun setGameYesOrNotCount(count: Int) = viewModelScope.launch { provider.setting.save(GAME_YES_OR_NO_COUNT, count.toString()) }

	fun getGameYesOrNotGameCount() = provider.setting.getFlow(GAME_YES_OR_NO_COUNT)

	fun clean() = viewModelScope.launch(Dispatchers.IO) { provider.clean() }


}