package com.uogames.remembercards.ui.registerFragment

import androidx.lifecycle.ViewModel
import com.uogames.flags.Countries
import kotlinx.coroutines.flow.MutableStateFlow

class RegisterViewModel : ViewModel() {

    val isRegister = MutableStateFlow(false)

    val country: MutableStateFlow<Countries?> = MutableStateFlow(null)
}
