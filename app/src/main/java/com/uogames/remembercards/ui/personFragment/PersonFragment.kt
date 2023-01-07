package com.uogames.remembercards.ui.personFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.flags.Countries
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentPersonBinding
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.zip.CRC32
import javax.inject.Inject

class PersonFragment : DaggerFragment() {

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    private var _bind: FragmentPersonBinding? = null
    private val bind get() = _bind!!
    private var closed = false

    private var observer: Job? = null

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { _ ->
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_bind == null) _bind = FragmentPersonBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (closed) return

        auth = Firebase.auth
        auth.currentUser?.reload()

        auth.currentUser?.let {
            bind.txtStatus.text = requireContext().getText(R.string.connected)
            bind.txtStatus.setTextColor(requireContext().getColor(R.color.btn_positive))
            val src = CRC32().apply { update((it.displayName + it.uid).toByteArray()) }
            bind.txtGlobalName.text = it.displayName + "#" + src.value
        }.ifNull {
            bind.txtStatus.text = requireContext().getText(R.string.disconnected)
            bind.txtStatus.setTextColor(requireContext().getColor(R.color.btn_negative))
        }
        observer = createObservers()

        bind.btnSetting.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.settingFragment)
        }
    }

    private fun createObservers(): Job = lifecycleScope.launchWhenStarted {
        globalViewModel.getUserName().observe(this){
            bind.txtPersonName.text = it.orEmpty()
        }

        globalViewModel.getUserNativeCountry().observeNotNull(this){
            bind.imgFlag.setImageResource(Countries.valueOf(it).res)
        }

        globalViewModel.getCountPhrases().observeNotNull(this) {
            bind.txtPhrasesCount.text = it.toString()
        }

       globalViewModel.getCountCards().observeNotNull(this) {
            bind.txtCardsCount.text = it.toString()
        }

        globalViewModel.getCountModules().observeNotNull(this) {
            bind.txtModulesCount.text = it.toString()
        }

        globalViewModel.getGameYesOrNotGameCount().observe(this) {
            bind.txtYesOrNoCount.text = it.ifNullOrEmpty { "0" }
        }
    }

    private fun aut() {
        if (auth.currentUser != null) {
            Toast.makeText(requireContext(), "${auth.currentUser}", Toast.LENGTH_SHORT).show()
            AuthUI.getInstance().signOut(requireContext())
            return
        }

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().setSignInOptions(
                GoogleSignInOptions.Builder().requestIdToken(
                    "572904362912-bm9tiqhnj5gsjmjvudkc499be6rena5r.apps.googleusercontent.com"
                ).requestEmail().build()
            ).build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observer?.cancel()
        _bind = null
        closed = true
    }
}
