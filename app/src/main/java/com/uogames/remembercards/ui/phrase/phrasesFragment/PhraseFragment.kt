package com.uogames.remembercards.ui.phrase.phrasesFragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.dto.global.GlobalPhrase
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentPhraseBinding
import com.uogames.remembercards.ui.dialogs.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.ui.dialogs.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.phrase.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.ui.reportFragment.ReportFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class PhraseFragment : DaggerFragment() {

    @Inject
    lateinit var model: PhraseViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    private var _bind: FragmentPhraseBinding? = null
    private val bind get() = _bind!!

    private var observers: Job? = null

    private var imm: InputMethodManager? = null

    private val textWatcher = createTextWatcher()
    private val editCall: (Int) -> Unit = { navigateToAdd(it) }
    private val reportCall = { gp: GlobalPhrase -> navigateToReport(gp) }

    private val searchImages = listOf(R.drawable.ic_baseline_search_off_24, R.drawable.ic_baseline_search_24)
    private val cloudImages = listOf(R.drawable.ic_baseline_cloud_off_24, R.drawable.ic_baseline_cloud_24)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_bind == null) _bind = FragmentPhraseBinding.inflate(inflater, container, false)
        return _bind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        model.update()

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        bind.clSearchBar.setTextSearch(model.like.value)
        bind.clSearchBar.addTextSearchWatcher(textWatcher)

        bind.btnAdd.setOnClickListener { navigateToAdd() }
        bind.btnNetwork.setOnClickListener { model.cloud.setOpposite() }
        bind.btnSearch.setOnClickListener { model.search.setOpposite() }
        bind.clSearchBar.setOnClickLanguageFirst{
            model.language.toNull().ifTrue { return@setOnClickLanguageFirst }
            ChoiceLanguageDialog(listOf(Locale.getDefault())) {
                model.language.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
        }
        bind.clSearchBar.setOnClickCountryFirst {
            model.country.toNull().ifTrue { return@setOnClickCountryFirst }
            ChoiceCountryDialog {
                model.country.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
        }

        bind.clSearchBar.selectedNewest = model.newest.value
        bind.clSearchBar.setOnSelectedNewestListener{ model.newest.value = it }

        model.addEditCall(editCall)
        model.addReportCall(reportCall)

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
            model.language.observe(this) {
                bind.clSearchBar.languageFirst = it ?: Locale.forLanguageTag(requireContext().getText(R.string.label_all).toString())
            }
            model.country.observe(this) {
                bind.clSearchBar.setFlagResourceFirst(it?.res)
            }
            model.isSearching.observe(this){
                when(it){
                    PhraseViewModel.SearchingState.SEARCHING -> {
                        bind.lpiLoadIndicator.setIndicatorColor(MaterialColors.getColor(requireContext(), R.attr.colorPrimary, Color.BLACK))
                        bind.lpiLoadIndicator.isIndeterminate = true
                        bind.lpiLoadIndicator.visibility = View.VISIBLE
                    }
                    PhraseViewModel.SearchingState.SEARCHED -> bind.lpiLoadIndicator.visibility = View.GONE
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
        model.like.value = it?.toString() ?: ""
    }

    private fun navigateToAdd(id: Int) = navigateToAdd( bundleOf(EditPhraseFragment.ID_PHRASE to id))

    private fun navigateToAdd(bundle: Bundle? = null) = navigate(R.id.addPhraseFragment, bundle)

    private fun navigateToReport(gp: GlobalPhrase) = navigate(
        R.id.reportFragment,
        bundleOf(
            ReportFragment.TYPE to ReportFragment.types.PHRASE,
            ReportFragment.CLAIMANT to Firebase.auth.currentUser?.uid,
            ReportFragment.ACCUSED to gp.globalOwner,
            ReportFragment.ITEM_ID to gp.globalId
        )
    )

    override fun onStop() {
        super.onStop()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        observers?.cancel()
        bind.clSearchBar.removeTextSearchWatcher(textWatcher)
        model.removeEditCall(editCall)
        model.removeReportCall(reportCall)
        bind.recycler.adapter = null
        imm = null
        _bind = null
        super.onDestroyView()
    }

}