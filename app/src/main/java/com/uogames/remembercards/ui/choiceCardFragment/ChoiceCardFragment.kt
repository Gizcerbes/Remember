package com.uogames.remembercards.ui.choiceCardFragment

import android.content.Context
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.dto.global.GlobalCard
import com.uogames.dto.local.LocalCard
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentChoiceCardBinding
import com.uogames.remembercards.ui.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.ui.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.editCardFragment.EditCardFragment
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

    private var _bind: FragmentChoiceCardBinding? = null
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
        if (_bind == null) _bind = FragmentChoiceCardBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalViewModel.shouldReset.ifTrue { model.reset() }

        model.update()

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        receivedTAG = arguments?.getString(TAG).ifNull { return }

        bind.tilSearch.editText?.setText(model.like.value)
        bind.tilSearch.editText?.setSelection(model.like.value?.length ?: 0)
        bind.tilSearch.editText?.addTextChangedListener(textWatcher)

        bind.btnAdd.setOnClickListener { navigateSelectedToEdit() }
        bind.btnNetwork.setOnClickListener { model.cloud.setOpposite() }
        bind.btnSearch.setOnClickListener { model.search.setOpposite() }
        bind.btnBack.setOnClickListener { findNavController().popBackStack() }
        bind.btnLang.setOnClickListener {
            model.languageFirst.toNull().ifTrue { return@setOnClickListener }
            ChoiceLanguageDialog(listOf(Locale.getDefault())) {
                model.languageFirst.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
        }
        bind.btnLangSecond.setOnClickListener {
            model.languageSecond.toNull().ifTrue { return@setOnClickListener }
            ChoiceLanguageDialog(listOf(Locale.getDefault())) {
                model.languageSecond.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
        }
        bind.btnCountry.setOnClickListener {
            model.countryFirst.toNull().ifTrue { return@setOnClickListener }
            ChoiceCountryDialog {
                model.countryFirst.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
        }
        bind.btnCountrySecond.setOnClickListener {
            model.countrySecond.toNull().ifTrue { return@setOnClickListener }
            ChoiceCountryDialog {
                model.countrySecond.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
        }

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
            }
            model.size.observe(this) {
                bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
            }
            model.languageFirst.observe(this) {
                bind.tvLanguage.text = if (it != null) it.displayLanguage
                else requireContext().getText(R.string.label_all)
            }
            model.languageSecond.observe(this) {
                bind.tvLanguageSecond.text = if (it != null) it.displayLanguage
                else requireContext().getText(R.string.label_all)
            }
            model.countryFirst.observe(this) {
                if (it != null) bind.imgFlag.setImageResource(it.res)
                else bind.imgFlag.setImageResource(R.drawable.ic_baseline_add_24)
            }
            model.countrySecond.observe(this) {
                if (it != null) bind.imgFlagSecond.setImageResource(it.res)
                else bind.imgFlagSecond.setImageResource(R.drawable.ic_baseline_add_24)
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
        bind.tilSearch.editText?.removeTextChangedListener(textWatcher)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        model.recyclerStat = bind.recycler.layoutManager?.onSaveInstanceState()
        model.removeChoiceListener(choiceCard)
        model.removeReportListener(reportCall)
        bind.recycler.adapter = null
        imm = null
        _bind = null
    }
}
