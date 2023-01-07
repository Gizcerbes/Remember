package com.uogames.remembercards.ui.gamesFragment

import androidx.lifecycle.ViewModel
import com.uogames.dto.local.LocalModule
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class GamesViewModel constructor(val provider: DataProvider) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val selectedModule: MutableStateFlow<LocalModule?> = MutableStateFlow(null)

    val cardOwner: MutableStateFlow<String> = MutableStateFlow("")

    val countItems = selectedModule.flatMapLatest {
        it?.let {
            cardOwner.value = it.owner
            provider.moduleCard.getCountByModuleIdFlow(it.id)
        }.ifNull {
            cardOwner.value = provider.setting.getFlow(GlobalViewModel.USER_NAME).first().orEmpty()
            provider.cards.getCountFlow()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
}
