package com.uogames.remembercards.utils

import android.text.Editable
import android.text.TextWatcher

class ShortTextWatcher(val call: (text: CharSequence?) -> Unit) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        call(p0)
    }

    override fun afterTextChanged(p0: Editable?) {
    }
}
