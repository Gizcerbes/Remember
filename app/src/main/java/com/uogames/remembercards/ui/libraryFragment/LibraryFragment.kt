package com.uogames.remembercards.ui.libraryFragment

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentLbraryBinding
import com.uogames.remembercards.ui.editModuleFragment.EditModuleFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.*
import javax.inject.Inject

class LibraryFragment : DaggerFragment() {

	@Inject
	lateinit var libraryViewModel: LibraryViewModel

	@Inject
	lateinit var networkLibraryViewModel: NetworkLibraryViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private var _bind: FragmentLbraryBinding? = null
	private val bind get() = _bind!!

	private var keyObserver: Job? = null
	private var sizeObserver: Job? = null
	private val searchWatcher: TextWatcher = createSearchWatcher()

	private var adapter: ClosableAdapter<*>? = null
	private var cloud = false

	private val dialog = DialogNewModule {
		libraryViewModel.createModule(it) { moduleID ->
			navigateToEdit(moduleID)
		}
	}

	private var imm: InputMethodManager? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (_bind == null) _bind = FragmentLbraryBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		globalViewModel.shouldReset.ifTrue {
			libraryViewModel.reset()
		}

		imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

		keyObserver = createKeyObserver()
		sizeObserver = createSizeObserver()
		bind.tilSearch.editText?.addTextChangedListener(searchWatcher)


		bind.tilSearch.visibility = View.GONE

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				val text = when (adapter) {
					is LibraryAdapter -> libraryViewModel.like.value
					is NetworkLibraryAdapter -> networkLibraryViewModel.like.value
					else -> ""
				}
				bind.tilSearch.editText?.setText(text)
				bind.tilSearch.editText?.setSelection(text.length)
				imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_FORCED)
			} else {
				imm?.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}

		bind.btnAdd.setOnClickListener {
			dialog.show(requireActivity().supportFragmentManager, DialogNewModule.TAG)
		}

		adapter = LibraryAdapter(libraryViewModel) { navigateToEdit(it.id) }

		lifecycleScope.launchWhenStarted {
			delay(300)
			bind.recycler.adapter = adapter
		}

		bind.btnNetwork.setOnClickListener {
			lifecycleScope.launchWhenStarted {
				cloud = !cloud
				adapter?.close()
				if (cloud) {
					adapter = NetworkLibraryAdapter(networkLibraryViewModel) {}
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_off_24)
					bind.btnAdd.visibility = View.GONE
					bind.recycler.adapter = null
					delay(50)
					networkLibraryViewModel.like.value = libraryViewModel.like.value
					bind.txtBookEmpty.visibility = if (networkLibraryViewModel.size.value == 0L) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
				} else {
					adapter = LibraryAdapter(libraryViewModel) { navigateToEdit(it.id) }
					bind.imgNetwork.setImageResource(R.drawable.ic_baseline_cloud_24)
					bind.btnAdd.visibility = View.VISIBLE
					bind.recycler.adapter = null
					delay(50)
					libraryViewModel.like.value = networkLibraryViewModel.like.value
					bind.txtBookEmpty.visibility = if (libraryViewModel.size.value == 0) View.VISIBLE else View.GONE
					bind.recycler.adapter = adapter
				}
			}
		}
	}

	private fun createKeyObserver() = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
		val res = it && !dialog.isVisible
		bind.tilSearch.visibility = if (res) View.VISIBLE else View.GONE
		bind.btnAdd.visibility = if (res) View.GONE else View.VISIBLE
		if (res) {
			bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
		} else {
			bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
		}
	}

	private fun createSizeObserver(): Job = lifecycleScope.launchWhenStarted {
		libraryViewModel.size.observeWhile(this) {
			if (adapter is LibraryAdapter)
				bind.txtBookEmpty.visibility = if (!cloud && it == 0) View.VISIBLE else View.GONE
		}
		networkLibraryViewModel.size.observeWhile(this) {
			if (adapter is NetworkLibraryAdapter)
				bind.txtBookEmpty.visibility = if (cloud && it == 0L) View.VISIBLE else View.GONE
		}
	}

	private fun createSearchWatcher(): TextWatcher = ShortTextWatcher {
		when (adapter) {
			is LibraryAdapter -> libraryViewModel.like.value = it.toString()
			is NetworkLibraryAdapter -> networkLibraryViewModel.like.value = it.toString()
		}
	}

	private fun navigateToEdit(moduleID: Int) {
		requireActivity().findNavController(R.id.nav_host_fragment).navigate(
			R.id.editModuleFragment,
			Bundle().apply {
				putInt(EditModuleFragment.MODULE_ID, moduleID)
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

	override fun onDestroyView() {
		super.onDestroyView()
		keyObserver?.cancel()
		sizeObserver?.cancel()
		bind.tilSearch.editText?.removeTextChangedListener(searchWatcher)
		adapter?.close()
		imm?.hideSoftInputFromWindow(view?.windowToken, 0)
		adapter = null
		imm = null
		_bind = null
	}
}
