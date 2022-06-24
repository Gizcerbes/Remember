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
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.databinding.FragmentChoiceDialogBinding
import com.uogames.remembercards.ui.bookFragment.BookViewModel
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ChoicePhraseFragment() : DaggerFragment() {

	companion object {
		const val TAG = "CHOICE_PHRASE_DIALOG"
	}

	@Inject
	lateinit var bookViewModel: BookViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private lateinit var bind: FragmentChoiceDialogBinding

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	private var receivedTAG: String? = null

	private val adapter by lazy {
		ChoicePhraseAdapter(bookViewModel) { phrase ->
			imm.hideSoftInputFromWindow(view?.windowToken, 0)
			receivedTAG?.let { globalViewModel.saveData(it, phrase.id.toString()) }
				.ifNull {
					Toast.makeText(requireContext(), "Argument Problem", Toast.LENGTH_SHORT).show()
				}
			findNavController().popBackStack()
		}
	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentChoiceDialogBinding.inflate(inflater, container, false)
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

			bind.rvChoice.adapter = adapter

			bind.tilEdit.editText?.doOnTextChanged { text, _, _, _ ->
				bookViewModel.like.value = text.toString()
			}
		}

	}

}