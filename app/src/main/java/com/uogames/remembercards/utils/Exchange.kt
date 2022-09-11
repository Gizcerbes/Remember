package com.uogames.remembercards.utils

import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalTypeInference

suspend fun <T> Flow<T>.next(skip: Int = 1): T {
	var count = 0
	return first {
		skip == count++
	}
}

fun <T> Flow<T>.observeWhile(
	scope: CoroutineScope,
	dispatcher: CoroutineContext = scope.coroutineContext,
	checkBefore: (T) -> Boolean = { true },
	checkAfter: (T) -> Boolean = { true },
	listener: suspend (T) -> Unit
): Job = scope.launch(dispatcher) {
	collect {
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
	listener: suspend CoroutineScope.(T) -> Unit
): Job = scope.launchWhenStarted {
	collect() { listener(it) }
}

inline fun <C> C?.ifNull(defaultValue: () -> C): C =
	this ?: defaultValue()

inline fun <C : CharSequence?> C.ifNullOrEmpty(defaultValue: () -> C): C {
	return if (isNullOrEmpty()) {
		defaultValue()
	} else {
		this
	}
}

inline fun Boolean.ifTrue(body: () -> Unit): Boolean {
	if (this) body()
	return this
}

inline fun Boolean.ifFalse(body: () -> Unit): Boolean {
	if (!this) body()
	return this
}

inline fun <C> safely(catcher: (Exception) -> C? = { null }, run: () -> C?): C? {
	return try {
		run()
	} catch (e: Exception) {
		catcher(e)
	}
}

fun <C : Drawable> C.asAnimationDrawable(): AnimationDrawable = this as AnimationDrawable
