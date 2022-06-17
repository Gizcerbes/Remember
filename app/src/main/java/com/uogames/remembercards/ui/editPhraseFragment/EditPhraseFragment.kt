package com.uogames.remembercards.ui.editPhraseFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uogames.remembercards.databinding.FragmentEditBinding
import dagger.android.support.DaggerFragment

class EditPhraseFragment:DaggerFragment() {

	private lateinit var bind: FragmentEditBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentEditBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
	}


}