package com.uogames.remembercards.ui.games.notification

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentNotificationWorkerBinding
import com.uogames.remembercards.ui.module.choiceModuleDialog.ChoiceModuleDialog
import com.uogames.remembercards.ui.module.choiceModuleDialog.ChoiceModuleViewModel
import com.uogames.remembercards.ui.module.library.LibraryViewModel
import com.uogames.remembercards.utils.Permission
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observe
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationWorkerFragment : DaggerFragment() {

    companion object {
        const val MODULE_ID = "NotificationWorkerFragment_MODULE_ID"
    }

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var model: NotificationViewModel

    @Inject
    lateinit var libraryViewModel: LibraryViewModel

    @Inject
    lateinit var choiceModuleViewModel: ChoiceModuleViewModel

    private var _bind: FragmentNotificationWorkerBinding? = null
    private val bind get() = _bind!!

    private var observers: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = FragmentNotificationWorkerBinding.inflate(inflater, container, false)
        return _bind?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moduleID = arguments?.get(MODULE_ID) as? Int

        model.moduleID.value = moduleID

        bind.btnStart.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Permission.POST_NOTIFICATIONS.requestPermission(requireActivity()) { it.ifTrue { startWork() } }
            } else {
                startWork()
            }
        }

        bind.btnPrepareModule.setOnClickListener {
            val dialog = ChoiceModuleDialog(choiceModuleViewModel) {
                model.moduleID.value = when (it) {
                    is ChoiceModuleViewModel.ChoiceAll -> null
                    is ChoiceModuleViewModel.ChoiceLocalModule -> it.view.id
                }
            }
            dialog.show(requireActivity().supportFragmentManager, ChoiceModuleDialog.TAG)
        }

        bind.btnStop.setOnClickListener {
            workManager.cancelUniqueWork(NotificationWorker.WORKER_UNIQUE_NAME)
        }
        bind.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        observers = lifecycleScope.launchWhenStarted {

            model.notificationModuleID.observe(this) {
                runCatching {
                    val id = it?.toInt()
                    if (id != null) {
                        val module = model.getModuleByIdAsync(id).await()
                        bind.tvSelectedModule.text = module?.name
                    } else {
                        bind.tvSelectedModule.text = requireContext().getText(R.string.all_cards)
                    }
                }
            }
            model.moduleID.observe(this) { id ->
                runCatching {
                    if (id != null) {
                        val module = model.getModuleByIdAsync(id).await()
                        bind.tvSelectedPrepareModule.text = module?.name
                    } else {
                        bind.tvSelectedPrepareModule.text = requireContext().getText(R.string.all_cards)
                    }
                }
            }
        }
    }

    private fun startWork() {
        model.saveSelectedModule()
        val work = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES).build()
        workManager.enqueueUniquePeriodicWork(NotificationWorker.WORKER_UNIQUE_NAME, ExistingPeriodicWorkPolicy.UPDATE, work)
    }

    override fun onStart() {
        super.onStart()
        workManager.getWorkInfosForUniqueWorkLiveData(NotificationWorker.WORKER_UNIQUE_NAME).observe(requireActivity()) {
            if (!isAdded) return@observe
            try {
                if (it.isEmpty()) {
                    bind.tvStatus.text = requireContext().getText(R.string.status_stopped)
                    bind.btnStop.visibility = View.GONE
                } else {
                    val last = it.last()
                    val type = when (last.state) {
                        WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED -> true
                        else -> false
                    }
                    bind.tvStatus.text = requireActivity().getText(if (type) R.string.status_working else R.string.status_stopped)

                    bind.btnStart.visibility = if (type) View.GONE else View.VISIBLE
                    bind.btnStop.visibility = if (type) View.VISIBLE else View.GONE

                }
            } catch (e: Throwable) {
                bind.tvStatus.text = requireActivity().getText(R.string.status_stopped)
                bind.btnStop.visibility = View.GONE
            }
        }

    }

    override fun onStop() {
        super.onStop()
        workManager.getWorkInfosForUniqueWorkLiveData(NotificationWorker.WORKER_UNIQUE_NAME).removeObservers(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observers?.cancel()
    }

}