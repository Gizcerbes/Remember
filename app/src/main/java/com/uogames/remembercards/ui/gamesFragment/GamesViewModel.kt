package com.uogames.remembercards.ui.gamesFragment

import androidx.lifecycle.ViewModel
import com.uogames.dto.Module
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat

class GamesViewModel constructor(val provider: DataProvider) : ViewModel() {

	val selectedModule: MutableStateFlow<Module?> = MutableStateFlow(null)

	val cardOwner: MutableStateFlow<String> = MutableStateFlow("")

	val countItems = selectedModule.flatMapConcat {
		it?.let {
			cardOwner.value = it.owner
			provider.moduleCard.getCountByModuleID(it.id)
		}.ifNull {
			cardOwner.value = provider.setting.getFlow(GlobalViewModel.USER_NAME).first().orEmpty()
			provider.cards.getCountFlow()
		}
	}



}