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
import com.uogames.remembercards.ui.cardFragment.CardViewModel
import com.uogames.remembercards.ui.editCardFragment.EditCardFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

class ChoiceCardFragment : DaggerFragment() {

	companion object {
		const val TAG = "ChoiceCardFragment_CHOICE_CARD_FRAGMENT"
	}

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var cardViewModel: CardViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	private var _bind: FragmentCardBinding? = null
	private val bind get() = _bind!!

	private var imm: InputMethodManager? = null

	private var keyObserver: Job? = null
	private var sizeObserver: Job? = null
	private val searchTextWatcher = createSearchTextWatcher()

	private var receivedTAG: String? = null

	private var adapter: ChoiceCardAdapter? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (_bind == null) _bind = FragmentCardBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		globalViewModel.shouldReset.ifTrue { cardViewModel.reset() }

		receivedTAG = arguments?.getString(TAG).ifNull { return }
		init()
		bind.txtTopName.text = requireContext().getString(R.string.choice_card)
		keyObserver = createKeyObserver()
		sizeObserver = createSizeObserver()
		lifecycleScope.launchWhenStarted {
			delay(300)
			bind.recycler.adapter = adapter
		}
		setListeners()
	}

	private fun init() {
		imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

		adapter = ChoiceCardAdapter(cardViewModel, player) { id ->
			imm?.hideSoftInputFromWindow(view?.windowToken, 0)
			receivedTAG?.let {
				setFragmentResult(it, bundleOf("ID" to id))
			}.ifNull {
				Toast.makeText(requireContext(), "Argument Problem", Toast.LENGTH_SHORT).show()
			}
			findNavController().popBackStack()
		}
	}

	private fun createKeyObserver(): Job = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
		bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
		bind.btnAdd.visibility = if (it) View.GONE else View.VISIBLE
		bind.btnBack.visibility = if (it) View.GONE else View.VISIBLE
		if (it) bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
		else bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
	}

	private fun createSizeObserver(): Job = cardViewModel.size.observeWhenStarted(lifecycleScope) {
		bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
		adapter?.notifyDataSetChanged()
	}


	private fun setListeners() {
		bind.btnBack.setOnClickListener {
			findNavController().popBackStack()
		}

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
			} else {
				imm?.hideSoftInputFromWindow(view?.windowToken, 0)
			}
		}

		bind.btnAdd.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(
				R.id.editCardFragment,
				Bundle().apply {
					putString(EditCardFragment.CREATE_FOR, receivedTAG)
					putInt(EditCardFragment.POP_BACK_TO, findNavController().currentDestination?.id.ifNull { 0 })
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

		bind.tilSearch.editText?.addTextChangedListener(searchTextWatcher)
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