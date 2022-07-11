package com.uogames.remembercards.ui.editCardFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.Phrase
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditCardBinding
import com.uogames.remembercards.ui.choicePhraseFragment.ChoicePhraseFragment
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.utils.*
import com.uogames.repository.DataProvider.Companion.toImage
import com.uogames.repository.DataProvider.Companion.toPronounce
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class EditCardFragment : DaggerFragment() {

	companion object {
		private const val FIRST_PHRASE = "EditCardFragment_FIRST_PHRASE"
		private const val SECOND_PHRASE = "EditCardFragment_SECOND_PHRASE"
		const val CREATE_FOR = "EditCardFragment_CREATE_FOR"
		const val POP_BACK_TO = "EditCardFragment_POP_BACK_TO"
		const val EDIT_ID = "EditCardFragment_EDIT_ID"
	}

	@Inject
	lateinit var editCardViewModel: EditCardViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var cropViewModel: CropViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	private lateinit var bind: FragmentEditCardBinding

	private val bundleFirst = Bundle().apply { putString(ChoicePhraseFragment.TAG, FIRST_PHRASE) }

	private val bundleSecond = Bundle().apply { putString(ChoicePhraseFragment.TAG, SECOND_PHRASE) }

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentEditCardBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val id = arguments?.getInt(EDIT_ID)?.let { if (it == 0) null else it }
		val createFor = arguments?.getString(CREATE_FOR)
		val popBackTo = arguments?.getInt(POP_BACK_TO)

		id?.let {
			editCardViewModel.load(id)
			bind.btnDelete.visibility = View.VISIBLE
			bind.btnDelete.setOnClickListener {
				editCardViewModel.delete { if (it) findNavController().popBackStack() }
			}
			bind.btnSave.setOnClickListener {
				editCardViewModel.update { if (it) findNavController().popBackStack() }
			}
		}.ifNull {
			bind.btnDelete.visibility = View.GONE
			bind.btnSave.setOnClickListener {
				editCardViewModel.save { res ->
					res?.let {
						if (!createFor.isNullOrEmpty() && popBackTo != null && popBackTo != 0) {
							globalViewModel.saveData(createFor, res.toString()) {
								findNavController().popBackStack(popBackTo, true)
							}
						} else {
							findNavController().popBackStack()
						}
					}
				}
			}
		}

		bind.btnBack.setOnClickListener {
			findNavController().popBackStack()
		}

		bind.btnEditFist.setOnClickListener { openChoicePhraseFragment(bundleFirst) }

		bind.btnEditSecond.setOnClickListener { openChoicePhraseFragment(bundleSecond) }


		bind.btnEditReason.setOnClickListener {
			bind.tilEditReason.requestFocus()
			imm.showSoftInput(bind.tilEditReason.editText, InputMethodManager.SHOW_IMPLICIT)
		}

		bind.tilEditReason.editText?.doOnTextChanged { text, _, _, _ ->
			editCardViewModel.setReason(text.toString())
		}

		globalViewModel.getFlow(FIRST_PHRASE).observeWhenStarted(lifecycleScope) {
			try {
				editCardViewModel.selectFirstPhrase(it!!.toInt())
				globalViewModel.saveData(FIRST_PHRASE, null)
			} catch (e: Exception) {
			}
		}

		globalViewModel.getFlow(SECOND_PHRASE).observeWhenStarted(lifecycleScope) {
			try {
				editCardViewModel.selectSecondPhrase(it!!.toInt())
				globalViewModel.saveData(SECOND_PHRASE, null)
			} catch (e: Exception) {
			}
		}

		globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
			bind.editBar.visibility = if (it) View.GONE else View.VISIBLE
			bind.tilEditReason.visibility = if (it) View.VISIBLE else View.GONE
		}

		editCardViewModel.firstPhrase.observeWhenStarted(lifecycleScope) {
			it?.let { phrase ->
				setButtonData(phrase, bind.mcvFirst, bind.txtPhraseFirst, bind.imgSoundFirst, bind.txtLangFirst, bind.imgCardFirst)
			}.ifNull {
				setDefaultButtonData(bind.imgSoundFirst, bind.txtPhraseFirst, bind.txtLangFirst, bind.imgCardFirst)
			}
		}

		editCardViewModel.secondPhrase.observeWhenStarted(lifecycleScope) {
			it?.let { phrase ->
				setButtonData(phrase, bind.mcvSecond, bind.txtPhraseSecond, bind.imgSoundSecond, bind.txtLangSecond, bind.imgCardSecond)
			}.ifNull {
				setDefaultButtonData(bind.imgSoundSecond, bind.txtPhraseSecond, bind.txtLangSecond, bind.imgCardSecond)
			}
		}

		editCardViewModel.reason.observeWhenStarted(lifecycleScope) {
			bind.txtReason.text = it.ifNullOrEmpty { "Reason" }
		}

	}

	private suspend fun setButtonData(
		phrase: Phrase,
		container: MaterialCardView,
		txtPhrase: TextView,
		imgSound: ImageView,
		lang: TextView,
		imageCard: ImageView
	) {
		txtPhrase.text = phrase.phrase.ifNullOrEmpty { requireContext().getString(R.string.phrase_label) }
		lang.text = showLang(phrase)

		phrase.toPronounce()?.let { pronounce ->
			imgSound.visibility = View.VISIBLE
			container.setOnClickListener {
				player.play(requireContext(), pronounce.audioUri.toUri(), imgSound.background.asAnimationDrawable())
			}
		}.ifNull {
			imgSound.visibility = View.GONE
		}

		phrase.toImage()?.let {
			imageCard.setImageURI(it.imgUri.toUri())
			imageCard.visibility = View.VISIBLE
		}.ifNull {
			imageCard.visibility = View.GONE
		}
	}

	private fun setDefaultButtonData(imgSound: ImageView, txtPhrase: TextView, txtLang: TextView, imageCard: ImageView) {
		imgSound.visibility = View.GONE
		txtPhrase.text = requireContext().getString(R.string.phrase_label)
		txtLang.text = Locale.getDefault().displayLanguage
		imageCard.visibility = View.GONE
	}

	private fun showLang(phrase: Phrase): String {
		return safely {
			val data = phrase.lang.split("-")
			Locale(data[0]).displayLanguage
		}.orEmpty()
	}

	private fun openChoicePhraseFragment(bundle: Bundle) {
		requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.choicePhraseDialog, bundle)
	}

}