package com.uogames.network.map

import com.uogames.dto.global.Image
import com.uogames.network.response.ImageResponse

object ImageMap : Map<ImageResponse, Image> {

	override fun ImageResponse.toDTO() = Image(
		globalId = globalId,
		globalOwner = globalOwner,
		imageUri = imageUri
	)

	override fun Image.toResponse() = ImageResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		imageUri = imageUri
	)

}