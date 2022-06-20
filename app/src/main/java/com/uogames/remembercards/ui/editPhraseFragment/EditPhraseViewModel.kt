package com.uogames.remembercards.ui.editPhraseFragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaRecorder
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Image
import com.uogames.dto.Phrase
import com.uogames.dto.Pronunciation
import com.uogames.flags.Countries
import com.uogames.flags.Languages
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.toStringBase64
import com.uogames.repository.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.inject.Inject

class EditPhraseViewModel @Inject constructor(
	private val provider: DataProvider
) : ViewModel() {

	private val _imgPhrase: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
	val imgPhrase = _imgPhrase.asStateFlow().map { it?.let { BitmapFactory.decodeByteArray(it, 0, it.size) } }
	val isImgPhraseNotNull get() = _imgPhrase.value?.isNotEmpty() ?: false

	private val _country = MutableStateFlow(Countries.UNITED_KINGDOM)
	val country = _country.asStateFlow()

	private val _lang = MutableStateFlow(Locale.getDefault())
	val lang = _lang.asStateFlow()

	private val _phrase = MutableStateFlow("")
	val phrase = _phrase.asStateFlow()

	private val _definition = MutableStateFlow("")
	val definition = _definition.asStateFlow()

	private val _isFileWriting = MutableStateFlow(false)
	val isFileWriting = _isFileWriting.asStateFlow()

	private val _timeWriting = MutableStateFlow(-1)
	val timeWriting = _timeWriting.asStateFlow()
	private var jobWriting: Job? = null

	private var _tempAudioFile = File.createTempFile("audio", ".gpp")
	val tempAudioSource get() = MediaBytesSource(_tempAudioFile.readBytes())

	fun reset() {
		_imgPhrase.value = null
		_country.value = Countries.UNITED_KINGDOM
		_lang.value = Locale.getDefault()
		_phrase.value = ""
		_definition.value = ""
		_isFileWriting.value = false
		_timeWriting.value = -1
		_tempAudioFile = File.createTempFile("audio", ".gpp")
	}

	fun loadByID(id: Int) {
		viewModelScope.launch {
			val phrase = provider.phrase.getByIdFlow(id).first()
			phrase?.let {
				_phrase.value = phrase.phrase
				_definition.value = phrase.definition ?: ""
				phrase.lang?.let {
					val l = it.split("-")
					_lang.value = Locale.forLanguageTag(it)
					_country.value = Countries.valueOf(l[1])
				}
				val img = provider.images.getByPhrase(phrase).first()
				img?.let {
					_imgPhrase.value = Base64.decode(it.imgBase64, Base64.DEFAULT)
				}
				val audio = provider.pronounce.getByPhrase(phrase).first()
				audio?.let {
					_isFileWriting.value = true
					_tempAudioFile.writeBytes(Base64.decode(it.dataBase64, Base64.DEFAULT))
					_isFileWriting.value = false
				}
			}
		}
	}

	fun setBitmapImage(bitmap: Bitmap?) {
		if (bitmap != null) viewModelScope.launch(Dispatchers.IO) {
			val stream = ByteArrayOutputStream()
			val newWidth = 800
			val newHeight = bitmap.height * newWidth / bitmap.width
			val newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
			newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
			_imgPhrase.value = stream.toByteArray()
		} else {
			_imgPhrase.value = null
		}
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
		_phrase.value = phrase
	}

	fun setDefinition(definition: String) {
		_definition.value = definition
	}

	fun setLang(tag: Locale) {
		_lang.value = if (tag.isO3Language.isNotEmpty()) tag else Locale.getDefault()
	}

	fun save(call: (Boolean) -> Unit) {
		if (_phrase.value.isNotEmpty()) viewModelScope.launch {
			val phrase = createPhrase(0)
			val res = provider.phrase.addAsync(phrase).await()
			call(res > 0)
		} else call(false)
	}

	fun update(id: Int, call: (Boolean) -> Unit) {
		if (_phrase.value.isNotEmpty()) viewModelScope.launch {
			val phrase = createPhrase(id)
			val res = provider.phrase.updateAsync(phrase).await()
			provider.clean()
			call(res)
		} else call(false)
	}

	private suspend fun createPhrase(id: Int = 0): Phrase {
		val imageID = _imgPhrase.value?.let {
			if (it.isNotEmpty()) provider.images.addAsync(Image(0, it.toStringBase64())).await()
			else null
		}
		val pronounceID = if (_tempAudioFile.length() > 0)
			provider.pronounce.addAsync(Pronunciation(0, _tempAudioFile.readBytes().toStringBase64())).await()
		else null
		return Phrase(
			id = id,
			phrase = _phrase.value,
			definition = _definition.value,
			lang = lang.first().isO3Language + "-" + _country.value.toString(),
			idPronounce = pronounceID?.toInt(),
			idImage = imageID?.toInt(),
			timeChange = Date().time
		)
	}

	fun delete(id: Int, call: (Boolean) -> Unit) {
		viewModelScope.launch {
			val res = provider.phrase.deleteAsync(Phrase(id)).await()
			provider.clean()
			call(res)
		}
	}

}