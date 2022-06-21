package com.uogames.remembercards.utils

import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

inline fun <T> loop(init: () -> T, check: (T) -> Boolean, step: (T) -> T, body: (T) -> Unit) {
	var i = init()
	while (check(i)) {
		body(i)
		i = step(i)
	}
}

suspend fun <T> Flow<T>.next(skip: Int = 1): T {
	var count = 0
	return first {
		skip == count++
	}
}

fun <T> Flow<T>.observeWhile(
	scope: CoroutineScope,
	checkBefore: (T) -> Boolean = { true },
	checkAfter: (T) -> Boolean = { true },
	listener: (T) -> Unit
): Job = scope.launch {
	collect() {
		if (!checkBefore(it)) {
			this.cancel()
			return@collect
		} else {
			listener(it)
		}
		if (!checkAfter(it)) this.cancel()
	}
}

fun ByteArray.toStringBase64(): String = Base64.encodeToString(this, Base64.DEFAULT)

fun String.toByteArrayBase64(): ByteArray = Base64.decode(this, Base64.DEFAULT)

inline fun <C> C?.ifNull(defaultValue: () -> C): C =
	this ?: defaultValue()

inline fun <C : CharSequence?> C.ifNullOrEmpty(defaultValue: () -> C): C {
	return if (isNullOrEmpty()) defaultValue()
	else this
}