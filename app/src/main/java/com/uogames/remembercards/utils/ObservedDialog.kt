package com.uogames.remembercards.utils

import androidx.fragment.app.DialogFragment

open class ObservedDialog<T>(private val callBack: (T) -> Unit) : DialogFragment() {

    fun setData(obj: T) {
        callBack(obj)
    }
}
