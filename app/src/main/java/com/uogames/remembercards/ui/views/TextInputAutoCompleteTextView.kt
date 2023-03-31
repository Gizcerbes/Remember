package com.uogames.remembercards.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

class TextInputAutoCompleteTextView(context: Context, attrs: AttributeSet? = null): AppCompatAutoCompleteTextView(context, attrs) {


    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        return super.onCreateInputConnection(outAttrs)
    }

}