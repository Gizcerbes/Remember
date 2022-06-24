package com.uogames.remembercards

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.repository.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GlobalViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

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

	fun saveData(key:String, value:String?) {
		viewModelScope.launch{
			provider.setting.saveAsync(key, value).await()
		}
	}

	fun removeData(key: String){
		viewModelScope.launch {
			provider.setting.removeAsync(key).await()
		}
	}

	fun getFlow(key: String) = provider.setting.getFlow(key)

}