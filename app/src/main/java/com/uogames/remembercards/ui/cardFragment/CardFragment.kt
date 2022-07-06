package com.uogames.remembercards.ui.cardFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentCardBinding
import com.uogames.remembercards.ui.editCardFragment.EditCardViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.observeWhenStarted
import com.uogames.remembercards.utils.observeWhile
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class CardFragment : DaggerFragment() {

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var editCardViewModel: EditCardViewModel

	@Inject
	lateinit var cardViewModel: CardViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	lateinit var bind: FragmentCardBinding

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentCardBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		bind.btnBack.visibility = View.GONE

		globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
			bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
			bind.btnAdd.visibility = if (it) View.GONE else View.VISIBLE
			if (it) {
				bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
			} else {
				bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
			}
		}

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				imm.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
			} else {
				imm.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}

		bind.btnAdd.setOnClickListener {
			editCardViewModel.reset()
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.editCardFragment)
		}

		cardViewModel.size.observeWhenStarted(lifecycleScope) {
			bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
		}

		bind.tilSearch.editText?.doOnTextChanged { text, _, _, _ ->
			cardViewModel.like.value = text.toString()
		}

		bind.recycler.adapter = CardAdapter(cardViewModel, player, lifecycleScope)

	}


}