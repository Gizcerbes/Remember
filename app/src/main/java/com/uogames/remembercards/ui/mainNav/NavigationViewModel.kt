package com.uogames.remembercards.ui.mainNav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NavigationViewModel @Inject constructor() : ViewModel() {

	private val _id = MutableStateFlow(-1)
	val id = _id.asStateFlow()

	fun setId(id: Int): Boolean = _id.compareAndSet(_id.value, id)

}