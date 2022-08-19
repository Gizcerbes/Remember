package com.uogames.remembercards.ui.libraryFragment

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
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
import com.uogames.remembercards.utils.ShortTextWatcher
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.*
import javax.inject.Inject

class LibraryFragment : DaggerFragment() {

	@Inject
	lateinit var libraryViewModel: LibraryViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private var _bind: FragmentLbraryBinding? = null
	private val bind get() = _bind!!

	private var keyObserver: Job? = null

	private val searchWatcher: TextWatcher = createSearchWatcher()

	private var adapter: LibraryAdapter? = null

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

		adapter = LibraryAdapter(libraryViewModel) {
			navigateToEdit(it.id)
		}

		imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


		keyObserver = createKeyObserver()

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				imm?.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
			} else {
				imm?.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}

		bind.btnAdd.setOnClickListener {
			dialog.show(requireActivity().supportFragmentManager, DialogNewModule.TAG)
		}

		bind.tilSearch.editText?.addTextChangedListener(searchWatcher)

		lifecycleScope.launchWhenStarted {
			delay(300)
			bind.recycler.adapter = adapter
		}

	}

	private fun createKeyObserver() = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
		val res = it && !dialog.isVisible
		bind.tilSearch.visibility = if (res) View.VISIBLE else View.GONE
		bind.btnAdd.visibility = if (res) View.GONE else View.VISIBLE
		if (res) bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
		else bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
	}

	private fun createSearchWatcher(): TextWatcher = ShortTextWatcher {
		libraryViewModel.like.value = it.toString()
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
		bind.tilSearch.editText?.removeTextChangedListener(searchWatcher)
		adapter?.onDestroy()
		adapter = null
		imm = null
		_bind = null
	}

}