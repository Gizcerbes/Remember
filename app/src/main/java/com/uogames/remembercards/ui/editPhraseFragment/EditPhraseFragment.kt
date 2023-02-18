package com.uogames.remembercards.ui.editPhraseFragment

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.text.TextWatcher
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
import androidx.navigation.navOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import com.uogames.dto.local.LocalImage
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.findNavHostFragment
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditPhraseBinding
import com.uogames.remembercards.ui.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject

class EditPhraseFragment : DaggerFragment() {

    companion object {
        const val ID_PHRASE = "ID_PHRASE"
        const val CREATE_FOR = "EditPhraseFragment_CREATE_FOR"
        const val POP_BACK_TO = "EditPhraseFragment_POP_BACK_TO"
    }

    @Inject
    lateinit var model: EditPhraseViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var cropViewModel: CropViewModel

    private var observers: Job? = null

    private var _bind: FragmentEditPhraseBinding? = null
    private val bind get() = _bind!!

    private val chooser = FileChooser(this, "image/*")

    private var recorder: MediaRecorder? = null

    private var textWatcher: TextWatcher? = null

    private var bottomSheet: BottomSheetBehavior<View>? = null

    private var imm: InputMethodManager? = null

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
        globalViewModel.shouldReset.ifTrue { model.reset() }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        val createForCard = arguments?.getString(CREATE_FOR)
        val popBackTo = arguments?.getInt(POP_BACK_TO)

        val idPhrase = arguments?.getInt(ID_PHRASE) ?: 0

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        cropViewModel.getData()?.let { bitmap ->
            model.setBitmapImage(bitmap)
            cropViewModel.reset()
        }

        if (idPhrase > 0) {
            bind.txtFragmentName.text = getString(R.string.edit_phrase)
            bind.btnDelete.visibility = View.VISIBLE
            globalViewModel.shouldReset.ifTrue { model.loadByID(idPhrase) }
            val r = { res: Boolean -> if (res) findNavController().popBackStack() }
            bind.btnSave.setOnClickListener { model.update(idPhrase, r) }
            bind.btnDelete.setOnClickListener { model.delete(idPhrase, r) }
        } else {
            bind.btnSave.setOnClickListener {
                model.save { res ->
                    if (createForCard != null && popBackTo != null && popBackTo != 0) {
                        setFragmentResult(createForCard, bundleOf("ID" to res.toInt()))
                        findNavController().popBackStack(popBackTo, true)
                    } else {
                        findNavController().popBackStack()
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

        lifecycleScope.launchWhenStarted {
            delay(250)
            bind.rvImages.adapter = model.adapter
        }

        bind.btnNewFile.setOnClickListener {
            chooser.getBitmap {
                cropViewModel.putData(it)
                findNavHostFragment().navigate(
                    R.id.cropFragment,
                    null,
                    navOptions {
                        anim {
                            enter = R.anim.from_bottom
                            exit = R.anim.hide
                            popEnter = R.anim.show
                            popExit = R.anim.to_bottom
                        }
                    }
                )
            }
        }

        bind.blind.setOnClickListener { bottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN }

        bind.btnEditAudio.setOnClickListener {
            Permission.RECORD_AUDIO.requestPermission(requireActivity()) {
                if (it && !model.isFileWriting.value) {
                    recorder = if (Build.VERSION.SDK_INT < 31) {
                        MediaRecorder()
                    } else {
                        MediaRecorder(requireContext())
                    }
                    recorder?.let { recorder -> model.startRecordAudio(recorder) }
                } else {
                    recorder?.let { recorder -> model.stopRecordAudio(recorder) }
                }
            }
        }

        bind.btnEditPhrase.setOnClickListener {
            textWatcher = setTextWatcher(textWatcher, model.phrase.value) { text, _, _, _ ->
                text?.let { model.setPhrase(it.toString()) }
                imm?.currentInputMethodSubtype?.languageTag?.let {
                    if (it.isNotEmpty()) model.setLang(Locale.forLanguageTag(it))
                }
            }
        }

        bind.btnEditDefinition.setOnClickListener {
            textWatcher = setTextWatcher(textWatcher, model.definition.value.orEmpty()) { text, _, _, _ ->
                text?.let { model.setDefinition(it.toString()) }
            }
        }

        bind.btnEditImage.setOnClickListener {
            if (model.imgPhrase.value == null) {
                bottomSheet?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            } else {
                model.setBitmapImage(null)
            }
        }

        bind.btnSound.setOnClickListener { model.play(bind.imgBtnSound.background.asAnimationDrawable()) }

        bind.btnBack.setOnClickListener { findNavHostFragment().popBackStack() }

        bind.btnEditLanguage.setOnClickListener {
            ChoiceLanguageDialog(model.languages.value) {
                model.forceSetLang(it)
            }.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
        }

        observers = lifecycleScope.launchWhenStarted {
            globalViewModel.isShowKey.observe(this) {
                bind.editBar.visibility = if (it) View.GONE else View.VISIBLE
                bind.tilEdit.visibility = if (it) View.VISIBLE else View.GONE
            }
            model.phrase.observe(this) {
                bind.txtPhrase.text = it.ifEmpty { requireContext().getString(R.string.phrase2) }
            }
            model.definition.observe(this) {
                bind.txtDefinition.text = it.ifNullOrEmpty { requireContext().getString(R.string.definition) }
            }
            model.lang.observe(this) {
                bind.txtLang.text = it.displayLanguage
            }
            model.isFileWriting.observe(this) {
                val size = model.tempAudioSource.size.ifNull { 0L }
                bind.btnSound.visibility = if (it || size <= 0L) View.GONE else View.VISIBLE
                bind.imgMic.background.asAnimationDrawable().selectDrawable(if (it) 1 else 0)
                bind.txtEditor.text = if (!model.isFileWriting.value) {
                    requireContext().getText(R.string.editor)
                } else {
                    requireContext().getText(R.string.record_sp).toString().replace("||TIME||", model.timeWriting.value.toString())
                }
            }
            model.timeWriting.observe(this) {
                bind.txtEditor.text = if (model.isFileWriting.value) {
                    requireContext().getText(R.string.record_sp).toString().replace("||TIME||", it.toString())
                } else {
                    requireContext().getText(R.string.editor)
                }
            }
            model.imgPhrase.observe(this) {
                setImagePhrase(it)
                if (it != null) bottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN
            }
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
        imm?.showSoftInput(bind.tilEdit.editText, InputMethodManager.SHOW_IMPLICIT)
        return textWatcher
    }

    private fun setImagePhrase(image: LocalImage?) = image?.let {
        Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(bind.imgPhrase)
        bind.imgPhrase.visibility = View.VISIBLE
        bind.imgBtnImage.setImageResource(R.drawable.ic_baseline_hide_image_24)
    }.ifNull {
        bind.imgPhrase.visibility = View.GONE
        bind.imgBtnImage.setImageResource(R.drawable.ic_baseline_image_24)
    }

    override fun onStop() {
        super.onStop()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        recorder?.let { model.stopRecordAudio(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observers?.cancel()
        bind.tilEdit.editText?.removeTextChangedListener(textWatcher)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        imm = null
        bottomSheet = null
        bind.rvImages.adapter = null
        _bind = null
    }
}
