package com.uogames.remembercards.ui.registerFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentRegisterBinding
import com.uogames.remembercards.ui.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RegisterFragment : DaggerFragment() {

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var registerViewModel: RegisterViewModel

    private var _bind: FragmentRegisterBinding? = null
    private val bind get() = _bind!!

    private var observers: Job? = null

    private var watcher: TextWatcher = createWatcher()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = FragmentRegisterBinding.inflate(inflater, container, false)
        return bind.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bind.tilName.editText?.setText(registerViewModel.name.value)
        bind.tilName.editText?.addTextChangedListener(watcher)
        registerViewModel.maxText.value = bind.tilName.counterMaxLength
        bind.cbAgreeWithRules.isChecked = registerViewModel.privacyChecked.value


        lifecycleScope.launchWhenStarted {
            globalViewModel.getUserName().first()?.let {
                registerViewModel.isRegister.value = true
            }
        }

        bind.btnChoiceLanguage.setOnClickListener {
            val dialog = ChoiceCountryDialog {
                registerViewModel.country.value = it
            }
            dialog.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
        }

        bind.btnEnd.setOnClickListener {
            registerViewModel.allowed.value.ifFalse { return@setOnClickListener }
            val txt = registerViewModel.name.value
            globalViewModel.saveUserName(txt)
            globalViewModel.saveUserNativeCountry(registerViewModel.country.value.toString())
            globalViewModel.acceptRules()
            registerViewModel.isRegister.value = true
        }

        bind.tvPrivacy.setOnClickListener { navigate(R.id.privacyPolicy) }

        bind.cbAgreeWithRules.setOnCheckedChangeListener { _, b -> registerViewModel.privacyChecked.value = b }

        observers = lifecycleScope.launchWhenStarted {
            registerViewModel.isRegister.observe(this){
                if (it) {
                    val graph = findNavController().navInflater.inflate(R.navigation.nav_graph).apply { setStartDestination(R.id.mainNaviFragment) }
                    findNavController().setGraph(graph, null)
                }
            }
            registerViewModel.country.observe(this){
                it?.let {
                    bind.clLanguageEmpty.visibility = View.GONE
                    bind.rlLanguageNotEmpty.visibility = View.VISIBLE
                    bind.imgFlag.setImageResource(it.res)
                    bind.llLanguages.removeAllViews()
                    it.country.forEach { countryText ->
                        val tv = TextView(requireContext())
                        tv.setTextAppearance(R.attr.textAppearanceBody2)
                        tv.text = countryText.value
                        bind.llLanguages.addView(tv)
                    }
                }.ifNull {
                    bind.clLanguageEmpty.visibility = View.VISIBLE
                    bind.rlLanguageNotEmpty.visibility = View.GONE
                }
            }
            registerViewModel.allowed.observe(this){
                bind.btnEnd.isEnabled = it
                bind.tvEnd.isEnabled = it
            }
        }

    }

    private fun createWatcher(): TextWatcher = ShortTextWatcher{
        registerViewModel.name.value = it?.toString() ?: ""
    }

    override fun onDestroyView() {
        bind.tilName.editText?.removeTextChangedListener(watcher)
        observers?.cancel()
        _bind = null
        super.onDestroyView()
    }
}
