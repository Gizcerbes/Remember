package com.uogames.remembercards.ui.cardFragment

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
import com.uogames.dto.local.Card
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentCardBinding
import com.uogames.remembercards.ui.editCardFragment.EditCardFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

class CardFragment : DaggerFragment() {

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

	private var adapter: ClosableAdapter<*>? = null
	private var cloud = false

	private var keyObserver: Job? = null
	private var sizeObserver: Job? = null
	private val searchTextWatcher = createSearchTextWatcher()

	private var imm: InputMethodManager? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (_bind == null) _bind = FragmentCardBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		globalViewModel.shouldReset.ifTrue {
			cardViewModel.reset()
		}

		imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

		bind.btnBack.visibility = View.GONE

		keyObserver = createKeyObserver()
		sizeObserver = createSizeObserver()
		bind.tilSearch.editText?.addTextChangedListener(searchTextWatcher)

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				val text = when(adapter){
					is CardAdapter -> cardViewModel.like.value
					is NetworkCardAdapter -> networkCardViewModel.like.value
					else -> ""
				}
				bind.tilSearch.editText?.setText(text)
				bind.tilSearch.editText?.setSelection(text.length)
				imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_FORCED)
			} else {
				imm?.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}

		bind.btnAdd.setOnClickListener { navigateToEdit() }

		adapter = CardAdapter(cardViewModel, player) { navigateToEdit(it) }

		lifecycleScope.launchWhenStarted {
			delay(300)
			bind.recycler.adapter = adapter
		}

		bind.btnNetwork.setOnClickListener {
			lifecycleScope.launchWhenStarted {
				cloud = !cloud
				adapter?.close()
				if (cloud) {
					adapter = NetworkCardAdapter(networkCardViewModel, player)
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_off_24)
					bind.btnAdd.visibility = View.GONE
					bind.recycler.adapter = null
					delay(50)
					networkCardViewModel.like.value = cardViewModel.like.value
					bind.txtBookEmpty.visibility = if (networkCardViewModel.size.value == 0L) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
				} else {
					val ca = CardAdapter(cardViewModel, player) { navigateToEdit(it) }
					adapter = ca
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_24)
					bind.btnAdd.visibility = View.VISIBLE
					bind.recycler.adapter = null
					delay(50)
					cardViewModel.like.value = networkCardViewModel.like.value
					bind.txtBookEmpty.visibility = if (cardViewModel.size.value == 0) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
				}
			}
		}
	}

	private fun navigateToEdit(bundle: Bundle? = null) {
		imm?.hideSoftInputFromWindow(view?.windowToken, 0)
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

	private fun navigateToEdit(card: Card) = navigateToEdit(bundleOf(EditCardFragment.EDIT_ID to card.id))

	private fun createKeyObserver(): Job = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
		bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
		bind.btnAdd.visibility = if (it || cloud) View.GONE else View.VISIBLE
		if (it) {
			bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
		} else {
			bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
		}
	}

	private fun createSizeObserver(): Job = lifecycleScope.launchWhenStarted {
		cardViewModel.size.observeWhile(this) {
			if (adapter is CardAdapter)
				bind.txtBookEmpty.visibility = if (!cloud && it == 0) View.VISIBLE else View.GONE
		}
		networkCardViewModel.size.observeWhile(this) {
			if (adapter is NetworkCardAdapter)
				bind.txtBookEmpty.visibility = if (cloud && it == 0L) View.VISIBLE else View.GONE
		}
	}

	private fun createSearchTextWatcher(): TextWatcher = ShortTextWatcher {
		when (adapter) {
			is CardAdapter -> cardViewModel.like.value = it.toString()
			is NetworkCardAdapter -> networkCardViewModel.like.value = it.toString()
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
