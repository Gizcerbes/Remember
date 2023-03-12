package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.response.ImageResponse
import com.uogames.dto.global.GlobalImage


object ImageMap : Map<ImageResponse, GlobalImage> {

	override fun ImageResponse.toDTO() = GlobalImage(
		globalId = globalId,
		globalOwner = globalOwner,
		imageUri = imageUri
	)

	override fun GlobalImage.toResponse() = ImageResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		imageUri = imageUri
	)

}