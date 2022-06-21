package com.uogames.remembercards.ui.bookFragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Card
import com.uogames.dto.Phrase
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.toByteArrayBase64
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BookViewModel @Inject constructor(
	private val provider: DataProvider,
) : ViewModel() {

	val like = MutableStateFlow("")

	private val _size = MutableStateFlow(0)
	val size = _size.asStateFlow()

	private val likeSize = like.flatMapLatest { provider.phrase.countFlow(it) }

	init {
		likeSize.onEach { _size.value = it }.launchIn(viewModelScope)
	}

	fun reset() {
		like.value = ""
	}

	fun get(position: Int) = provider.phrase.getFlow(like.value, position)

	fun getAudio(phrase: Phrase) = provider.pronounce.getByPhrase(phrase).map {
		Uri.parse(it?.dataBase64.orEmpty())
	}

	fun getImage(phrase: Phrase) = provider.images.getByPhrase(phrase).map {
		Uri.parse(it?.imgBase64)
	}

	fun getImage(phrase: Phrase, call: (Uri?) -> Unit) {
		CoroutineScope(Dispatchers.IO)
		viewModelScope.launch {
			call(getImage(phrase).first())
		}
	}

	fun delete(phrase: Phrase, result: (Boolean) -> Unit = {}) = provider.phrase.delete(phrase, result)

	fun updateCard(phrase: Phrase, result: (Boolean) -> Unit = {}) = provider.phrase.update(phrase, result)


}