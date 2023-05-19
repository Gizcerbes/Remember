package com.uogames.remembercards.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardModuleBinding

class CardModuleView(context: Context?, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val bind = CardModuleBinding.inflate(LayoutInflater.from(context), this, false)

    private val ocImages = arrayOf(R.drawable.ic_baseline_keyboard_arrow_down_24, R.drawable.ic_baseline_keyboard_arrow_up_24)

    var isOpened: Boolean = false
        set(value) {
            field = value
            bind.llBar.visibility = if (value) VISIBLE else GONE
            bind.imgAction.setImageResource(ocImages[if (value) 1 else 0])
        }

    var name: CharSequence = ""
        set(value) {
            field = value
            bind.txtName.text = value
        }

    var count: Int = 0
        set(value) {
            field = value
            bind.txtCountItems.text = context.getString(R.string.count_items).replace("||COUNT||", value.toString())
        }

    var owner: CharSequence = ""
        set(value) {
            field = value
            bind.txtOwner.text = value
        }

    var showReport: Boolean = false
        set(value) {
            field = value
            bind.btnReport.visibility = if (value) VISIBLE else GONE
        }

    var showProgress: Boolean = false
        set(value) {
            field = value
            bind.progressLoading.visibility = if (value) VISIBLE else GONE
        }

    var showStop: Boolean = false
        set(value) {
            field = value
            bind.btnStop.visibility = if (value) VISIBLE else GONE
        }

    var showDownload: Boolean = false
        set(value) {
            field = value
            bind.btnDownload.visibility = if (value) VISIBLE else GONE
        }

    var showUpload: Boolean = false
        set(value) {
            field = value
            bind.btnShare.visibility = if (value) VISIBLE else GONE
        }

    var showEdit: Boolean = false
        set(value) {
            field = value
            bind.btnEdit.visibility = if (value) VISIBLE else GONE
        }

    var showWatch: Boolean = false
        set(value) {
            field = value
            bind.btnShow.visibility = if (value) VISIBLE else GONE
        }

    var showActions: Boolean = false
        set(value){
            field = value
            bind.llActions.visibility = if (value) VISIBLE else GONE
        }


    init {
        addView(bind.root)

        val typedArray = context?.theme?.obtainStyledAttributes(
            attrs,
            R.styleable.CardModuleView, 0, 0
        )

        typedArray?.apply {
            name = getString(R.styleable.CardModuleView_name).orEmpty()
            count = getInt(R.styleable.CardModuleView_count, 0)
            owner = getString(R.styleable.CardModuleView_owner).orEmpty()
            isOpened = getBoolean(R.styleable.CardModuleView_opened, false)
            showReport = getBoolean(R.styleable.CardModuleView_show_button_report, false)
            showProgress = getBoolean(R.styleable.CardModuleView_show_progress_loading, false)
            showStop = getBoolean(R.styleable.CardModuleView_show_button_stop, false)
            showDownload = getBoolean(R.styleable.CardModuleView_show_button_download, false)
            showUpload = getBoolean(R.styleable.CardModuleView_show_button_share, false)
            showEdit = getBoolean(R.styleable.CardModuleView_show_button_edit, false)
            showWatch = getBoolean(R.styleable.CardModuleView_show_button_show, false)
            showActions = getBoolean(R.styleable.CardModuleView_show_buttons, false)
        }?.recycle()

        bind.btnAction.setOnClickListener { isOpened = !isOpened }

    }

    fun reset(){
        name = ""
        count = 0
        owner = ""
        isOpened = false
        setOnClickButtonReport(l = null)
        showProgress = false
        setOnButtonStop(l = null)
        setOnClickButtonDownload(l = null)
        setOnClickButtonUpload(l = null)
        setOnClickButtonEdit(l = null)
        setOnClickButtonWatch(l = null)
        showActions = false
        setOnClick(null)
    }

    fun setOnClickButtonReport(show: Boolean = true, l: OnClickListener?) {
        bind.btnReport.setOnClickListener(l)
        showReport = show && l != null
    }

    fun setOnButtonStop(show: Boolean = true, l: OnClickListener?) {
        bind.btnStop.setOnClickListener(l)
        showStop = show && l != null
    }

    fun setOnClickButtonDownload(show: Boolean = true, l: OnClickListener?) {
        bind.btnDownload.setOnClickListener(l)
        showDownload = show && l != null
    }

    fun setOnClickButtonUpload(show: Boolean = true, l: OnClickListener?) {
        bind.btnShare.setOnClickListener(l)
        showDownload = show && l != null
    }

    fun setOnClickButtonEdit(show: Boolean = true, l: OnClickListener?) {
        bind.btnEdit.setOnClickListener(l)
        showEdit = show && l != null
    }

    fun setOnClickButtonWatch(show: Boolean = true, l: OnClickListener?) {
        bind.btnShow.setOnClickListener(l)
        showWatch = show && l != null
    }

    fun setOnClick(l: OnClickListener?){
        bind.root.setOnClickListener(l)
    }

}