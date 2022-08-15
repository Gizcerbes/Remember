package com.uogames.remembercards.ui.editModuleFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.Card
import com.uogames.dto.local.Module
import com.uogames.dto.local.ModuleCard
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditModuleViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

	val moduleID = MutableStateFlow(0)

	val module = moduleID.flatMapLatest { provider.module.getByIdFlow(it) }

	val moduleCardsList = module.flatMapLatest { provider.moduleCard.getByModule(it.ifNull { Module() }) }

	fun reset(){
		moduleID.value = 0
	}

	fun getCard(moduleCard: ModuleCard) = provider.cards.getByModuleCardFlow(moduleCard)

	fun getCard(id: Int) = provider.cards.getByIdFlow(id)

	suspend fun delete(module: Module) = provider.module.delete(module)

	fun addModuleCard(moduleID: Int, card: Card, call: (Boolean) -> Unit) = viewModelScope.launch {
		val res = provider.moduleCard.insert(ModuleCard(idModule =  moduleID, idCard = card.id))
		call(res > 0)
	}


	fun removeModuleCard(moduleCard: ModuleCard, call: (Boolean) -> Unit) = viewModelScope.launch {
		val res = provider.moduleCard.delete(moduleCard)
		call(res)
	}

}