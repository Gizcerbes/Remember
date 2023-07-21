package com.uogames.database

import java.lang.StringBuilder

fun notNull(vararg any: Any?): Boolean {
	any.forEach { if (it == null) return false }
	return true
}

fun withNotNull(vararg any: Any?): Boolean {
	any.forEach { if (it != null) return true }
	return false
}

fun Array<*>.prepareQueryCollectionIN(): String {
	return "(${joinToString(",") { "?" }})"
}