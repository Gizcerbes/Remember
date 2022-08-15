package com.uogames.network.map

import com.uogames.dto.global.Pronunciation
import com.uogames.network.response.PronunciationResponse

object PronunciationMap : Map<PronunciationResponse, Pronunciation> {
	override fun PronunciationResponse.toDTO() = Pronunciation(
		globalId = globalId,
		globalOwner = globalOwner,
		audioUri = audioUri
	)


	override fun Pronunciation.toResponse() = PronunciationResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		audioUri = audioUri
	)
}