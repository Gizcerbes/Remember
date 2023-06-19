package com.uogames.network.map

import com.uogames.dto.global.GlobalImage
import com.uogames.network.response.ImageResponse

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