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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.uogames.dto.local.Phrase
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditCardBinding
import com.uogames.remembercards.ui.choicePhraseFragment.ChoicePhraseFragment
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.utils.*
import com.uogames.repository.DataProvider.Companion.toImage
import com.uogames.repository.DataProvider.Companion.toPronounce
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
    lateinit var editCardViewModel: EditCardViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var cropViewModel: CropViewModel

    @Inject
    lateinit var player: ObservableMediaPlayer

    private var _bind: FragmentEditCardBinding? = null
    private val bind get() = _bind!!

    private val bundleFirst = Bundle().apply { putString(ChoicePhraseFragment.TAG, FIRST_PHRASE) }
    private val bundleSecond = Bundle().apply { putString(ChoicePhraseFragment.TAG, SECOND_PHRASE) }

    private var imm: InputMethodManager? = null

    private var keyObserver: Job? = null
    private var firstPhraseObserver: Job? = null
    private var secondPhraseObserver: Job? = null
    private var reasonObserver: Job? = null
    private val reasonTextWatcher: TextWatcher by lazy { createReasonTextWatcher() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_bind == null) _bind = FragmentEditCardBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        globalViewModel.shouldReset.ifTrue {
            editCardViewModel.reset()
        }

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val id = arguments?.getInt(EDIT_ID)?.let { if (it == 0) null else it }
        // val idChanged = editCardViewModel.setArgCardId(id)

        val createFor = arguments?.getString(CREATE_FOR)
        val popBackTo = arguments?.getInt(POP_BACK_TO)

        setFragmentResultListener(FIRST_PHRASE) { _, b ->
            editCardViewModel.selectFirstPhrase(b.getInt("ID"))
        }

        setFragmentResultListener(SECOND_PHRASE) { _, b ->
            editCardViewModel.selectSecondPhrase(b.getInt("ID"))
        }

        id?.let {
            loadDataWithID(id)
        }.ifNull {
            loadData(createFor, popBackTo)
        }

        bind.btnBack.setOnClickListener {
            findNavController().popBackStack()
            editCardViewModel.resetID()
        }

        bind.btnEditFist.setOnClickListener { openChoicePhraseFragment(bundleFirst) }

        bind.btnEditSecond.setOnClickListener { openChoicePhraseFragment(bundleSecond) }

        bind.btnEditReason.setOnClickListener {
            bind.tilEditReason.requestFocus()
            bind.tilEditReason.editText?.setText(editCardViewModel.reason.value)
            bind.tilEditReason.editText?.setSelection(editCardViewModel.reason.value.length)
            imm?.showSoftInput(bind.tilEditReason.editText, InputMethodManager.SHOW_IMPLICIT)
        }

        bind.tilEditReason.editText?.removeTextChangedListener(reasonTextWatcher)
        bind.tilEditReason.editText?.addTextChangedListener(reasonTextWatcher)

        keyObserver = createKeyObserveJob()
        firstPhraseObserver = createFirstPhraseObserveJob()
        secondPhraseObserver = createSecondPhraseObserverJob()
        reasonObserver = createReasonObserveJob()
    }

    private fun loadDataWithID(id: Int) {
        globalViewModel.shouldReset.ifTrue { editCardViewModel.load(id) }
        bind.btnDelete.visibility = View.VISIBLE
        bind.btnDelete.setOnClickListener {
            editCardViewModel.delete {
                if (it) {
                    findNavController().popBackStack()
                }
            }
        }
        bind.btnSave.setOnClickListener {
            editCardViewModel.update {
                if (it) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun loadData(createFor: String?, popBackTo: Int?) {
        bind.btnDelete.visibility = View.GONE
        bind.btnSave.setOnClickListener {
            editCardViewModel.save { res ->
                if (res == null) return@save
                if (!createFor.isNullOrEmpty() && popBackTo != null && popBackTo != 0) {
                    setFragmentResult(createFor, bundleOf("ID" to res.toInt()))
                    findNavController().popBackStack(popBackTo, true)
                    editCardViewModel.resetID()
                } else {
                    findNavController().popBackStack()
                    editCardViewModel.resetID()
                }
            }
        }
    }

    private fun createKeyObserveJob(): Job = globalViewModel.isShowKey.observeWhenStarted(lifecycleScope) {
        bind.editBar.visibility = if (it) View.GONE else View.VISIBLE
        bind.tilEditReason.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun createReasonObserveJob(): Job = editCardViewModel.reason.observeWhenStarted(lifecycleScope) {
        bind.txtReason.text = it.ifNullOrEmpty { "*Reason" }
    }

    private fun createFirstPhraseObserveJob(): Job = editCardViewModel.firstPhrase.observeWhenStarted(lifecycleScope) {
        it?.let { phrase ->
            setButtonData(phrase, bind.mcvFirst, bind.txtPhraseFirst, bind.imgSoundFirst, bind.txtLangFirst, bind.imgCardFirst)
        }.ifNull {
            setDefaultButtonData(bind.imgSoundFirst, bind.txtPhraseFirst, bind.txtLangFirst, bind.imgCardFirst)
        }
    }

    private fun createSecondPhraseObserverJob(): Job = editCardViewModel.secondPhrase.observeWhenStarted(lifecycleScope) {
        it?.let { phrase ->
            setButtonData(phrase, bind.mcvSecond, bind.txtPhraseSecond, bind.imgSoundSecond, bind.txtLangSecond, bind.imgCardSecond)
        }.ifNull {
            setDefaultButtonData(bind.imgSoundSecond, bind.txtPhraseSecond, bind.txtLangSecond, bind.imgCardSecond)
        }
    }

    private fun createReasonTextWatcher(): TextWatcher = ShortTextWatcher {
        editCardViewModel.setReason(it.toString())
    }

    private suspend fun setButtonData(
        phrase: Phrase,
        container: MaterialCardView,
        txtPhrase: TextView,
        imgSound: ImageView,
        lang: TextView,
        imageCard: ImageView
    ) {
        txtPhrase.text = phrase.phrase.ifNullOrEmpty { requireContext().getString(R.string.phrase_label) }
        lang.text = showLang(phrase)

        phrase.toPronounce()?.let { pronounce ->
            imgSound.visibility = View.VISIBLE
            container.setOnClickListener {
                player.play(requireContext(), pronounce.audioUri.toUri(), imgSound.background.asAnimationDrawable())
            }
        }.ifNull {
            imgSound.visibility = View.GONE
        }

        phrase.toImage()?.let {
            Picasso.get().load(it.imgUri.toUri()).placeholder(R.drawable.noise).into(imageCard)
            imageCard.visibility = View.VISIBLE
        }.ifNull {
            imageCard.visibility = View.GONE
        }
    }

    private fun setDefaultButtonData(imgSound: ImageView, txtPhrase: TextView, txtLang: TextView, imageCard: ImageView) {
        imgSound.visibility = View.GONE
        txtPhrase.text = requireContext().getString(R.string.phrase_label)
        txtLang.text = Locale.getDefault().displayLanguage
        imageCard.visibility = View.GONE
    }

    private fun showLang(phrase: Phrase): String {
        return safely {
            val data = phrase.lang.split("-")
            Locale(data[0]).displayLanguage
        }.orEmpty()
    }

    private fun openChoicePhraseFragment(bundle: Bundle) {
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(
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
        keyObserver?.cancel()
        firstPhraseObserver?.cancel()
        secondPhraseObserver?.cancel()
        reasonObserver?.cancel()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        bind.tilEditReason.editText?.removeTextChangedListener(reasonTextWatcher)
        _bind = null
    }
}
