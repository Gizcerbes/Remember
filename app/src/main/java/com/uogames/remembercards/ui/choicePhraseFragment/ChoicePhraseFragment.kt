package com.uogames.remembercards.ui.choicePhraseFragment

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
import com.uogames.remembercards.databinding.FragmentChoicePhraseBinding
import com.uogames.remembercards.ui.bookFragment.BookViewModel
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

class ChoicePhraseFragment() : DaggerFragment() {

    companion object {
        const val TAG = "CHOICE_PHRASE_DIALOG"
    }

    @Inject
    lateinit var bookViewModel: BookViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var player: ObservableMediaPlayer

    private var _bind: FragmentChoicePhraseBinding? = null
    private val bind get() = _bind!!

    private var imm: InputMethodManager? = null

    private var receivedTAG: String? = null

    private var adapter: ChoicePhraseAdapter? = null
    private val searchWatcher = createSearchWatcher()
    private var sizeObserver: Job? = null
    private var keyObserver: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_bind == null) _bind = FragmentChoicePhraseBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        globalViewModel.shouldReset.ifTrue {
            bookViewModel.reset()
        }

        bookViewModel.reset()

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        adapter = createAdapter()

        receivedTAG = arguments?.getString(TAG)

        receivedTAG?.let {
            sizeObserver = createSizeObserver()
            lifecycleScope.launchWhenStarted {
                delay(300)
                bind.recycler.adapter = adapter
            }
            bind.tilSearch.editText?.addTextChangedListener(searchWatcher)
        }

        keyObserver = createKeyObserver()

        bind.btnSearch.setOnClickListener {
            if (!globalViewModel.isShowKey.value) {
                bind.tilSearch.requestFocus()
                imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
            } else {
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        bind.btnAdd.setOnClickListener { openEditFragment() }

        bind.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun createAdapter(): ChoicePhraseAdapter = ChoicePhraseAdapter(bookViewModel, player, {
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(
            R.id.addPhraseFragment,
            Bundle().apply {
                putInt(EditPhraseFragment.ID_PHRASE, it.id)
            },
            navOptions {
                anim {
                    enter = R.anim.from_bottom
                    exit = R.anim.hide
                    popEnter = R.anim.show
                    popExit = R.anim.to_bottom
                }
            }
        )
    }) { phrase ->
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        receivedTAG?.let {
            setFragmentResult(it, bundleOf("ID" to phrase.id))
        }.ifNull {
            Toast.makeText(requireContext(), "Argument Problem", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }

    private fun createSearchWatcher(): TextWatcher = ShortTextWatcher {
        bookViewModel.like.value = it.toString()
    }

    private fun createSizeObserver(): Job = bookViewModel.size.observeWhenStarted(lifecycleScope) {
        bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
    }

    private fun createKeyObserver(): Job = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
        bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
        bind.btnAdd.visibility = if (it) View.GONE else View.VISIBLE
        if (it) {
            bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
        } else {
            bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
        }
    }

    private fun openEditFragment() {
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(
            R.id.addPhraseFragment,
            Bundle().apply {
                putString(EditPhraseFragment.CREATE_FOR, receivedTAG)
                putInt(EditPhraseFragment.POP_BACK_TO, findNavController().currentDestination?.id.ifNull { 0 })
            },
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
        sizeObserver?.cancel()
        keyObserver?.cancel()
        bind.tilSearch.editText?.removeTextChangedListener(searchWatcher)
        adapter?.onDestroy()
        adapter = null
        imm = null
        _bind = null
    }
}
