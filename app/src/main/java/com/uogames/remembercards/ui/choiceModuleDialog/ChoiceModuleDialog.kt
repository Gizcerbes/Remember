package com.uogames.remembercards.ui.choiceModuleDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uogames.dto.Module
import com.uogames.remembercards.utils.ObservedDialog

class ChoiceModuleDialog(call: (Module) -> Unit):ObservedDialog<Module>(call) {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
	}


}