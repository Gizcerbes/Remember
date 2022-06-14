package com.uogames.remembercards.ui.addPhrase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.core.graphics.get
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Phrase
import com.uogames.flags.Countries
import com.uogames.remembercards.utils.loop
import com.uogames.repository.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

class AddPhraseViewModel @Inject constructor(
	private val provider: DataProvider
) : ViewModel() {

	private val _imgPhrase: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
	val imgPhrase = _imgPhrase.asStateFlow().map { it?.let { BitmapFactory.decodeByteArray(it, 0, it.size) } }

	private val _country = MutableStateFlow(Countries.BELARUS)
	val country = _country.asStateFlow()

	private val _phrase = MutableStateFlow("")
	val phrase = _phrase.asStateFlow()

	private val _definition = MutableStateFlow("")
	val definition = _definition.asStateFlow()

	fun add(phrase: Phrase, result: (Boolean) -> Unit = {}) = viewModelScope.launch {
		val res = provider.phrase.addAsync(phrase).await() > 0
		launch(Dispatchers.Main) { result(res) }
	}

	fun setBitmapImage(bitmap: Bitmap) {
		viewModelScope.launch(Dispatchers.IO) {
			val stream = ByteArrayOutputStream()
			val newWidth = 800
			val newHeight = bitmap.height * newWidth / bitmap.width
			val newBitmap = Bitmap.createScaledBitmap(bitmap,newWidth,newHeight,true)
			newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
			_imgPhrase.value = stream.toByteArray()
		}
	}

	fun setPhrase(phrase: String) {
		_phrase.value = phrase
	}

	fun setDefinition(definition: String) {
		_definition.value = definition
	}

	fun save() {

	}

}