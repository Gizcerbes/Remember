package com.uogames.remembercards.ui.editCardFragment

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Card
import com.uogames.dto.Image
import com.uogames.dto.Phrase
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifNullOrEmpty
import com.uogames.repository.DataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class EditCardViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

	private val _cardID = MutableStateFlow(0)

	private val _firstPhrase: MutableStateFlow<Phrase?> = MutableStateFlow(null)
	val firstPhrase = _firstPhrase.asStateFlow()

	private val _secondPhrase: MutableStateFlow<Phrase?> = MutableStateFlow(null)
	val secondPhrase = _secondPhrase.asStateFlow()

	private val _image: MutableStateFlow<Image?> = MutableStateFlow(null)
	val image = _image.asStateFlow()

	private val _reason = MutableStateFlow("")
	val reason = _reason.asStateFlow()

	val listImageFlow = provider.images.getListFlow()

	fun reset() {
		_firstPhrase.value = null
		_secondPhrase.value = null
		_image.value = null
		_reason.value = ""
		_cardID.value = 0
	}

	fun load(id: Int) = viewModelScope.launch {
		reset()
		val card = provider.cards.getByIdFlow(id).first()
		card?.let {
			_cardID.value = card.id
			_firstPhrase.value = provider.phrase.getByIdFlow(card.idPhrase).first()
			_secondPhrase.value = provider.phrase.getByIdFlow(card.idTranslate).first()
			it.idImage?.let { imageID ->
				_image.value = provider.images.getById(imageID).first()
			}
			_reason.value = card.reason
		}
	}

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
		_image.value = image
	}

	fun getAudio(phrase: Phrase) = provider.pronounce.getByPhrase(phrase).map {
		android.net.Uri.parse(it?.audioUri.orEmpty())
	}

	fun setReason(reason: String) {
		_reason.value = reason
	}

	fun setBitmapImage(bitmap: Bitmap?) {
		if (bitmap != null) {
			viewModelScope.launch {
				val stream = ByteArrayOutputStream()
				val newWidth = 800
				val newHeight = bitmap.height * newWidth / bitmap.width
				val newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
				newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
				val id = provider.images.addAsync(Image(), stream.toByteArray()).await()
				_image.value = provider.images.getById(id).first()
			}
		} else {
			_image.value = null
		}
	}

	fun save(call: (Boolean) -> Unit) = viewModelScope.launch {
		val card = build().ifNull { return@launch call(false) }
		val res = provider.cards.addAsync(card).await()
		call(res > 0)
	}


	fun update(call: (Boolean) -> Unit) = viewModelScope.launch {
		val card = build().ifNull { return@launch call(false) }
		val res = provider.cards.updateAsync(card).await()
		call(res)
	}


	fun delete(call: (Boolean) -> Unit) = viewModelScope.launch {
		val res = provider.cards.deleteAsync(Card(_cardID.value, 0, 0, null, "")).await()
		call(res)
	}

	private fun build(): Card? {
		val id = _cardID.value
		val firstID = _firstPhrase.value?.id.ifNull { return null }
		val secondID = _secondPhrase.value?.id.ifNull { return null }
		val imageID = _image.value?.id
		val reason = _reason.value.ifNullOrEmpty { return null }
		return Card(id, firstID, secondID, imageID, reason)
	}

}