package com.uogames.remembercards.ui.editPhraseFragment

import android.graphics.Bitmap
import android.media.MediaRecorder
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Image
import com.uogames.dto.Phrase
import com.uogames.dto.Pronunciation
import com.uogames.flags.Countries
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifTrue
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.inject.Inject

class EditPhraseViewModel @Inject constructor(
	private val provider: DataProvider
) : ViewModel() {

	private class PhraseObject() {
		val id = MutableStateFlow(0)
		val phrase = MutableStateFlow("")
		val definition: MutableStateFlow<String?> = MutableStateFlow(null)
		val lang: MutableStateFlow<String> = MutableStateFlow("eng-gb")
		val idPronounce: MutableStateFlow<Int?> = MutableStateFlow(null)
		val idImage: MutableStateFlow<Int?> = MutableStateFlow(null)
		val timeChange = MutableStateFlow(0L)

		fun create() = Phrase(
			id = id.value,
			phrase = phrase.value,
			definition = definition.value,
			lang = lang.value,
			idPronounce = idPronounce.value,
			idImage = idImage.value,
			timeChange = Date().time
		)

		fun set(obj: Phrase) {
			id.value = obj.id
			phrase.value = obj.phrase
			definition.value = obj.definition
			lang.value = obj.lang
			idPronounce.value = obj.idPronounce
			idImage.value = obj.idImage
			timeChange.value = obj.timeChange
		}
	}

	private val phraseObject = PhraseObject()

	val phrase = phraseObject.phrase.asStateFlow()
	val definition = phraseObject.definition.asStateFlow()

	private val _imgPhrase: MutableStateFlow<Image?> = MutableStateFlow(null)
	val imgPhrase = _imgPhrase.asStateFlow()

	private val _country = MutableStateFlow(Countries.UNITED_KINGDOM)

	private val _lang = MutableStateFlow(Locale.getDefault())
	val lang = _lang.asStateFlow()

	private val _isFileWriting = MutableStateFlow(false)
	val isFileWriting = _isFileWriting.asStateFlow()

	private val _timeWriting = MutableStateFlow(-1)
	val timeWriting = _timeWriting.asStateFlow()
	private var jobWriting: Job? = null

	private var _audioChanged = MutableStateFlow(false)
	private var _tempAudioFile = File.createTempFile("audio", ".gpp")
	val tempAudioSource get() = MediaBytesSource(_tempAudioFile.readBytes())

	val listImageFlow = provider.images.getListFlow()

	init {
		phraseObject.idImage.onEach {
			_imgPhrase.value = it?.let { provider.images.getByIdFlow(it).first() }
		}.launchIn(viewModelScope)
		viewModelScope.launch {
			_country.value = Countries.valueOf(provider.setting.getFlow(GlobalViewModel.USER_NATIVE_COUNTRY).first().ifNull { "UNITED_KINGDOM" })
		}
	}

	fun reset() {
		phraseObject.set(Phrase())
		_imgPhrase.value = null
		viewModelScope.launch {
			_country.value = Countries.valueOf(provider.setting.getFlow(GlobalViewModel.USER_NATIVE_COUNTRY).first().ifNull { "UNITED_KINGDOM" })
		}
		_lang.value = Locale.getDefault()
		_isFileWriting.value = false
		_timeWriting.value = -1
		_audioChanged.value = false
		_tempAudioFile.writeBytes(ByteArray(0))
	}

	fun loadByID(id: Int) = viewModelScope.launch(Dispatchers.IO) {
		val phrase = provider.phrase.getByIdFlow(id).first().ifNull { Phrase() }
		phraseObject.set(phrase)
		val splitLang = phrase.lang.ifNull { "eng-gb" }.split("-")
		_lang.value = Locale.forLanguageTag(splitLang[0])
		_country.value = Countries.search(splitLang[1]).ifNull { Countries.UNITED_KINGDOM }
		_imgPhrase.value = provider.images.getByPhrase(phrase).first()
		val audio = provider.pronounce.getByPhrase(phrase).first()
		_isFileWriting.value = true
		audio?.audioUri?.let { if (it.isNotEmpty()) _tempAudioFile.writeBytes(it.toUri().toFile().readBytes()) }
		_audioChanged.value = false
		_isFileWriting.value = false
		_timeWriting.value = -1
	}


	fun selectImage(image: Image) {
		phraseObject.idImage.value = image.id
	}

	fun setBitmapImage(bitmap: Bitmap?) = bitmap?.let {
		viewModelScope.launch {
			val stream = ByteArrayOutputStream()
			val newWidth = 800
			val newHeight = bitmap.height * newWidth / bitmap.width
			val newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
			newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
			val id = provider.images.addAsync(Image(), stream.toByteArray()).await()
			phraseObject.idImage.value = id
		}
	}.ifNull {
		phraseObject.idImage.value = null
	}


	fun startRecordAudio(recorder: MediaRecorder) {
		jobWriting?.cancel()
		_timeWriting.value = 0
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
		recorder.setOutputFile(_tempAudioFile)
		recorder.prepare()
		recorder.start()
		_isFileWriting.value = true
		jobWriting = viewModelScope.launch {
			while (true) {
				delay(1000)
				_timeWriting.value++
				if (_timeWriting.value >= 60) stopRecordAudio(recorder)
			}
		}
		_audioChanged.value = true
	}

	fun stopRecordAudio(recorder: MediaRecorder): Boolean = _isFileWriting.value.ifTrue {
		recorder.stop()
		recorder.release()
		_isFileWriting.value = false
		jobWriting?.cancel()
	}


	fun setPhrase(phrase: String) {
		phraseObject.phrase.value = phrase
	}

	fun setDefinition(definition: String) {
		phraseObject.definition.value = definition
	}

	fun setLang(tag: Locale) {
		_lang.value = if (tag.isO3Language.isNotEmpty()) tag else Locale.getDefault()
	}

	fun save(call: (Long?) -> Unit) {
		if (phrase.value.isNotEmpty()) viewModelScope.launch {
			val phrase = build(0)
			val res = provider.phrase.addAsync(phrase).await()
			call(res)
		} else call(null)
	}

	fun update(id: Int, call: (Boolean) -> Unit) {
		if (phrase.value.isNotEmpty()) viewModelScope.launch {
			val phrase = build(id)
			val res = provider.phrase.updateAsync(phrase).await()
			call(res)
			provider.clean()
		} else call(false)
	}

	private suspend fun build(id: Int = 0): Phrase {
		phraseObject.id.value = id
		phraseObject.idPronounce.value = savePronounceToId()
		phraseObject.lang.value = lang.value.isO3Language + "-" + _country.value.toString()
		return phraseObject.create()
	}

	private suspend fun savePronounceToId(): Int? {
		return if (_audioChanged.value && _tempAudioFile.length() > 0) {
			provider.pronounce.addAsync(_tempAudioFile.readBytes()).await()
		} else {
			phraseObject.idPronounce.value
		}
	}

	fun delete(id: Int, call: (Boolean) -> Unit) = viewModelScope.launch {
		val phrase = provider.phrase.getById(id).ifNull { return@launch }
		val res = provider.phrase.deleteAsync(phrase).await()
		call(res)
	}

}