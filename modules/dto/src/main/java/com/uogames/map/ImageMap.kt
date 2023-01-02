package com.uogames.map

import com.uogames.dto.local.Image

object ImageMap {

	fun Image.update(image: com.uogames.dto.global.GlobalImage) = Image(
		id = id,
		imgUri = imgUri,
		globalId = image.globalId,
		globalOwner = image.globalOwner
	)

}