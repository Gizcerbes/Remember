package com.uogames.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class Provider {
    val ioScope = CoroutineScope(Dispatchers.IO)
}