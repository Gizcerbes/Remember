package com.uogames.remembercards

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observe
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GlobalViewModel @Inject constructor(
    context: Context
) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val USER_NAME = "USER_NAME"
        const val USER_NATIVE_COUNTRY = "USER_NATIVE_COUNTRY"
        const val PRIVACY_AND_POLICY = "PRIVACY_AND_POLICY_v1"
        const val SHARE_NOTICE = "SHARE_NOTICE"

        const val GAME_YES_OR_NO_COUNT = "GAME_YES_OR_NO_COUNT"
    }


    val auth = Firebase.auth
    private val data = {
        val uid = auth.uid
        if (uid != null) mapOf(
            "Identifier" to (userName.value),
            "User UID" to (uid),
            "UTM" to System.currentTimeMillis().toString()
        )
        else mapOf()
    }

    val provider = DataProvider.get(
        context,
        {
            if (BuildConfig.DEBUG) "secret"
            else "It doesn't matter"
        },
        data
    )

    private val _isShowKey = MutableStateFlow(false)
    val isShowKey = _isShowKey.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private var lastDestination: NavDestination? = null

    private var _backSize: Int = 0
    val backSize get() = _backSize

    private var _shouldReset: Boolean = false
    val shouldReset get() = _shouldReset

    val nativeCountry = provider.setting.getFlow(USER_NATIVE_COUNTRY).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    val privacyAndPolicy = provider.setting.getFlow(PRIVACY_AND_POLICY).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    val shareNotice = provider.setting.getFlow(SHARE_NOTICE).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val countPhrases = provider.phrase.countFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val gameYesOrNotCount = provider.setting.getFlow(GAME_YES_OR_NO_COUNT).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)



    private var job: Job? = null

    init {
        getUserName().observe(viewModelScope) { _userName.value = it.orEmpty() }
    }

    fun setShowKeyboard(view: View) {

        job?.cancel()
        job = viewModelScope.launch {
            delay(10)
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            _isShowKey.value = r.bottom / view.rootView.height.toDouble() < 0.8
        }
    }

    fun setBackQueue(arrBackStack: ArrayDeque<NavBackStackEntry>?) {
        arrBackStack?.let {
            val lastDest = it.last().destination
            _shouldReset = it.size > _backSize || (it.size == _backSize && lastDestination != lastDest)
            _backSize = it.size
            lastDestination = lastDest
        }.ifNull {
            _shouldReset = false
        }
    }

    private fun saveData(key: String, value: String?, finishCall: () -> Unit = {}) {
        viewModelScope.launch {
            provider.setting.save(key, value)
            finishCall()
        }
    }

    private fun removeData(key: String) {
        viewModelScope.launch {
            provider.setting.remove(key)
        }
    }

    fun saveUserName(name: String) = viewModelScope.launch { provider.setting.save(USER_NAME, name) }

    fun getUserName() = provider.setting.getFlow(USER_NAME)
    fun saveUserNativeCountry(name: String) = viewModelScope.launch { provider.setting.save(USER_NATIVE_COUNTRY, name) }

    fun getUserNativeCountry() = provider.setting.getFlow(USER_NATIVE_COUNTRY)

    fun getCountPhrases() = provider.phrase.countFlow()

    fun getCountCards() = provider.cards.getCountFlow()

    fun getCountModules() = provider.module.getCount()

    fun addGameYesOrNoGameCount() = viewModelScope.launch {
        val count = provider.setting.getFlow(GAME_YES_OR_NO_COUNT).first()?.toInt().ifNull { 0 }
        provider.setting.save(GAME_YES_OR_NO_COUNT, (count + 1).toString())
    }
     fun getGameYesOrNotGameCount() = provider.setting.getFlow(GAME_YES_OR_NO_COUNT)
    suspend fun getAcceptRules() = provider.setting.get(PRIVACY_AND_POLICY)
    fun acceptRules() = viewModelScope.launch { provider.setting.save(PRIVACY_AND_POLICY, true.toString()) }

    fun showShareNotice(b: Boolean) {
        viewModelScope.launch {
            if (b) removeData(SHARE_NOTICE)
            else saveData(SHARE_NOTICE, false.toString())
        }
    }
    fun clean() = viewModelScope.launch { provider.clean() }

    fun getPicasso(context: Context) = provider.images.getPicasso(context)
}
