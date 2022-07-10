package com.uogames.remembercards.ui.choicePhraseFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentChoicePhraseBinding
import com.uogames.remembercards.ui.bookFragment.BookViewModel
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
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
	lateinit var editPhraseViewModel: EditPhraseViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	private lateinit var bind: FragmentChoicePhraseBinding

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	private var receivedTAG: String? = null

	private val adapter by lazy {
		ChoicePhraseAdapter(lifecycleScope, bookViewModel, player, {
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.addPhraseFragment, Bundle().apply {
				putInt(EditPhraseFragment.ID_PHRASE, it.id)
			})
		}) { phrase ->
			imm.hideSoftInputFromWindow(view?.windowToken, 0)
			receivedTAG?.let {
				globalViewModel.saveData(it, phrase.id.toString())
			}.ifNull {
				Toast.makeText(requireContext(), "Argument Problem", Toast.LENGTH_SHORT).show()
			}
			findNavController().popBackStack()
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentChoicePhraseBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		bookViewModel.reset()

		receivedTAG = arguments?.getString(TAG)

		receivedTAG?.let {
			bookViewModel.size.observeWhenStarted(lifecycleScope) {
				adapter.notifyDataSetChanged()
			}

			bind.recycler.adapter = adapter

			bind.tilSearch.editText?.doOnTextChanged { text, _, _, _ ->
				bookViewModel.like.value = text.toString()
			}
		}

		bookViewModel.size.observeWhenStarted(lifecycleScope) {
			bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
			adapter.notifyDataSetChanged()
		}

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

		bind.btnAdd.setOnClickListener { openEditFragment() }
	}

	private fun openEditFragment() {
		editPhraseViewModel.reset()
		requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.addPhraseFragment)
	}

}