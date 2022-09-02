package com.uogames.remembercards

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.uogames.remembercards.utils.Permission
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Job
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    private var backStackObserver: Job? = null
    private var keyListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val view = findViewById<FragmentContainerView>(R.id.nav_host_fragment)
        keyListener = createKeyListener(view)
        view.viewTreeObserver.addOnGlobalLayoutListener(keyListener)

        val navController = findNavController(R.id.nav_host_fragment)
        backStackObserver = navController.currentBackStackEntryFlow.observeWhenStarted(lifecycleScope) {
            globalViewModel.setBackQueue(navController.backQueue)
        }
    }

    fun createKeyListener(view: View): ViewTreeObserver.OnGlobalLayoutListener {
        return ViewTreeObserver.OnGlobalLayoutListener { globalViewModel.setShowKeyboard(view) }
    }

    override fun onStop() {
        super.onStop()
        backStackObserver?.cancel()
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
// 		runBlocking { globalViewModel.clean().join() }
// 		cacheDir.listFiles()?.forEach {
// 			try {
// 				it.delete()
// 			} catch (e: Exception) {
// 				Log.e("CLEAN_CACHE_ERROR", "$e")
// 			}
// 		}
        globalViewModel.setBackQueue(null)
    }
}
