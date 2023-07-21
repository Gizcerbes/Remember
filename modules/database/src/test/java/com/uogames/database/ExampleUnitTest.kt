package com.uogames.database

import androidx.room.util.convertUUIDToByte
import org.junit.Test

import java.util.UUID

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        //2E024E1E32B846E7B7D96D898D022170
        val uuid = UUID.randomUUID()
        println(convertUUIDToByte(uuid).toString())

    }


}