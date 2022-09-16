package com.uogames.remembercards.ui.settingFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.flags.Countries
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.ComponentTextImputLayoutBinding
import com.uogames.remembercards.databinding.FragmentSettingsBinding
import com.uogames.remembercards.ui.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observeNotNull
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import java.util.zip.CRC32
import javax.inject.Inject

class SettingFragment : DaggerFragment() {

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private var _bind: FragmentSettingsBinding? = null
	private val bind get() = _bind!!

	private var observeJob: Job? = null
	private val auth = Firebase.auth
	private val signInLauncher = registerForActivityResult(
		FirebaseAuthUIActivityResultContract()
	) {
		setUser(auth.currentUser)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (_bind == null) _bind = FragmentSettingsBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		observeJob = createObservers()
		setUser(auth.currentUser)

		bind.btnSignIn.setOnClickListener { aut() }

		bind.btnChoiceLanguage.setOnClickListener {
			val dialog = ChoiceCountryDialog {
				globalViewModel.saveUserNativeCountry(it.toString())
			}
			dialog.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
		}
		bind.btnBack.setOnClickListener {
			findNavController().popBackStack()
		}
		bind.btnChangeName.setOnClickListener {

			val til = ComponentTextImputLayoutBinding.inflate(LayoutInflater.from(requireContext()))
			til.root.editText?.setText(bind.txtUserName.text)

			MaterialAlertDialogBuilder(requireContext())
				.setView(til.root)
				.setNegativeButton(R.string.close) { _, _ -> }
				.setPositiveButton(R.string.accept) { _, _ ->
					globalViewModel.saveUserName(til.root.editText?.text.toString())
				}
				.show()
		}
	}

	@SuppressLint("ResourceType")
	private fun createObservers(): Job = lifecycleScope.launchWhenStarted {
		globalViewModel.getUserName().observeNotNull(this) {
			bind.txtUserName.text = it
		}
		globalViewModel.getUserNativeCountry().observeNotNull(this) {
			val country = Countries.valueOf(it)
			bind.rlLanguageNotEmpty.visibility = View.VISIBLE
			bind.imgFlag.setImageResource(country.res)
			bind.llLanguages.removeAllViews()
			country.country.forEach { countryText ->
				val tv = TextView(requireContext())
				tv.setTextAppearance(R.attr.textAppearanceBody2)
				tv.text = countryText.value
				bind.llLanguages.addView(tv)
			}
		}
	}

	private fun setUser(user: FirebaseUser?) {
		user?.let {
			bind.txtLogIn.setTextColor(bind.labelLogIn.currentTextColor)
			val src = CRC32().apply { update((it.displayName + it.uid).toByteArray()) }
			bind.txtLogIn.text = "${it.displayName}#${src.value}"
			bind.labelLogIn.text = requireContext().getText(R.string.log_out)
		}.ifNull {
			bind.txtLogIn.text = requireContext().getText(R.string.disconnected)
			bind.txtLogIn.setTextColor(requireContext().getColor(R.color.btn_negative))
			bind.labelLogIn.text = requireContext().getText(R.string.log_in)
		}
	}

	private fun aut() {
		if (auth.currentUser != null) {
			AuthUI.getInstance().signOut(requireContext())
			setUser(null)
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


	override fun onDestroy() {
		super.onDestroy()
		observeJob?.cancel()
	}

}