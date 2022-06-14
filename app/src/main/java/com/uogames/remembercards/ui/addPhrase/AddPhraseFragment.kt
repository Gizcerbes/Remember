package com.uogames.remembercards.ui.addPhrase

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditBinding
import com.uogames.remembercards.utils.FileChooser
import com.uogames.remembercards.utils.Permission
import com.uogames.remembercards.utils.observeWhile
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class AddPhraseFragment : DaggerFragment() {

	@Inject
	lateinit var viewModel: AddPhraseViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private lateinit var bind: FragmentEditBinding

	private val chooser = FileChooser(this, "image/*")

	private var textWatcher: TextWatcher? = null

	private val imm by lazy {
		requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
		bind.btnSave.setOnClickListener {
			viewModel.save()
		}
		bind.btnEditAudio.setOnClickListener {

		}
		bind.btnEditCountry.setOnClickListener {

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
			Permission.READ_EXTERNAL_STORAGE.requestPermission(requireActivity()) { permission ->
				if (permission) chooser.getBitmap { viewModel.setBitmapImage(it) }
			}
		}
		bind.btnSound.setOnClickListener {

		}
		lifecycleScope.launchWhenStarted {

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

		view.viewTreeObserver.addOnGlobalLayoutListener {
			globalViewModel.setShowKeyboard(view)
		}

		globalViewModel.isShowKey.observeWhile(lifecycleScope) {
			bind.editBar.visibility = if (it) View.GONE else View.VISIBLE
			bind.tilEdit.visibility = if (it) View.VISIBLE else View.GONE
		}

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
	}

}