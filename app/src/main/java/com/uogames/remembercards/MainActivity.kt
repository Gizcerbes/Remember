package com.uogames.remembercards

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.uogames.remembercards.MainActivity.Companion.findNavHostFragment
import com.uogames.remembercards.ui.editModuleFragment.EditModuleFragment
import com.uogames.remembercards.utils.Permission
import com.uogames.remembercards.utils.observe
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.Locale
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    private var keyListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var observers: Job? = null

    companion object {
        fun FragmentActivity.findNavHostFragment() : NavController{
           return findNavController(R.id.nav_host_fragment)
        }

        fun Fragment.findNavHostFragment(): NavController{
            return requireActivity().findNavHostFragment()
        }

        fun Fragment.navigate(@IdRes res: Int, bundle: Bundle? = null) {
            findNavHostFragment().navigate(
                res,
                bundle,
                navOptions {
                    anim {
                        enter = R.anim.from_bottom
                        exit = R.anim.hide
                        popEnter = R.anim.show
                        popExit = R.anim.to_bottom
                    }
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Locale.setDefault(Locale.ENGLISH)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

    }

    override fun onStart() {
        super.onStart()
        val view = findViewById<FragmentContainerView>(R.id.nav_host_fragment)
        keyListener = createKeyListener(view)
        view.viewTreeObserver.addOnGlobalLayoutListener(keyListener)

        val navController = findNavController(R.id.nav_host_fragment)
        observers = lifecycleScope.launchWhenStarted {
            navController.currentBackStackEntryFlow.observe(this){  globalViewModel.setBackQueue(navController.backQueue) }
            globalViewModel.screenMode.observe(this){
                if (AppCompatDelegate.getDefaultNightMode() == it) return@observe
               AppCompatDelegate.setDefaultNightMode(it)
            }
        }
    }

    fun createKeyListener(view: View): ViewTreeObserver.OnGlobalLayoutListener {
        return ViewTreeObserver.OnGlobalLayoutListener { globalViewModel.setShowKeyboard(view) }
    }

    override fun onStop() {
        super.onStop()
        observers?.cancel()
        val view = findViewById<FragmentContainerView>(R.id.nav_host_fragment)
        view.viewTreeObserver.removeOnGlobalLayoutListener(keyListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Permission.values()[requestCode].onRequestPermissionResult(grantResults)
    }


    override fun onDestroy() {
        super.onDestroy()
        globalViewModel.setBackQueue(null)
    }
}
