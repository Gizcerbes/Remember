package com.uogames.remembercards.ui.module.choiceModuleDialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.uogames.remembercards.databinding.FragmentChoiceModuleBinding
import com.uogames.remembercards.utils.ObservedDialog
import com.uogames.remembercards.utils.ShortTextWatcher
import kotlinx.coroutines.delay

class ChoiceModuleDialog(private val libraryViewModel: ChoiceModuleViewModel, call: (ChoiceModuleViewModel.Choice) -> Unit) :
    ObservedDialog<ChoiceModuleViewModel.Choice>(call) {

    companion object {
        const val TAG = "ChoiceModuleDialog_TAG"
    }

    private var _bind: FragmentChoiceModuleBinding? = null
    private val bind get() = _bind!!

    private val select = { c: ChoiceModuleViewModel.Choice ->
        setData(c)
        dismiss()
    }

    private val textWatcher = ShortTextWatcher{
        libraryViewModel.like.value = it
        libraryViewModel.updateSize()
        Log.e("TAG", ": $it", )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_bind == null) _bind = FragmentChoiceModuleBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        bind.tilSearch.editText?.addTextChangedListener(textWatcher)

        lifecycleScope.launchWhenStarted {
            //delay(250)
            bind.rvModules.adapter = libraryViewModel.adapter
            libraryViewModel.updateSize()
            libraryViewModel.addChoiceAction(select)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        libraryViewModel.removeChoiceAction(select)
        bind.tilSearch.editText?.removeTextChangedListener(textWatcher)
        bind.rvModules.adapter = null
        _bind = null
    }
}
