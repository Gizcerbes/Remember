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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import com.uogames.dto.local.LocalImage
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.findNavHostFragment
import com.uogames.remembercards.MainActivity.Companion.navigate
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

    private val phraseWatcher: TextWatcher = createPhraseWatcher()
    private val definitionWatcher: TextWatcher = createDefinitionWatcher()

    private var bottomSheet: BottomSheetBehavior<View>? = null

    private val audioIcons = listOf(R.drawable.ic_baseline_volume_add_24, R.drawable.ic_baseline_volume_remove_24)
    private val previewIcons = listOf(R.drawable.ic_preview_show, R.drawable.ic_preview_hide)
    private val addIcons = listOf(R.drawable.ic_baseline_add_24, R.drawable.ic_baseline_remove_24)
    private val micIcons = listOf(R.drawable.ic_baseline_mic_24, R.drawable.ic_baseline_mic_off_24)

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
            globalViewModel.shouldReset.ifTrue {
                lifecycleScope.launchWhenStarted {
                    model.loadByID(idPhrase).join()
                    bind.tilEditPhrase.editText?.setText(model.phrase.value)
                    bind.tilEditPhrase.editText?.setSelection(model.phrase.value.length)
                    bind.tilEditDefinition.editText?.setText(model.definition.value.orEmpty())
                    bind.tilEditDefinition.editText?.setSelection(model.definition.value.orEmpty().length)
                }
            }
            val r = { res: Boolean -> if (res) findNavController().popBackStack() }
            bind.btnSave.setOnLongClickListener {
                model.update(idPhrase, r)
                true
            }
            bind.btnDelete.setOnLongClickListener {
                model.delete(idPhrase, r)
                true
            }

        } else {
            bind.btnSave.setOnLongClickListener {
                model.save { res ->
                    if (createForCard != null && popBackTo != null && popBackTo != 0) {
                        setFragmentResult(createForCard, bundleOf("ID" to res.toInt()))
                        findNavController().popBackStack(popBackTo, true)
                    } else {
                        findNavController().popBackStack()
                    }
                }
                true
            }
        }

        bind.btnSave.setOnClickListener { Toast.makeText(requireContext(), requireContext().getText(R.string.press_to_save), Toast.LENGTH_SHORT).show() }
        bind.btnDelete.setOnClickListener { Toast.makeText(requireContext(), requireContext().getText(R.string.press_to_delete), Toast.LENGTH_SHORT).show() }

        bind.tilEditPhrase.editText?.setText(model.phrase.value)
        bind.tilEditPhrase.editText?.setSelection(model.phrase.value.length)
        bind.tilEditDefinition.editText?.setText(model.definition.value.orEmpty())
        bind.tilEditDefinition.editText?.setSelection(model.definition.value.orEmpty().length)

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
                navigate(R.id.cropFragment)
            }
        }

        bind.blind.setOnClickListener { bottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN }

        bind.btnRecord.setOnClickListener {
            Permission.RECORD_AUDIO.requestPermission(requireActivity()) {
                if (it && model.dataSize.value == 0) {
                    model.showRecord.value = true
                } else {
                    model.stopRecordAudio()
                    model.resetAudioData()
                }
            }
        }

        bind.btnCloseRecorder.setOnClickListener {
            model.showRecord.value = false
            model.stopRecordAudio()
        }

        bind.mcvImgEdit.setOnClickListener {
            if (model.imgPhrase.value == null) {
                bottomSheet?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            } else {
                model.setBitmapImage(null)
            }
        }

        bind.btnDeleteRecord.setOnClickListener { model.resetAudioData() }

        bind.btnSound.setOnClickListener { model.play(bind.imgBtnSound.background.asAnimationDrawable()) }

        bind.btnBack.setOnClickListener { findNavHostFragment().popBackStack() }

        bind.btnPreview.setOnClickListener { model.preview.setOpposite() }

        bind.mcvEditLanguage.setOnClickListener {
            ChoiceLanguageDialog(model.languages.value) {
                model.forceSetLang(it)
            }.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
        }

        bind.btnStartRecord.setOnLongClickListener {
            if (!model.isFileWriting.value) model.startRecordAudio(createMediaRecorder())
            else model.stopRecordAudio()
            true
        }

        bind.btnStartRecord.setOnClickListener {
            model.stopRecordAudio()
        }

        bind.tilEditPhrase.editText?.addTextChangedListener(phraseWatcher)
        bind.tilEditDefinition.editText?.addTextChangedListener(definitionWatcher)

        bind.btnPlay.setOnClickListener { model.play(bind.ivBtnPlay.background.asAnimationDrawable()) }

        observers = lifecycleScope.launchWhenStarted {

            model.phrase.observe(this) {
                bind.txtPhrase.text = it
            }
            model.definition.observe(this) {
                bind.txtDefinition.text = it.orEmpty()
            }
            model.lang.observe(this) {
                bind.txtLang.text = it.displayLanguage
                bind.txtEditLanguage.text = it.displayLanguage
            }
            model.isFileWriting.observe(this) {
                val size = model.tempAudioSource.size.ifNull { 0L }
                bind.btnSound.visibility = if (it || size <= 0L) View.GONE else View.VISIBLE
                bind.ivRecordBtn.setImageResource(micIcons[if (it) 1 else 0])
                bind.tvPressToRecord.text = requireContext().getText(if (it) R.string.click_to_stop else R.string.press_to_record)
            }
            model.timeWriting.observe(this) {
                bind.txtStatusRecording.text = requireContext().getText(R.string.status_record_sp).toString().replace("||TIME||", model.timeWriting.value.toString())
            }
            model.imgPhrase.observe(this) {
                setImagePhrase(it)
                Picasso.get().load(it?.imgUri?.toUri()).placeholder(R.drawable.noise).into(bind.ivEditImagePhrase)
                if (it != null) bottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN
                bind.ivIconEditImage.setImageResource(addIcons[if (it == null) 0 else 1])

            }
            model.dataSize.observe(this) {
                bind.imgBtnRecord.setImageResource(audioIcons[if (it == 0) 0 else 1])
                bind.btnPlay.isEnabled = it != 0
                bind.ivBtnPlay.isEnabled = it != 0
                bind.ivBtnDeleteRecord.isEnabled = it != 0
                bind.btnDeleteRecord.isEnabled = it != 0
            }
            model.preview.observe(this) {
                bind.imgPreview.setImageResource(previewIcons[if (it) 1 else 0])
                bind.mcvCard.visibility = if (it) View.VISIBLE else View.GONE
                bind.mcEditor.visibility = if (it) View.GONE else View.VISIBLE
            }
            model.showRecord.observe(this) {
                bind.clRecord.visibility = if (it) View.VISIBLE else View.GONE
            }
            model.statusRecord.observe(this) {
                when (it) {
                    0 -> bind.txtStatusRecording.text = requireContext().getText(R.string.status_ready)
                    1 -> bind.txtStatusRecording.text = requireContext().getText(R.string.status_record_sp).toString().replace("||TIME||", model.timeWriting.value.toString())
                    2 -> bind.txtStatusRecording.text = requireContext().getText(R.string.status_recorded)
                }
            }
        }
    }

    private fun createMediaRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT < 31) {
            MediaRecorder()
        } else {
            MediaRecorder(requireContext())
        }
    }

    private fun createPhraseWatcher() = ShortTextWatcher { text ->
        model.setPhrase(text?.toString().orEmpty())
        imm?.currentInputMethodSubtype?.languageTag?.let {
            if (it.isNotEmpty()) model.setLang(Locale.forLanguageTag(it))
        }
    }

    private fun createDefinitionWatcher() = ShortTextWatcher { text ->
        model.setDefinition(text?.toString().orEmpty())
    }

    private fun setImagePhrase(image: LocalImage?) = image?.let {
        Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(bind.imgPhrase)
        bind.imgPhrase.visibility = View.VISIBLE
    }.ifNull {
        bind.imgPhrase.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        model.stopRecordAudio()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observers?.cancel()
        bind.tilEditPhrase.editText?.removeTextChangedListener(phraseWatcher)
        bind.tilEditDefinition.editText?.removeTextChangedListener(definitionWatcher)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        imm = null
        bottomSheet = null
        bind.rvImages.adapter = null
        _bind = null
    }
}
