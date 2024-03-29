package com.uogames.remembercards.ui.phrase.choicePhraseFragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.local.LocalPhrase
import com.uogames.flags.Countries
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentPhraseChoiceBinding
import com.uogames.remembercards.models.SearchingState
import com.uogames.remembercards.ui.dialogs.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.ui.dialogs.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.phrase.editPhraseFragment.EditPhraseFragment
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

class ChoicePhraseFragment() : DaggerFragment() {

    interface Model {

        val like: MutableStateFlow<String?>

        val cloud: MutableStateFlow<Boolean>

        val search: MutableStateFlow<Boolean>

        val language: MutableStateFlow<Locale?>

        val country: MutableStateFlow<Countries?>

        val newest: MutableStateFlow<Boolean>

        val size: Flow<Int>

        val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>

        var recyclerStat: Parcelable?

        val isSearching: Flow<SearchingState>

        fun reset()

        fun update()

        fun addChoiceCall(box: (LocalPhrase) -> Unit)

        fun removeChoiceCall(box: (LocalPhrase) -> Unit)

        fun addReportCall(box: (GlobalPhrase) -> Unit)

        fun removeReportCall(box: (GlobalPhrase) -> Unit)

    }

    companion object {
        const val TAG = "CHOICE_PHRASE_DIALOG"
    }

    @Inject
    lateinit var vm: Model

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var player: ObservableMediaPlayer

    private var _bind: FragmentPhraseChoiceBinding? = null
    private val bind get() = _bind!!

    private var observers: Job? = null

    private var imm: InputMethodManager? = null

    private val textWatcher = createTextWatcher()

    private val searchImages = listOf(R.drawable.ic_baseline_search_off_24, R.drawable.ic_baseline_search_24)
    private val cloudImages = listOf(R.drawable.ic_baseline_cloud_off_24, R.drawable.ic_baseline_cloud_24)

    private val choiceCall = { lp: LocalPhrase -> selectPhrase(lp) }
    private val reportCall = { gp: GlobalPhrase -> navigateToReport(gp) }

    private var receivedTAG: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_bind == null) _bind = FragmentPhraseChoiceBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalViewModel.shouldReset.ifTrue { vm.reset() }

        vm.update()

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        receivedTAG = arguments?.getString(TAG).ifNull { return }

        bind.clSearchBar.setTextSearch(vm.like.value)
        bind.clSearchBar.addTextSearchWatcher(textWatcher)

        bind.btnAdd.setOnClickListener { openEditFragment() }
        bind.btnNetwork.setOnClickListener { vm.cloud.value = !vm.cloud.value }
        bind.btnSearch.setOnClickListener { vm.search.value = !vm.search.value }
        bind.btnBack.setOnClickListener { findNavController().popBackStack() }
        bind.clSearchBar.setOnClickLanguageFirst{
            vm.language.toNull().ifTrue { return@setOnClickLanguageFirst }
            ChoiceLanguageDialog(listOf(Locale.getDefault())) {
                vm.language.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
        }
        bind.clSearchBar.setOnClickCountryFirst {
            vm.country.toNull().ifTrue { return@setOnClickCountryFirst }
            ChoiceCountryDialog {
                vm.country.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
        }
        bind.clSearchBar.selectedNewest = vm.newest.value
        bind.clSearchBar.setOnSelectedNewestListener{ vm.newest.value = it }

        vm.addChoiceCall(choiceCall)
        vm.addReportCall(reportCall)

        lifecycleScope.launch {
            delay(250)
            bind.recycler.adapter = vm.adapter
        }

        vm.recyclerStat?.let { bind.recycler.layoutManager?.onRestoreInstanceState(it) }

        observers = lifecycleScope.launch {
            vm.search.observe(this) {
                bind.searchImage.setImageResource(searchImages[if (it) 0 else 1])
                bind.clSearchBar.visibility = if (it) View.VISIBLE else View.GONE
            }
            vm.cloud.observe(this) {
                bind.imgNetwork.setImageResource(cloudImages[if (it) 0 else 1])
                bind.clSearchBar.showNewest = !it
            }
            vm.size.observe(this) {
                bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
            }
            vm.language.observe(this) {
                bind.clSearchBar.languageFirst = it ?: Locale.forLanguageTag(requireContext().getText(R.string.label_all).toString())
            }
            vm.country.observe(this) {
                bind.clSearchBar.setFlagResourceFirst(it?.res)
            }
            vm.isSearching.observe(this){
                when(it){
                    SearchingState.SEARCHING -> {
                        bind.lpiLoadIndicator.setIndicatorColor(MaterialColors.getColor(requireContext(), R.attr.colorPrimary, Color.BLACK))
                        bind.lpiLoadIndicator.isIndeterminate = true
                        bind.lpiLoadIndicator.visibility = View.VISIBLE
                    }
                    SearchingState.SEARCHED -> bind.lpiLoadIndicator.visibility = View.INVISIBLE
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

    private fun createTextWatcher(): TextWatcher = ShortTextWatcher {
        vm.like.value = it?.toString() ?: ""
    }

    private fun selectPhrase(lp: LocalPhrase) {
        receivedTAG?.let {
            setFragmentResult(it, bundleOf("ID" to lp.id))
        }.ifNull {
            Toast.makeText(requireContext(), "Argument Problem", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }


    private fun openEditFragment() = navigate(
        R.id.addPhraseFragment,
        bundleOf(
            EditPhraseFragment.CREATE_FOR to receivedTAG,
            EditPhraseFragment.POP_BACK_TO to findNavController().currentDestination?.id.ifNull { 0 }
        )
    )

    private fun navigateToReport(gp: GlobalPhrase) = navigate(
        R.id.reportFragment,
        bundleOf(
            ReportFragment.TYPE to ReportFragment.types.PHRASE,
            ReportFragment.CLAIMANT to Firebase.auth.currentUser?.uid,
            ReportFragment.ACCUSED to gp.globalOwner,
            ReportFragment.ITEM_ID to gp.globalId
        )
    )

    override fun onDestroyView() {
        bind.clSearchBar.removeTextSearchWatcher(textWatcher)
        vm.removeReportCall(reportCall)
        vm.removeChoiceCall(choiceCall)
        bind.recycler.adapter = null
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        imm = null
        _bind = null
        super.onDestroyView()
    }
}
