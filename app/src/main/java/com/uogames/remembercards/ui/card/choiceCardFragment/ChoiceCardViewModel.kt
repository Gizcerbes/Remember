package com.uogames.remembercards.ui.card.choiceCardFragment

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uogames.dto.global.GlobalCard
import com.uogames.dto.global.GlobalCardView
import com.uogames.dto.global.GlobalPronunciationView
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalPronunciationView
import com.uogames.flags.Countries
import com.uogames.map.CardMap.toGlobalCard
import com.uogames.map.CardMap.toLocalCard
import com.uogames.remembercards.models.GlobalCardModel
import com.uogames.remembercards.models.LocalCardModel
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

class ChoiceCardViewModel(
	private val gvm: GlobalViewModel,
	private val player: ObservableMediaPlayer
) : ViewModel(),
	ChoiceCardAdapter.Model,
	ChoiceCardFragment.Model {

	private val viewModelScope = CoroutineScope(Dispatchers.IO)

	private val _size = MutableStateFlow(0)
	override val size: Flow<Int> = _size.asStateFlow()

	override val like = MutableStateFlow<String?>(null)
	override val languageFirst = MutableStateFlow<Locale?>(null)
	override val languageSecond = MutableStateFlow<Locale?>(null)
	override val countryFirst = MutableStateFlow<Countries?>(null)
	override val countrySecond = MutableStateFlow<Countries?>(null)

	override val cloud = MutableStateFlow(false)
	override val search = MutableStateFlow(false)
	override val newest = MutableStateFlow(false)

	override val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder> = ChoiceCardAdapter(this)

	private val _isSearching = MutableStateFlow(SearchingState.SEARCHED)
	override val isSearching: Flow<SearchingState> = _isSearching.asStateFlow()
	override var recyclerStat: Parcelable? = null

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


	private val reportCallList = ArrayList<(GlobalCard) -> Unit>()
	private val choiceCallList = ArrayList<(LocalCard) -> Unit>()


	init {
		like.observe(viewModelScope) { update() }
		languageFirst.observe(viewModelScope) { update() }
		languageSecond.observe(viewModelScope) { update() }
		countryFirst.observe(viewModelScope) { update() }
		countrySecond.observe(viewModelScope) { update() }
		cloud.observe(viewModelScope) {
			_size.value = 0
			update()
		}
		newest.observe(viewModelScope) {
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
		languageFirst.toNull()
		languageSecond.toNull()
		countryFirst.toNull()
		countrySecond.toNull()
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

	override fun addReportListener(box: (GlobalCard) -> Unit) {
		reportCallList.add(box)
	}

	override fun removeReportListener(box: (GlobalCard) -> Unit) {
		reportCallList.remove(box)
	}

	override fun addChoiceListener(box: (LocalCard) -> Unit) {
		choiceCallList.add(box)
	}

	override fun removeChoiceListener(box: (LocalCard) -> Unit) {
		choiceCallList.remove(box)
	}

	override suspend fun getLocal(position: Int): LocalCardModel? {
		return localPagination.get(position)?.let { LocalCardModel(it, player, localPronLoader) }
	}

	override suspend fun getGlobal(position: Int): GlobalCardModel? {
		return globalPagination.get(position)?.let { GlobalCardModel(it, player, globalPronLoader) }
	}

	override fun onAddAction(v: LocalCardView) {
		choiceCallList.forEach { it(v.toLocalCard()) }
	}

	override fun getPicasso(context: Context): Picasso {
		return gvm.getPicasso(context)
	}

	override fun onSave(v: GlobalCardView) {
		viewModelScope.launch { runCatching {
			val c = gvm.provider.cards.save(v)
			choiceCallList.forEach { it(c) }
		} }
	}

	override fun isCloud(): Boolean {
		return cloud.value
	}

	override fun onReportAction(v: GlobalCardView) {
		reportCallList.forEach { it(v.toGlobalCard()) }
	}

	private suspend fun count(): Int = gvm.provider.cards.count(
		like = like.value,
		langFirst = languageFirst.value?.isO3Language,
		langSecond = languageSecond.value?.isO3Language,
		countryFirst = countryFirst.value?.toString(),
		countrySecond = countrySecond.value?.toString(),
	)

	private suspend fun getList(position: Int, limit: Int): List<LocalCardView> = gvm.provider.cards.getListView(
		like = like.value,
		langFirst = languageFirst.value?.isO3Language,
		langSecond = languageSecond.value?.isO3Language,
		countryFirst = countryFirst.value?.toString(),
		countrySecond = countrySecond.value?.toString(),
		newest = newest.value,
		position = position,
		limit
	)

	private suspend fun globalCount(): Int = gvm.provider.cards.countGlobal(
		text = like.value,
		langFirst = languageFirst.value?.isO3Language,
		langSecond = languageSecond.value?.isO3Language,
		countryFirst = countryFirst.value?.toString(),
		countrySecond = countrySecond.value?.toString(),
	).toInt()

	private suspend fun getGlobalList(position: Int, limit: Int): List<GlobalCardView> = gvm.provider.cards.getGlobalListView(
		text = like.value,
		langFirst = languageFirst.value?.isO3Language,
		langSecond = languageSecond.value?.isO3Language,
		countryFirst = countryFirst.value?.toString(),
		countrySecond = countrySecond.value?.toString(),
		number = position.toLong(),
		limit
	)

}