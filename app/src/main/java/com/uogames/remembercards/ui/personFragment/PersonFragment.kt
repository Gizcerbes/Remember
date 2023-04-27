package com.uogames.remembercards.ui.personFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.flags.Countries
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentPersonBinding
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import javax.inject.Inject

class PersonFragment : DaggerFragment() {

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    private var _bind: FragmentPersonBinding? = null
    private val bind get() = _bind!!

    private var observer: Job? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = FragmentPersonBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        auth = Firebase.auth
        auth.currentUser?.reload()

        auth.currentUser?.let {
            bind.txtStatus.text = requireContext().getText(R.string.connected)
            bind.txtStatus.setTextColor(requireContext().getColor(R.color.btn_positive))
            bind.txtGlobalName.text = UserGlobalName(globalViewModel.userName.value.orEmpty(), it.uid).userName
        }.ifNull {
            bind.txtStatus.text = requireContext().getText(R.string.disconnected)
            bind.txtStatus.setTextColor(requireContext().getColor(R.color.red))
        }
        observer = createObservers()

        bind.btnSetting.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.settingFragment)
        }

    }

    private fun createObservers(): Job = lifecycleScope.launchWhenStarted {

        globalViewModel.userName.observe(this) { bind.txtPersonName.text = it.orEmpty() }

        globalViewModel.nativeCountry.observeNotNull(this) { bind.imgFlag.setImageResource(Countries.valueOf(it).res) }

        globalViewModel.countPhrases.observe(this) { bind.txtPhrasesCount.text = it.toString() }

        globalViewModel.countCards.observe(this) { bind.txtCardsCount.text = it.toString() }

        globalViewModel.countModules.observe(this) { bind.txtModulesCount.text = it.toString() }

        globalViewModel.gameYesOrNotCount.observe(this) { bind.txtYesOrNoCount.text = it.ifNullOrEmpty { "0" } }

        globalViewModel.cardCountFree.observe(this) { bind.txtFreeCards.text = it.toString() }

        globalViewModel.phraseCountFree.observe(this) { bind.txtFreePhrases.text = it.toString() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observer?.cancel()
        _bind = null
    }
}
