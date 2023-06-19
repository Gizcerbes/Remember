package com.uogames.network.map

import com.uogames.dto.global.GlobalPronunciation
import com.uogames.network.response.PronunciationResponse

object PronunciationMap : Map<PronunciationResponse, GlobalPronunciation> {
	override fun PronunciationResponse.toDTO() = GlobalPronunciation(
		globalId = globalId,
		globalOwner = globalOwner,
		audioUri = audioUri
	)


	override fun GlobalPronunciation.toResponse() = PronunciationResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		audioUri = audioUri
	)
}