package com.uogames.remembercards.ui.card.cardFragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.dto.global.GlobalCard
import com.uogames.dto.local.LocalCard
import com.uogames.flags.Countries
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentCardBinding
import com.uogames.remembercards.models.SearchingState
import com.uogames.remembercards.ui.dialogs.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.ui.dialogs.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.card.editCardFragment.EditCardFragment
import com.uogames.remembercards.ui.reportFragment.ReportFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class CardFragment : DaggerFragment() {

	interface Model {

		val like: MutableStateFlow<String?>
		val languageFirst: MutableStateFlow<Locale?>
		val languageSecond: MutableStateFlow<Locale?>
		val countryFirst: MutableStateFlow<Countries?>
		val countrySecond: MutableStateFlow<Countries?>

		val cloud: MutableStateFlow<Boolean>
		val search: MutableStateFlow<Boolean>
		val newest: MutableStateFlow<Boolean>

		val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>
		val size: Flow<Int>
		val isSearching: Flow<SearchingState>


		var recyclerStat: Parcelable?

		fun reset()

		fun update()

		fun addReportListener(box: (GlobalCard) -> Unit)

		fun removeReportListener(box: (GlobalCard) -> Unit)

		fun addEditListener(box: (LocalCard) -> Unit)

		fun removeEditListener(box: (LocalCard) -> Unit)

	}

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var model: Model

	@Inject
	lateinit var player: ObservableMediaPlayer

	private var _bind: FragmentCardBinding? = null
	private val bind get() = _bind!!

	private var observers: Job? = null

	private val textWatcher = createSearchTextWatcher()

	private val reportCall = { card: GlobalCard -> navigateToReport(card) }
	private val editCall = { card: LocalCard -> navigateToEdit(card) }

	private var imm: InputMethodManager? = null

	private val searchImages = listOf(R.drawable.ic_baseline_search_off_24, R.drawable.ic_baseline_search_24)
	private val cloudImages = listOf(R.drawable.ic_baseline_cloud_off_24, R.drawable.ic_baseline_cloud_24)

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (_bind == null) _bind = FragmentCardBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		globalViewModel.shouldReset.ifTrue { model.reset() }
		model.update()

		imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

		bind.clSearchBar.setTextSearch(model.like.value)
		bind.clSearchBar.addTextSearchWatcher(textWatcher)

		bind.btnAdd.setOnClickListener { navigateToEdit() }
		bind.btnNetwork.setOnClickListener { model.cloud.setOpposite() }
		bind.btnSearch.setOnClickListener { model.search.setOpposite() }

		bind.clSearchBar.setOnClickLanguageFirst {
			model.languageFirst.toNull().ifTrue { return@setOnClickLanguageFirst }
			ChoiceLanguageDialog(listOf(Locale.getDefault())) {
				model.languageFirst.value = it
			}.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
		}
		bind.clSearchBar.setOnClickLanguageSecond {
			model.languageSecond.toNull().ifTrue { return@setOnClickLanguageSecond }
			ChoiceLanguageDialog(listOf(Locale.getDefault())) {
				model.languageSecond.value = it
			}.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
		}
		bind.clSearchBar.setOnClickCountryFirst {
			model.countryFirst.toNull().ifTrue { return@setOnClickCountryFirst }
			ChoiceCountryDialog {
				model.countryFirst.value = it
			}.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
		}
		bind.clSearchBar.setOnClickCountrySecond {
			model.countrySecond.toNull().ifTrue { return@setOnClickCountrySecond }
			ChoiceCountryDialog {
				model.countrySecond.value = it
			}.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
		}
		bind.clSearchBar.selectedNewest = model.newest.value
		bind.clSearchBar.setOnSelectedNewestListener { model.newest.value = it }

		model.addReportListener(reportCall)
		model.addEditListener(editCall)

		lifecycleScope.launchWhenStarted {
			delay(250)
			bind.recycler.adapter = model.adapter
		}

		model.recyclerStat?.let { bind.recycler.layoutManager?.onRestoreInstanceState(it) }

		observers = lifecycleScope.launch {
			model.search.observe(this) {
				bind.searchImage.setImageResource(searchImages[if (it) 0 else 1])
				bind.clSearchBar.visibility = if (it) View.VISIBLE else View.GONE
			}
			model.cloud.observe(this) {
				bind.imgNetwork.setImageResource(cloudImages[if (it) 0 else 1])
				bind.clSearchBar.showNewest = !it
			}
			model.size.observe(this) {
				bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
			}
			model.languageFirst.observe(this) {
				bind.clSearchBar.languageFirst = it.ifNull {
					Locale.forLanguageTag(requireContext().getText(R.string.label_all).toString())
				}
			}
			model.languageSecond.observe(this) {
				bind.clSearchBar.languageSecond = it.ifNull {
					Locale.forLanguageTag(requireContext().getText(R.string.label_all).toString())
				}
			}
			model.countryFirst.observe(this) {
				bind.clSearchBar.setFlagResourceFirst(it?.res)
			}
			model.countrySecond.observe(this) {
				bind.clSearchBar.setFlagResourceSecond(it?.res)
			}
			model.isSearching.observe(this) {
				when (it) {
					SearchingState.SEARCHING -> {
						bind.lpiLoadIndicator.setIndicatorColor(MaterialColors.getColor(requireContext(), R.attr.colorPrimary, Color.BLACK))
						bind.lpiLoadIndicator.isIndeterminate = true
						bind.lpiLoadIndicator.visibility = View.VISIBLE
					}

					SearchingState.SEARCHED -> bind.lpiLoadIndicator.visibility = View.GONE
					else -> {
						bind.lpiLoadIndicator.setIndicatorColor(requireContext().getColor(R.color.red))
						bind.lpiLoadIndicator.isIndeterminate = false
						bind.lpiLoadIndicator.progress = 100
						bind.lpiLoadIndicator.visibility = View.VISIBLE
					}
				}
			}
		}

	}


	private fun navigateToEdit(card: LocalCard) = navigateToEdit(bundleOf(EditCardFragment.EDIT_ID to card.id))
	private fun navigateToEdit(bundle: Bundle? = null) = navigate(R.id.editCardFragment, bundle)

	private fun navigateToReport(card: GlobalCard) = navigate(
		R.id.reportFragment,
		bundleOf(
			ReportFragment.TYPE to ReportFragment.types.CARD,
			ReportFragment.CLAIMANT to Firebase.auth.currentUser?.uid,
			ReportFragment.ACCUSED to card.globalOwner,
			ReportFragment.ITEM_ID to card.globalId
		)
	)

	private fun createSearchTextWatcher(): TextWatcher = ShortTextWatcher {
		model.like.value = it?.toString() ?: ""
	}

	override fun onDestroyView() {
		super.onDestroyView()
		observers?.cancel()
		//bind.tilSearch.editText?.removeTextChangedListener(textWatcher)
		bind.clSearchBar.removeTextSearchWatcher(textWatcher)
		imm?.hideSoftInputFromWindow(view?.windowToken, 0)
		model.recyclerStat = bind.recycler.layoutManager?.onSaveInstanceState()
		model.removeEditListener(editCall)
		model.removeReportListener(reportCall)
		bind.recycler.adapter = null
		imm = null
		_bind = null
	}
}
