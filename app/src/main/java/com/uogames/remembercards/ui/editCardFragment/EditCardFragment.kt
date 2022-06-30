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

	private lateinit var bottomSheet: BottomSheetBehavior<View>

	private val fileChooser = FileChooser(this, "image/*")

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	private val callback by lazy {
		object : OnBackPressedCallback(false) {
			override fun handleOnBackPressed() {
				bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
			}
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentEditCardBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val id: Int? = arguments?.getInt(EDIT_ID)

		requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

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

		bottomSheet = BottomSheetBehavior.from(bind.rlBehavior)
		bottomSheet.halfExpandedRatio = 0.4f
		bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
			override fun onStateChanged(bottomSheet: View, newState: Int) {
				if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
					bind.blind.visibility = View.GONE
					callback.isEnabled = false
				} else {
					bind.blind.visibility = View.VISIBLE
					callback.isEnabled = true
				}
			}

			override fun onSlide(bottomSheet: View, slideOffset: Float) {
			}
		})

		bind.rvImages.adapter = ImageAdapter(lifecycleScope, editCardViewModel) {
			editCardViewModel.selectImage(it)
			bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
		}

		bind.btnNewFile.setOnClickListener {
			fileChooser.getBitmap {
				cropViewModel.putData(it)
				requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.cropFragment)
			}
		}

		bind.blind.setOnClickListener {
			bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
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

		bind.btnEditImage.setOnClickListener {
			bottomSheet.state = BottomSheetBehavior.STATE_HALF_EXPANDED
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
				//bind.mcvFirst.visibility = View.VISIBLE
				setButtonData(phrase, bind.mcvFirst, bind.txtPhraseFirst, bind.imgSoundFirst, bind.txtLangFirst)
			}.ifNull {
				bind.imgSoundFirst.visibility = View.GONE
				bind.txtPhraseFirst.text = requireContext().getString(R.string.phrase)
				bind.txtLangFirst.text = Locale.getDefault().displayLanguage
			}
		}

		editCardViewModel.secondPhrase.observeWhenStarted(lifecycleScope) {
			it?.let { phrase ->
				setButtonData(phrase, bind.mcvSecond, bind.txtPhraseSecond, bind.imgSoundSecond, bind.txtLangSecond)
			}.ifNull {
				bind.imgSoundSecond.visibility = View.GONE
				bind.txtPhraseSecond.text = requireContext().getString(R.string.phrase)
				bind.txtLangSecond.text = Locale.getDefault().displayLanguage
			}
		}

		editCardViewModel.reason.observeWhenStarted(lifecycleScope) {
			bind.txtReason.text = it.ifNullOrEmpty {
				"Reason"
			}
		}

		editCardViewModel.image.observeWhenStarted(lifecycleScope) {
			it?.let {
				bind.imgPhraseFirst.visibility = View.VISIBLE
				bind.imgPhraseFirst.setImageURI(it.imgUri.toUri())
			}.ifNull {
				bind.imgPhraseFirst.visibility = View.GONE
			}
		}

		cropViewModel.getData()?.let {
			editCardViewModel.setBitmapImage(it)
			cropViewModel.reset()
			bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
		}

	}

	private fun setButtonData(
		phrase: Phrase,
		container: MaterialCardView,
		txtPhrase: TextView,
		imgSound: ImageView,
		lang: TextView
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