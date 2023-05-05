package com.uogames.remembercards.ui.phrase.editPhraseFragment

import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.media.MediaRecorder
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.LocalPhrase
import com.uogames.flags.Countries
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.utils.*
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.inject.Inject

class EditPhraseViewModel @Inject constructor(
    private val provider: DataProvider,
    private val player: ObservableMediaPlayer
) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    private class PhraseObject() {
        val id = MutableStateFlow(0)
        val phrase = MutableStateFlow("")
        val definition: MutableStateFlow<String?> = MutableStateFlow(null)
        val lang: MutableStateFlow<String> = MutableStateFlow("eng")
        val country: MutableStateFlow<String> = MutableStateFlow("UNITED_KINGDOM")
        val idPronounce: MutableStateFlow<Int?> = MutableStateFlow(null)
        val idImage: MutableStateFlow<Int?> = MutableStateFlow(null)
        val timeChange = MutableStateFlow(0L)
        private var globalId: UUID? = null
        private var globalOwner: String? = null

        fun create() = LocalPhrase(
            id = id.value,
            phrase = phrase.value,
            definition = definition.value,
            lang = lang.value,
            country = country.value,
            idPronounce = idPronounce.value,
            idImage = idImage.value,
            timeChange = Date().time,
            globalId = globalId,
            globalOwner = globalOwner,
            changed = true
        )

        fun set(obj: LocalPhrase) {
            id.value = obj.id
            phrase.value = obj.phrase
            definition.value = obj.definition
            lang.value = obj.lang
            country.value = obj.country
            idPronounce.value = obj.idPronounce
            idImage.value = obj.idImage
            timeChange.value = obj.timeChange
            globalId = obj.globalId
            globalOwner = obj.globalOwner
        }
    }

    private val phraseObject = PhraseObject()

    val phrase = phraseObject.phrase.asStateFlow()
    val definition = phraseObject.definition.asStateFlow()

    private val _imgPhrase: MutableStateFlow<LocalImage?> = MutableStateFlow(null)
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
    private var _tempAudioFile = File.createTempFile("audio", ".mp4")
    private var _tempAudioBytes = MutableStateFlow(ByteArray(0))
    val dataSize = _tempAudioBytes.map { it.size }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val tempAudioSource get() = MediaBytesSource(_tempAudioBytes.value)
    private val _statusRecorder = MutableStateFlow(0)
    val statusRecord = _statusRecorder.asStateFlow()

    val listImageFlow = provider.images.getListFlow()

    val adapter = ImageAdapter(this) { selectImage(it) }

    val preview = MutableStateFlow(false)
    val showRecord = MutableStateFlow(false)

    private var recorder: MediaRecorder? = null

    init {
        dataSize.observe(viewModelScope) {
            _statusRecorder.value = statusRecord()
        }
        isFileWriting.observe(viewModelScope) {
            _statusRecorder.value = statusRecord()
        }
        timeWriting.observe(viewModelScope) {
            _statusRecorder.value = statusRecord()
        }
        phraseObject.idImage.observe(viewModelScope) {
            _imgPhrase.value = it?.let { provider.images.getByIdFlow(it).first() }
        }
        phraseObject.phrase.observe(viewModelScope) {
            val languageIdentifier = LanguageIdentification.getClient(
                LanguageIdentificationOptions.Builder()
                    .setConfidenceThreshold(0.1f)
                    .build()
            )
            languageIdentifier.identifyPossibleLanguages(it).addOnSuccessListener { identifiedLang ->
                _languages.value = identifiedLang.map { loc -> Locale.forLanguageTag(loc.languageTag) }
            }
        }
        viewModelScope.launch {
            val country = provider.setting.get(GlobalViewModel.USER_NATIVE_COUNTRY).ifNull { "UNITED_KINGDOM" }
            _country.value = Countries.valueOf(country)
            phraseObject.country.value = country
        }
    }

    fun reset() {
        phraseObject.set(LocalPhrase())
        _imgPhrase.value = null
        viewModelScope.launch {
            val country = provider.setting.get(GlobalViewModel.USER_NATIVE_COUNTRY).ifNull { "UNITED_KINGDOM" }
            _country.value = Countries.valueOf(country)
            phraseObject.country.value = country
        }
        _lang.value = Locale.getDefault()
        _isFileWriting.value = false
        _timeWriting.value = -1
        _audioChanged.value = false
        _tempAudioBytes.value = ByteArray(0)
        _languages.value = listOf()
        _enableLanguageChoice.value = false
    }

    fun resetAudioData() {
        _tempAudioBytes.value = ByteArray(0)
    }

    private fun initRecorder(recorder: MediaRecorder) {
        this.recorder = recorder
        stopRecordAudio()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(_tempAudioFile)
        recorder.prepare()
    }

    fun statusRecord(): Int {
        if (isFileWriting.value) return 1
        return if (dataSize.value == 0) 0
        else 2
    }

    fun loadByID(id: Int): Job {
        _isFileWriting.value = true
        return viewModelScope.launch {
            val phrase = provider.phrase.getByIdFlow(id).first().ifNull { LocalPhrase() }
            phraseObject.set(phrase)
            //_lang.value = Locale.forLanguageTag(phrase.lang)
            forceSetLang(Locale.forLanguageTag(phrase.lang))
            _country.value = Countries.valueOf(phrase.country)
            _imgPhrase.value = provider.images.getByPhrase(phrase).first()
            val audio = provider.pronounce.getByPhrase(phrase).first()
            _tempAudioBytes.value = audio?.audioUri?.let { if (it.isNotEmpty()) it.toUri().toFile().readBytes() else null } ?: ByteArray(0)
            _tempAudioBytes.value.let { _tempAudioFile.writeBytes(it) }
            _audioChanged.value = false
            _isFileWriting.value = false
            _timeWriting.value = -1
        }
    }

    private fun selectImage(image: LocalImage) {
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
        resetAudioData()
        _timeWriting.value = 0
        initRecorder(recorder)
        player.stop()
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

    fun stopRecordAudio() = recorder?.let { stopRecordAudio(it) }

    private fun stopRecordAudio(recorder: MediaRecorder): Boolean = _isFileWriting.value.ifTrue {
        recorder.stop()
        recorder.release()
        val bytes = _tempAudioFile.readBytes()
        if (bytes.isNotEmpty()) _tempAudioBytes.value = bytes
        _isFileWriting.value = false
        jobWriting?.cancel()
    }

    fun play(anim: AnimationDrawable) {
        val resource = tempAudioSource.ifNull { return }
        player.play(resource, anim)
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

    fun recognizeLang() {
        val text = phraseObject.phrase.value
        if (text.isNotEmpty()){
            setLang(Locale.getDefault())
            return
        } else {
            val languageIdentifier = LanguageIdentification.getClient(
                LanguageIdentificationOptions.Builder()
                    .setConfidenceThreshold(0.1f)
                    .build()
            )
            languageIdentifier.identifyPossibleLanguages(text).addOnSuccessListener { identifiedLang ->
                _languages.value = identifiedLang.map { loc -> Locale.forLanguageTag(loc.languageTag) }
                if (identifiedLang.isNotEmpty()) setLang(Locale.forLanguageTag(identifiedLang.first().languageTag))
            }
        }
    }

    fun forceSetLang(locale: Locale) {
        _lang.value = if (locale.isO3Language.isNotEmpty()) locale else Locale.getDefault()
        _enableLanguageChoice.value = true
    }

    fun save(call: (Long) -> Unit) {
        if (phrase.value.isNotEmpty()) {
            viewModelScope.launch {
                val phrase = build(0)
                val res = provider.phrase.add(phrase)
                launch(Dispatchers.Main) { call(res) }
            }
        }
    }

    fun update(id: Int, call: (Boolean) -> Unit) {
        if (phrase.value.isNotEmpty()) {
            viewModelScope.launch {
                val phrase = build(id)
                val res = provider.phrase.update(phrase)
                launch(Dispatchers.Main) { call(res) }
                provider.clean()
            }
        } else {
            call(false)
        }
    }

    private suspend fun build(id: Int = 0): LocalPhrase {
        phraseObject.id.value = id
        phraseObject.idPronounce.value = savePronounceToId()
        phraseObject.lang.value = lang.value.isO3Language
        phraseObject.country.value = _country.value.toString()
        return phraseObject.create()
    }

    private suspend fun savePronounceToId(): Int? {
        val bytes = _tempAudioBytes.value.ifNull { ByteArray(0) }
        return if (_audioChanged.value && bytes.isNotEmpty()) {
            provider.pronounce.add(bytes)
        } else {
            phraseObject.idPronounce.value
        }
    }

    fun delete(id: Int, call: (Boolean) -> Unit) = viewModelScope.launch {
        val phrase = provider.phrase.getById(id).ifNull { return@launch }
        val res = provider.phrase.delete(phrase)
        launch(Dispatchers.Main) { call(res) }
    }
}
