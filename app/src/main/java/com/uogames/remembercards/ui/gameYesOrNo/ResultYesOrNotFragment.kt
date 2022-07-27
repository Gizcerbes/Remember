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
import com.uogames.remembercards.utils.ifTrue
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ResultYesOrNotFragment : DaggerFragment() {

	@Inject
	lateinit var model: GameYesOrNotViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private var _bind: FragmentResultMistakesBinding? = null
	private val bind get() = _bind!!

	private var adapter : ResultGameYesOrNoAdapter?  = null

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		if (_bind == null) _bind = FragmentResultMistakesBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		adapter = ResultGameYesOrNoAdapter(model)

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

	override fun onDestroyView() {
		super.onDestroyView()
		adapter?.onDestroy()
		adapter = null
		_bind = null
	}

}