package com.uogames.remembercards.ui.module.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.DialogNewModuleBinding
import com.uogames.remembercards.utils.ObservedDialog

class DialogNewModule(call: (String) -> Unit) : ObservedDialog<String>(call) {

    companion object {
        const val TAG = "DialogNewModule_TAG"
    }

    private var _bind: DialogNewModuleBinding? = null
    private val bind get() = _bind!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (_bind == null) _bind = DialogNewModuleBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.btnAccept.setOnClickListener {
            val txt = bind.tilText.editText?.text?.toString().orEmpty()
            if (txt.isEmpty()){
                bind.tilText.error = context?.getString(R.string.module_name_error_empty)
            } else {
                setData(txt)
                dismiss()
            }
        }
        bind.btnClose.setOnClickListener {
            dismiss()
        }
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }
}
