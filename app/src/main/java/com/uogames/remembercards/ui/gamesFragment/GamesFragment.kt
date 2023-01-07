package com.uogames.remembercards.ui.gamesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentGamesBinding
import com.uogames.remembercards.ui.choiceModuleDialog.ChoiceModuleDialog
import com.uogames.remembercards.ui.gameYesOrNo.GameYesOrNotFragment
import com.uogames.remembercards.ui.gameYesOrNo.GameYesOrNotViewModel
import com.uogames.remembercards.ui.libraryFragment.LibraryViewModel
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

    private var _bind: FragmentGamesBinding? = null
    private val bind get() = _bind!!
    private var closed = false

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
        if (closed) return
        val nav = requireActivity().findNavController(R.id.nav_host_fragment)

        bind.gameYesOrNot.visibility = View.GONE

        selectedModuleObserver = createSelectedModuleObserver()

        cardOwnerObserver = createOwnerObserver()

        countItemsObserver = createCountItemObserver()

        bind.gameYesOrNot.setOnClickListener {
            gamesViewModel.selectedModule.value?.let {
                nav.navigate(
                    R.id.gameYesOrNotFragment,
                    Bundle().apply { putInt(GameYesOrNotFragment.MODULE_ID, it.id) },
                    navOptions {
                        anim {
                            enter = R.anim.from_bottom
                            exit = R.anim.hide
                            popEnter = R.anim.show
                            popExit = R.anim.to_bottom
                        }
                    }
                )
            }.ifNull {
                nav.navigate(
                    R.id.gameYesOrNotFragment,
                    null,
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
        }

        bind.mcvSelectModule.setOnClickListener {
            val dialog = ChoiceModuleDialog(libraryViewModel) {
                gamesViewModel.selectedModule.value = it
            }
            dialog.show(requireActivity().supportFragmentManager, ChoiceModuleDialog.TAG)
        }

        bind.btnClear.setOnClickListener {
            gamesViewModel.selectedModule.value = null
        }
    }

    private fun createSelectedModuleObserver() = gamesViewModel.selectedModule.observeWhenStarted(lifecycleScope) { module ->
        module?.let {
            bind.txtName.text = module.name
            //bind.txtLikes.visibility = View.VISIBLE
            //bind.imgThumb.visibility = View.VISIBLE
            bind.btnClear.visibility = View.VISIBLE
            //bind.txtLikes.text = "${(module.like / (module.like + module.dislike).toDouble() * 100).toInt()}%"
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
        if (it > 2) {
            bind.gameYesOrNot.visibility = View.VISIBLE
        } else {
            bind.gameYesOrNot.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        selectedModuleObserver?.cancel()
        cardOwnerObserver?.cancel()
        countItemsObserver?.cancel()
        _bind = null
        closed = true
    }
}
