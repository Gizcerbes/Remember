package com.uogames.remembercards.ui.libraryFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Module
import com.uogames.repository.DataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class LibraryViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

	val like = MutableStateFlow("")

	val list = like.flatMapLatest { provider.module.getListLike(it) }

	fun reset(){
		like.value = ""
	}

	fun createModule(name: String, call: (Int) -> Unit) = viewModelScope.launch {
		val res = provider.module.add(Module(name = name))
		call(res.toInt())
	}

	fun getCountByModuleID(id: Int) = provider.moduleCard.getCountByModuleID(id)



}