package com.uogames.remembercards.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uogames.remembercards.databinding.DialogThemeChoiceBinding
import com.uogames.remembercards.utils.ObservedDialog

class ChoiceThemeDialog(choice: (Int) -> Unit) : ObservedDialog<Int>(choice) {

    companion object {
        const val TAG = "CHOICE_THEME"
    }

    private var _bind: DialogThemeChoiceBinding? = null
    private val bind get() = _bind!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = DialogThemeChoiceBinding.inflate(inflater, container, false)
        return _bind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        bind.btnSystem.setOnClickListener {
            dismiss()
            setData(0)
        }
        bind.btnDay.setOnClickListener {
            dismiss()
            setData(1)
        }
        bind.btnNight.setOnClickListener {
            dismiss()
            setData(2)
        }

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    }


}