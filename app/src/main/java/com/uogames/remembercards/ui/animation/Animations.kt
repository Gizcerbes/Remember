package com.uogames.remembercards.ui.animation

import android.animation.AnimatorInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.animation.doOnEnd
import com.uogames.remembercards.R
import com.uogames.remembercards.utils.animation.AnimationListenerImpl
import com.uogames.remembercards.utils.asAnimatorSet

object Animations {


    fun toRightAndFromLeft(view: View, onMiddle: (() -> Unit)? = null) {
        val anim2 = AnimationUtils.loadAnimation(view.context, R.anim.from_left)
        val anim = AnimationUtils.loadAnimation(view.context, R.anim.to_right).apply {
            setAnimationListener(AnimationListenerImpl {
                onMiddle?.let { it1 -> it1() }
                view.startAnimation(anim2)
            })
        }
        view.startAnimation(anim)
    }

    fun toLeftAndFromRight(view: View, onMiddle: (() -> Unit)? = null) {
        val anim2 = AnimationUtils.loadAnimation(view.context, R.anim.from_right)
        val anim = AnimationUtils.loadAnimation(view.context, R.anim.to_left).apply {
            setAnimationListener(AnimationListenerImpl {
                onMiddle?.let { it1 -> it1() }
                view.startAnimation(anim2)
            })
        }
        view.startAnimation(anim)
    }

    fun flipOver(view: View, onMiddle: (() -> Unit)? = null) {
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val anim2 = AnimatorInflater.loadAnimator(view.context, R.animator.flip_in).apply {
            setTarget(view)
            doOnEnd { view.setLayerType(View.LAYER_TYPE_NONE, null) }
        }
        val anim = AnimatorInflater.loadAnimator(view.context, R.animator.flip_out).asAnimatorSet().apply {
            setTarget(view)
            doOnEnd {
                onMiddle?.let { it1 -> it1() }
                anim2.start()
            }
        }
        anim.start()
    }

    fun rotate360(view: View) {
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        AnimatorInflater.loadAnimator(view.context, R.animator.rorate).apply {
            setTarget(view)
            doOnEnd { view.setLayerType(View.LAYER_TYPE_NONE, null) }
        }.start()
    }

    fun shake(view: View, onEnd: (() -> Unit)? = null) {
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val anim3 = AnimatorInflater.loadAnimator(view.context, R.animator.roll_center).apply {
            setTarget(view)
            doOnEnd {
                onEnd?.let { it1 -> it1() }
                view.setLayerType(View.LAYER_TYPE_NONE, null)
            }
        }
        val anim2 = AnimatorInflater.loadAnimator(view.context, R.animator.roll_right_to_left).apply {
            setTarget(view)
            doOnEnd { anim3.start() }
        }
        AnimatorInflater.loadAnimator(view.context, R.animator.roll_left).apply {
            setTarget(view)
            doOnEnd { anim2.start() }
        }.start()
    }

    fun accept(view: View, onEnd: (() -> Unit)? = null){
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val anim2 = AnimatorInflater.loadAnimator(view.context, R.animator.accept_to_center).apply {
            setTarget(view)
            doOnEnd {
                onEnd?.let { it1 -> it1() }
                view.setLayerType(View.LAYER_TYPE_NONE, null)
            }
        }
        AnimatorInflater.loadAnimator(view.context, R.animator.accept_to_botom).apply {
            setTarget(view)
            doOnEnd { anim2.start() }
        }.start()
    }

}