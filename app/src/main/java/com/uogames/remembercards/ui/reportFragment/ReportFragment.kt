package com.uogames.remembercards.ui.reportFragment

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentReportBinding
import com.uogames.remembercards.utils.ShortTextWatcher
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observe
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import java.util.*
import javax.inject.Inject

class ReportFragment : DaggerFragment() {

    companion object {
        class Types {
            val PHRASE = 0
            val CARD = 1
            val MODULE = 2
        }

        const val TYPE = "ReportFragment_TYPE"
        const val ITEM_ID = "ReportFragment_ITEM_ID"
        const val CLAIMANT = "ReportFragment_CLAIMANT"
        const val ACCUSED = "ReportFragment_ACCUSED"
        val types = Types()

    }

    @Inject
    lateinit var model: ReportViewModel

    @Inject
    lateinit var globalModel: GlobalViewModel

    private var _bind: FragmentReportBinding? = null
    private val bind get() = _bind!!

    private var observers: Job? = null
    private var watcher: TextWatcher? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = FragmentReportBinding.inflate(inflater, container, false)
        return _bind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        globalModel.shouldReset.ifTrue { model.reset() }

        model.type.value = arguments?.getInt(TYPE)
        model.itemId.value = arguments?.get(ITEM_ID) as? UUID       // UUID.fromString(arguments?.getString(ITEM_ID))
        model.claimant.value = arguments?.getString(CLAIMANT)
        model.accused.value = arguments?.getString(ACCUSED)

        watcher = textWatcher()
        bind.tilMessage.editText?.setText(model.message.value)
        bind.tilMessage.editText?.addTextChangedListener(watcher)

        bind.btnSend.setOnClickListener {
            model.send { if (it) findNavController().popBackStack() }
        }
        bind.btnBack.setOnClickListener { findNavController().popBackStack() }

        observers = lifecycleScope.launchWhenStarted {
            model.type.observe(this) {
                when (it) {
                    0 -> bind.tvType.text = requireContext().getText(R.string.phrase_id)
                    1 -> bind.tvType.text = requireContext().getText(R.string.card_id)
                    2 -> bind.tvType.text = requireContext().getText(R.string.module_id)
                }
            }
            model.itemId.observe(this) {
                bind.tvItemId.text = it?.toString().orEmpty()
            }
        }

    }

    private fun textWatcher(): ShortTextWatcher = ShortTextWatcher {
        model.message.value = it?.toString()
    }

    override fun onDestroyView() {
        observers?.cancel()
        bind.tilMessage.editText?.removeTextChangedListener(watcher)
        watcher = null
        _bind = null
        super.onDestroyView()
    }


}