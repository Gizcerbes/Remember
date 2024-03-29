package com.uogames.remembercards.ui.dialogs.choiceLanguageDialog

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentChoiceLanguageBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.delay
import java.util.*

class ChoiceLanguageDialog(val list: List<Locale>, call: (Locale) -> Unit) : ObservedDialog<Locale>(call) {

    companion object {
        const val TAG = "CHOICE_LANGUAGE"
    }

    private var _bind: FragmentChoiceLanguageBinding? = null
    private val bind get() = _bind!!

    private var imm: InputMethodManager? = null

    private var adapter: ChoiceLanguageAdapter? = null
    private val searchWatcher: TextWatcher = createSearchWatcher()

    private var seeAll = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_bind == null) _bind = FragmentChoiceLanguageBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        adapter = ChoiceLanguageAdapter() {
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            setData(it)
            dismiss()
        }

        adapter?.setItemList(list)

        bind.btnSeeAll.setOnClickListener {
            seeAll = !seeAll
            val searchedText = bind.tilSearch.editText?.text.ifNull { "" }.toString().uppercase()
            seeAll.ifTrue {
                adapter?.setItemList(
                    Locale.getISOLanguages().map { Locale.forLanguageTag(it) }.filter {
                        it.displayLanguage.uppercase().contains(searchedText)
                    }
                )
                bind.txtType.text = requireContext().getText(R.string.label_all)

            }.ifFalse {
                adapter?.setItemList(
                    list.filter {
                        it.displayLanguage.uppercase().contains(searchedText)
                    }
                )
                bind.txtType.text = requireContext().getText(R.string.label_recommend)
                bind.btnSeeAll.setText(R.string.see_all)
            }
        }

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        lifecycleScope.launchWhenStarted {
            delay(300)
            bind.rvCountries.adapter = adapter
        }

        bind.tilSearch.editText?.addTextChangedListener(searchWatcher)
    }

    private fun createSearchWatcher(): TextWatcher = ShortTextWatcher {
        val searchedText = bind.tilSearch.editText?.text.ifNull { "" }.toString().uppercase()
        seeAll.ifTrue {
            adapter?.setItemList(
                Locale.getISOLanguages().map { Locale.forLanguageTag(it) }.filter {
                    it.displayLanguage.uppercase().contains(searchedText)
                }
            )
        }.ifFalse {
            adapter?.setItemList(
                list.filter {
                    it.displayLanguage.uppercase().contains(searchedText)
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bind.tilSearch.editText?.removeTextChangedListener(searchWatcher)
        adapter?.onDestroy()
        adapter = null
        _bind = null
    }
}
