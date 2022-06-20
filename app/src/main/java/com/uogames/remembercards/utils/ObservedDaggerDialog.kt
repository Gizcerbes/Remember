package com.uogames.remembercards.utils

import androidx.fragment.app.DialogFragment

open class ObservedDaggerDialog<T>(private val callBack: (T) -> Unit): DialogFragment()