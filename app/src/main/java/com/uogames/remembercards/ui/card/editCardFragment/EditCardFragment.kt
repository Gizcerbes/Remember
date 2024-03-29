package com.uogames.remembercards.ui.card.editCardFragment

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.uogames.dto.local.LocalPhraseView
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditCardBinding
import com.uogames.remembercards.ui.phrase.choicePhraseFragment.ChoicePhraseFragment
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.*
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
    lateinit var model: EditCardViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var cropViewModel: CropViewModel

    private var _bind: FragmentEditCardBinding? = null
    private val bind get() = _bind!!

    private val bundleFirst = Bundle().apply { putString(ChoicePhraseFragment.TAG, FIRST_PHRASE) }
    private val bundleSecond = Bundle().apply { putString(ChoicePhraseFragment.TAG, SECOND_PHRASE) }

    private var imm: InputMethodManager? = null

    private var observers: Job? = null

    private val reasonTextWatcher: TextWatcher by lazy { createReasonTextWatcher() }
    private var full = false

    private val addIcons = listOf(R.drawable.ic_baseline_add_24, R.drawable.ic_baseline_remove_24)
    private val previewIcons = listOf(R.drawable.ic_preview_show, R.drawable.ic_preview_hide)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = FragmentEditCardBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalViewModel.shouldReset.ifTrue { model.reset() }

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val id = arguments?.getInt(EDIT_ID) ?: 0

        val createFor = arguments?.getString(CREATE_FOR)
        val popBackTo = arguments?.getInt(POP_BACK_TO)

        setFragmentResultListener(FIRST_PHRASE) { _, b ->
            model.selectFirstPhrase(b.getInt("ID"))
        }

        setFragmentResultListener(SECOND_PHRASE) { _, b ->
            model.selectSecondPhrase(b.getInt("ID"))
        }

        if (id > 0) {
            loadDataWithID(id)
        } else loadData(createFor, popBackTo)

        bind.btnBack.setOnClickListener {
            findNavController().popBackStack()
            model.resetID()
        }

        bind.mcvFirstPrev.setOnClickListener {
            if (model.firstPhrase.value != null) model.selectFirstPhrase(null)
            else openChoicePhraseFragment(bundleFirst)
        }

        bind.mcvSecondPrev.setOnClickListener {
            if (model.secondPhrase.value != null) model.selectSecondPhrase(null)
            else openChoicePhraseFragment(bundleSecond)
        }

        bind.txtReasonPrev.editText?.setText(model.reason.value)
        bind.txtReasonPrev.editText?.setSelection(model.reason.value.length)
        bind.txtReasonPrev.editText?.addTextChangedListener(reasonTextWatcher)
        bind.btnCardActionPrev.isEnabled = false

        bind.cvCardView.reset()
        bind.cvCardView.showButtons = true

        bind.btnPreview.setOnClickListener { model.preview.setOpposite() }

        bind.btnSave.setOnClickListener {
            Toast.makeText(requireContext(), requireContext().getText(R.string.press_to_save), Toast.LENGTH_SHORT).show()
        }

        bind.btnDelete.setOnClickListener {
            Toast.makeText(requireContext(), requireContext().getText(R.string.press_to_delete), Toast.LENGTH_SHORT).show()
        }

        bind.cbSingleCard.setOnClickListener { model.singleCard.setOpposite() }

        observers = lifecycleScope.launchWhenStarted {
            model.reason.observe(this) { bind.cvCardView.clue = it.ifNullOrEmpty { requireContext().getText(R.string.clue).toString() } }
            model.firstPhrase.observe(this) {
                it?.let { phrase ->
                    bind.cvCardView.phraseFirst = phrase.phrase
                    phrase.pronounce?.let {
                        bind.cvCardView.showAudioFirst = true
                        bind.cvCardView.setOnClickButtonCardFirst { v -> model.play(v.background.asAnimationDrawable(), phrase) }
                    }.ifNull {
                        bind.cvCardView.showAudioFirst = false
                        bind.cvCardView.setOnClickButtonCardFirst(null)
                    }
                    bind.cvCardView.phraseFirst = phrase.phrase
                    bind.cvCardView.languageTagFirst = phrase.lang.let { l -> Locale.forLanguageTag(l) }
                    bind.cvCardView.definitionFirst = phrase.definition.orEmpty()
                    phrase.image?.let { image ->
                        Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(bind.cvCardView.getFirstImageView())
                        bind.cvCardView.showImageFirst = true
                    }.ifNull { bind.cvCardView.showImageFirst = false }

                    setPreviewData(
                        phrase,
                        bind.txtPhraseFirstPrev,
                        bind.imgSoundFirstPrev,
                        bind.txtLangFirstPrev,
                        bind.imgCardFirstPrev,
                        bind.txtDefinitionFirstPrev
                    )
                }.ifNull {
                    bind.cvCardView.showAudioFirst = false
                    bind.cvCardView.phraseFirst = requireContext().getText(R.string.first_phrase).toString()
                    bind.cvCardView.languageTagFirst = Locale.getDefault()
                    bind.cvCardView.showImageFirst = false
                    bind.cvCardView.definitionFirst = requireContext().getText(R.string.fist_suggestion).toString()
                    bind.txtPhraseFirstPrev.text = requireContext().getText(R.string.first_phrase)
                    bind.imgSoundFirstPrev.visibility = View.VISIBLE
                    bind.txtLangFirstPrev.text = Locale.getDefault().displayLanguage
                    bind.imgCardFirstPrev.setImageResource(R.drawable.noise)
                    bind.imgCardFirstPrev.visibility = View.VISIBLE
                    bind.txtDefinitionFirstPrev.text = requireContext().getText(R.string.fist_suggestion)
                }
                bind.ivAddFirst.setImageResource(addIcons[if (it == null) 0 else 1])
            }
            model.secondPhrase.observe(this) {
                it?.let { phrase ->
                    bind.cvCardView.phraseSecond = phrase.phrase
                    phrase.pronounce?.let {
                        bind.cvCardView.showAudioSecond = true
                        bind.cvCardView.setOnClickButtonCardSecond { v -> model.play(v.background.asAnimationDrawable(), phrase) }
                    }.ifNull {
                        bind.cvCardView.showAudioSecond = false
                        bind.cvCardView.setOnClickButtonCardSecond(null)
                    }
                    bind.cvCardView.phraseSecond = phrase.phrase
                    bind.cvCardView.languageTagSecond = phrase.lang.let { l -> Locale.forLanguageTag(l) }
                    bind.cvCardView.definitionSecond = phrase.definition.orEmpty()
                    phrase.image?.let { image ->
                        Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(bind.cvCardView.getSecondImageView())
                        bind.cvCardView.showImageSecond = true
                    }.ifNull { bind.cvCardView.showImageSecond = false }
                    setPreviewData(
                        phrase,
                        bind.txtPhraseSecondPrev,
                        bind.imgSoundSecondPrev,
                        bind.txtLangSecondPrev,
                        bind.imgCardSecondPrev,
                        bind.txtDefinitionSecondPrev
                    )
                }.ifNull {
                    bind.cvCardView.showAudioSecond = false
                    bind.cvCardView.phraseSecond = requireContext().getText(R.string.second_phrase).toString()
                    bind.cvCardView.languageTagSecond = Locale.getDefault()
                    bind.cvCardView.showImageSecond = false
                    bind.cvCardView.definitionSecond = requireContext().getText(R.string.second_suggestion).toString()
                    bind.txtPhraseSecondPrev.text = requireContext().getText(R.string.second_phrase)
                    bind.imgSoundSecondPrev.visibility = View.VISIBLE
                    bind.txtLangSecondPrev.text = Locale.getDefault().displayLanguage
                    bind.imgCardSecondPrev.setImageResource(R.drawable.noise)
                    bind.imgCardSecondPrev.visibility = View.VISIBLE
                    bind.txtDefinitionSecondPrev.text = requireContext().getText(R.string.second_suggestion)
                }
                bind.ivAddSecond.setImageResource(addIcons[if (it == null) 0 else 1])
            }
            model.preview.observe(this) {
                bind.mcvPreview.visibility = if (it) View.GONE else View.VISIBLE
                bind.cvCardView.visibility = if (it) View.VISIBLE else View.GONE
                bind.imgPreview.setImageResource(previewIcons[if (it) 1 else 0])
            }
            model.singleCard.observe(this) { bind.cbSingleCard.isChecked = it }
            model.clues.observe(this) {
                (bind.txtReasonPrev.editText as AutoCompleteTextView).setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, it))
            }
        }

    }

    private fun loadDataWithID(id: Int) {
        globalViewModel.shouldReset.ifTrue {
            lifecycleScope.launch {
                model.load(id).join()
                bind.txtReasonPrev.editText?.setText(model.reason.value)
                bind.txtReasonPrev.editText?.setSelection(model.reason.value.length)
                bind.txtReasonPrev.editText?.addTextChangedListener(reasonTextWatcher)
                bind.btnCardActionPrev.isEnabled = false
            }
        }
        bind.btnDelete.visibility = View.VISIBLE
        bind.btnDelete.setOnLongClickListener {
            model.delete {
                if (it) {
                    findNavController().popBackStack()
                }
            }
            true
        }
        bind.btnSave.setOnLongClickListener {
            model.update { if (it) findNavController().popBackStack() }
            true
        }
    }

    private fun loadData(createFor: String?, popBackTo: Int?) {
        bind.btnDelete.visibility = View.GONE
        bind.btnSave.setOnLongClickListener {
            model.save { res ->
                if (res == null) return@save
                if (!createFor.isNullOrEmpty() && popBackTo != null && popBackTo != 0) {
                    setFragmentResult(createFor, bundleOf("ID" to res.toInt()))
                    findNavController().popBackStack(popBackTo, true)
                    model.resetID()
                } else {
                    findNavController().popBackStack()
                    model.resetID()
                }
            }
            true
        }
    }

    private fun createReasonTextWatcher(): TextWatcher = ShortTextWatcher { model.setReason(it.toString()) }

    private fun setPreviewData(
        phrase: LocalPhraseView,
        txtPhrase: TextView,
        imgSound: ImageView,
        lang: TextView,
        imageCard: ImageView,
        definition: TextView
    ) {
        txtPhrase.text = phrase.phrase.ifNullOrEmpty { requireContext().getString(R.string.phrase_label) }
        lang.text = phrase.lang.let { l -> Locale.forLanguageTag(l).displayLanguage }
        definition.text = phrase.definition.orEmpty()

        phrase.pronounce?.let {
            imgSound.visibility = View.VISIBLE
        }.ifNull { imgSound.visibility = View.GONE }

        phrase.image?.let {
            Picasso.get().load(it.imgUri.toUri()).placeholder(R.drawable.noise).into(imageCard)
            imageCard.visibility = View.VISIBLE
        }.ifNull { imageCard.visibility = View.GONE }
    }

    private fun openChoicePhraseFragment(bundle: Bundle) {
        navigate(R.id.choicePhraseDialog, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observers?.cancel()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        bind.txtReasonPrev.editText?.removeTextChangedListener(reasonTextWatcher)
        imm = null
        _bind = null
    }
}
