package com.uogames.remembercards.ui.cropFragment

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CropViewModel @Inject constructor() : ViewModel() {

    private val _data: MutableStateFlow<Bitmap?> = MutableStateFlow(null)

    private val _rotateStat = MutableStateFlow(0)
    val rotateStat = _rotateStat.asStateFlow()

    fun reset() {
        _rotateStat.value = 0
        _data.value = null
    }

    fun putData(bitmap: Bitmap) {
        _data.value = bitmap
        _rotateStat.value = 0
    }

    fun getData(): Bitmap? {
        return _data.value
    }

    fun leftRotate() {
        val rot = _rotateStat.value - 1
        if (rot < 0) {
            _rotateStat.value = 3
        } else {
            _rotateStat.value = rot
        }
    }

    fun rightRotate() {
        _rotateStat.value++
    }
}
