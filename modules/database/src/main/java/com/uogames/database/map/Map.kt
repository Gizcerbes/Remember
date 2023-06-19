package com.uogames.database.map

interface Map<E, D> {

    fun E.toDTO(): D

    fun D.toEntity(): E
}

interface ViewMap<E,D> {

    suspend fun toDTO(entity: E): D

    suspend fun toEntity(dto: D): E

}