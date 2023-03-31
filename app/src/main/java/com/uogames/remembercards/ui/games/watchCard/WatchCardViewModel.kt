package com.uogames.remembercards.ui.games.watchCard

import android.content.Context
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalModuleCardView
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class WatchCardViewModel @Inject constructor(
    private val gm: GlobalViewModel
) {

    private val provider = gm.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val moduleID = MutableStateFlow<Int?>(null)

    private var _position = MutableStateFlow(0)
    val position = _position.map { it + 1 }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val backSize = MutableStateFlow(false)

    private val _card = MutableStateFlow<LocalCardView?>(null)
    val card = _card.asStateFlow()

    val phrase = backSize.flatMapLatest {
        _card.map {
            if (backSize.value) it?.translate
            else it?.phrase
        }
    }

    val count = moduleID.flatMapLatest {
        it?.let {
            provider.moduleCard.getCountByModuleIdFlow(it)
        }.ifNull {
            provider.cards.getCountFlow()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    init {
        moduleID.observe(viewModelScope) {
            _card.value = moduleID.value?.let {
                provider.moduleCard.getView(it, _position.value)?.card
            }.ifNull {
                provider.cards.getView(position = _position.value)
            }
        }
    }

    fun reset() {
        moduleID.value = null
        _position.value = 0
    }

    fun next() = viewModelScope.launch {
        if (count.value > position.value) _position.value++
        _card.value = moduleID.value?.let {
            provider.moduleCard.getView(it, _position.value)?.card
        }.ifNull {
            provider.cards.getView(position = _position.value)
        }
    }

    fun previous() = viewModelScope.launch {
        if (position.value > 1) _position.value--
        _card.value = moduleID.value?.let {
            provider.moduleCard.getView(it, _position.value)?.card
        }.ifNull {
            provider.cards.getView(position = _position.value)
        }
    }

    fun getPicasso(context: Context) = gm.getPicasso(context)

}