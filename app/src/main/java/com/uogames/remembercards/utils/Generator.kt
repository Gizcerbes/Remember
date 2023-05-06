package com.uogames.remembercards.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Generator(val hz: Double, val durationMils: Int, val event: suspend (progress: Double, last: Boolean) -> Unit) {


    fun start(scope: CoroutineScope) = scope.launch {
        if (hz <= 0) return@launch
        if (durationMils <= 0) return@launch
        val start = System.currentTimeMillis()
        val finish = start + durationMils
        val step = durationMils / (1000 / hz)
        var count = 0
        var nextStep = start
        var progress = 0.0
        do {
            progress = (nextStep - start).toDouble() / durationMils
            event(progress, progress >= 100.0)
            while (nextStep < System.currentTimeMillis()) {
                count++
                nextStep = (start + step * count).toLong()
            }
            val wait = nextStep - System.currentTimeMillis()
            if (wait > 0) delay(wait)
        } while (nextStep < finish)
        if (progress < 100.0) event(100.0, true)
    }


}