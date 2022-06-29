package com.uogames.remembercards.ui.bookFragment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Phrase
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

	private val _listID:MutableStateFlow<List<Int>> = MutableStateFlow(listOf())
	val listId = _listID.asStateFlow()


	@ExperimentalCoroutinesApi
	private val likeSize = like.flatMapLatest { provider.phrase.countFlow(it) }.apply {
		onEach { _size.value = it }.launchIn(viewModelScope)
	}
	@ExperimentalCoroutinesApi
	private val likeListId  = like.flatMapLatest { provider.phrase.getListId(it) }.apply {
		onEach { _listID.value = it }.launchIn(viewModelScope)
	}

	init {
		//likeSize.onEach { _size.value = it }.launchIn(viewModelScope)
		//likeListId.onEach { _listID.value = it }.launchIn(viewModelScope)
	}

	fun reset() {
		like.value = ""
	}

	fun get(position: Int) = provider.phrase.getFlow(like.value, position)

	fun getById(id:Int) = provider.phrase.getByIdFlow(id)

	fun getAudio(phrase: Phrase) = provider.pronounce.getByPhrase(phrase).map {
		Uri.parse(it?.audioUri.orEmpty())
	}

	fun getImage(phrase: Phrase) = provider.images.getByPhrase(phrase).map {
		Uri.parse(it?.imgUri)
	}

	fun getImage(phrase: Phrase, call: (Uri?) -> Unit) {
		CoroutineScope(Dispatchers.IO)
		viewModelScope.launch {
			call(getImage(phrase).first())
		}
	}

}