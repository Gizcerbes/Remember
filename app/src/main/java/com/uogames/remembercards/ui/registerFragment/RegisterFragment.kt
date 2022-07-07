package com.uogames.remembercards.ui.registerFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentRegisterBinding
import com.uogames.remembercards.ui.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifNullOrEmpty
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterFragment : DaggerFragment() {

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var registerViewModel: RegisterViewModel

	private lateinit var bind: FragmentRegisterBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentRegisterBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		registerViewModel.isRegister.observeWhenStarted(lifecycleScope) {
			if (it) {
				val graph = findNavController().navInflater.inflate(R.navigation.nav_graph).apply { setStartDestination(R.id.mainNaviFragment) }
				findNavController().setGraph(graph, null)
			}
		}

		lifecycleScope.launch {
			globalViewModel.getUserName().first()?.let {
				registerViewModel.isRegister.value = true
			}
		}

		registerViewModel.country.observeWhenStarted(lifecycleScope){
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

		bind.btnChoiceLanguage.setOnClickListener {
			val dialog = ChoiceCountryDialog {
				registerViewModel.country.value = it
			}
			dialog.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
		}

		bind.btnEnd.setOnClickListener {
			val txt = bind.tilName.editText?.text.toString()
				.ifNullOrEmpty { return@setOnClickListener }
				.also { if (it.length > bind.tilName.counterMaxLength) return@setOnClickListener }
			globalViewModel.saveUserName(txt)
			globalViewModel.saveUserNativeCountry(registerViewModel.country.value.toString())
			registerViewModel.isRegister.value = true
		}

	}


}