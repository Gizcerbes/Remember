package com.uogames.remembercards.ui.choiceCardFragment

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
import com.uogames.remembercards.databinding.FragmentCardBinding
import com.uogames.remembercards.ui.cardFragment.CardViewModel
import com.uogames.remembercards.ui.editCardFragment.EditCardViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ChoiceCardFragment : DaggerFragment() {

	companion object {
		const val TAG = "CHOICE_CARD_FRAGMENT"

	}

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

	private var receivedTAG: String? = null

	private val adapter by lazy {
		ChoiceCardAdapter(cardViewModel, player, lifecycleScope) { id ->
			imm.hideSoftInputFromWindow(view?.windowToken, 0)
			receivedTAG?.let {
				globalViewModel.saveData(it, id.toString())
			}.ifNull {
				Toast.makeText(requireContext(), "Argument Problem", Toast.LENGTH_SHORT).show()
			}
			findNavController().popBackStack()
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentCardBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		receivedTAG = arguments?.getString(TAG).ifNull { return }
		setData()
		setListeners()
	}
	
	private fun setData(){
		bind.txtTopName.text = requireContext().getString(R.string.choice_card)

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

		cardViewModel.size.observeWhenStarted(lifecycleScope) {
			bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
			adapter.notifyDataSetChanged()
		}

		bind.recycler.adapter = adapter
	}


	private fun setListeners(){
		bind.btnBack.setOnClickListener {
			findNavController().popBackStack()
		}

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				imm.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
			} else {
				imm.hideSoftInputFromWindow(view?.windowToken, 0)
			}
		}

		bind.btnAdd.setOnClickListener {
			editCardViewModel.reset()
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.editCardFragment)
		}


		bind.tilSearch.editText?.doOnTextChanged { text, _, _, _ ->
			cardViewModel.like.value = text.toString()
		}
	}


}