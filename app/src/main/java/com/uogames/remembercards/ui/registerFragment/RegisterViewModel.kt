package com.uogames.remembercards.ui.registerFragment

import androidx.lifecycle.ViewModel
import com.uogames.flags.Countries
import com.uogames.remembercards.utils.observe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

class RegisterViewModel : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val isRegister = MutableStateFlow(false)

    val name = MutableStateFlow("")

    val country: MutableStateFlow<Countries?> = MutableStateFlow(null)

    val privacyChecked = MutableStateFlow(false)

    val maxText = MutableStateFlow(0)

    val allowed = MutableStateFlow(false)

    init {
        name.observe(viewModelScope) { allowed.value = check() }
        country.observe(viewModelScope) { allowed.value = check() }
        privacyChecked.observe(viewModelScope) { allowed.value = check() }
    }

    private fun check(): Boolean {
        if (name.value.isEmpty()) return false
        if (name.value.length > maxText.value) return false
        if (country.value == null) return false
        if (!privacyChecked.value) return false
        return true
    }

}
