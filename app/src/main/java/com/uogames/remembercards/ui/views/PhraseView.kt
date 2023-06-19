package com.uogames.remembercards.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import java.util.Locale

class PhraseView(context: Context?, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val bind = CardPhraseBinding.inflate(LayoutInflater.from(context), this, false)

    private val ocImages = arrayOf(R.drawable.ic_baseline_keyboard_arrow_down_24, R.drawable.ic_baseline_keyboard_arrow_up_24)

    var isOpened: Boolean = false
        set(value) {
            field = value
            bind.imgAction.setImageResource(ocImages[if (value) 1 else 0])
            bind.llBtns.visibility = if (value) VISIBLE else GONE
        }

    var language: Locale = Locale.getDefault()
        set(value) {
            field = value
            bind.txtLang.text = value.displayLanguage
        }

    var phrase: String = ""
        set(value) {
            field = value
            bind.txtPhrase.text = field
        }

    var definition: String = ""
        set(value) {
            field = value
            bind.txtDefinition.text = field
        }

    var showImage: Boolean = false
        set(value) {
            field = value
            bind.imgPhrase.visibility = if (value) VISIBLE else GONE
        }

    var showButtonSound: Boolean = false
        set(value) {
            field = value
            bind.btnSound.visibility = if (value) VISIBLE else GONE
        }

    var showButtonReport: Boolean = false
        set(value) {
            field = value
            bind.btnReport.visibility = if (value) VISIBLE else GONE
        }

    var showProgressLoading: Boolean = false
        set(value) {
            field = value
            bind.progressLoading.visibility = if (value) VISIBLE else GONE
        }

    var showButtonStop: Boolean = false
        set(value) {
            field = value
            bind.btnStop.visibility = if (value) VISIBLE else GONE
        }

    var showButtonDownload: Boolean = false
        set(value) {
            field = value
            bind.btnDownload.visibility = if (value) VISIBLE else GONE
        }

    var showButtonShare: Boolean = false
        set(value) {
            field = value
            bind.btnShare.visibility = if (value) VISIBLE else GONE
        }

    var showButtonEdit: Boolean = false
        set(value) {
            field = value
            bind.btnEdit.visibility = if (value) VISIBLE else GONE
        }

    var showButtonAdd: Boolean = false
        set(value) {
            field = value
            bind.btnAdd.visibility = if (value) VISIBLE else GONE
        }

    var showButtonAction: Boolean = true
        set(value) {
            field = value
            bind.btnAction.visibility = if (value) VISIBLE else GONE
        }

    var country: String = ""
        set(value) {
            field = value
            bind.txtCountry.text = field
        }

    init {
        addView(bind.root)
        val typedArray = context?.theme?.obtainStyledAttributes(
            attrs,
            R.styleable.PhraseView, 0, 0
        )

        typedArray?.apply {
            isOpened = getBoolean(R.styleable.PhraseView_opened, false)
            language = getString(R.styleable.PhraseView_language_tag).orEmpty().let { Locale.forLanguageTag(it) }
            phrase = getString(R.styleable.PhraseView_phrase).orEmpty()
            definition = getString(R.styleable.PhraseView_definition).orEmpty()
            showImage = getBoolean(R.styleable.PhraseView_show_image, false)
            showButtonSound = getBoolean(R.styleable.PhraseView_show_button_sound, false)
            showButtonReport = getBoolean(R.styleable.PhraseView_show_button_report, false)
            showProgressLoading = getBoolean(R.styleable.PhraseView_show_progress_loading, false)
            showButtonStop = getBoolean(R.styleable.PhraseView_show_button_stop, false)
            showButtonDownload = getBoolean(R.styleable.PhraseView_show_button_download, false)
            showButtonShare = getBoolean(R.styleable.PhraseView_show_button_share, false)
            showButtonEdit = getBoolean(R.styleable.PhraseView_show_button_edit, false)
            showButtonAdd = getBoolean(R.styleable.PhraseView_show_button_add, false)
            showButtonAction = getBoolean(R.styleable.PhraseView_show_buttons, true)
        }?.recycle()

        bind.btnAction.setOnClickListener {
            isOpened = !isOpened
        }

    }

    fun setOnClickButtonSound(show: Boolean = true, l: OnClickListener?) {
        bind.btnSound.setOnClickListener { l?.onClick(bind.imgBtnSound) }
        showButtonSound = show && l != null
    }

    fun setOnClickButtonReport(show: Boolean = true, l: OnClickListener?) {
        bind.btnReport.setOnClickListener(l)
        showButtonReport = show && l != null
    }

    fun setOnClickButtonStop(show: Boolean = true, l: OnClickListener?) {
        bind.btnStop.setOnClickListener(l)
        showButtonStop = show && l != null
    }

    fun setOnClickButtonDownload(show: Boolean = true, l: OnClickListener?) {
        bind.btnDownload.setOnClickListener(l)
        showButtonDownload = show && l != null
    }

    fun setOnClickButtonShare(show: Boolean = true, l: OnClickListener?) {
        bind.btnShare.setOnClickListener(l)
        showButtonShare = show && l != null
    }

    fun setOnClickListenerButtonEdit(show: Boolean = true, l: OnClickListener?) {
        bind.btnEdit.setOnClickListener(l)
        showButtonEdit = show && l != null
    }

    fun setOnClickButtonAdd(show: Boolean = true, l: OnClickListener?) {
        bind.btnAdd.setOnClickListener(l)
        showButtonAdd = show && l != null
    }

    fun getImageView() = bind.imgPhrase

    fun reset() {
        isOpened = false
        language = Locale.getDefault()
        phrase = ""
        definition = ""
        showImage = false
        setOnClickButtonSound(false, null)
        setOnClickButtonReport(false, null)
        showProgressLoading = false
        setOnClickButtonStop(false, null)
        setOnClickButtonDownload(false, null)
        setOnClickButtonShare(false, null)
        setOnClickListenerButtonEdit(false, null)
        setOnClickButtonAdd(false, null)
    }

}