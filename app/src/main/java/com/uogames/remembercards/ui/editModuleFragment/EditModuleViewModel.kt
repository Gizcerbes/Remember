package com.uogames.remembercards.ui.editModuleFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Card
import com.uogames.dto.Module
import com.uogames.dto.ModuleCard
import com.uogames.dto.Phrase
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditModuleViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

	val moduleID = MutableStateFlow(0)

	val module = moduleID.flatMapLatest { provider.module.getByID(it) }

	val moduleCardsList = module.flatMapLatest { provider.moduleCard.getByModule(it.ifNull { Module() }) }

	fun getCard(moduleCard: ModuleCard) = provider.cards.getByModuleCard(moduleCard)

	fun getCard(id: Int) = provider.cards.getByIdFlow(id)

	suspend fun getImage(phrase: Phrase) = provider.images.getByPhrase(phrase).first()

	suspend fun getPhrase(id: Int) = provider.phrase.getByIdFlow(id).first()

	suspend fun getPronounce(phrase: Phrase) = provider.pronounce.getByPhrase(phrase).first()

	suspend fun delete(module: Module) = provider.module.deleteAsync(module).await()

	fun addModuleCard(moduleID: Int,card: Card, call: (Boolean) -> Unit){
		viewModelScope.launch {
			val res = provider.moduleCard.insertAsync(ModuleCard(idModule = moduleID, idCard = card.id)).await()
			call(res > 0)
		}
	}

	fun removeModuleCard(moduleCard: ModuleCard, call: (Boolean) -> Unit){
		viewModelScope.launch {
			val res =provider.moduleCard.deleteAsync(moduleCard).await()
			call(res)
		}
	}
}