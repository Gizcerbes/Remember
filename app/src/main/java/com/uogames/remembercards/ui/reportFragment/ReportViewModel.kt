package com.uogames.remembercards.ui.reportFragment

import androidx.lifecycle.ViewModel
import com.uogames.dto.global.GlobalReport
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifNullOrEmpty
import com.uogames.remembercards.utils.toNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class ReportViewModel @Inject constructor(
    private val provider: DataProvider
) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val type = MutableStateFlow<Int?>(null)

    val itemId = MutableStateFlow<UUID?>(null)

    val message = MutableStateFlow<String?>(null)

    val claimant = MutableStateFlow<String?>(null)

    val accused = MutableStateFlow<String?>(null)

    fun reset(){
        type.toNull()
        itemId.toNull()
        message.toNull()
        claimant.toNull()
        claimant.toNull()
        accused.toNull()
    }

    fun send(call: (Boolean) -> Unit) = viewModelScope.launch {
        val type = type.value.ifNull { return@launch }
        val itemID = itemId.value.ifNull { return@launch }
        val message = message.value.ifNullOrEmpty { return@launch }
        val claimant = claimant.value.ifNullOrEmpty { return@launch }
        val accused = accused.value.ifNullOrEmpty { return@launch }

        val report = GlobalReport(
            claimant = claimant,
            message = message,
            accused = accused
        )
        when (type) {
            0 -> report.idPhrase = itemID
            1 -> report.idCard = itemID
            2 -> report.idModule = itemID
            else -> return@launch
        }

        runCatching {
            val res = provider.report.send(report)
            viewModelScope.launch(Dispatchers.Main) { call(res) }
        }.onFailure {
            viewModelScope.launch(Dispatchers.Main) { call(false) }
        }

    }


}