package com.uogames.map

import com.uogames.dto.local.LocalImage

object ImageMap {

	fun LocalImage.update(image: com.uogames.dto.global.GlobalImage) = LocalImage(
		id = id,
		imgUri = imgUri,
		globalId = image.globalId,
		globalOwner = image.globalOwner
	)

}