package com.uogames.remembercards.ui.editCardFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Image
import com.uogames.dto.Phrase
import com.uogames.repository.DataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class EditCardViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

	private val _firstPhrase: MutableStateFlow<Phrase?> = MutableStateFlow(null)
	val firstPhrase = _firstPhrase.asStateFlow()

	private val _secondPhrase: MutableStateFlow<Phrase?> = MutableStateFlow(null)
	val secondPhrase = _secondPhrase.asStateFlow()

	private val _image: MutableStateFlow<Image?> = MutableStateFlow(null)
	val image = _image.asStateFlow()

	private val _imageID: MutableStateFlow<Image?> = MutableStateFlow(null)
	val imageID = _imageID.asStateFlow()

	private val _reason = MutableStateFlow("")
	val reason = _reason.asStateFlow()

	fun selectFirstPhrase(id: Int) {
		viewModelScope.launch {
			provider.phrase.getByIdFlow(id).first().let {
				_firstPhrase.value = it
			}
		}
	}

	fun selectSecondPhrase(id: Int) {
		viewModelScope.launch {
			provider.phrase.getByIdFlow(id).first().let {
				_secondPhrase.value = it
			}
		}
	}

	private suspend fun getByID(id: Int): Phrase? {
		return provider.phrase.getByIdFlow(id).first()
	}

	fun selectImage(image: Image) {
		_imageID.value = image
	}

	fun getAudio(phrase: Phrase) = provider.pronounce.getByPhrase(phrase).map {
		android.net.Uri.parse(it?.dataBase64.orEmpty())
	}

	fun setReason(reason:String){
		_reason.value = reason
	}

	fun setImage(id: Int){
		viewModelScope.launch {
			provider.images.getById(id).first().let {
				_image.value = it
			}
		}
	}


}