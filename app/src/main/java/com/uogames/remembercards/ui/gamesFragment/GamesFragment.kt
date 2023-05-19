package com.uogames.remembercards.ui.gamesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentGamesBinding
import com.uogames.remembercards.ui.module.choiceModuleDialog.ChoiceModuleDialog
import com.uogames.remembercards.ui.games.gameYesOrNo.GameYesOrNotFragment
import com.uogames.remembercards.ui.games.gameYesOrNo.GameYesOrNotViewModel
import com.uogames.remembercards.ui.games.notification.NotificationWorkerFragment
import com.uogames.remembercards.ui.games.watchCard.WatchCardFragment
import com.uogames.remembercards.ui.module.choiceModuleDialog.ChoiceModuleViewModel
import com.uogames.remembercards.ui.module.library.LibraryViewModel
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import javax.inject.Inject

class GamesFragment : DaggerFragment() {

    @Inject
    lateinit var gamesViewModel: GamesViewModel

    @Inject
    lateinit var gameYesOrNotViewModel: GameYesOrNotViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var libraryViewModel: LibraryViewModel

    @Inject
    lateinit var choiceModuleViewModel: ChoiceModuleViewModel

    private var _bind: FragmentGamesBinding? = null
    private val bind get() = _bind!!

    private var selectedModuleObserver: Job? = null
    private var cardOwnerObserver: Job? = null
    private var countItemsObserver: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_bind == null) _bind = FragmentGamesBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bind.gameYesOrNot.visibility = View.GONE

        selectedModuleObserver = createSelectedModuleObserver()

        cardOwnerObserver = createOwnerObserver()

        countItemsObserver = createCountItemObserver()

        bind.gameYesOrNot.setOnClickListener {
            gamesViewModel.selectedModuleId.value?.let {
                navigate(
                    R.id.gameYesOrNotFragment,
                    bundleOf(GameYesOrNotFragment.MODULE_ID to it.id)
                )
            }.ifNull {
                navigate(R.id.gameYesOrNotFragment)
            }
        }


        bind.gameWatchCard.setOnClickListener {
            gamesViewModel.selectedModuleId.value?.let {
                navigate(
                    R.id.watchCardFragment,
                    bundleOf(WatchCardFragment.MODULE_ID to it.id)
                )
            }.ifNull {
                navigate(R.id.watchCardFragment)
            }
        }

        bind.notificationWorker.setOnClickListener {
            gamesViewModel.selectedModuleId.value?.let {
                navigate(
                    R.id.notificationWorkerFragment,
                    bundleOf(NotificationWorkerFragment.MODULE_ID to it.id)
                )
            }.ifNull {
                navigate(R.id.notificationWorkerFragment)
            }
        }

        bind.mcvSelectModule.setOnClickListener {
            val dialog = ChoiceModuleDialog(choiceModuleViewModel) {
                gamesViewModel.selectedModuleId.value = when (it) {
                    is ChoiceModuleViewModel.ChoiceAll -> null
                    is ChoiceModuleViewModel.ChoiceLocalModule -> it.view
                }
            }
            dialog.show(requireActivity().supportFragmentManager, ChoiceModuleDialog.TAG)
        }

        bind.btnClear.setOnClickListener {
            gamesViewModel.selectedModuleId.value = null
        }
    }

    private fun createSelectedModuleObserver() = gamesViewModel.selectedModuleId.observeWhenStarted(lifecycleScope) { module ->
        module?.let {
            bind.txtName.text = module.name
            bind.btnClear.visibility = View.VISIBLE
        }.ifNull {
            bind.txtName.text = requireContext().getText(R.string.all_cards)
            bind.txtLikes.visibility = View.GONE
            bind.imgThumb.visibility = View.GONE
            bind.btnClear.visibility = View.GONE
        }
    }

    private fun createOwnerObserver() = gamesViewModel.cardOwner.observeWhenStarted(lifecycleScope) {
        bind.txtOwner.text = it
    }

    private fun createCountItemObserver() = gamesViewModel.countItems.observeWhenStarted(lifecycleScope) {
        bind.txtCountItems.text = requireContext().getString(R.string.count_items).replace("||COUNT||", it.toString())
        if (it > 2) bind.gameYesOrNot.visibility = View.VISIBLE
        else bind.gameYesOrNot.visibility = View.GONE

        if (it > 0) bind.gameWatchCard.visibility = View.VISIBLE
        else bind.gameWatchCard.visibility = View.GONE

        bind.notificationWorker.visibility = if (it > 0) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        selectedModuleObserver?.cancel()
        cardOwnerObserver?.cancel()
        countItemsObserver?.cancel()
        _bind = null
    }
}
