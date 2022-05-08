package com.uogames.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class Provider {
    val mainScope = CoroutineScope(Dispatchers.Main)
    val ioScope = CoroutineScope(Dispatchers.IO)
}