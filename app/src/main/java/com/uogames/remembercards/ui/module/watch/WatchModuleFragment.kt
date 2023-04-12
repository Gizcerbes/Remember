package com.uogames.remembercards.ui.module.watch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uogames.dto.local.LocalModule
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentWatchModuleBinding
import com.uogames.remembercards.ui.module.editModuleFragment.EditModuleFragment
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observe
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class WatchModuleFragment : DaggerFragment() {
    enum class ModuleType {
        LOCAL, GLOBAL
    }

    companion object {
        const val MODULE_TYPE = "WatchModuleFragment_MODULE_TYPE"
        const val MODULE_ID = "WatchModuleFragment_MODULE_ID"
    }

    @Inject
    lateinit var model: WatchModuleViewModel

    private var _bind: FragmentWatchModuleBinding? = null
    private val bind get() = _bind!!

    private val editCall = { id: Int -> navigateToEdit(id) }

    private var observer: Job? = null

    private val startAction: () -> Unit = {
        bind.btnDownload.isEnabled = false
        bind.ivBtnDownload.isEnabled = false
        bind.lpiIndicator.visibility = View.VISIBLE
    }

    private val endAction: (String) -> Unit = {
        lifecycleScope.launchWhenStarted {
            bind.btnDownload.isEnabled = true
            bind.ivBtnDownload.isEnabled = true
            bind.lpiIndicator.visibility = View.GONE
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = FragmentWatchModuleBinding.inflate(inflater, container, false)
        return _bind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.shouldReset.ifTrue { model.reset() }

        model.type.value = (arguments?.get(MODULE_TYPE) as? ModuleType) == ModuleType.GLOBAL
        model.localID.value = arguments?.get(MODULE_ID) as? Int
        model.globalID.value = arguments?.get(MODULE_ID) as? UUID

        model.apply { if (localID.value == null && globalID.value == null) findNavController().popBackStack() }

        bind.lpiIndicator.visibility = View.GONE

        bind.btnBack.setOnClickListener { findNavController().popBackStack() }

        lifecycleScope.launchWhenStarted {
            delay(250)
            bind.recycler.adapter = model.adapter
            model.update()
        }

        observer = lifecycleScope.launch {

            model.size.observe(this) {
                bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
            }
            model.localModule.observe(this) { model ->
                model?.let { bind.txtTopName.text = it.module.name }
                bind.btnEdit.visibility = if (model == null) View.GONE else View.VISIBLE
                bind.btnEdit.setOnClickListener { model?.module?.id?.let { editCall(it) } }
            }
            model.globalModule.observe(this) { m ->
                bind.btnDownload.visibility = if (m == null) View.GONE else View.VISIBLE
                if (m != null){
                    bind.txtTopName.text = m.module.name
                    model.setDownloadAction(m.module, endAction).ifTrue(startAction)
                    bind.btnDownload.setOnClickListener {
                        startAction()
                        model.download(m.module, endAction)
                    }
                }
            }
        }

    }

    private fun navigateToEdit(moduleID: Int) = navigate(
        R.id.editModuleFragment,
        bundleOf(EditModuleFragment.MODULE_ID to moduleID)
    )

    override fun onDestroyView() {
        super.onDestroyView()
        bind.recycler.adapter = null
    }


}