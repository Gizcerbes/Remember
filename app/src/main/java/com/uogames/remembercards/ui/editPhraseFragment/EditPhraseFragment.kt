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
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import com.uogames.dto.Image
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditPhraseBinding
import com.uogames.remembercards.ui.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.util.*
import javax.inject.Inject

class EditPhraseFragment : DaggerFragment() {

	companion object {
		const val ID_PHRASE = "ID_PHRASE"
		const val CREATE_FOR = "EditPhraseFragment_CREATE_FOR"
		const val POP_BACK_TO = "EditPhraseFragment_POP_BACK_TO"
	}

	@Inject
	lateinit var editPhraseViewModel: EditPhraseViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	@Inject
	lateinit var cropViewModel: CropViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	private var keyObserver: Job? = null
	private var phraseObserver: Job? = null
	private var definitionObserver: Job? = null
	private var langObserver: Job? = null
	private var fileWriteObserver: Job? = null
	private var timeWriteObserver: Job? = null
	private var imageObserver: Job? = null

	private var _bind: FragmentEditPhraseBinding? = null
	private val bind get() = _bind!!

	private val chooser = FileChooser(this, "image/*")

	private var recorder: MediaRecorder? = null

	private var textWatcher: TextWatcher? = null

	private var bottomSheet: BottomSheetBehavior<View>? = null

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	private var adapter: ImageAdapter? = null

	private val callback by lazy {
		object : OnBackPressedCallback(false) {
			override fun handleOnBackPressed() {
				bottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN
			}
		}
	}


	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		if (_bind == null) _bind = FragmentEditPhraseBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		globalViewModel.shouldReset.ifTrue {
			editPhraseViewModel.reset()
		}

		requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

		val createForCard = arguments?.getString(CREATE_FOR)
		val popBackTo = arguments?.getInt(POP_BACK_TO)

		val idPhrase = arguments?.getInt(ID_PHRASE)
		val idChanged = editPhraseViewModel.setArgPhraseId(idPhrase)

		cropViewModel.getData()?.let { bitmap ->
			editPhraseViewModel.setBitmapImage(bitmap)
			cropViewModel.reset()
		}

		idPhrase?.let { id ->
			if (id == 0) return@let null
			bind.txtFragmentName.text = getString(R.string.edit_phrase)
			bind.btnDelete.visibility = View.VISIBLE
			idChanged.ifTrue { editPhraseViewModel.loadByID(id) }
			bind.btnSave.setOnClickListener {
				editPhraseViewModel.update(id) { res -> if (res) findNavController().popBackStack() }
			}
			bind.btnDelete.setOnClickListener {
				editPhraseViewModel.delete(id) { res -> if (res) findNavController().popBackStack() }
			}
		}.ifNull {
			bind.btnSave.setOnClickListener {
				editPhraseViewModel.save { res ->
					res?.let {
						if (createForCard != null && popBackTo != null && popBackTo != 0) {
							setFragmentResult(createForCard, bundleOf("ID" to res.toInt()))
							findNavController().popBackStack(popBackTo, true)
						} else {
							findNavController().popBackStack()
						}
					}
				}
			}
		}

		bottomSheet = BottomSheetBehavior.from(bind.rlBehavior)
		bottomSheet?.halfExpandedRatio = 0.4f
		bottomSheet?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

		adapter = ImageAdapter(editPhraseViewModel) {
			editPhraseViewModel.selectImage(it)
			bottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN
		}

		bind.rvImages.adapter = adapter

		bind.btnNewFile.setOnClickListener {
			chooser.getBitmap {
				cropViewModel.putData(it)
				requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.cropFragment)
			}
		}

		bind.blind.setOnClickListener {
			bottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN
		}

		bind.btnEditAudio.setOnClickListener {
			Permission.RECORD_AUDIO.requestPermission(requireActivity()) {
				if (it && !editPhraseViewModel.isFileWriting.value) {
					recorder = if (Build.VERSION.SDK_INT < 31) {
						MediaRecorder()
					} else {
						MediaRecorder(requireContext())
					}
					recorder?.let { recorder -> editPhraseViewModel.startRecordAudio(recorder) }
				} else {
					recorder?.let { recorder -> editPhraseViewModel.stopRecordAudio(recorder) }
				}
			}
		}

		bind.btnEditPhrase.setOnClickListener {
			textWatcher = setTextWatcher(textWatcher, editPhraseViewModel.phrase.value) { text, _, _, _ ->
				text?.let { editPhraseViewModel.setPhrase(it.toString()) }
				imm.currentInputMethodSubtype.languageTag.isEmpty().ifFalse {
					editPhraseViewModel.setLang(Locale.forLanguageTag(imm.currentInputMethodSubtype.languageTag))
				}
			}
		}
		bind.btnEditDefinition.setOnClickListener {
			textWatcher = setTextWatcher(textWatcher, editPhraseViewModel.definition.value.orEmpty()) { text, _, _, _ ->
				text?.let { editPhraseViewModel.setDefinition(it.toString()) }
			}
		}
		bind.btnEditImage.setOnClickListener {
			if (editPhraseViewModel.imgPhrase.value == null) {
				bottomSheet?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
			} else {
				editPhraseViewModel.setBitmapImage(null)
			}
		}

		bind.btnSound.setOnClickListener {
			val resource = editPhraseViewModel.tempAudioSource.ifNull { return@setOnClickListener }
			player.play(resource, bind.imgBtnSound.background.asAnimationDrawable())
		}

		bind.btnBack.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
		}

		bind.btnEditLanguage.setOnClickListener {
			val dialog = ChoiceLanguageDialog(editPhraseViewModel.languages.value){
				editPhraseViewModel.forceSetLang(it)
			}
			dialog.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
		}

		createObservers()
	}

	private fun createObservers() {
		keyObserver = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
			bind.editBar.visibility = if (it) View.GONE else View.VISIBLE
			bind.tilEdit.visibility = if (it) View.VISIBLE else View.GONE
		}

		 phraseObserver = editPhraseViewModel.phrase.observeWhenStarted(lifecycleScope) {
			bind.txtPhrase.text = it.ifEmpty { requireContext().getString(R.string.phrase2) }
		}

		definitionObserver = editPhraseViewModel.definition.observeWhenStarted(lifecycleScope) {
			bind.txtDefinition.text = it.ifNullOrEmpty { requireContext().getString(R.string.definition) }
		}

		langObserver = editPhraseViewModel.lang.observeWhenStarted(lifecycleScope) {
			bind.txtLang.text = it.displayLanguage
		}

		fileWriteObserver = editPhraseViewModel.isFileWriting.observeWhenStarted(lifecycleScope) {
			val size = editPhraseViewModel.tempAudioSource.size.ifNull { 0L }
			bind.btnSound.visibility = if (it || size <= 0L) View.GONE else View.VISIBLE
			bind.imgMic.background.asAnimationDrawable().selectDrawable(if (it) 1 else 0)
			if (!editPhraseViewModel.isFileWriting.value) bind.txtEditor.text = requireContext().getText(R.string.editor)
			else bind.txtEditor.text =
				requireContext().getText(R.string.record_sp).toString().replace("||TIME||", editPhraseViewModel.timeWriting.value.toString())
		}

		timeWriteObserver = editPhraseViewModel.timeWriting.observeWhenStarted(lifecycleScope) {
			bind.txtEditor.text =
				if (editPhraseViewModel.isFileWriting.value)
					requireContext().getText(R.string.record_sp).toString().replace("||TIME||", it.toString())
				else requireContext().getText(R.string.editor)
		}

		imageObserver = editPhraseViewModel.imgPhrase.observeWhenStarted(lifecycleScope) { setImagePhrase(it) }
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
		recorder?.let { editPhraseViewModel.stopRecordAudio(it) }
	}

	private fun setImagePhrase(image: Image?) = image?.let {
		Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(bind.imgPhrase)
		bind.imgPhrase.visibility = View.VISIBLE
		bind.imgBtnImage.setImageResource(R.drawable.ic_baseline_hide_image_24)
	}.ifNull {
		bind.imgPhrase.visibility = View.GONE
		bind.imgBtnImage.setImageResource(R.drawable.ic_baseline_image_24)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		keyObserver?.cancel()
		phraseObserver?.cancel()
		definitionObserver?.cancel()
		langObserver?.cancel()
		fileWriteObserver?.cancel()
		timeWriteObserver?.cancel()
		imageObserver?.cancel()
		adapter?.onDestroy()
		adapter = null
		_bind = null
	}

}