package com.uogames.remembercards.ui.choiceCountry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.uogames.flags.Countries
import com.uogames.remembercards.databinding.FragmentChoiceCountryBinding
import com.uogames.remembercards.utils.ObservedDialog

class ChoiceCountryDialog(call: (Countries) -> Unit) : ObservedDialog<Countries>(call) {

	companion object{
		const val TAG ="ChoiceCountryDialog_TAG"
	}

	private lateinit var bind: FragmentChoiceCountryBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentChoiceCountryBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val adapter = ChoiceCountryAdapter {
			setData(it)
			dismiss()
		}
		bind.rvCountries.adapter = adapter

		dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

		adapter.setData(Countries.values().toList())

		bind.tilSearch.editText?.doOnTextChanged { text, _, _, _ ->
			val cou = Countries.values().filter { country ->
				for (it in country.country) {
					if (it.value.uppercase().contains(text.toString().uppercase())) return@filter true
				}
				false
			}
			adapter.setData(cou)
		}

	}


}