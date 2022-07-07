package com.uogames.remembercards.ui.libraryFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.card.MaterialCardView
import com.uogames.flags.Countries
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentLbraryBinding
import com.uogames.remembercards.ui.editModuleFragment.EditModuleFragment
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class LibraryFragment : DaggerFragment() {

	@Inject
	lateinit var libraryViewModel: LibraryViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private lateinit var bind: FragmentLbraryBinding

	private val dialog = DialogNewModule {
		libraryViewModel.createModule(it) { moduleID ->
			navigateToEdit(moduleID)
		}
	}

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentLbraryBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
			val res = it && !dialog.isVisible
			bind.tilSearch.visibility = if (res) View.VISIBLE else View.GONE
			bind.btnAdd.visibility = if (res) View.GONE else View.VISIBLE
			if (res) {
				bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
			} else {
				bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
			}
		}

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				imm.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
			} else {
				imm.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}

		bind.btnAdd.setOnClickListener {
			dialog.show(requireActivity().supportFragmentManager, DialogNewModule.TAG)
		}

		bind.tilSearch.editText?.doOnTextChanged { text, _, _, _ ->
			libraryViewModel.like.value = text.toString()
		}

		bind.recycler.scrollToPosition(0)

		bind.recycler.adapter = LibraryAdapter(lifecycleScope, libraryViewModel) {
			navigateToEdit(it.id)
		}
	}

	private fun navigateToEdit(moduleID: Int){
		requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.editModuleFragment, Bundle().apply {
			putInt(EditModuleFragment.MODULE_ID, moduleID)
		})
	}

}