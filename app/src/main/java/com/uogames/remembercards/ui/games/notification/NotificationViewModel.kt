package com.uogames.remembercards.ui.games.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalImageView
import com.uogames.remembercards.viewmodel.GlobalViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationViewModel @Inject constructor(
    private val globalViewModel: GlobalViewModel
) : ViewModel() {

    private val provider = globalViewModel.provider

    val moduleID = MutableStateFlow<Int?>(null)

    val notificationModuleID get() = globalViewModel.notificationModuleId

    suspend fun getRandomCard(): LocalCardView? {
        val moduleId = globalViewModel.provider.setting.get(GlobalViewModel.MODULE_ID_FOR_NOTIFICATION)?.toInt()
        return if (moduleId != null) provider.moduleCard.getRandomModuleView(moduleId)?.card
        else globalViewModel.provider.cards.getRandomView()
    }

    suspend fun getCardById(cardID: Int) = provider.cards.getViewByID(cardID)

    suspend fun getData(localImageView: LocalImageView) = globalViewModel.provider.images.load(localImageView)

    fun saveSelectedModule() = viewModelScope.launch { globalViewModel.saveNotificationModuleID(moduleID.value) }

    fun getModuleByIdAsync(id:Int) = viewModelScope.async { provider.module.getById(id) }

}