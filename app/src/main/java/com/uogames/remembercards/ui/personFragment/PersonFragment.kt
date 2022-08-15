package com.uogames.remembercards.ui.personFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.flags.Countries
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentPersonBinding
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifNullOrEmpty
import com.uogames.remembercards.utils.observeWhenStarted
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

	private var countPhrasesObserver: Job? = null
	private var countCardsObserver: Job? = null
	private var countModulesObserver: Job? = null
	private var gameYesOrNotCountObserver: Job? = null

	private val signInLauncher = registerForActivityResult(
		FirebaseAuthUIActivityResultContract()
	) { res ->

	}

	private lateinit var auth: FirebaseAuth

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (_bind == null) _bind = FragmentPersonBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		auth = Firebase.auth
		auth.currentUser?.reload()

		auth.currentUser?.let {
			bind.txtStatus.text = "Connected"
			bind.txtStatus.setTextColor(requireContext().getColor(R.color.btn_positive))
			val src = CRC32().apply { update((it.displayName + it.uid).toByteArray()) }
			bind.txtGlobalName.text = it.displayName + "#" + src.value
		}.ifNull {
			bind.txtStatus.text = "Disconnected"
			bind.txtStatus.setTextColor(requireContext().getColor(R.color.btn_negative))
		}

		lifecycleScope.launch {
			bind.txtPersonName.text = globalViewModel.getUserName().first().orEmpty()
			globalViewModel.getUserNativeCountry().first()?.let {
				bind.imgFlag.setImageResource(Countries.valueOf(it).res)
			}
		}

		countPhrasesObserver = globalViewModel.getCountPhrases().observeWhenStarted(lifecycleScope) {
			bind.txtPhrasesCount.text = it.toString()
		}

		countCardsObserver = globalViewModel.getCountCards().observeWhenStarted(lifecycleScope) {
			bind.txtCardsCount.text = it.toString()
		}

		countModulesObserver = globalViewModel.getCountModules().observeWhenStarted(lifecycleScope) {
			bind.txtModulesCount.text = it.toString()
		}

		gameYesOrNotCountObserver = globalViewModel.getGameYesOrNotGameCount().observeWhenStarted(lifecycleScope) {
			bind.txtYesOrNoCount.text = it.ifNullOrEmpty { "0" }
		}

		bind.btnSetting.setOnClickListener {
			aut()
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
			AuthUI.IdpConfig.EmailBuilder().build(),
		)

		val signInIntent = AuthUI.getInstance()
			.createSignInIntentBuilder()
			.setAvailableProviders(providers)
			.build()

		signInLauncher.launch(signInIntent)

	}

	override fun onDestroyView() {
		super.onDestroyView()
		countPhrasesObserver?.cancel()
		countCardsObserver?.cancel()
		countModulesObserver?.cancel()
		gameYesOrNotCountObserver?.cancel()
		_bind = null
	}

}