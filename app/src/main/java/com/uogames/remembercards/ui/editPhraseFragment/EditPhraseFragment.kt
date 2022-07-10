package com.uogames.remembercards.ui.editPhraseFragment

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.uogames.dto.Image
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditPhraseBinding
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class EditPhraseFragment : DaggerFragment() {

	@Inject
	lateinit var viewModel: EditPhraseViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var cropViewModel: CropViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	private lateinit var bind: FragmentEditPhraseBinding

	private val chooser = FileChooser(this, "image/*")

	private var recorder: MediaRecorder? = null

	private var textWatcher: TextWatcher? = null

	private lateinit var bottomSheet: BottomSheetBehavior<View>

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

	companion object {
		const val ID_PHRASE = "ID_PHRASE"
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		bind = FragmentEditPhraseBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

		arguments?.getInt(ID_PHRASE)?.let {
			bind.txtFragmentName.text = getString(R.string.edit_card)
			bind.btnDelete.visibility = View.VISIBLE
			cropViewModel.getData()?.let { bitmap ->
				viewModel.setBitmapImage(bitmap)
				cropViewModel.reset()
			}.ifNull {
				viewModel.loadByID(it)
			}
			bind.btnSave.setOnClickListener {
				viewModel.update(id) { res -> if (res) findNavController().popBackStack() }
			}
			bind.btnDelete.setOnClickListener {
				viewModel.delete(id) { res -> if (res) findNavController().popBackStack() }
			}
		}.ifNull {
			bind.btnSave.setOnClickListener {
				viewModel.save { res -> if (res) findNavController().popBackStack() }
			}
			cropViewModel.getData()?.let { bitmap ->
				viewModel.setBitmapImage(bitmap)
				cropViewModel.reset()
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

		bind.rvImages.adapter = ImageAdapter(lifecycleScope, viewModel) {
			viewModel.selectImage(it)
			bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
		}

		bind.btnNewFile.setOnClickListener {
			chooser.getBitmap {
				cropViewModel.putData(it)
				requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.cropFragment)
			}
		}

		bind.blind.setOnClickListener {
			bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
		}

		bind.btnEditAudio.setOnClickListener {
			Permission.RECORD_AUDIO.requestPermission(requireActivity()) {
				if (it && !viewModel.isFileWriting.value) {
					recorder = if (Build.VERSION.SDK_INT < 31) {
						MediaRecorder()
					} else {
						MediaRecorder(requireContext())
					}
					recorder?.let { recorder -> viewModel.startRecordAudio(recorder) }
				} else {
					recorder?.let { recorder -> viewModel.stopRecordAudio(recorder) }
				}
			}
		}

		bind.btnEditPhrase.setOnClickListener {
			textWatcher = setTextWatcher(textWatcher, viewModel.phrase.value) { text, _, _, _ ->
				text?.let { viewModel.setPhrase(it.toString()) }
				viewModel.setLang(Locale.forLanguageTag(imm.currentInputMethodSubtype.languageTag))
			}
		}
		bind.btnEditDefinition.setOnClickListener {
			textWatcher = setTextWatcher(textWatcher, viewModel.definition.value.orEmpty()) { text, _, _, _ ->
				text?.let { viewModel.setDefinition(it.toString()) }
			}
		}
		bind.btnEditImage.setOnClickListener {
			if (viewModel.imgPhrase.value == null) {
				bottomSheet.state = BottomSheetBehavior.STATE_HALF_EXPANDED
			} else {
				viewModel.setBitmapImage(null)
			}
		}

		bind.btnSound.setOnClickListener {
			val resource = viewModel.tempAudioSource
			player.play(resource, bind.imgBtnSound.background.asAnimationDrawable())
		}

		globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
			bind.editBar.visibility = if (it) View.GONE else View.VISIBLE
			bind.tilEdit.visibility = if (it) View.VISIBLE else View.GONE
		}


		viewModel.phrase.observeWhenStarted(lifecycleScope) {
			bind.txtPhrase.text = it.ifEmpty { requireContext().getString(R.string.phrase2) }
		}
		viewModel.definition.observeWhenStarted(lifecycleScope) {
			bind.txtDefinition.text = it.ifNullOrEmpty { requireContext().getString(R.string.definition) }
		}

		viewModel.lang.observeWhenStarted(lifecycleScope) {
			bind.txtLang.text = it.displayLanguage
		}

		viewModel.isFileWriting.observeWhenStarted(lifecycleScope)
		{
			bind.btnSound.visibility = if (it || viewModel.tempAudioSource.size == 0L) View.GONE else View.VISIBLE
			bind.imgMic.background.asAnimationDrawable().selectDrawable(if (it) 1 else 0)
			if (!viewModel.isFileWriting.value) bind.txtEditor.text = requireContext().getText(R.string.editor)
			else bind.txtEditor.text =
				requireContext().getText(R.string.record_sp).toString().replace("||TIME||", viewModel.timeWriting.value.toString())
		}

		viewModel.timeWriting.observeWhenStarted(lifecycleScope)
		{
			bind.txtEditor.text =
				if (viewModel.isFileWriting.value)
					requireContext().getText(R.string.record_sp).toString().replace("||TIME||", it.toString())
				else requireContext().getText(R.string.editor)
		}

		bind.btnBack.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
		}

		viewModel.imgPhrase.observeWhenStarted(lifecycleScope) { setImagePhrase(it) }

	}

	private inline fun setTextWatcher(
		oldWatcher: TextWatcher?,
		text: String,
		crossinline action: (
			text: CharSequence?,
			start: Int,
			before: Int,
			count: Int
		) -> Unit
	): TextWatcher? {
		bind.tilEdit.editText?.removeTextChangedListener(oldWatcher)
		bind.tilEdit.requestFocus()
		bind.tilEdit.editText?.setText(text)
		bind.tilEdit.editText?.setSelection(text.length)
		val textWatcher = bind.tilEdit.editText?.doOnTextChanged(action)
		imm.showSoftInput(bind.tilEdit.editText, InputMethodManager.SHOW_IMPLICIT)
		return textWatcher
	}

	override fun onStop() {
		super.onStop()
		imm.hideSoftInputFromWindow(view?.windowToken, 0)
		bind.tilEdit.editText?.removeTextChangedListener(textWatcher)
		recorder?.let { viewModel.stopRecordAudio(it) }
	}

	private fun setImagePhrase(image: Image?) = image?.let {
		bind.imgPhrase.visibility = View.VISIBLE
		bind.imgPhrase.setImageURI(image.imgUri.toUri())
		bind.imgBtnImage.setImageResource(R.drawable.ic_baseline_hide_image_24)
	}.ifNull {
		bind.imgPhrase.visibility = View.GONE
		bind.imgBtnImage.setImageResource(R.drawable.ic_baseline_image_24)
	}

}