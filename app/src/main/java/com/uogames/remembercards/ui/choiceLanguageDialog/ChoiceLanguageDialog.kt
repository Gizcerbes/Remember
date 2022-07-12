package com.uogames.remembercards.ui.choiceLanguageDialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.uogames.flags.Languages
import com.uogames.remembercards.databinding.FragmentChoicePhraseBinding
import com.uogames.remembercards.utils.ObservedDialog

class ChoiceLanguageDialog(call: (Languages) -> Unit) : ObservedDialog<Languages>(call) {

	companion object {
		const val TAG = "CHOICE_LANGUAGE"
	}


	private lateinit var bind: FragmentChoicePhraseBinding

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	private val adapter: ChoiceLanguageAdapter by lazy {
		ChoiceLanguageAdapter(lifecycleScope) {
			//call(it)
			imm.hideSoftInputFromWindow(view?.windowToken, 0)
			dismiss()
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentChoicePhraseBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

		bind.recycler.adapter = adapter

		bind.tilSearch.editText?.doOnTextChanged { text, _, _, _ ->
			//adapter.setMask(text.toString())
		}

	}

}