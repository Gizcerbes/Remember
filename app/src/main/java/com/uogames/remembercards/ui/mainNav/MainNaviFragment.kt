package com.uogames.remembercards.ui.mainNav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentMainNaviBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MainNaviFragment : DaggerFragment() {

    @Inject
    lateinit var navigationViewModel: NavigationViewModel

    private lateinit var bind: FragmentMainNaviBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentMainNaviBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigationViewModel.id.value.let { if (it != -1) bind.bottomNavigation.selectedItemId = it }
    }

    override fun onStart() {
        super.onStart()
        val nav = bind.navFragment.findNavController()

        bind.bottomNavigation.setOnItemSelectedListener {
            nav.navigate(it.itemId)
            navigationViewModel.setId(it.itemId)
        }

    }


}