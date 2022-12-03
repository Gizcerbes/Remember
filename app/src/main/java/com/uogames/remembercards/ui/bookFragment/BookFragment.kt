package com.uogames.remembercards.ui.bookFragment

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentBookBinding
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

class BookFragment : DaggerFragment() {

	@Inject
	lateinit var bookViewModel: BookViewModel

	@Inject
	lateinit var networkBookViewModel: NetworkBookViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	private var _bind: FragmentBookBinding? = null
	private val bind get() = _bind!!

	private var imm: InputMethodManager? = null

	private var sizeObserver: Job? = null
	private var keyObserver: Job? = null
	private val textWatcher = createTextWatcher()

	private var adapter: ClosableAdapter<*>? = null
	private var cloud = false

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

		imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				val text = when (adapter) {
					is BookAdapter -> bookViewModel.like.value
					is NetworkBookAdapter -> networkBookViewModel.like.value
					else -> ""
				}
				bind.tilSearch.editText?.setText(text)
				bind.tilSearch.editText?.setSelection(text.length)
				imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_FORCED)
			} else {
				imm?.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}

		bind.tilSearch.editText?.addTextChangedListener(textWatcher)

		sizeObserver = createSizeObserver()
		keyObserver = createKeyObserver()

		bind.btnAdd.setOnClickListener { navigateToAdd() }

		adapter = createBookAdapter()
		bookViewModel.recyclerStat?.let { bind.recycler.layoutManager?.onRestoreInstanceState(it) }

		lifecycleScope.launchWhenStarted {
			delay(300)
			bind.recycler.adapter = adapter
		}

		bind.btnNetwork.setOnClickListener {
			lifecycleScope.launchWhenStarted {
				cloud = !cloud
				adapter?.close()
				bind.recycler.adapter = null
				if (cloud) {
					adapter = createNetworkBookAdapter()
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_off_24)
					bind.btnAdd.visibility = View.GONE
					delay(50)
					networkBookViewModel.like.value = bookViewModel.like.value
					bind.txtBookEmpty.visibility = if (networkBookViewModel.size.value == 0L) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
				} else {
					adapter = createBookAdapter()
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_24)
					bind.btnAdd.visibility = View.VISIBLE
					delay(50)
					bookViewModel.like.value = networkBookViewModel.like.value
					bind.txtBookEmpty.visibility = if (bookViewModel.size.value == 0) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
					bookViewModel.recyclerStat?.let { bind.recycler.layoutManager?.onRestoreInstanceState(it) }
				}
			}
		}
	}

	fun createBookAdapter() = BookAdapter(bookViewModel, player) {
		bookViewModel.recyclerStat = bind.recycler.layoutManager?.onSaveInstanceState()
		navigateToAdd(it)
	}

	fun createNetworkBookAdapter() = NetworkBookAdapter(networkBookViewModel, player)

	private fun navigateToAdd(bundle: Bundle? = null) {
		imm?.hideSoftInputFromWindow(view?.windowToken, 0)
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

	private fun navigateToAdd(id: Int) = navigateToAdd(bundleOf(EditPhraseFragment.ID_PHRASE to id))

	private fun createTextWatcher(): TextWatcher = ShortTextWatcher {
		when (adapter) {
			is BookAdapter -> bookViewModel.like.value = it.toString()
			is NetworkBookAdapter -> networkBookViewModel.like.value = it.toString()
		}
	}

	private fun createSizeObserver(): Job = lifecycleScope.launchWhenStarted {
		bookViewModel.size.observeWhile(this) {
			if (adapter is BookAdapter)
				bind.txtBookEmpty.visibility = if (!cloud && it == 0) View.VISIBLE else View.GONE
		}
		networkBookViewModel.size.observeWhile(this) {
			if (adapter is NetworkBookAdapter)
				bind.txtBookEmpty.visibility = if (cloud && it == 0L) View.VISIBLE else View.GONE
		}
	}

	private fun createKeyObserver(): Job = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
		bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
		bind.clSearchBar.visibility = if (it) View.VISIBLE else View.GONE
		bind.btnAdd.visibility = if (it || cloud) View.GONE else View.VISIBLE
		if (it) {
			bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
		} else {
			bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		sizeObserver?.cancel()
		keyObserver?.cancel()
		bind.tilSearch.editText?.removeTextChangedListener(textWatcher)
		adapter?.close()
		imm?.hideSoftInputFromWindow(view?.windowToken, 0)
		adapter = null
		imm = null
		_bind = null
	}
}
