package com.uogames.remembercards.ui.bookFragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.uogames.flags.Languages
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentBookBinding
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import java.util.*
import javax.inject.Inject

class BookFragment : DaggerFragment() {

	@Inject
	lateinit var bookViewModel: BookViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var editPhraseViewModel: EditPhraseViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	lateinit var bind: FragmentBookBinding

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	private val adapter by lazy {
		BookAdapter(bookViewModel, player, lifecycleScope) {
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.addPhraseFragment, Bundle().apply {
				putInt(EditPhraseFragment.ID_PHRASE, it)
			})
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		bind = FragmentBookBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		bookViewModel.reset()

		bookViewModel.size.observeWhenStarted(lifecycleScope) {
			bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
			adapter.notifyDataSetChanged()
		}

		globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
			bind.tilSearch.visibility = if (it) View.VISIBLE else View.GONE
			bind.btnAdd.visibility = if (it) View.GONE else View.VISIBLE
			if (it) {
				bind.searchImage.setImageResource(R.drawable.ic_baseline_close_24)
			} else {
				bind.searchImage.setImageResource(R.drawable.ic_baseline_search_24)
			}
		}

		bind.btnSearch.setOnClickListener {
			if (!globalViewModel.isShowKey.value) {
				bind.tilSearch.requestFocus()
				imm.showSoftInput(bind.tilSearch.editText, InputMethodManager.SHOW_IMPLICIT)
			} else {
				imm.hideSoftInputFromWindow(view.windowToken, 0)
			}
		}

		bind.btnAdd.setOnClickListener { openEditFragment() }

		bind.tilSearch.editText?.doOnTextChanged { text, _, _, _ ->
			//Log.e("TAG", "${System.getProperty("user.language", "de")} ", )
//			val locale = imm.currentInputMethodSubtype.locale
//			Log.e("TAG", "${locale} ", )

			val languageIdentifier = LanguageIdentification.getClient()
//			val languageIdentifier = LanguageIdentification
//				.getClient(LanguageIdentificationOptions.Builder()
//					.setConfidenceThreshold(0.34f)
//					.build())

			languageIdentifier.identifyPossibleLanguages(text.toString())
				.addOnSuccessListener { languageCode ->
					Log.e("TAG", "size: ${languageCode.size} ", )
					languageCode.forEach {
						Log.e("TAG", "$it ", )
					}
				}
				.addOnFailureListener {
					// Model couldn’t be loaded or other internal error.
					// ...
				}



			bookViewModel.like.value = text.toString()
		}

		bind.recycler.adapter = adapter

	}

	private fun openEditFragment() {
		editPhraseViewModel.reset()
		requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.addPhraseFragment)
	}

}