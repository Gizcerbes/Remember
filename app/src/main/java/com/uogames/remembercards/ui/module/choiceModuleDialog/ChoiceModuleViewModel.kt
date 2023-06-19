package com.uogames.remembercards.ui.module.choiceModuleDialog

import androidx.lifecycle.ViewModel
import com.uogames.dto.local.LocalModuleView
import com.uogames.remembercards.viewmodel.MViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChoiceModuleViewModel @Inject constructor(
    private val model: MViewModel
): ViewModel() {

    enum class ChoiceStat { ALL, ID }
    sealed interface Choice
    class ChoiceAll() : Choice
    class ChoiceLocalModule(val view: LocalModuleView) : Choice

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val like = MutableStateFlow<CharSequence?>(null)
    private val _size = MutableStateFlow(1)
    val size = _size.asStateFlow()

    val adapter = ChoiceModuleAdapter(this)
    private val choiceModule = ArrayList<(Choice) -> Unit>()

    private var searchJob: Job? = null

    fun reset() {
        choiceModule.clear()
    }

    fun updateSize() {
        _size.value = 0
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            runCatching {
                delay(100)
                val text = like.value?.toString()
                _size.value = model.getLocalSize(text = text) + if (like.value.isNullOrEmpty()) 1 else 0
            }.onFailure {
                _size.value = 0
            }
        }
    }

    fun getCountOfAllCardsAsync() = viewModelScope.async { model.globalViewModel.provider.cards.count() }

    fun getUserName() = model.globalViewModel.userName.value ?: ""

    fun addChoiceAction(l: (Choice) -> Unit) = choiceModule.add(l)

    fun removeChoiceAction(l: (Choice) -> Unit) = choiceModule.remove(l)

    fun selectModule(state: Choice) = choiceModule.forEach { it(state) }

    fun getType(position: Int): ChoiceStat {
        return if (!like.value.isNullOrEmpty()) ChoiceStat.ID
        else when (position) {
            0 -> ChoiceStat.ALL
            else -> ChoiceStat.ID
        }
    }

    suspend fun getLocalModel(position: Int) = model.getLocalModel(
        text = like.value?.toString(),
        position = position - if (like.value.isNullOrEmpty()) 1 else 0
    )

}