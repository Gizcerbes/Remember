package com.uogames.map

import com.uogames.dto.local.Image

object ImageMap {

	fun Image.update(image: com.uogames.dto.global.Image) = Image(
		id = id,
		imgUri = imgUri,
		globalId = image.globalId,
		globalOwner = image.globalOwner
	)

}