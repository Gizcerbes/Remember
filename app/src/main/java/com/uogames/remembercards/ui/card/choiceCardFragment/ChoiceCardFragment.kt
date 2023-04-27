package com.uogames.remembercards.ui.card.choiceCardFragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
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
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.dto.global.GlobalCard
import com.uogames.dto.local.LocalCard
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentCardChoiceBinding
import com.uogames.remembercards.ui.dialogs.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.ui.dialogs.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.card.editCardFragment.EditCardFragment
import com.uogames.remembercards.ui.reportFragment.ReportFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class ChoiceCardFragment : DaggerFragment() {

    companion object {
        const val TAG = "ChoiceCardFragment_CHOICE_CARD_FRAGMENT"
    }

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var model: ChoiceCardViewModel

    private var _bind: FragmentCardChoiceBinding? = null
    private val bind get() = _bind!!

    private var imm: InputMethodManager? = null

    private val textWatcher = createSearchTextWatcher()

    private val reportCall = { card: GlobalCard -> navigateToReport(card) }
    private val choiceCard = { card: LocalCard -> receiveCall(card.id) }

    private var observers: Job? = null

    private var receivedTAG: String? = null

    private val searchImages = listOf(R.drawable.ic_baseline_search_off_24, R.drawable.ic_baseline_search_24)
    private val cloudImages = listOf(R.drawable.ic_baseline_cloud_off_24, R.drawable.ic_baseline_cloud_24)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_bind == null) _bind = FragmentCardChoiceBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalViewModel.shouldReset.ifTrue { model.reset() }

        model.update()

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        receivedTAG = arguments?.getString(TAG).ifNull { return }

        bind.clSearchBar.setTextSearch(model.like.value)
        bind.clSearchBar.addTextSearchWatcher(textWatcher)

        bind.btnAdd.setOnClickListener { navigateSelectedToEdit() }
        bind.btnNetwork.setOnClickListener { model.cloud.setOpposite() }
        bind.btnSearch.setOnClickListener { model.search.setOpposite() }
        bind.btnBack.setOnClickListener { findNavController().popBackStack() }
        bind.clSearchBar.setOnClickLanguageFirst{
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
        bind.clSearchBar.setOnSelectedNewestListener{ model.newest.value = it }

        lifecycleScope.launch {
            delay(250)
            bind.recycler.adapter = model.adapter
        }

        model.addChoiceListener(choiceCard)
        model.addReportListener(reportCall)

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
                when(it){
                    ChoiceCardViewModel.SearchingState.SEARCHING -> {
                        bind.lpiLoadIndicator.setIndicatorColor(MaterialColors.getColor(requireContext(), R.attr.colorPrimary, Color.BLACK))
                        bind.lpiLoadIndicator.isIndeterminate = true
                        bind.lpiLoadIndicator.visibility = View.VISIBLE
                    }
                    ChoiceCardViewModel.SearchingState.SEARCHED -> bind.lpiLoadIndicator.visibility = View.GONE
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

    private fun receiveCall(): (Int) -> Unit = { id -> receiveCall(id) }

    private fun receiveCall(id: Int) {
        receivedTAG?.let {
            setFragmentResult(it, bundleOf("ID" to id))
        }.ifNull {
            Toast.makeText(requireContext(), "Argument Problem", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }

    private fun navigateToReport(card: GlobalCard) = navigate(
        R.id.reportFragment,
        bundleOf(
            ReportFragment.TYPE to ReportFragment.types.CARD,
            ReportFragment.CLAIMANT to Firebase.auth.currentUser?.uid,
            ReportFragment.ACCUSED to card.globalOwner,
            ReportFragment.ITEM_ID to card.globalId
        )
    )

    private fun navigateSelectedToEdit() = navigateToEdit(
        bundleOf(
            EditCardFragment.CREATE_FOR to receivedTAG,
            EditCardFragment.POP_BACK_TO to findNavController().currentDestination?.id.ifNull { 0 }
        )
    )

    private fun navigateToEdit(bundle: Bundle? = null) {
        navigate(R.id.editCardFragment, bundle)
    }

    private fun createSearchTextWatcher(): TextWatcher = ShortTextWatcher {
        model.like.value = it?.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observers?.cancel()
        bind.clSearchBar.removeTextSearchWatcher(textWatcher)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        model.recyclerStat = bind.recycler.layoutManager?.onSaveInstanceState()
        model.removeChoiceListener(choiceCard)
        model.removeReportListener(reportCall)
        bind.recycler.adapter = null
        imm = null
        _bind = null
    }
}
