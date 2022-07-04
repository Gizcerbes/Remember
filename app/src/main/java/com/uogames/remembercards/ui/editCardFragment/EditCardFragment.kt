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
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.Phrase
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditCardBinding
import com.uogames.remembercards.ui.choicePhraseFragment.ChoicePhraseFragment
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.utils.*
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


	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentEditCardBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val id: Int? = arguments?.getInt(EDIT_ID)


		id?.let {
			editCardViewModel.load(id)
			bind.btnDelete.visibility = View.VISIBLE
			bind.btnDelete.setOnClickListener {
				editCardViewModel.delete {
					if (it) findNavController().popBackStack()
				}
			}
			bind.btnSave.setOnClickListener {
				editCardViewModel.update {
					if (it) findNavController().popBackStack()
				}
			}
		}.ifNull {
			bind.btnDelete.visibility = View.GONE
			bind.btnSave.setOnClickListener {
				editCardViewModel.save {
					if (it) findNavController().popBackStack()
				}
			}
		}

		bind.btnBack.setOnClickListener {
			findNavController().popBackStack()
		}

		bind.btnEditFist.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(
				R.id.choicePhraseDialog,
				Bundle().apply {
					putString(ChoicePhraseFragment.TAG, FIRST_PHRASE)
				}
			)
		}

		bind.btnEditSecond.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(
				R.id.choicePhraseDialog,
				Bundle().apply {
					putString(ChoicePhraseFragment.TAG, SECOND_PHRASE)
				}
			)
		}


		bind.btnEditReason.setOnClickListener {
			bind.tilEditReason.requestFocus()
			imm.showSoftInput(bind.tilEditReason.editText, InputMethodManager.SHOW_IMPLICIT)
		}

		bind.tilEditReason.editText?.doOnTextChanged { text, _, _, _ ->
			editCardViewModel.setReason(text.toString())
		}

		globalViewModel.getFlow(FIRST_PHRASE).observeWhenStarted(lifecycleScope) {
			it?.let {
				try {
					editCardViewModel.selectFirstPhrase(it.toInt())
					globalViewModel.saveData(FIRST_PHRASE, null)
				} catch (e: Exception) {
					Toast.makeText(requireContext(), "Error firs id", Toast.LENGTH_SHORT).show()
				}
			}
		}

		globalViewModel.getFlow(SECOND_PHRASE).observeWhenStarted(lifecycleScope) {
			it?.let {
				try {
					editCardViewModel.selectSecondPhrase(it.toInt())
					globalViewModel.saveData(SECOND_PHRASE, null)
				} catch (e: Exception) {
					Toast.makeText(requireContext(), "Error second id", Toast.LENGTH_SHORT).show()
				}
			}
		}

		globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
			bind.editBar.visibility = if (it) View.GONE else View.VISIBLE
			bind.tilEditReason.visibility = if (it) View.VISIBLE else View.GONE
		}

		editCardViewModel.firstPhrase.observeWhenStarted(lifecycleScope) {
			it?.let { phrase ->
				lifecycleScope.launch {
					setButtonData(phrase, bind.mcvFirst, bind.txtPhraseFirst, bind.imgSoundFirst, bind.txtLangFirst, bind.imgCardFirst)
				}
			}.ifNull {
				bind.imgSoundFirst.visibility = View.GONE
				bind.txtPhraseFirst.text = requireContext().getString(R.string.phrase)
				bind.txtLangFirst.text = Locale.getDefault().displayLanguage
				bind.imgCardFirst.visibility = View.GONE
			}
		}

		editCardViewModel.secondPhrase.observeWhenStarted(lifecycleScope) {
			it?.let { phrase ->
				lifecycleScope.launch {
					setButtonData(phrase, bind.mcvSecond, bind.txtPhraseSecond, bind.imgSoundSecond, bind.txtLangSecond, bind.imgCardSecond)
				}
			}.ifNull {
				bind.imgSoundSecond.visibility = View.GONE
				bind.txtPhraseSecond.text = requireContext().getString(R.string.phrase)
				bind.txtLangSecond.text = Locale.getDefault().displayLanguage
				bind.imgCardSecond.visibility = View.GONE
			}
		}

		editCardViewModel.reason.observeWhenStarted(lifecycleScope) {
			bind.txtReason.text = it.ifNullOrEmpty {
				"Reason"
			}
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
		txtPhrase.text = phrase.phrase.ifNullOrEmpty {
			requireContext().getString(R.string.phrase)
		}
		lang.text = showLang(phrase).orEmpty()
		phrase.idPronounce?.let {
			imgSound.visibility = View.VISIBLE
			container.setOnClickListener {
				lifecycleScope.launch(Dispatchers.IO) {
					play(phrase, imgSound)
				}
			}
		}.ifNull {
			imgSound.visibility = View.GONE
		}
		editCardViewModel.getImage(phrase).first()?.let {
			imageCard.setImageURI(it)
			imageCard.visibility = View.VISIBLE
		}.ifNull {
			imageCard.visibility = View.GONE
		}
	}

	private suspend fun play(phrase: Phrase, imgSound: ImageView) {
		val audio = editCardViewModel.getAudio(phrase).first()
		player.setStatListener {
			when (it) {
				ObservableMediaPlayer.Status.PLAY -> imgSound.background.asAnimationDrawable().start()
				else -> {
					imgSound.background.asAnimationDrawable().stop()
					imgSound.background.asAnimationDrawable().selectDrawable(0)
				}
			}
		}
		player.play(requireContext(), audio)
	}

	private fun showLang(phrase: Phrase): String? {
		return phrase.lang?.let {
			val data = it.split("-")
			if (data.isNotEmpty()) try {
				Locale(data[0]).displayLanguage
			} catch (e: Exception) {
				null
			} else null
		}
	}


}