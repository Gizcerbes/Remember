package com.uogames.remembercards.ui.choiceModuleDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.uogames.dto.Module
import com.uogames.remembercards.databinding.FragmentChoiceModuleBinding
import com.uogames.remembercards.ui.editModuleFragment.EditModuleViewModel
import com.uogames.remembercards.ui.libraryFragment.LibraryAdapter
import com.uogames.remembercards.ui.libraryFragment.LibraryViewModel
import com.uogames.remembercards.utils.ObservedDialog

class ChoiceModuleDialog(private val libraryViewModel: LibraryViewModel, call: (Module) -> Unit) : ObservedDialog<Module>(call) {

	companion object {
		const val TAG = "ChoiceModuleDialog_TAG"
	}

	private lateinit var bind: FragmentChoiceModuleBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentChoiceModuleBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

		val adapter = LibraryAdapter(lifecycleScope, libraryViewModel) {
			setData(it)
			dismiss()
		}
		bind.rvModules.adapter = adapter

	}


}