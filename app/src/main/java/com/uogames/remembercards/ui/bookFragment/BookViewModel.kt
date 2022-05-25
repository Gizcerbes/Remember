package com.uogames.remembercards.ui.bookFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.uogames.dto.Card
import com.uogames.remembercards.utils.DataBuffer
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class BookViewModel @Inject constructor(
    private val provider: DataProvider
) : ViewModel() {
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val buffer = DataBuffer(15, Card()) { getCard(it) }

    private val _like = MutableStateFlow("")
    val like = _like.asStateFlow()

    private val _size = MutableStateFlow(0)
    val size = _size.asStateFlow()

    private val _updateBlocking = MutableStateFlow(false)
    val updateBlocking = _updateBlocking.asStateFlow()

    private val _updateCall = MutableStateFlow(-1)
    val updateCall = _updateCall.asStateFlow()

    private var scrollJob: Job? = null

    private var job: Job? = null

    private suspend fun getCard(position: Int): Card {
        return provider.cards.getCardAsync(position.toLong(), _like.value).await() ?: Card()
    }

    fun size(call: (Int) -> Unit = {}) = provider.cards.cardCount(_like.value) {
        _size.value = it
        mainScope.launch { call(it) }
    }

    fun get(position: Int) = buffer.getDataFlow(position)


    fun refresh(position: Int) = buffer.update(position)

    fun refreshAll() = buffer.updateAll()

    fun delete(card: Card, result: (Boolean) -> Unit = {}) = ioScope.launch {
        val res = provider.cards.deleteCardAsync(card).await()
        if (res && _size.value > 0) _size.value--
        launch(Dispatchers.Main) { result(res) }
        _updateCall.value = (Math.random() * Int.MAX_VALUE).toInt()
    }

    fun updateCard(card: Card) = provider.cards.updateCard(card)

    fun add(card: Card, result: (Boolean) -> Unit = {}) = ioScope.launch {
        val res = provider.cards.addCardAsync(card).await()
        if (res) _size.value++
        launch(Dispatchers.Main) { result(res) }
    }

    fun setSpeedScrolling(dy: Int) {
        val res = if (_updateBlocking.value) dy > 50 || dy < -50
        else dy > 150 || dy < -150
        _updateBlocking.value = res
        scrollJob?.cancel()
        if (res) scrollJob = ioScope.launch {
            delay(50)
            _updateBlocking.value = false
        }
    }

    fun setLike(string: String, call: () -> Unit = {}) {
        job?.cancel()
        job = ioScope.launch {
            delay(500)
            _like.value = string
            size()
            launch(Dispatchers.Main) { call() }
        }
    }
}