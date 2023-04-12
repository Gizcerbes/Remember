package com.uogames.remembercards.ui.module.editModuleFragment

import androidx.lifecycle.ViewModel
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleCard
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditModuleViewModel @Inject constructor(
    private val globalViewModel: GlobalViewModel,
    player: ObservableMediaPlayer
) : ViewModel() {

    private val provider = globalViewModel.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val moduleID = MutableStateFlow(0)

    val module = moduleID.flatMapLatest { provider.module.getByIdFlow(it) }

    val moduleCardsList = module.flatMapLatest { provider.moduleCard.getByModuleFlow(it.ifNull { LocalModule() }) }

   // val size = moduleID.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0).map { provider.moduleCard.getCountByModuleId(it) }

    val size = moduleID.flatMapLatest { provider.moduleCard.getCountByModuleIdFlow(it) }

    val adapter = EditModuleAdapter(this, player)

    fun reset() {
        moduleID.value = 0
    }

    fun getCard(id: Int) = provider.cards.getByIdFlow(id)

    fun getModuleCardViewAsync(position: Int) = viewModelScope.async { getModuleCardView(position) }

    suspend fun getModuleCardView(position: Int) = provider.moduleCard.getView(moduleID.value, position)

    suspend fun delete(module: LocalModule) = provider.module.delete(module)

    fun addModuleCard(moduleID: Int, card: LocalCard, call: (Boolean) -> Unit) = viewModelScope.launch {
        val res = provider.moduleCard.insert(LocalModuleCard(idModule = moduleID, idCard = card.id))
        call(res > 0)
    }

    fun removeModuleCard(moduleCard: LocalModuleCard, call: (Boolean) -> Unit) = viewModelScope.launch {
        val res = provider.moduleCard.delete(moduleCard)
        call(res)
    }
}
