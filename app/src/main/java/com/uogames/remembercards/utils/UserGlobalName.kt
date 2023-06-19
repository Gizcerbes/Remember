package com.uogames.remembercards.utils

import java.util.zip.CRC32

class UserGlobalName {

    val userName: String

    constructor(name: String, uid: String) {
        val src = CRC32().apply { update((name + uid).toByteArray()) }
        userName = name+ "#" + src.value
    }

    constructor(name: String){
        userName = name
    }


}