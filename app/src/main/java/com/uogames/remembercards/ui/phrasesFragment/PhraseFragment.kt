package com.uogames.remembercards.ui.phrasesFragment

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentBookBinding
import com.uogames.remembercards.ui.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.ui.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
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

    @Inject
    lateinit var player: ObservableMediaPlayer

    private var _bind: FragmentBookBinding? = null
    private val bind get() = _bind!!
    private var closed = false

    private var observers: Job? = null

    private var imm: InputMethodManager? = null

    private val textWatcher = createTextWatcher()

    private val searchImages =
        listOf(R.drawable.ic_baseline_search_off_24, R.drawable.ic_baseline_search_24)
    private val cloudImages =
        listOf(R.drawable.ic_baseline_cloud_off_24, R.drawable.ic_baseline_cloud_24)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentBookBinding.inflate(inflater, container, false)
        return _bind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (closed) return
        globalViewModel.shouldReset.ifTrue { model.reset() }

        model.update()

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        bind.tilSearch.editText?.setText(model.like.value)
        bind.tilSearch.editText?.setSelection(model.like.value?.length ?: 0)
        bind.tilSearch.editText?.addTextChangedListener(textWatcher)

        bind.btnAdd.setOnClickListener { navigateToAdd() }
        bind.btnNetwork.setOnClickListener { model.cloud.setOpposite() }
        bind.btnSearch.setOnClickListener { model.search.setOpposite() }
        bind.btnLang.setOnClickListener {
            model.language.toNull().ifTrue { return@setOnClickListener }
            ChoiceLanguageDialog(listOf(Locale.getDefault())) {
                model.language.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
        }
        bind.btnCountry.setOnClickListener {
            model.country.toNull().ifTrue { return@setOnClickListener }
            ChoiceCountryDialog {
                model.country.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
        }

        lifecycleScope.launch {
            delay(300)
            bind.recycler.adapter = PhraseAdapter(model, player) {
                model.recyclerStat = bind.recycler.layoutManager?.onSaveInstanceState()
                navigateToAdd(it)
            }
        }

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
            model.language.observe(this) {
                bind.tvLanguage.text = if (it != null) it.displayLanguage
                else requireContext().getText(R.string.label_all)
            }
            model.country.observe(this) {
                if (it != null) bind.imgFlag.setImageResource(it.res)
                else bind.imgFlag.setImageResource(R.drawable.ic_baseline_add_24)
            }
        }


    }

    private fun createTextWatcher(): TextWatcher = ShortTextWatcher {
        model.like.value = it?.toString() ?: ""
    }

    private fun navigateToAdd(id: Int) = navigateToAdd(bundleOf(EditPhraseFragment.ID_PHRASE to id))

    private fun navigateToAdd(bundle: Bundle? = null) {
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(
            R.id.addPhraseFragment,
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

    override fun onDestroyView() {
        super.onDestroyView()
        observers?.cancel()
        bind.tilSearch.editText?.removeTextChangedListener(textWatcher)
        (bind.recycler.adapter as? PhraseAdapter)?.close()
        bind.recycler.adapter = null
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        imm = null
        _bind = null
        closed = true
    }

}