package com.uogames.network

import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.util.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.experimental.xor

object JWTBuilder {

	private class DataInfo(val oldData: Map<String, String>, val crc: Byte)

	private var dataInfo = DataInfo(mapOf(), 0)

	fun create(secret: String, data: Map<String, String>, time: Int = 6000): String {
		return JWT.create().apply {
			if (dataInfo.oldData != data) synchronized(this) {
				var byteCode: Byte = 0
				data.forEach { (t, u) ->
					u.toByteArray().forEach { b -> byteCode = byteCode.xor(b) }
				}
				dataInfo = DataInfo(data, byteCode)
			}
			withExpiresAt(Date(System.currentTimeMillis() + time))
			withClaim("stringMap", data)
		}.sign(Algorithm.HMAC256("$secret${dataInfo.crc}".encodeBase64()))
	}

	fun check(secret: String, token: String): DecodedJWT {
		val decoded = JWT.decode(token)
		var byteCode: Byte = 0
		val data = decoded.getClaim("stringMap").asMap()
		data.forEach {
			it.value.toString().toByteArray().forEach { b -> byteCode = byteCode.xor(b) }
		}
		val res = JWT.require(Algorithm.HMAC256("$secret$byteCode".encodeBase64())).build()
		return res.verify(token)
	}


}