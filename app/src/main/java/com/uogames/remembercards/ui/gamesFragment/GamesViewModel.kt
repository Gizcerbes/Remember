package com.uogames.remembercards.ui.gamesFragment

import androidx.lifecycle.ViewModel
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleView
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.utils.UserGlobalName
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class GamesViewModel constructor(val provider: DataProvider) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val selectedModule: MutableStateFlow<LocalModuleView?> = MutableStateFlow(null)

    val cardOwner: MutableStateFlow<String> = MutableStateFlow("")

    val countItems = selectedModule.flatMapLatest {
        it?.let {
            cardOwner.value = it.globalOwner?.let { owner ->
                provider.user.getByUid(owner)?.let { user -> UserGlobalName(user.name, user.globalOwner).userName }
            }.ifNull { provider.setting.get(GlobalViewModel.USER_NAME)?.let { user -> UserGlobalName(user).userName }.orEmpty() }
            provider.moduleCard.getCountByModuleIdFlow(it.id)
        }.ifNull {
            cardOwner.value = provider.setting.get(GlobalViewModel.USER_NAME).orEmpty()
            provider.cards.getCountFlow()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
}
