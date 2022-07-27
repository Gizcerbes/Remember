package com.uogames.remembercards.ui.bookFragment

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
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ShortTextWatcher
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import javax.inject.Inject

class BookFragment : DaggerFragment() {

    @Inject
    lateinit var bookViewModel: BookViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var editPhraseViewModel: EditPhraseViewModel

    @Inject
    lateinit var player: ObservableMediaPlayer

    private var _bind: FragmentBookBinding? = null
    private val bind get() = _bind!!

    private var imm: InputMethodManager? = null

    private var sizeObserver: Job? = null
    private var keyObserver: Job? = null
    private val textWatcher = createTextWatcher()

    private var adapter: BookAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_bind == null) _bind = FragmentBookBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        globalViewModel.shouldReset.ifTrue {
            bookViewModel.reset()
        }

        init()

        bind.btnSearch.setOnClickListener {
            if (!globalViewModel.isShowKey.value) {
                bind.tilSearch.requestFocus()
                imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
            } else {
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        bind.btnAdd.setOnClickListener { openEditFragment() }

        bind.tilSearch.editText?.addTextChangedListener(textWatcher)

        bind.recycler.adapter = adapter

    }

    private fun init(){
        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        adapter = BookAdapter(bookViewModel, player) {
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(
                R.id.addPhraseFragment,
                bundleOf(EditPhraseFragment.ID_PHRASE to it)
            )
        }

        sizeObserver = createSizeObserver()
        keyObserver = createKeyObserver()
    }

    private fun createTextWatcher(): TextWatcher = ShortTextWatcher {
        bookViewModel.like.value = it.toString()
    }

    private fun createSizeObserver(): Job = bookViewModel.size.observeWhenStarted(lifecycleScope) {
        bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
        adapter?.notifyDataSetChanged()
    }

    private fun createKeyObserver(): Job = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
        bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
        bind.btnAdd.visibility = if (it) View.GONE else View.VISIBLE
        if (it) bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
        else bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
    }

    private fun openEditFragment() {
        requireActivity().findNavController(R.id.nav_host_fragment)
            .navigate(R.id.addPhraseFragment, null, navOptions { anim { enter = R.anim.show } })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sizeObserver?.cancel()
        keyObserver?.cancel()
        bind.tilSearch.editText?.removeTextChangedListener(textWatcher)
        adapter?.onDestroy()
        adapter = null
        imm = null
        _bind = null
    }

}