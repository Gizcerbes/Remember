package com.uogames.remembercards.ui.rootFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.utils.ifNull
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RootFragment : DaggerFragment() {

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_root, container, false)
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launchWhenStarted {
            delay(1000)
            globalViewModel.getUserName().first()?.let {
                val graph = findNavController().navInflater.inflate(R.navigation.nav_graph).apply { setStartDestination(R.id.mainNaviFragment) }
                findNavController().setGraph(graph, null)
            }.ifNull {
                val graph = findNavController().navInflater.inflate(R.navigation.nav_graph).apply { setStartDestination(R.id.registerFragment) }
                findNavController().setGraph(graph, null)
            }

        }

    }
}
