package com.uogames.remembercards.ui.views

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardSearchBinding
import com.uogames.remembercards.utils.ifNull
import java.util.Locale

class SearchView(context: Context?, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val bind = CardSearchBinding.inflate(LayoutInflater.from(context), this, false)

    var languageFirst = Locale.getDefault()
        set(value){
            field = value
            bind.tvLanguageFirst.text = value.displayLanguage
        }

    var languageSecond = Locale.getDefault()
        set(value){
            field = value
            bind.tvLanguageSecond.text = value.displayLanguage
        }

    var full = false
        set(value) {
            field = value
            val vis = if (value) VISIBLE else INVISIBLE
            bind.btnLangSecond.visibility = vis
            bind.ivArrayLanguage.visibility = vis
            bind.btnCountrySecond.visibility = vis
            bind.ivArrayCountry.visibility = vis
        }

    init {
        addView(bind.root)
        val typedArray = context?.theme?.obtainStyledAttributes(
            attrs,
            R.styleable.SearchView, 0, 0
        )

        typedArray?.apply {
            languageFirst = getString(R.styleable.SearchView_language_tag_first).orEmpty().let { Locale.forLanguageTag(it) }
            languageSecond = getString(R.styleable.SearchView_language_tag_second).orEmpty().let { Locale.forLanguageTag(it) }
            setFlagResourceFirst(getResourceId(R.styleable.SearchView_country_src_first, 0).let { if (it == 0) null else it })
            setFlagResourceSecond(getResourceId(R.styleable.SearchView_country_src_second, 0).let { if (it == 0) null else it })
            full = getBoolean(R.styleable.SearchView_full, false)
        }

    }

    fun setFlagResourceFirst(@DrawableRes res: Int?) = bind.imgFlagFirst.setImageResource(res.ifNull { R.drawable.ic_flag_earth })


    fun setFlagResourceSecond(@DrawableRes res: Int?) = bind.imgFlagSecond.setImageResource(res.ifNull { R.drawable.ic_flag_earth })

    fun setTextSearch(text: String?) {
        bind.tilSearch.editText?.setText(text)
        bind.tilSearch.editText?.setSelection(text?.length ?: 0)
    }

    fun addTextSearchWatcher(watcher: TextWatcher) = bind.tilSearch.editText?.addTextChangedListener(watcher)

    fun removeTextSearchWatcher(watcher: TextWatcher) = bind.tilSearch.editText?.removeTextChangedListener(watcher)

    fun setOnClickLanguageFirst(l: OnClickListener?) = bind.btnLangFirst.setOnClickListener(l)

    fun setOnClickLanguageSecond(l: OnClickListener?) = bind.btnLangSecond.setOnClickListener(l)

    fun setOnClickCountryFirst(l: OnClickListener?) = bind.btnCountryFirst.setOnClickListener(l)

    fun setOnClickCountrySecond(l: OnClickListener?) = bind.btnCountrySecond.setOnClickListener(l)
}