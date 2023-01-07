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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentCardBinding
import com.uogames.remembercards.databinding.FragmentChoiceCardBinding
import com.uogames.remembercards.ui.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.ui.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.editCardFragment.EditCardFragment
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

    @Inject
    lateinit var player: ObservableMediaPlayer

    private var _bind: FragmentChoiceCardBinding? = null
    private val bind get() = _bind!!
    private var closed = false

    private var imm: InputMethodManager? = null

    private val textWatcher = createSearchTextWatcher()

    private var observers: Job? = null

    private var receivedTAG: String? = null

    private val searchImages =
        listOf(R.drawable.ic_baseline_search_off_24, R.drawable.ic_baseline_search_24)
    private val cloudImages =
        listOf(R.drawable.ic_baseline_cloud_off_24, R.drawable.ic_baseline_cloud_24)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_bind == null) _bind = FragmentChoiceCardBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (closed) return
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
            ChoiceCountryDialog{
                model.countrySecond.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
        }

        lifecycleScope.launch{
            delay(300)
            bind.recycler.adapter = ChoiceCardAdapter(model, player,receiveCall())
        }

        model.recyclerStat?.let { bind.recycler.layoutManager?.onRestoreInstanceState(it) }

        observers = lifecycleScope.launch{
            model.search.observe(this){
                bind.searchImage.setImageResource(searchImages[if (it) 0 else 1])
                bind.clSearchBar.visibility = if (it) View.VISIBLE else View.GONE
            }
            model.cloud.observe(this){
                bind.imgNetwork.setImageResource(cloudImages[if (it) 0 else 1])
            }
            model.size.observe(this){
                bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
            }
            model.languageFirst.observe(this) {
                bind.tvLanguage.text = if (it != null) it.displayLanguage
                else requireContext().getText(R.string.label_all)
            }
            model.languageSecond.observe(this) {
                bind.tvLanguageSecond.text = if(it != null) it.displayLanguage
                else requireContext().getText(R.string.label_all)
            }
            model.countryFirst.observe(this){
                if (it != null) bind.imgFlag.setImageResource(it.res)
                else bind.imgFlag.setImageResource(R.drawable.ic_baseline_add_24)
            }
            model.countrySecond.observe(this) {
                if (it != null) bind.imgFlagSecond.setImageResource(it.res)
                else bind.imgFlagSecond.setImageResource(R.drawable.ic_baseline_add_24)
            }
        }

    }

    private fun receiveCall(): (Int) -> Unit = { id ->
        receivedTAG?.let {
            setFragmentResult(it, bundleOf("ID" to id))
        }.ifNull {
            Toast.makeText(requireContext(), "Argument Problem", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }

    private fun createKeyObserver(): Job =
        globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
            bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
            bind.btnAdd.visibility = if (it) View.GONE else View.VISIBLE
            bind.btnBack.visibility = if (it) View.GONE else View.VISIBLE
            if (it) {
                bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
            } else {
                bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
            }
        }

    private fun navigateSelectedToEdit() = navigateToEdit(
        bundleOf(
            EditCardFragment.CREATE_FOR to receivedTAG,
            EditCardFragment.POP_BACK_TO to findNavController().currentDestination?.id.ifNull { 0 }
        )
    )

    private fun navigateToEdit(bundle: Bundle? = null) {
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(
            R.id.editCardFragment,
            bundle,
            navOptions {
                anim {
                    enter = R.anim.from_bottom
                    exit = R.anim.hide
                    popEnter = R.anim.show
                    popExit = R.anim.to_bottom
                }
            }
        )
    }

    private fun createSearchTextWatcher(): TextWatcher = ShortTextWatcher {
        model.like.value = it?.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observers?.cancel()
        bind.tilSearch.editText?.removeTextChangedListener(textWatcher)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        (bind.recycler.adapter as? ChoiceCardAdapter)?.close()
        bind.recycler.adapter = null
        imm = null
        _bind = null
        closed = true
    }
}
