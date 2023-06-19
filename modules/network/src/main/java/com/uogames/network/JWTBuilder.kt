package com.uogames.network

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.util.*
import java.util.*
import kotlin.experimental.xor

object JWTBuilder {


	fun create(secret: String, data: Map<String, String>, time: Int = 6000): String {
		var byteCode: Byte = 0
		return JWT.create().apply {
			data.forEach { (t, u) ->
				u.toByteArray().forEach { b -> byteCode = byteCode.xor(b) }
			}
			withExpiresAt(Date(System.currentTimeMillis() + time))
			withClaim("stringMap", data)
		}.sign(Algorithm.HMAC256("$secret$byteCode".encodeBase64()))
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