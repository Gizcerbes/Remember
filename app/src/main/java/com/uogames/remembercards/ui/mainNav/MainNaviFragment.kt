package com.uogames.remembercards.ui.mainNav

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentMainNaviBinding
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


class MainNaviFragment : DaggerFragment() {

	@Inject
	lateinit var navigationViewModel: NavigationViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private var _bind: FragmentMainNaviBinding? = null
	private val bind get() = _bind!!

	private var keyObserver: Job? = null

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		if (_bind == null)_bind = FragmentMainNaviBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		navigationViewModel.id.value.let { if (it != -1) bind.bottomNavigation.selectedItemId = it }
		keyObserver = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
			bind.bottomNavigation.visibility = if (it) View.GONE else View.VISIBLE
		}
	}

	override fun onStart() {
		super.onStart()
		val nav = bind.navFragment.findNavController()
		if (navigationViewModel.id.value != -1) nav.navigate(navigationViewModel.id.value)
		bind.bottomNavigation.setOnItemSelectedListener {
			if (it.itemId != navigationViewModel.id.value) nav.navigate(it.itemId,null, navOptions { anim { enter = R.anim.show }})
			navigationViewModel.setId(it.itemId)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		keyObserver?.cancel()
		_bind = null
	}
}