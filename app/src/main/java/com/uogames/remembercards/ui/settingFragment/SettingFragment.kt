package com.uogames.remembercards.ui.settingFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.flags.Countries
import com.uogames.remembercards.BuildConfig
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.ComponentTextImputLayoutBinding
import com.uogames.remembercards.databinding.FragmentSettingsBinding
import com.uogames.remembercards.ui.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
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
        _bind = FragmentSettingsBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        observeJob = createObservers()
        setUser(auth.currentUser)
        bind.txtVersion.text = requireContext().getString(R.string.version).replace("|| V ||", BuildConfig.VERSION_NAME)

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
                    val txt = til.root.editText?.text.toString()
                        .ifNullOrEmpty { return@setPositiveButton }
                        .also { if (it.length > til.root.counterMaxLength) return@setPositiveButton }
                    globalViewModel.saveUserName(txt)
                }
                .show()
        }
        bind.btnPolicy.setOnClickListener { navigate(R.id.privacyPolicy) }

        bind.btnScreenMode.setOnClickListener {
			val items = arrayOf(
				requireContext().getText(R.string.screen_theme_system),
				requireContext().getText(R.string.screen_theme_day),
				requireContext().getText(R.string.screen_theme_dark)
			)
			MaterialAlertDialogBuilder(requireContext())
				.setTitle("Themes")
				.setItems(items) { d, v ->
					globalViewModel.setScreenMode(v)
				}.show()
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
                tv.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1.0f
                    gravity = Gravity.END
                }
                tv.setTextAppearance(R.attr.textAppearanceBody2)
                tv.text = countryText.value
                bind.llLanguages.addView(tv)
            }
        }
        globalViewModel.screenMode.observe(this) {
            bind.txtScreenMode.text = when (it) {
                0 -> requireContext().getText(R.string.screen_theme_system)
                1 -> requireContext().getText(R.string.screen_theme_day)
                2 -> requireContext().getText(R.string.screen_theme_dark)
                else -> requireContext().getText(R.string.screen_theme_system)
            }
        }
    }

    private fun setUser(user: FirebaseUser?) {
        user?.let {
            bind.txtLogIn.setTextColor(bind.labelLogIn.currentTextColor)
            bind.txtLogIn.text = UserGlobalName(globalViewModel.userName.value, it.uid).userName
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