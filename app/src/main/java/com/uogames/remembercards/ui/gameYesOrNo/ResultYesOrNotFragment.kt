package com.uogames.remembercards.ui.gameYesOrNo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentResultMistakesBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ResultYesOrNotFragment : DaggerFragment() {

	@Inject
	lateinit var model: GameYesOrNotViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private lateinit var bind: FragmentResultMistakesBinding

	private val adapter : GameYesOrNoAdapter by lazy { GameYesOrNoAdapter(model,lifecycleScope) }

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		bind = FragmentResultMistakesBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		bind.recycler.adapter = adapter

		bind.btnClose.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
		}

		bind.btnRepeat.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.gameYesOrNotFragment,
				null, NavOptions.Builder().setPopUpTo(R.id.mainNaviFragment, false).build())
		}

		globalViewModel.addGameYesOrNoGameCount()

	}

}