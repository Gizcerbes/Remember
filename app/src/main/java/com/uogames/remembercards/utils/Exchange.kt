package com.uogames.remembercards.utils

import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
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

fun <T> Flow<T?>.observeNotNull(
	scope: CoroutineScope,
	listener: suspend CoroutineScope.(T) -> Unit
): Job = scope.launch {
	collect { it?.let { it1 -> listener(it1) } }
}

fun <T> Flow<T>.observe(
	scope: CoroutineScope,
	listener: suspend CoroutineScope.(T) -> Unit
): Job = scope.launch {
	collect { listener(it) }
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

fun <C : Drawable> C.asAnimationDrawable(): AnimationDrawable = this as AnimationDrawable

fun CoroutineScope.safeLaunch(
	context: CoroutineContext = EmptyCoroutineContext,
	start: CoroutineStart = CoroutineStart.DEFAULT,
	block: suspend CoroutineScope.() -> Unit
): Job = launch(context, start) { runCatching { block() } }
