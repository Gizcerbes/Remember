package com.uogames.remembercards.ui.phrase.choicePhraseFragment

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.global.GlobalPronunciationView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import com.uogames.dto.local.LocalPronunciationView
import com.uogames.flags.Countries
import com.uogames.map.PhraseMap.toGlobalPhrase
import com.uogames.map.PhraseMap.toLocalPhrase
import com.uogames.remembercards.models.GlobalPhraseModel
import com.uogames.remembercards.models.LocalPhraseModel
import com.uogames.remembercards.models.SearchingState
import com.uogames.remembercards.pagination.Pagination
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.observe
import com.uogames.remembercards.utils.toNull
import com.uogames.remembercards.viewmodel.GlobalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class ChoicePhraseViewModel @Inject constructor(
	private val gvm: GlobalViewModel,
	private val player: ObservableMediaPlayer
) : ViewModel(),
	ChoicePhraseAdapter.Model,
	ChoicePhraseFragment.Model {

	private val viewModelScope = CoroutineScope(Dispatchers.IO)
	override val like: MutableStateFlow<String?> = MutableStateFlow(null)
	override val cloud: MutableStateFlow<Boolean> = MutableStateFlow(false)
	override val search: MutableStateFlow<Boolean> = MutableStateFlow(false)
	override val language: MutableStateFlow<Locale?> = MutableStateFlow(null)
	override val country: MutableStateFlow<Countries?> = MutableStateFlow(null)
	override val newest: MutableStateFlow<Boolean> = MutableStateFlow(false)
	private val _size = MutableStateFlow(0)
	override val size: Flow<Int> = _size.asStateFlow()
	override var recyclerStat: Parcelable? = null
	private val _isSearching = MutableStateFlow(SearchingState.SEARCHED)
	override val isSearching: Flow<SearchingState> = _isSearching.asStateFlow()
	override val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder> = ChoicePhraseAdapter(this)

	private val reportCallList = ArrayList<(GlobalPhrase) -> Unit>()
	private val choiceCallList = ArrayList<(LocalPhrase) -> Unit>()

	private val localPagination = Pagination(
		viewModelScope,
		100,
		{ count() },
		{ count, offset -> getList(offset, count) }
	)

	private val globalPagination = Pagination(
		viewModelScope,
		100,
		{ globalCount() },
		{ count, offset -> getGlobalList(offset, count) }
	)

	private val localPronLoader = { pv: LocalPronunciationView -> viewModelScope.async { gvm.provider.pronounce.load(pv) ?: ByteArray(0) } }
	private val globalPronLoader = { pv: GlobalPronunciationView -> viewModelScope.async { gvm.provider.pronounce.downloadData(pv.globalId) } }

	init {
		like.observe(viewModelScope) { update() }
		country.observe(viewModelScope) { update() }
		language.observe(viewModelScope) { update() }
		newest.observe(viewModelScope) {
			_size.value = 0
			update()
		}
		cloud.observe(viewModelScope) {
			_size.value = 0
			update()
		}
		localPagination.loadState.observe(viewModelScope){
			if (!cloud.value) _isSearching.value = it
		}
		localPagination.countFlow().observe(viewModelScope) { if (!cloud.value) _size.value = it }
		globalPagination.loadState.observe(viewModelScope){
			if (cloud.value) _isSearching.value = it
		}
		globalPagination.countFlow().observe(viewModelScope) { if (cloud.value) _size.value = it }

	}

	override fun reset() {
		like.toNull()
		country.toNull()
		language.toNull()
		cloud.value = false
		search.value = false
		newest.value = false
		reportCallList.clear()
		choiceCallList.clear()
	}

	override fun update() {
		viewModelScope.launch {
			if (!cloud.value) localPagination.reload()
			else globalPagination.reload()
		}
	}

	override fun addChoiceCall(box: (LocalPhrase) -> Unit) {
		choiceCallList.add(box)
	}

	override fun removeChoiceCall(box: (LocalPhrase) -> Unit) {
		choiceCallList.remove(box)
	}

	override fun addReportCall(box: (GlobalPhrase) -> Unit) {
		reportCallList.add(box)
	}

	override fun removeReportCall(box: (GlobalPhrase) -> Unit) {
		reportCallList.remove(box)
	}

	override suspend fun getLocal(position: Int): LocalPhraseModel? {
		return localPagination.get(position)?.let { LocalPhraseModel(it, player, localPronLoader) }
	}

	override suspend fun getGlobal(position: Int): GlobalPhraseModel? {
		return globalPagination.get(position)?.let { GlobalPhraseModel(it, player, globalPronLoader) }
	}

	override fun getPicasso(context: Context): Picasso {
		return gvm.getPicasso(context)
	}

	override fun onSave(v: GlobalPhraseView) {
		viewModelScope.launch {
			runCatching {
				val r = gvm.provider.phrase.save(v)
				viewModelScope.launch(Dispatchers.Main) { choiceCallList.forEach { it(r) } }
			}
		}
	}

	override fun isCloud(): Boolean {
		return cloud.value
	}

	override fun onAddAction(v: LocalPhraseView) {
		choiceCallList.forEach { it(v.toLocalPhrase()) }
	}

	override fun onReportAction(v: GlobalPhraseView) {
		reportCallList.forEach { it(v.toGlobalPhrase()) }
	}

	private suspend fun count(): Int = gvm.provider.phrase.count(
		like = like.value,
		lang = language.value?.isO3Language,
		country = country.value?.toString()
	)

	private suspend fun getList(position: Int, limit: Int): List<LocalPhraseView> = gvm.provider.phrase.getListView(
		like = like.value,
		newest = newest.value,
		offset = position,
		limit = limit,
		lang = language.value?.isO3Language,
		country = country.value?.toString()
	)

	private suspend fun globalCount(): Int = gvm.provider.phrase.countGlobal(
		text = like.value,
		lang = language.value?.isO3Language,
		country = country.value?.toString()
	).toInt()

	private suspend fun getGlobalList(position: Int, limit: Int): List<GlobalPhraseView> = gvm.provider.phrase.getGlobalListView(
		text = like.value,
		number = position.toLong(),
		limit = limit,
		lang = language.value?.isO3Language,
		country = country.value?.toString()
	)

}