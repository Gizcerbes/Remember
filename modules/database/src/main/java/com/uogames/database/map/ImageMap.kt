package com.uogames.database.map

import com.uogames.database.entity.ImageEntity
import com.uogames.dto.local.LocalImage

object ImageMap : Map<ImageEntity, LocalImage> {
	override fun ImageEntity.toDTO() = LocalImage(
		id = id,
		imgUri = imgUri,
		globalId = globalId,
		globalOwner = globalOwner
	)

	override fun LocalImage.toEntity() = ImageEntity(
		id = id,
		imgUri = imgUri,
		globalId = globalId,
		globalOwner = globalOwner
	)

}