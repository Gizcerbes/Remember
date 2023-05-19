package com.uogames.remembercards.viewmodel

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.remembercards.BuildConfig
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
        const val SCREEN_MODE = "SCREEN_MODE"
        const val MODULE_ID_FOR_NOTIFICATION = "MODULE_ID_FOR_NOTIFICATION"
        const val GAME_YES_OR_NO_COUNT = "GAME_YES_OR_NO_COUNT"
    }


    val auth = Firebase.auth
    private val data = {
        val uid = auth.uid
        if (uid != null) mapOf(
            "Identifier" to (userName.value.orEmpty()),
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

    private val _userName = MutableStateFlow<String?>(null)
    val userName = _userName.asStateFlow()

    private var lastDestination: NavDestination? = null

    private var _backSize: Int = 0
    val backSize get() = _backSize

    private var _shouldReset: Boolean = false
    val shouldReset get() = _shouldReset

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    val nativeCountry = provider.setting.getFlow(USER_NATIVE_COUNTRY).stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val privacyAndPolicy = provider.setting.getFlow(PRIVACY_AND_POLICY).stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val shareNotice = provider.setting.getFlow(SHARE_NOTICE).stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val screenMode = provider.setting.getFlow(SCREEN_MODE).map {
        when (it) {
            "0" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> AppCompatDelegate.MODE_NIGHT_NO
            "2" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_YES
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppCompatDelegate.MODE_NIGHT_YES)

    val countPhrases = provider.phrase.countFlow().stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val countCards = provider.cards.getCountFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val countModules = provider.module.getCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val gameYesOrNotCount = provider.setting.getFlow(GAME_YES_OR_NO_COUNT).stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val notificationModuleId = provider.setting.getFlow(MODULE_ID_FOR_NOTIFICATION).stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val cardCountFree = provider.cards.countFree().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val phraseCountFree = provider.phrase.countFree().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val shareCount = provider.share.countFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)


    private var job: Job? = null

    private val shareJob: Job = sharing()

    init {
        getUserName().observe(viewModelScope) {
            _userName.value = it
            delay(500)
            _isLoading.value = false
        }
        auth.currentUser?.reload()
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

    fun setScreenMode(mode: Int) {
        saveData(SCREEN_MODE, mode.toString())
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

    fun addGameYesOrNoGameCount() = viewModelScope.launch {
        val count = provider.setting.getFlow(GAME_YES_OR_NO_COUNT).first()?.toInt().ifNull { 0 }
        provider.setting.save(GAME_YES_OR_NO_COUNT, (count + 1).toString())
    }

    suspend fun getAcceptRules() = provider.setting.get(PRIVACY_AND_POLICY)

    fun acceptRules() = viewModelScope.launch { provider.setting.save(PRIVACY_AND_POLICY, true.toString()) }

    suspend fun saveNotificationModuleID(moduleID: Int?) {
        if (moduleID != null)
            provider.setting.save(MODULE_ID_FOR_NOTIFICATION, moduleID.toString())
        else
            provider.setting.remove(MODULE_ID_FOR_NOTIFICATION)
    }

    fun showShareNotice(b: Boolean) {
        viewModelScope.launch {
            if (b) removeData(SHARE_NOTICE)
            else saveData(SHARE_NOTICE, false.toString())
        }
    }

    fun clean() = viewModelScope.launch { provider.clean() }

    fun getPicasso(context: Context) = provider.images.getPicasso(context)

    fun deleteFreePhrases() = viewModelScope.launch {
        provider.phrase.deleteFree()
        provider.clean()
    }

    fun deleteFreeCards() = viewModelScope.launch { provider.cards.deleteFree() }

    private fun sharing() = viewModelScope.launch {
        var errors = 0
        while (true) {
            while (provider.share.count() > 0) {
                runCatching {
                    val f = provider.share.getFirst()
                    f?.idImage?.let { provider.images.shareV2(it) }
                    f?.idPronounce?.let { provider.pronounce.shareV2(it) }
                    f?.idPhrase?.let { provider.phrase.shareV2(it) }
                    f?.idCard?.let { provider.cards.shareV2(it) }
                    if (f?.idModuleCard == null) f?.idModule?.let { provider.module.share(it) }
                    else f.idModuleCard?.let { provider.moduleCard.shareV2(it) }
                    f?.let { provider.share.remove(it) }
                    errors = 0
                }.onFailure {
                    errors++
                    if (errors > 100) {
                        errors = 0
                        provider.share.clean()
                    }
                    delay(1000)
                }
            }
            delay(1000)
        }
    }

}
