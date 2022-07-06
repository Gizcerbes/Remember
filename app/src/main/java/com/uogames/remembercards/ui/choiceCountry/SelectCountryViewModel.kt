package com.uogames.remembercards.ui.choiceCountry

import androidx.lifecycle.ViewModel
import com.uogames.flags.Countries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SelectCountryViewModel @Inject constructor() : ViewModel() {

	private val _country: MutableStateFlow<Countries?>  = MutableStateFlow(null)
	val country = _country.asStateFlow()



}