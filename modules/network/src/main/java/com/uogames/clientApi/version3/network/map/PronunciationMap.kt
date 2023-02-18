package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.response.PronunciationResponse
import com.uogames.dto.global.GlobalPronunciation

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