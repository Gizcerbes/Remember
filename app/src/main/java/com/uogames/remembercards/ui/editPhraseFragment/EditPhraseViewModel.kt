package com.uogames.remembercards.ui.editPhraseFragment

import android.graphics.Bitmap
import android.media.MediaRecorder
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.uogames.dto.Image
import com.uogames.dto.Phrase
import com.uogames.flags.Countries
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.utils.*
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

	private val argPhraseId: MutableStateFlow<Int?> = MutableStateFlow(null)

	private val phraseObject = PhraseObject()

	val phrase = phraseObject.phrase.asStateFlow()
	val definition = phraseObject.definition.asStateFlow()

	private val _imgPhrase: MutableStateFlow<Image?> = MutableStateFlow(null)
	val imgPhrase = _imgPhrase.asStateFlow()

	private val _country = MutableStateFlow(Countries.UNITED_KINGDOM)

	private val _languages: MutableStateFlow<List<Locale>> = MutableStateFlow(listOf())
	val languages = _languages.asStateFlow()

	private val _enableLanguageChoice = MutableStateFlow(false)
	val enableLanguageChoice = _enableLanguageChoice.asStateFlow()

	private val _lang = MutableStateFlow(Locale.getDefault())
	val lang = _lang.asStateFlow()

	private val _isFileWriting = MutableStateFlow(false)
	val isFileWriting = _isFileWriting.asStateFlow()

	private val _timeWriting = MutableStateFlow(-1)
	val timeWriting = _timeWriting.asStateFlow()
	private var jobWriting: Job? = null

	private var _audioChanged = MutableStateFlow(false)
	private var _tempAudioFile = File.createTempFile("audio", ".gpp")
	private var _tempAudioBytes: ByteArray? = null
	val tempAudioSource get() = MediaBytesSource(_tempAudioBytes)

	val listImageFlow = provider.images.getListFlow()

	init {
		phraseObject.idImage.observeWhile(viewModelScope, Dispatchers.IO) {
			_imgPhrase.value = it?.let { provider.images.getByIdFlow(it).first() }
		}
		viewModelScope.launch(Dispatchers.IO) {
			_country.value = Countries.valueOf(provider.setting.get(GlobalViewModel.USER_NATIVE_COUNTRY).ifNull { "UNITED_KINGDOM" })
		}
		phraseObject.phrase.observeWhile(viewModelScope, Dispatchers.IO){
			val languageIdentifier = LanguageIdentification.getClient(
				LanguageIdentificationOptions.Builder()
					.setConfidenceThreshold(0.1f)
					.build()
			)
			languageIdentifier.identifyPossibleLanguages(it)
				.addOnSuccessListener { identifiedLang ->
					_languages.value = identifiedLang.map { loc -> Locale.forLanguageTag(loc.languageTag) }
				}
		}
	}

	fun setArgPhraseId(id: Int?): Boolean {
		val res = id != argPhraseId.value
		argPhraseId.value = id
		return res
	}

	fun reset() {
		phraseObject.set(Phrase())
		_imgPhrase.value = null
		viewModelScope.launch(Dispatchers.IO) {
			_country.value = Countries.valueOf(provider.setting.get(GlobalViewModel.USER_NATIVE_COUNTRY).ifNull { "UNITED_KINGDOM" })
		}
		_lang.value = Locale.getDefault()
		_isFileWriting.value = false
		_timeWriting.value = -1
		_audioChanged.value = false
		_tempAudioBytes = ByteArray(0)
		_languages.value = listOf()
		_enableLanguageChoice.value = false
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
			val id = provider.images.add(stream.toByteArray())
			phraseObject.idImage.value = id
		}
	}.ifNull {
		phraseObject.idImage.value = null
	}


	fun startRecordAudio(recorder: MediaRecorder) {
		stopRecordAudio(recorder)
		_tempAudioFile = File.createTempFile("audio", ".gpp")
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
		val bytes = _tempAudioFile.readBytes()
		if (bytes.isNotEmpty()) _tempAudioBytes = bytes
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
		_enableLanguageChoice.value.ifFalse {
			_lang.value = if (tag.isO3Language.isNotEmpty()) tag else Locale.getDefault()
		}
	}

	fun forceSetLang(locale: Locale){
		_lang.value = if (locale.isO3Language.isNotEmpty()) locale else Locale.getDefault()
		_enableLanguageChoice.value = true
	}

	fun save(call: (Long?) -> Unit) {
		if (phrase.value.isNotEmpty()) viewModelScope.launch {
			val phrase = build(0)
			val res = provider.phrase.add(phrase)
			call(res)
		} else call(null)
	}

	fun update(id: Int, call: (Boolean) -> Unit) {
		if (phrase.value.isNotEmpty()) viewModelScope.launch {
			val phrase = build(id)
			val res = provider.phrase.update(phrase)
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
		val bytes = _tempAudioBytes.ifNull { ByteArray(0) }
		return if (_audioChanged.value && bytes.isNotEmpty()) {
			provider.pronounce.add(bytes)
		} else {
			phraseObject.idPronounce.value
		}
	}

	fun delete(id: Int, call: (Boolean) -> Unit) = viewModelScope.launch {
		val phrase = provider.phrase.getById(id).ifNull { return@launch }
		val res = provider.phrase.delete(phrase)
		call(res)
	}
}