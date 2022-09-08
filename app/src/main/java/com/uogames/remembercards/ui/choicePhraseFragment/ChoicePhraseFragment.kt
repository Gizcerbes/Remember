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
import com.uogames.dto.local.Phrase
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentChoicePhraseBinding
import com.uogames.remembercards.ui.bookFragment.BookAdapter
import com.uogames.remembercards.ui.bookFragment.BookViewModel
import com.uogames.remembercards.ui.bookFragment.NetworkBookAdapter
import com.uogames.remembercards.ui.bookFragment.NetworkBookViewModel
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChoicePhraseFragment() : DaggerFragment() {

	companion object {
		const val TAG = "CHOICE_PHRASE_DIALOG"
	}

	@Inject
	lateinit var bookViewModel: BookViewModel

	@Inject
	lateinit var networkBookViewModel: NetworkBookViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	private var _bind: FragmentChoicePhraseBinding? = null
	private val bind get() = _bind!!

	private var imm: InputMethodManager? = null

	private var receivedTAG: String? = null

	private var adapter: ClosableAdapter<*>? = null
	private var cloud = false

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

		imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

		receivedTAG = arguments?.getString(TAG).ifNull { return }

		keyObserver = createKeyObserver()

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				val text = when (adapter) {
					is ChoicePhraseAdapter -> bookViewModel.like.value
					is ChoiceNetworkPhraseAdapter -> networkBookViewModel.like.value
					else -> ""
				}
				bind.tilSearch.editText?.setText(text)
				bind.tilSearch.editText?.setSelection(text.length)
				imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
			} else {
				imm?.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}

		bind.btnAdd.setOnClickListener { openEditFragment() }

		bind.btnBack.setOnClickListener { findNavController().popBackStack() }

		sizeObserver = createSizeObserver()

		adapter = createLocalAdapter()

		lifecycleScope.launchWhenStarted {
			delay(300)
			bind.recycler.adapter = adapter
		}
		bind.tilSearch.editText?.addTextChangedListener(searchWatcher)

		bind.btnNetwork.setOnClickListener {
			lifecycleScope.launchWhenStarted {
				cloud = !cloud
				if (cloud) {
					adapter = createNetworkAdapter()
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_off_24)
					bind.btnAdd.visibility = View.GONE
					bind.recycler.adapter = null
					delay(300)
					networkBookViewModel.like.value = bookViewModel.like.value
					bind.txtBookEmpty.visibility = if (networkBookViewModel.size.value == 0L) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
				} else {
					adapter = createLocalAdapter()
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_24)
					bind.btnAdd.visibility = View.VISIBLE
					bind.recycler.adapter = null
					delay(300)
					bookViewModel.like.value = networkBookViewModel.like.value
					bind.txtBookEmpty.visibility = if (bookViewModel.size.value == 0) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
					bookViewModel.recyclerStat?.let { bind.recycler.layoutManager?.onRestoreInstanceState(it) }
				}
			}
		}
	}

	private fun createLocalAdapter() = ChoicePhraseAdapter(bookViewModel, player, editCall(), selectedCall())

	private fun createNetworkAdapter() = ChoiceNetworkPhraseAdapter(networkBookViewModel, player, selectedCall())

	private fun selectedCall(): (Phrase) -> Unit = { phrase ->
		receivedTAG?.let {
			setFragmentResult(it, bundleOf("ID" to phrase.id))
		}.ifNull {
			Toast.makeText(requireContext(), "Argument Problem", Toast.LENGTH_SHORT).show()
		}
		findNavController().popBackStack()
	}

	private fun createSearchWatcher(): TextWatcher = ShortTextWatcher {
		when (adapter) {
			is ChoicePhraseAdapter -> bookViewModel.like.value = it.toString()
			is ChoiceNetworkPhraseAdapter -> networkBookViewModel.like.value = it.toString()
		}
	}

	private fun createSizeObserver(): Job = lifecycleScope.launchWhenStarted {
		bookViewModel.size.observeWhile(this) {
			if (adapter is BookAdapter)
				bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
		}
		networkBookViewModel.size.observeWhile(this) {
			if (adapter is NetworkBookAdapter)
				bind.txtBookEmpty.visibility = if (it == 0L) View.VISIBLE else View.GONE
		}
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

	private fun editCall(): (Phrase) -> Unit = {
		openEditFragment(bundleOf(EditPhraseFragment.ID_PHRASE to it.id))
	}

	private fun openEditFragment() = openEditFragment(bundleOf(
		EditPhraseFragment.CREATE_FOR to receivedTAG,
		EditPhraseFragment.POP_BACK_TO to findNavController().currentDestination?.id.ifNull { 0 }
	))

	private fun openEditFragment(bundle: Bundle? = null) {
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
		sizeObserver?.cancel()
		keyObserver?.cancel()
		bind.tilSearch.editText?.removeTextChangedListener(searchWatcher)
		adapter?.close()
		imm?.hideSoftInputFromWindow(view?.windowToken, 0)
		adapter = null
		imm = null
		_bind = null
	}
}
