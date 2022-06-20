package com.uogames.remembercards.ui.bookFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentBookBinding
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseViewModel
import com.uogames.remembercards.utils.observeWhile
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class BookFragment : DaggerFragment() {

	@Inject
	lateinit var bookViewModel: BookViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var addPhraseViewModel: EditPhraseViewModel

	lateinit var bind: FragmentBookBinding

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	private val adapter by lazy { BookAdapter(bookViewModel) }

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		bind = FragmentBookBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		bookViewModel.reset()

		bookViewModel.size.onEach {
			adapter.dataChanged()
		}.launchIn(lifecycleScope)

		bind.recycler.layoutManager = LinearLayoutManager(requireContext()).apply {
			orientation = LinearLayoutManager.VERTICAL
		}

		bookViewModel.size.onEach {
			bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
		}.launchIn(lifecycleScope)

		globalViewModel.isShowKey.observeWhile(lifecycleScope){
			bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
			bind.btnAdd.visibility = if (it) View.GONE else View.VISIBLE
			if (it) {
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

		bind.btnAdd.setOnClickListener { openEditFragment() }

		bind.tilSearch.editText?.doOnTextChanged { text, _, _, _ ->
			bookViewModel.like.value = text.toString()
		}

		bind.recycler.adapter = adapter
	}

	private fun openEditFragment() {
		addPhraseViewModel.reset()
		requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.addPhraseFragment)
	}

}