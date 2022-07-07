package com.uogames.remembercards.ui.gamesFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
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
import kotlinx.coroutines.flow.first
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

	private lateinit var bind: FragmentGamesBinding

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		bind = FragmentGamesBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val nav = requireActivity().findNavController(R.id.nav_host_fragment)

		bind.gameYesOrNot.visibility = View.GONE

		gamesViewModel.selectedModule.observeWhenStarted(lifecycleScope) { module ->
			module?.let {
				bind.txtName.text = module.name
				bind.txtLikes.visibility = View.VISIBLE
				bind.imgThumb.visibility = View.VISIBLE
				bind.btnClear.visibility = View.VISIBLE
				bind.txtLikes.text = "${(module.like / (module.like + module.dislike).toDouble() * 100).toInt()}%"
			}.ifNull {
				bind.txtName.text = requireContext().getText(R.string.all_cards)
				bind.txtLikes.visibility = View.GONE
				bind.imgThumb.visibility = View.GONE
				bind.btnClear.visibility = View.GONE
			}
		}

		gamesViewModel.cardOwner.observeWhenStarted(lifecycleScope) {
			bind.txtOwner.text = it
		}

		gamesViewModel.countItems.observeWhenStarted(lifecycleScope) {
			bind.txtCountItems.text = requireContext().getString(R.string.count_items).replace("||COUNT||", it.toString())
			if (it > 2) {
				bind.gameYesOrNot.visibility = View.VISIBLE
			} else {
				bind.gameYesOrNot.visibility = View.GONE
			}
		}

		bind.gameYesOrNot.setOnClickListener {
			gamesViewModel.selectedModule.value?.let {
				nav.navigate(R.id.gameYesOrNotFragment, Bundle().apply { putInt(GameYesOrNotFragment.MODULE_ID, it.id) })
			}.ifNull {
				nav.navigate(R.id.gameYesOrNotFragment)
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


}