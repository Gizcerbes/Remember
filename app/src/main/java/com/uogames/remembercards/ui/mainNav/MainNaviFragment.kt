package com.uogames.remembercards.ui.mainNav

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.databinding.FragmentMainNaviBinding
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


class MainNaviFragment : DaggerFragment() {

	@Inject
	lateinit var navigationViewModel: NavigationViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

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
		globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
			bind.bottomNavigation.visibility = if (it) View.GONE else View.VISIBLE
		}
	}

	override fun onStart() {
		super.onStart()
		val nav = bind.navFragment.findNavController()
		if (navigationViewModel.id.value != -1) nav.navigate(navigationViewModel.id.value)
		bind.bottomNavigation.setOnItemSelectedListener {
			if (it.itemId != navigationViewModel.id.value) nav.navigate(it.itemId)
			navigationViewModel.setId(it.itemId)
		}

	}
}