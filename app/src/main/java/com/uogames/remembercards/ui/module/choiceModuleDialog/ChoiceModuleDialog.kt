package com.uogames.remembercards.ui.module.choiceModuleDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleView
import com.uogames.remembercards.databinding.FragmentChoiceModuleBinding
import com.uogames.remembercards.ui.module.library.LibraryViewModel
import com.uogames.remembercards.utils.ObservedDialog
import kotlinx.coroutines.delay

class ChoiceModuleDialog(private val libraryViewModel: LibraryViewModel, call: (LocalModuleView) -> Unit) : ObservedDialog<LocalModuleView>(call) {

    companion object {
        const val TAG = "ChoiceModuleDialog_TAG"
    }

    private var _bind: FragmentChoiceModuleBinding? = null
    private val bind get() = _bind!!

    private var adapter: ChoiceModuleAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_bind == null) _bind = FragmentChoiceModuleBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChoiceModuleAdapter(libraryViewModel) {
            setData(it)
            dismiss()
        }

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        lifecycleScope.launchWhenCreated {
            delay(300)
            bind.rvModules.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _bind = null
    }
}
