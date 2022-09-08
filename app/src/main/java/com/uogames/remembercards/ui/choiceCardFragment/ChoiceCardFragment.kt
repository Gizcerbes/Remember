package com.uogames.remembercards.ui.choiceCardFragment

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
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
import com.uogames.remembercards.ui.cardFragment.NetworkCardViewModel
import com.uogames.remembercards.ui.editCardFragment.EditCardFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
	lateinit var networkCardViewModel: NetworkCardViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	private var _bind: FragmentCardBinding? = null
	private val bind get() = _bind!!

	private var imm: InputMethodManager? = null

	private var keyObserver: Job? = null
	private var sizeObserver: Job? = null
	private val searchTextWatcher = createSearchTextWatcher()

	private var receivedTAG: String? = null

	private var adapter: ClosableAdapter<*>? = null
	private var cloud = false

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (_bind == null) _bind = FragmentCardBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		globalViewModel.shouldReset.ifTrue { cardViewModel.reset() }

		imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

		receivedTAG = arguments?.getString(TAG).ifNull { return }

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				val text = when (adapter) {
					is ChoiceCardAdapter -> cardViewModel.like.value
					is ChoiceNetworkCardAdapter -> networkCardViewModel.like.value
					else -> ""
				}
				bind.tilSearch.editText?.setText(text)
				bind.tilSearch.editText?.setSelection(text.length)
				imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
			} else {
				imm?.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}

		bind.txtTopName.text = requireContext().getString(R.string.choice_card)
		keyObserver = createKeyObserver()
		sizeObserver = createSizeObserver()

		bind.btnAdd.setOnClickListener { navigateSelectedToEdit() }

		bind.btnBack.setOnClickListener { findNavController().popBackStack() }

		bind.tilSearch.editText?.addTextChangedListener(searchTextWatcher)

		adapter = createLocalAdapter()

		lifecycleScope.launchWhenStarted {
			delay(300)
			bind.recycler.adapter = adapter
		}

		bind.btnNetwork.setOnClickListener {
			lifecycleScope.launchWhenStarted {
				cloud = !cloud
				if (cloud) {
					adapter = createNetworkAdapter()
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_off_24)
					bind.btnAdd.visibility = View.GONE
					bind.recycler.adapter = null
					delay(300)
					networkCardViewModel.like.value = cardViewModel.like.value
					bind.txtBookEmpty.visibility = if (networkCardViewModel.size.value == 0L) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
				} else {
					adapter = createLocalAdapter()
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_24)
					bind.btnAdd.visibility = View.VISIBLE
					bind.recycler.adapter = null
					delay(300)
					cardViewModel.like.value = networkCardViewModel.like.value
					bind.txtBookEmpty.visibility = if (cardViewModel.size.value == 0) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
				}
			}
		}
	}

	private fun createLocalAdapter() = ChoiceCardAdapter(cardViewModel, player, receiveCall())

	private fun createNetworkAdapter() = ChoiceNetworkCardAdapter(networkCardViewModel, player, receiveCall())

	private fun receiveCall(): (Int) -> Unit = { id ->
		receivedTAG?.let {
			setFragmentResult(it, bundleOf("ID" to id))
		}.ifNull {
			Toast.makeText(requireContext(), "Argument Problem", Toast.LENGTH_SHORT).show()
		}
		findNavController().popBackStack()
	}

	private fun createKeyObserver(): Job = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
		bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
		bind.btnAdd.visibility = if (it) View.GONE else View.VISIBLE
		bind.btnBack.visibility = if (it) View.GONE else View.VISIBLE
		if (it) {
			bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
		} else {
			bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
		}
	}

	private fun createSizeObserver(): Job = lifecycleScope.launchWhenStarted {
		cardViewModel.size.observeWhile(this) {
			if (adapter is ChoiceCardAdapter)
				bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
		}
		networkCardViewModel.size.observeWhile(this) {
			if (adapter is ChoiceNetworkCardAdapter)
				bind.txtBookEmpty.visibility = if (it == 0L) View.VISIBLE else View.GONE
		}
	}

	private fun navigateSelectedToEdit() = navigateToEdit(
		bundleOf(
			EditCardFragment.CREATE_FOR to receivedTAG,
			EditCardFragment.POP_BACK_TO to findNavController().currentDestination?.id.ifNull { 0 }
		)
	)

	private fun navigateToEdit(bundle: Bundle? = null) {
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
		when (adapter) {
			is ChoiceCardAdapter -> cardViewModel.like.value = it.toString()
			is ChoiceNetworkCardAdapter -> networkCardViewModel.like.value = it.toString()
		}

	}

	override fun onDestroyView() {
		super.onDestroyView()
		keyObserver?.cancel()
		sizeObserver?.cancel()
		bind.tilSearch.editText?.removeTextChangedListener(searchTextWatcher)
		adapter?.close()
		imm?.hideSoftInputFromWindow(view?.windowToken, 0)
		adapter = null
		imm = null
		_bind = null
	}
}
