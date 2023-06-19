package com.uogames

class Singleton<T>(){

    private var INSTANCE: T? = null

    /**
     *  The build() -> T will call only once.
     */
    fun get(build: () -> T): T {
        if (INSTANCE == null) synchronized(this){
            if (INSTANCE == null) INSTANCE = build()
        }
        return INSTANCE!!
    }

    fun get(): T? = INSTANCE
}