package com.uogames.remembercards.ui.bookFragment

import android.content.Context
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentBookBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class BookFragment : DaggerFragment() {

	@Inject
	lateinit var bookViewModel: BookViewModel

	lateinit var bind: FragmentBookBinding

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	private val adapter by lazy { BookAdapter(bookViewModel) }

	private val isSearch = MutableStateFlow(false)

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

		bind.tilSearch.editText?.addTextChangedListener { bookViewModel.setLike(it.toString()) }

		bookViewModel.size.onEach {
			bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
		}.launchIn(lifecycleScope)

		TransitionManager.beginDelayedTransition(bind.root, AutoTransition())

		isSearch.onEach {
			bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
			bind.btnAdd.visibility = if (it) View.GONE else View.VISIBLE
			if (it) {
				bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
				bind.tilSearch.requestFocus()
				imm.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
			} else {
				bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
				imm.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}.launchIn(lifecycleScope)

		bind.btnSearch.setOnClickListener { isSearch.value = !isSearch.value }

		bind.btnAdd.setOnClickListener { openAddDialog() }

		bind.recycler.adapter = adapter
	}

	private fun openAddDialog() {
		AddCardDialog().show(requireActivity().supportFragmentManager, AddCardDialog.TAG)
	}

}