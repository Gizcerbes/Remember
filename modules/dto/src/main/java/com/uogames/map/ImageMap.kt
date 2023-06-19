package com.uogames.map

import com.uogames.dto.global.GlobalImage
import com.uogames.dto.global.GlobalImageView
import com.uogames.dto.local.LocalImage

object ImageMap {

	fun LocalImage.update(image: GlobalImage) = LocalImage(
		id = id,
		imgUri = imgUri,
		globalId = image.globalId,
		globalOwner = image.globalOwner
	)

	fun LocalImage.update(view: GlobalImageView) = LocalImage(
		id = id,
		imgUri = imgUri,
		globalId = view.globalId,
		globalOwner = view.user.globalOwner
	)

}