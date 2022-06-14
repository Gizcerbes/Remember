package com.uogames.remembercards.ui.selectCountry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uogames.remembercards.databinding.FragmentSelectCountryBinding
import dagger.android.support.DaggerFragment

class SelectCountryFragment: DaggerFragment() {

	private lateinit var bind: FragmentSelectCountryBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentSelectCountryBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
	}



}