package com.uogames.remembercards.ui.editModuleFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditModuleBinding
import com.uogames.remembercards.ui.choiceCardFragment.ChoiceCardFragment
import com.uogames.remembercards.ui.editCardFragment.EditCardFragment
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
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

	private val adapter by lazy {  EditModuleAdapter(editModuleViewModel, player, lifecycleScope)}

	private lateinit var bind: FragmentEditModuleBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentEditModuleBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		val id = arguments?.getInt(MODULE_ID)

		id?.let {
			setData(it)
		}.ifNull {
			findNavController().popBackStack()
		}

	}

	private fun setData(id: Int) {
		lifecycleScope.launchWhenStarted {
			val cardID = globalViewModel.getFlow(CARD_CALL_TAG).first().ifNull { return@launchWhenStarted }.toInt()
			globalViewModel.saveData(CARD_CALL_TAG, null)
			val card = editModuleViewModel.getCard(cardID).first().ifNull { return@launchWhenStarted }
			editModuleViewModel.addModuleCard(id, card) {

			}
		}
		editModuleViewModel.moduleID.value = id
		bind.btnAdd.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.choiceCardFragment, Bundle().apply {
				putString(ChoiceCardFragment.TAG, CARD_CALL_TAG)
			})
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

		editModuleViewModel.moduleCardsList.observeWhenStarted(lifecycleScope) {
			adapter.setListItems(it)
		}
		bind.rvCards.adapter = adapter


	}


}