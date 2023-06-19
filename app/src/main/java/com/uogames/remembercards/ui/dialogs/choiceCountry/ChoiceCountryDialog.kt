package com.uogames.remembercards.ui.dialogs.choiceCountry

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.uogames.flags.Countries
import com.uogames.remembercards.databinding.FragmentChoiceCountryBinding
import com.uogames.remembercards.utils.ObservedDialog
import com.uogames.remembercards.utils.ShortTextWatcher
import kotlinx.coroutines.delay

class ChoiceCountryDialog(call: (Countries) -> Unit) : ObservedDialog<Countries>(call) {

    companion object {
        const val TAG = "ChoiceCountryDialog_TAG"
    }

    private var _bind: FragmentChoiceCountryBinding? = null
    private val bind get() = _bind!!

    private var adapter: ChoiceCountryAdapter? = null

    private val searchWatcher = createSearchWatcher()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_bind == null) _bind = FragmentChoiceCountryBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChoiceCountryAdapter {
            setData(it)
            dismiss()
        }

        lifecycleScope.launchWhenStarted {
            delay(300)
            bind.rvCountries.adapter = adapter
        }

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        adapter?.setData(Countries.values().toList())

        bind.tilSearch.editText?.addTextChangedListener(searchWatcher)
    }

    private fun createSearchWatcher(): TextWatcher = ShortTextWatcher {
        val cou = Countries.values().filter { country ->
            for (countryName in country.country) {
                if (countryName.value.uppercase().contains(it.toString().uppercase())) return@filter true
            }
            false
        }
        adapter?.setData(cou)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bind.tilSearch.editText?.removeTextChangedListener(searchWatcher)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter = null
        _bind = null
    }
}
