package com.uogames.remembercards.ui.module.editModuleFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditModuleBinding
import com.uogames.remembercards.ui.card.choiceCardFragment.ChoiceCardFragment
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifTrue
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EditModuleFragment : DaggerFragment() {

    companion object {
        private const val CARD_CALL_TAG = "EditModuleFragment_CARD_CALL_TAG"
        const val MODULE_ID = "EditModuleFragment_MODULE_ID"
    }

    @Inject
    lateinit var editModuleViewModel: EditModuleViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel


    private var _bind: FragmentEditModuleBinding? = null
    private val bind get() = _bind!!

    private var moduleCardObserver: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (_bind == null) _bind = FragmentEditModuleBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalViewModel.shouldReset.ifTrue { editModuleViewModel.reset() }

        val id = arguments?.getInt(MODULE_ID)
        id?.let {
            setData(it)
        }.ifNull {
            findNavController().popBackStack()
        }
    }

    private fun setData(id: Int) {
        setFragmentResultListener(CARD_CALL_TAG) { _, b ->
            lifecycleScope.launchWhenStarted {
                val cardID = b.getInt("ID")
                val card = editModuleViewModel.getCard(cardID).first().ifNull { return@launchWhenStarted }
                editModuleViewModel.addModuleCard(id, card) {}
            }
        }

        editModuleViewModel.moduleID.value = id
        bind.btnAdd.setOnClickListener {
            navigate(R.id.choiceCardFragment, bundleOf(ChoiceCardFragment.TAG to CARD_CALL_TAG))
        }
        bind.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        bind.btnDelete.setOnClickListener {
            Toast.makeText(requireContext(), requireContext().getText(R.string.press_to_delete), Toast.LENGTH_SHORT).show()
        }
        bind.btnDelete.setOnLongClickListener {
            lifecycleScope.launchWhenStarted {
                editModuleViewModel.module.first()?.let {
                    editModuleViewModel.delete(it)
                }
                findNavController().popBackStack()
            }
            true
        }

        lifecycleScope.launchWhenStarted {
            delay(250)
            bind.rvCards.adapter = editModuleViewModel.adapter
        }
    }



    override fun onDestroyView() {
        moduleCardObserver?.cancel()
        bind.rvCards.adapter = null
        _bind = null
        super.onDestroyView()
    }
}
