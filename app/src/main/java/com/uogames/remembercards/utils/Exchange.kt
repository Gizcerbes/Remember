package com.uogames.remembercards.utils

import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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
			return@collect this.cancel()
		} else {
			listener(it)
		}
		if (!checkAfter(it)) this.cancel()
	}
}

fun <T> Flow<T>.observeWhenStarted(
	scope: LifecycleCoroutineScope,
	listener: (T) -> Unit
): Job = scope.launchWhenStarted {
	collect() { listener(it) }
}

inline fun <C> C?.ifNull(defaultValue: () -> C): C =
	this ?: defaultValue()


inline fun <C : CharSequence?> C.ifNullOrEmpty(defaultValue: () -> C): C {
	return if (isNullOrEmpty()) defaultValue()
	else this
}

fun <C : Drawable> C.asAnimationDrawable(): AnimationDrawable = this as AnimationDrawable
