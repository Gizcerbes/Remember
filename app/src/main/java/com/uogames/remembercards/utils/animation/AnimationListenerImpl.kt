package com.uogames.remembercards.utils.animation

import android.view.animation.Animation

class AnimationListenerImpl(
    val onStart: ((Animation?) -> Unit)? = null,
    val onRepeat: ((Animation?) -> Unit)? = null,
    val onEnd: ((Animation?) -> Unit)? = null,
) : AnimationListener() {

    override fun onAnimationStart(p0: Animation?) = onStart?.let { it(p0) } ?: Unit


    override fun onAnimationEnd(p0: Animation?) = onEnd?.let { it(p0) } ?: Unit


    override fun onAnimationRepeat(p0: Animation?) = onRepeat?.let { it(p0) } ?: Unit


}