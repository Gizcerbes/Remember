package com.uogames.remembercards.ui.choiceLanguageDialog

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.uogames.flags.Languages
import com.uogames.remembercards.databinding.FragmentChoicePhraseBinding
import com.uogames.remembercards.utils.ObservedDialog
import com.uogames.remembercards.utils.ShortTextWatcher

class ChoiceLanguageDialog(call: (Languages) -> Unit) : ObservedDialog<Languages>(call) {

    companion object {
        const val TAG = "CHOICE_LANGUAGE"
    }

    private var _bind: FragmentChoicePhraseBinding? = null
    private val bind get() = _bind!!

    private var imm: InputMethodManager? = null

    private var adapter: ChoiceLanguageAdapter? = null
    private val searchWatcher: TextWatcher = createSearchWatcher()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_bind == null) _bind = FragmentChoicePhraseBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        adapter = ChoiceLanguageAdapter(lifecycleScope) {
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            dismiss()
        }

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        bind.recycler.adapter = adapter

        bind.tilSearch.editText?.addTextChangedListener(searchWatcher)

    }

    private fun createSearchWatcher(): TextWatcher = ShortTextWatcher{

    }

    override fun onDestroyView() {
        super.onDestroyView()
        bind.tilSearch.editText?.removeTextChangedListener(searchWatcher)
        _bind = null
    }

}