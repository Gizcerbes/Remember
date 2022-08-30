package com.uogames.remembercards.ui.bookFragment

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

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				bind.tilSearch.editText?.setText(bookViewModel.like.value)
				imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_FORCED)
			} else {
				imm?.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}

		bind.tilSearch.editText?.addTextChangedListener(textWatcher)

		imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

		sizeObserver = createSizeObserver()
		keyObserver = createKeyObserver()

		bind.btnAdd.setOnClickListener { navigateToAdd() }

		adapter = BookAdapter(bookViewModel, player) { navigateToAdd(it) }

		lifecycleScope.launchWhenStarted {
			delay(300)
			bind.recycler.adapter = adapter
		}

		bind.btnNetwork.setOnClickListener {
			lifecycleScope.launchWhenStarted {
				cloud = !cloud
				if (cloud) {
					adapter = NetworkBookAdapter(networkBookViewModel, player)
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_off_24)
					bind.btnAdd.visibility = View.GONE
					bind.recycler.adapter = null
					delay(300)
					bind.txtBookEmpty.visibility = if (networkBookViewModel.size.value == 0L) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
				} else {
					adapter = BookAdapter(bookViewModel, player) { navigateToAdd(it) }
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_24)
					bind.btnAdd.visibility = View.VISIBLE
					bind.recycler.adapter = null
					delay(300)
					bind.txtBookEmpty.visibility = if (bookViewModel.size.value == 0) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
				}
			}
		}

	}

	private fun navigateToAdd(bundle: Bundle? = null) {
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
		bookViewModel.like.value = it.toString()
		networkBookViewModel.like.value = it.toString()
	}

	private fun createSizeObserver(): Job = lifecycleScope.launchWhenStarted {
		bookViewModel.size.observeWhile(this) {
			bind.txtBookEmpty.visibility = if (!cloud && it == 0) View.VISIBLE else View.GONE
		}
		networkBookViewModel.size.observeWhile(this) {
			bind.txtBookEmpty.visibility = if (cloud && it == 0L) View.VISIBLE else View.GONE
		}
	}

	private fun createKeyObserver(): Job = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
		bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
		bind.btnAdd.visibility = if (it || cloud) View.GONE else View.VISIBLE
		if (it) bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
		else bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		sizeObserver?.cancel()
		keyObserver?.cancel()
		bind.tilSearch.editText?.removeTextChangedListener(textWatcher)
		adapter?.close()
		adapter = null
		imm = null
		_bind = null
	}

}