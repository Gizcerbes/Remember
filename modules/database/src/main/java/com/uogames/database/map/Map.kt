package com.uogames.database.map

interface Map<E, D> {

    fun E.toDTO(): D

    fun D.toEntity(): E
}