package com.uogames.remembercards.ui.addPhraseFragment

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditBinding
import com.uogames.remembercards.utils.FileChooser
import com.uogames.remembercards.utils.Permission
import com.uogames.remembercards.utils.observeWhile
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import kotlin.math.log

class AddPhraseFragment : DaggerFragment() {

	@Inject
	lateinit var viewModel: AddPhraseViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private lateinit var bind: FragmentEditBinding

	private val chooser = FileChooser(this, "image/*")

	private var recorder: MediaRecorder? = null
	private var player: MediaPlayer? = null

	private var textWatcher: TextWatcher? = null

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	}

	companion object {
		const val ID_PHRASE = "ID_PHRASE"
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		bind = FragmentEditBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		val id = arguments?.getInt(ID_PHRASE)

		id?.let {
			bind.btnBack.visibility = View.GONE
			bind.btnDelete.visibility = View.VISIBLE
			viewModel.reset()
			viewModel.loadByID(it)
			bind.btnSave.setOnClickListener {
				viewModel.update(id) { res ->
					if (res) findNavController().popBackStack()
				}
			}
			bind.btnDelete.setOnClickListener {
				viewModel.delete(id) { res ->
					if (res) findNavController().popBackStack()
				}
			}
		} ?: bind.btnSave.setOnClickListener {
			viewModel.save {
				if (it) findNavController().popBackStack()
			}
		}

		bind.btnEditAudio.setOnClickListener {
			Permission.RECORD_AUDIO.requestPermission(requireActivity()) {
				if (it && !viewModel.isFileWriting.value) {
					recorder = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
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
			textWatcher =
				setTextWatcher(textWatcher, viewModel.phrase.value) { text, _, _, _ ->
					text?.let { viewModel.setPhrase(it.toString()) }
				}
		}
		bind.btnEditDefinition.setOnClickListener {
			textWatcher =
				setTextWatcher(textWatcher, viewModel.definition.value) { text, _, _, _ ->
					text?.let { viewModel.setDefinition(it.toString()) }
				}
		}
		bind.btnEditImage.setOnClickListener {
			chooser.getBitmap { viewModel.setBitmapImage(it) }
		}

		bind.btnSound.setOnClickListener {
			player = MediaPlayer()
			player?.setDataSource(viewModel.tempAudioSource)
			player?.prepare()
			player?.start()
		}

		view.viewTreeObserver.addOnGlobalLayoutListener {
			globalViewModel.setShowKeyboard(view)
			viewModel.setLang(imm.currentInputMethodSubtype.languageTag)
		}

		globalViewModel.isShowKey.observeWhile(lifecycleScope) {
			bind.editBar.visibility = if (it) View.GONE else View.VISIBLE
			bind.tilEdit.visibility = if (it) View.VISIBLE else View.GONE
		}

		viewModel.imgPhrase.observeWhile(lifecycleScope) {
			it?.let { bind.imgPhrase.setImageBitmap(it) }
		}

		viewModel.country.observeWhile(lifecycleScope) {
			bind.imgCountry.setImageResource(it.res)
		}
		viewModel.phrase.observeWhile(lifecycleScope) {
			bind.txtPhrase.text = it.ifEmpty { requireContext().getString(R.string.phrase2) }
		}
		viewModel.definition.observeWhile(lifecycleScope) {
			bind.txtDefinition.text = it.ifEmpty { requireContext().getString(R.string.definition) }
		}

		viewModel.lang.observeWhile(lifecycleScope) {
			bind.txtLang.text = it.language
		}

		viewModel.isFileWriting.observeWhile(lifecycleScope) {
			bind.btnSound.visibility = if (it || viewModel.tempAudioSource.size == 0L) View.GONE else View.VISIBLE
			bind.imgMic.setImageResource(
				if (it) R.drawable.ic_baseline_mic_off_24 else R.drawable.ic_baseline_mic_24
			)
			if (!viewModel.isFileWriting.value) bind.txtEditor.text = requireContext().getText(R.string.editor)
		}

		viewModel.timeWriting.observeWhile(lifecycleScope) {
			bind.txtEditor.text =
				if (viewModel.isFileWriting.value)
					requireContext().getText(R.string.record_sp).toString().replace("||TIME||", it.toString())
				else requireContext().getText(R.string.editor)
		}

		//					"${requireContext().getString(R.string.record)} ${it}${requireContext().getString(R.string.tag_second)} " +
//							"/ 60${requireContext().getString(R.string.tag_second)}"
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

	override fun onStart() {
		super.onStart()
		bind.btnBack.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
		}
	}

	override fun onStop() {
		super.onStop()
		bind.tilEdit.editText?.removeTextChangedListener(textWatcher)
		recorder?.let { viewModel.stopRecordAudio(it) }
	}

}