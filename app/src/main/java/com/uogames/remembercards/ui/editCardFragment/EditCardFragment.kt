package com.uogames.remembercards.ui.editCardFragment

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.uogames.dto.local.LocalPhrase
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.findNavHostFragment
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditCardBinding
import com.uogames.remembercards.ui.choicePhraseFragment.ChoicePhraseFragment
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.utils.*
import com.uogames.repository.DataProvider.Companion.toImage
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

        if (id > 0) loadDataWithID(id)
        else loadData(createFor, popBackTo)

        bind.btnBack.setOnClickListener {
            findNavController().popBackStack()
            model.resetID()
        }

        bind.btnEditFist.setOnClickListener { openChoicePhraseFragment(bundleFirst) }

        bind.btnEditSecond.setOnClickListener { openChoicePhraseFragment(bundleSecond) }

        bind.btnEditReason.setOnClickListener {
            bind.tilEditReason.requestFocus()
            bind.tilEditReason.editText?.setText(model.reason.value)
            bind.tilEditReason.editText?.setSelection(model.reason.value.length)
            imm?.showSoftInput(bind.tilEditReason.editText, InputMethodManager.SHOW_IMPLICIT)
        }

        bind.tilEditReason.editText?.addTextChangedListener(reasonTextWatcher)

        clear()
        bind.btnCardAction.setOnClickListener {
            full = !full
            bind.txtDefinitionFirst.visibility = if (full && bind.txtDefinitionFirst.text.isNotEmpty()) View.VISIBLE else View.GONE
            bind.txtDefinitionSecond.visibility = if (full && bind.txtDefinitionSecond.text.isNotEmpty()) View.VISIBLE else View.GONE
            val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
            bind.imgBtnAction.setImageResource(img)
        }

        observers = lifecycleScope.launchWhenStarted {
            globalViewModel.isShowKey.observe(this) {
                bind.editBar.visibility = if (it) View.GONE else View.VISIBLE
                bind.tilEditReason.visibility = if (it) View.VISIBLE else View.GONE
            }
            model.reason.observe(this) { bind.txtReason.text = it.ifNullOrEmpty { "*Reason" } }
            model.firstPhrase.observe(this) {
                it?.let { phrase ->
                    setButtonData(
                        phrase,
                        bind.mcvFirst,
                        bind.txtPhraseFirst,
                        bind.imgSoundFirst,
                        bind.txtLangFirst,
                        bind.imgCardFirst,
                        bind.txtDefinitionFirst
                    )
                }.ifNull {
                    setDefaultButtonData(bind.imgSoundFirst, bind.txtPhraseFirst, bind.txtLangFirst, bind.imgCardFirst, bind.txtDefinitionFirst)
                }
            }
            model.secondPhrase.observe(this) {
                it?.let { phrase ->
                    setButtonData(
                        phrase,
                        bind.mcvSecond,
                        bind.txtPhraseSecond,
                        bind.imgSoundSecond,
                        bind.txtLangSecond,
                        bind.imgCardSecond,
                        bind.txtDefinitionSecond
                    )
                }.ifNull {
                    setDefaultButtonData(bind.imgSoundSecond, bind.txtPhraseSecond, bind.txtLangSecond, bind.imgCardSecond, bind.txtDefinitionSecond)
                }
            }
        }

    }

    private fun clear() {
        full = false
        bind.txtDefinitionFirst.visibility = View.GONE
        bind.txtDefinitionSecond.visibility = View.GONE
        bind.imgCardFirst.visibility = View.GONE
        bind.imgSoundFirst.visibility = View.GONE
        bind.txtDefinitionFirst.text = ""
        bind.txtLangFirst.text = ""
        bind.txtPhraseFirst.text = ""
        bind.imgCardSecond.visibility = View.GONE
        bind.imgSoundSecond.visibility = View.GONE
        bind.txtDefinitionSecond.text = ""
        bind.txtLangSecond.text = ""
        bind.txtPhraseSecond.text = ""
        bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
    }

    private fun loadDataWithID(id: Int) {
        globalViewModel.shouldReset.ifTrue { model.load(id) }
        bind.btnDelete.visibility = View.VISIBLE
        bind.btnDelete.setOnClickListener {
            model.delete {
                if (it) {
                    findNavController().popBackStack()
                }
            }
        }
        bind.btnSave.setOnClickListener { model.update { if (it) findNavController().popBackStack() } }
    }

    private fun loadData(createFor: String?, popBackTo: Int?) {
        bind.btnDelete.visibility = View.GONE
        bind.btnSave.setOnClickListener {
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
        }
    }

    private fun createReasonTextWatcher(): TextWatcher = ShortTextWatcher { model.setReason(it.toString()) }

    private suspend fun setButtonData(
        phrase: LocalPhrase,
        container: MaterialCardView,
        txtPhrase: TextView,
        imgSound: ImageView,
        lang: TextView,
        imageCard: ImageView,
        definition: TextView
    ) {
        txtPhrase.text = phrase.phrase.ifNullOrEmpty { requireContext().getString(R.string.phrase_label) }
        lang.text = showLang(phrase)
        definition.text = phrase.definition.orEmpty()

        phrase.idPronounce?.let {
            imgSound.visibility = View.VISIBLE
            container.setOnClickListener { model.play(imgSound.background.asAnimationDrawable(), phrase) }
        }.ifNull { imgSound.visibility = View.GONE }

        phrase.toImage()?.let {
            Picasso.get().load(it.imgUri.toUri()).placeholder(R.drawable.noise).into(imageCard)
            imageCard.visibility = View.VISIBLE
        }.ifNull { imageCard.visibility = View.GONE }
    }

    private fun setDefaultButtonData(
        imgSound: ImageView,
        txtPhrase: TextView,
        txtLang: TextView,
        imageCard: ImageView,
        definition: TextView
    ) {
        imgSound.visibility = View.GONE
        txtPhrase.text = requireContext().getString(R.string.phrase_label)
        txtLang.text = Locale.getDefault().displayLanguage
        imageCard.visibility = View.GONE
        definition.text = ""
    }

    private fun showLang(phrase: LocalPhrase) = Locale.forLanguageTag(phrase.lang).displayLanguage


    private fun openChoicePhraseFragment(bundle: Bundle) {
        findNavHostFragment().navigate(
            R.id.choicePhraseDialog,
            bundle,
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

    override fun onDestroyView() {
        super.onDestroyView()
        observers?.cancel()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        bind.tilEditReason.editText?.removeTextChangedListener(reasonTextWatcher)
        imm = null
        _bind = null
    }
}
