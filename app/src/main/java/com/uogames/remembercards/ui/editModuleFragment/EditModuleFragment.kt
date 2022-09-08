package com.uogames.remembercards.ui.editModuleFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditModuleBinding
import com.uogames.remembercards.ui.choiceCardFragment.ChoiceCardFragment
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observeWhenStarted
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

    @Inject
    lateinit var player: ObservableMediaPlayer

    private var _bind: FragmentEditModuleBinding? = null
    private val bind get() = _bind!!

    private var adapter: EditModuleAdapter? = null

    private var moduleCardObserver: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (_bind == null) _bind = FragmentEditModuleBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalViewModel.shouldReset.ifTrue {
            editModuleViewModel.reset()
        }

        adapter = EditModuleAdapter(editModuleViewModel, player)
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
                Log.e("TAG", "cardID: $cardID", )
                val card = editModuleViewModel.getCard(cardID).first().ifNull { return@launchWhenStarted }
                Log.e("TAG", "card: $card", )
                editModuleViewModel.addModuleCard(id, card) {}
            }
        }

        editModuleViewModel.moduleID.value = id
        bind.btnAdd.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(
                R.id.choiceCardFragment,
                bundleOf(ChoiceCardFragment.TAG to CARD_CALL_TAG),
                navOptions {
                    anim {
                        enter = R.anim.from_bottom
                        exit = R.anim.hide
                        popEnter = R.anim.show
                        popExit = R.anim.to_bottom
                    }
                }
            )
        }
        bind.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        bind.btnDelete.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                editModuleViewModel.module.first()?.let {
                    editModuleViewModel.delete(it)
                }
                findNavController().popBackStack()
            }
        }

        moduleCardObserver = createModuleCardObserver()

        lifecycleScope.launchWhenStarted {
            delay(300)
            bind.rvCards.adapter = adapter
        }
    }

    private fun createModuleCardObserver(): Job = editModuleViewModel.moduleCardsList.observeWhenStarted(lifecycleScope) {
        adapter?.setListItems(it)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        moduleCardObserver?.cancel()
        adapter?.onDestroy()
        adapter = null
        _bind = null
    }
}
