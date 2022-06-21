package com.uogames.remembercards.ui.editPhraseFragment

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaRecorder
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Image
import com.uogames.dto.Phrase
import com.uogames.dto.Pronunciation
import com.uogames.flags.Countries
import com.uogames.remembercards.App
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.toStringBase64
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.inject.Inject

class EditPhraseViewModel @Inject constructor(
	private val provider: DataProvider,
	application: Application
) : AndroidViewModel(application) {

	private class PhraseObject() {
		val id = MutableStateFlow(0)
		val phrase = MutableStateFlow("")
		val definition: MutableStateFlow<String?> = MutableStateFlow(null)
		val lang: MutableStateFlow<String?> = MutableStateFlow(null)
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


	private val _imgChanged = MutableStateFlow(false)
	private val _imgPhrase: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
	val imgPhrase = _imgPhrase.asStateFlow().map { it?.let { BitmapFactory.decodeByteArray(it, 0, it.size) } }
	val isImgPhraseNotNull get() = _imgPhrase.value?.isNotEmpty() ?: false


	private val _country = MutableStateFlow(Countries.UNITED_KINGDOM)
	val country = _country.asStateFlow()

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

	fun getContext(): Context {
		return getApplication<Application>().applicationContext
	}

	fun reset() {
		phraseObject.set(Phrase())
		_imgChanged.value = false
		_imgPhrase.value = null
		_country.value = Countries.UNITED_KINGDOM
		_lang.value = Locale.getDefault()
		_isFileWriting.value = false
		_timeWriting.value = -1
		_tempAudioFile = File.createTempFile("audio", ".gpp")
		_audioChanged.value = false
	}

	fun loadByID(id: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			val phrase = provider.phrase.getByIdFlow(id).first().ifNull { Phrase() }
			phraseObject.set(phrase)
			val splitLang = phrase.lang.ifNull { "eng-gb" }.split("-")
			_lang.value = Locale.forLanguageTag(splitLang[0])
			_country.value = Countries.search(splitLang[1]).ifNull { Countries.UNITED_KINGDOM }
			_imgPhrase.value = provider.images.getByPhrase(phrase).first()?.let { it.imgBase64.toUri().toFile().readBytes() }
			val audio = provider.pronounce.getByPhrase(phrase).first()
			_isFileWriting.value = true
			_tempAudioFile.writeBytes(audio?.dataBase64?.toUri()?.toFile()?.readBytes().ifNull { ByteArray(0) })
			_imgChanged.value = false
			_audioChanged.value = false
			_isFileWriting.value = false
			_timeWriting.value = -1
		}
	}

	fun setBitmapImage(bitmap: Bitmap?) {
		if (bitmap != null) {
			viewModelScope.launch {
				val stream = ByteArrayOutputStream()
				val newWidth = 800
				val newHeight = bitmap.height * newWidth / bitmap.width
				val newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
				newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
				_imgPhrase.value = stream.toByteArray()
			}
		} else {
			_imgPhrase.value = null
		}
		_imgChanged.value = true
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

	fun stopRecordAudio(recorder: MediaRecorder) {
		if (_isFileWriting.value) {
			recorder.stop()
			recorder.release()
			_isFileWriting.value = false
			jobWriting?.cancel()
		}
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

	fun save(call: (Boolean) -> Unit) {
		if (phrase.value.isNotEmpty()) viewModelScope.launch {
			val phrase = build(0)
			val res = provider.phrase.addAsync(phrase).await()
			call(res > 0)
		} else call(false)
	}

	fun update(id: Int, call: (Boolean) -> Unit) {
		if (phrase.value.isNotEmpty()) viewModelScope.launch {
			val phrase = build(id)
			val res = provider.phrase.updateAsync(phrase).await()
			call(res)
		} else call(false)
	}

	private suspend fun build(id: Int = 0): Phrase {
		phraseObject.id.value = id
		phraseObject.idImage.value = saveImageToId()
		phraseObject.idPronounce.value = savePronounceToId()
		phraseObject.lang.value = lang.value.isO3Language + "-" + country.value.isoCode
		return phraseObject.create()
	}

	private suspend fun savePronounceToId(): Int? {
		return viewModelScope.async(Dispatchers.IO) {
			if (_audioChanged.value && _tempAudioFile.length() > 0) {
				val id = provider.pronounce.addAsync(Pronunciation(0, "")).await().toInt()
				val fileName = "$id.gpp"
				getContext().openFileOutput(fileName, Context.MODE_PRIVATE).use {
					it.write(_tempAudioFile.readBytes())
					it.flush()
					it.close()
				}
				provider.pronounce.updateAsync(Pronunciation(id, File(getContext().filesDir, fileName).toUri().toString())).await()
				id
			} else {
				phraseObject.idPronounce.value
			}
		}.await()
	}

	private suspend fun saveImageToId(): Int? {
		return viewModelScope.async(Dispatchers.IO) {
			if (_imgChanged.value) {
				_imgPhrase.value?.let {
					val id = provider.images.addAsync(Image(0, "")).await().toInt()
					val fileName = "$id.png"
					getContext().openFileOutput(fileName, Context.MODE_PRIVATE).use {
						it.write(_imgPhrase.value)
						it.flush()
						it.close()
					}
					provider.images.updateAsync(Image(id, File(getContext().filesDir, fileName).toUri().toString())).await()
					Log.e("TAG", ": $id", )
					id
				}
			} else {
				phraseObject.idImage.value
			}
		}.await()
	}

	fun delete(id: Int, call: (Boolean) -> Unit) {
		viewModelScope.launch {
			val res = provider.phrase.deleteAsync(Phrase(id)).await()
			call(res)
		}
	}

}