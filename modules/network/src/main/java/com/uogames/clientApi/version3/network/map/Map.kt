package com.uogames.clientApi.version3.network.map

interface Map<E, D> {

	fun E.toDTO(): D

	fun D.toResponse(): E
}