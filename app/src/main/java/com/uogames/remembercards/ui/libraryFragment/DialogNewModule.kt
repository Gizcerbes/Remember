package com.uogames.remembercards.ui.libraryFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uogames.remembercards.databinding.DialogNewModuleBinding
import com.uogames.remembercards.utils.ObservedDialog

class DialogNewModule(call: (String) -> Unit) : ObservedDialog<String>(call) {

	companion object{
		const val TAG = "DialogNewModule_TAG"
	}

	private lateinit var bind: DialogNewModuleBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		bind = DialogNewModuleBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		bind.btnAccept.setOnClickListener {
			setData(bind.tilText.editText?.text.toString())
			dismiss()
		}
		bind.btnClose.setOnClickListener {
			dismiss()
		}
		dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
	}


}