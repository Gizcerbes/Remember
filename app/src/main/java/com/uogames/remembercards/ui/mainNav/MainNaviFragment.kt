package com.uogames.remembercards.ui.mainNav

import android.graphics.Rect
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
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
        TransitionManager.beginDelayedTransition(bind.layout, AutoTransition())

        navigationViewModel.id.value.let { if (it != -1) bind.bottomNavigation.selectedItemId = it }

        navigationViewModel.isShowKey.onEach {
            bind.bottomNavigation.visibility = if (it) View.GONE else View.VISIBLE
        }.launchIn(lifecycleScope)

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            Log.e("BOTTOM", "onViewCreated: ${r.bottom / view.rootView.height.toDouble()}", )
            navigationViewModel.setShowKeyboard(r.bottom / view.rootView.height.toDouble() < 0.8)
        }

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