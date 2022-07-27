package com.uogames.remembercards.ui.cardFragment

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentCardBinding
import com.uogames.remembercards.ui.editCardFragment.EditCardFragment
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ShortTextWatcher
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import javax.inject.Inject

class CardFragment : DaggerFragment() {

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var cardViewModel: CardViewModel

    @Inject
    lateinit var player: ObservableMediaPlayer

    private var _bind: FragmentCardBinding? = null
    private val bind get() = _bind!!

    private var adapter: CardAdapter? = null

    private var imm: InputMethodManager? = null

    private var keyObserver: Job? = null
    private var sizeObserver: Job? = null
    private val searchTextWatcher = createSearchTextWatcher()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_bind == null) _bind = FragmentCardBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        globalViewModel.shouldReset.ifTrue {
            cardViewModel.reset()
        }

        adapter = CardAdapter(cardViewModel, player) {
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(
                R.id.editCardFragment,
                Bundle().apply { putInt(EditCardFragment.EDIT_ID, it.id) }
            )
        }

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        bind.btnBack.visibility = View.GONE

        keyObserver = createKeyObserver()
        sizeObserver = createSizeObserver()
        bind.tilSearch.editText?.addTextChangedListener(searchTextWatcher)

        bind.btnSearch.setOnClickListener {
            if (!globalViewModel.isShowKey.value) {
                bind.tilSearch.requestFocus()
                imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
            } else {
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        bind.btnAdd.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.editCardFragment)
        }

        bind.recycler.adapter = adapter

    }

    private fun createKeyObserver(): Job = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
        bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
        bind.btnAdd.visibility = if (it) View.GONE else View.VISIBLE
        if (it) bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
        else bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
    }

    private fun createSizeObserver(): Job = cardViewModel.size.observeWhenStarted(lifecycleScope) {
        bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
        adapter?.notifyDataSetChanged()
    }

    private fun createSearchTextWatcher(): TextWatcher = ShortTextWatcher {
        cardViewModel.like.value = it.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyObserver?.cancel()
        sizeObserver?.cancel()
        bind.tilSearch.editText?.removeTextChangedListener(searchTextWatcher)
        adapter?.onDestroy()
        adapter = null
        imm = null
        _bind = null
    }

}