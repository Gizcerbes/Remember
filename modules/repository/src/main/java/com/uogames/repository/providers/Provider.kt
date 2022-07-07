package com.uogames.repository.providers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class Provider {
    val ioScope = CoroutineScope(Dispatchers.IO)
}