package com.uogames.network.map

interface Map<E, D> {

	fun E.toDTO(): D

	fun D.toResponse(): E
}