package com.uogames.remembercards.ui.rootFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class RootFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_root, container, false)
	}

	override fun onStart() {
		super.onStart()

		lifecycleScope.launchWhenStarted {
			delay(500)
			GlobalViewModel(DataProvider.get(requireContext())).getUserName().first()?.let {
				val graph = findNavController().navInflater.inflate(R.navigation.nav_graph).apply { setStartDestination(R.id.mainNaviFragment) }
				findNavController().setGraph(graph, null)
			}.ifNull {
				val graph = findNavController().navInflater.inflate(R.navigation.nav_graph).apply { setStartDestination(R.id.registerFragment) }
				findNavController().setGraph(graph, null)
			}
		}


	}


}