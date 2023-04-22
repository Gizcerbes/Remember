package com.uogames.remembercards.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import java.util.Locale

class CardView(context: Context?, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val bind = CardCardBinding.inflate(LayoutInflater.from(context), this, false)

    private val ocImages = arrayOf(R.drawable.ic_baseline_keyboard_arrow_down_24, R.drawable.ic_baseline_keyboard_arrow_up_24)

    var isOpened: Boolean = false
        set(value) {
            field = value
            showDefinitionFirst = value && bind.txtDefinitionFirst.text.isNotEmpty()
            showDefinitionSecond = value && bind.txtDefinitionSecond.text.isNotEmpty()
            bind.llBtns.visibility = if (value) VISIBLE else GONE
            bind.imgBtnAction.setImageResource(ocImages[if (value) 1 else 0])
        }

    var clue: String = ""
        set(value) {
            field = value
            bind.txtReason.text = value
        }

    var languageTagFirst: Locale = Locale.getDefault()
        set(value) {
            field = value
            bind.txtLangFirst.text = value.displayLanguage
        }

    var showAudioFirst: Boolean = false
        set(value) {
            field = value
            bind.imgSoundFirst.visibility = if (value) VISIBLE else GONE
        }

    var phraseFirst: String = ""
        set(value) {
            field = value
            bind.txtPhraseFirst.text = value
        }

    var definitionFirst: String = ""
        set(value) {
            field = value
            bind.txtDefinitionFirst.text = value
        }

    var showDefinitionFirst: Boolean = false
        set(value) {
            field = value
            bind.txtDefinitionFirst.visibility = if (value) VISIBLE else GONE
        }

    var showImageFirst: Boolean = false
        set(value) {
            field = value
            bind.imgCardFirst.visibility = if (value) VISIBLE else GONE
        }

    var languageTagSecond: Locale = Locale.getDefault()
        set(value) {
            field = value
            bind.txtLangSecond.text = value.displayLanguage
        }

    var showAudioSecond: Boolean = false
        set(value) {
            field = value
            bind.imgSoundSecond.visibility = if (value) VISIBLE else GONE
        }

    var phraseSecond: String = ""
        set(value) {
            field = value
            bind.txtPhraseSecond.text = value
        }

    var definitionSecond: String = ""
        set(value) {
            field = value
            bind.txtDefinitionSecond.text = value
        }

    var showDefinitionSecond: Boolean = false
        set(value) {
            field = value
            bind.txtDefinitionSecond.visibility = if (value) VISIBLE else GONE
        }

    var showImageSecond: Boolean = false
        set(value) {
            field = value
            bind.imgCardSecond.visibility = if (value) VISIBLE else GONE
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

    var showButtons: Boolean = false
        set(value) {
            field = value
            bind.btnCardAction.visibility = if (value) VISIBLE else GONE
        }

    var showButtonRemove: Boolean = false
        set(value) {
            field = value
            bind.btnRemove.visibility = if (value) VISIBLE else GONE
        }

    init {
        addView(bind.root)
        val typedArray = context?.theme?.obtainStyledAttributes(
            attrs,
            R.styleable.CardView, 0, 0
        )

        typedArray?.apply {
            //isOpened = getBoolean(R.styleable.CardView_opened, false)
            clue = getString(R.styleable.CardView_clue).orEmpty()
            languageTagFirst = getString(R.styleable.CardView_language_tag_first).orEmpty().let { Locale.forLanguageTag(it) }
            showAudioFirst = getBoolean(R.styleable.CardView_show_audio_first, false)
            phraseFirst = getString(R.styleable.CardView_phrase_first).orEmpty()
            definitionFirst = getString(R.styleable.CardView_definition_first).orEmpty()
            showDefinitionFirst = getBoolean(R.styleable.CardView_show_definition_first, false)
            showImageFirst = getBoolean(R.styleable.CardView_show_image_first, false)
            languageTagSecond = getString(R.styleable.CardView_language_tag_second).orEmpty().let { Locale.forLanguageTag(it) }
            showAudioSecond = getBoolean(R.styleable.CardView_show_audio_second, false)
            phraseSecond = getString(R.styleable.CardView_phrase_second).orEmpty()
            definitionSecond = getString(R.styleable.CardView_definition_second).orEmpty()
            showDefinitionSecond = getBoolean(R.styleable.CardView_show_definition_second, false)
            showImageSecond = getBoolean(R.styleable.CardView_show_image_second, false)
            showButtonReport = getBoolean(R.styleable.CardView_show_button_report, false)
            showProgressLoading = getBoolean(R.styleable.CardView_show_progress_loading, false)
            showButtonStop = getBoolean(R.styleable.CardView_show_button_stop, false)
            showButtonDownload = getBoolean(R.styleable.CardView_show_button_download, false)
            showButtonShare = getBoolean(R.styleable.CardView_show_button_share, false)
            showButtonEdit = getBoolean(R.styleable.CardView_show_button_edit, false)
            showButtonAdd = getBoolean(R.styleable.CardView_show_button_add, false)
            showButtons = getBoolean(R.styleable.CardView_show_buttons, false)
            isOpened = getBoolean(R.styleable.CardView_opened, false)
            showButtonRemove = getBoolean(R.styleable.CardView_show_button_remove, false)
        }?.recycle()

        bind.btnCardAction.setOnClickListener { isOpened = !isOpened }

    }

    fun setOnClickButtonAddListener(show: Boolean = true, l: OnClickListener?) {
        bind.btnAdd.setOnClickListener(l)
        showButtonAdd = show && l != null
    }

    fun setOnClickButtonCardFirst(l: OnClickListener?) {
        bind.mcvFirst.setOnClickListener { l?.onClick(bind.imgSoundFirst) }
    }

    fun getFirstImageView() = bind.imgCardFirst

    fun setOnClickButtonCardSecond(l: OnClickListener?) {
        bind.mcvSecond.setOnClickListener { l?.onClick(bind.imgSoundSecond) }
    }

    fun getSecondImageView() = bind.imgCardSecond

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

    fun setOnClickEdit(show: Boolean = true, l: OnClickListener?) {
        bind.btnEdit.setOnClickListener(l)
        showButtonEdit = show && l != null
    }

    fun setOnClickRemove(show: Boolean = true, l: OnClickListener?) {
        bind.btnRemove.setOnClickListener(l)
        showButtonRemove = show && l != null
    }

    fun setOnLongClickRemove(show: Boolean = true, l: OnLongClickListener?) {
        bind.btnRemove.setOnLongClickListener(l)
        showButtonRemove = show && l != null
    }

    fun reset() {
        isOpened = false
        clue = ""
        languageTagFirst = Locale.getDefault()
        showAudioFirst = false
        phraseFirst = ""
        definitionFirst = ""
        showDefinitionFirst = false
        showImageFirst = false
        languageTagSecond = Locale.getDefault()
        showAudioSecond = false
        phraseSecond = ""
        definitionSecond = ""
        showDefinitionSecond = false
        showImageSecond = false
        showButtonRemove = false
        setOnClickButtonAddListener(false, null)
        setOnClickButtonCardFirst(null)
        setOnClickButtonCardSecond(null)
        setOnClickButtonReport(false, null)
        setOnClickButtonStop(false, null)
        setOnClickButtonDownload(false, null)
        setOnClickButtonShare(false, null)
        setOnClickEdit(false, null)
    }

}