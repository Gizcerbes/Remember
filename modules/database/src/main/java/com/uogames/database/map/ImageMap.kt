package com.uogames.database.map

import com.uogames.database.entity.ImageEntity
import com.uogames.dto.local.Image
import java.util.*

object ImageMap : Map<ImageEntity, Image> {
	override fun ImageEntity.toDTO() = Image(
		id = id,
		imgUri = imgUri,
		globalId = globalId,
		globalOwner = globalOwner
	)

	override fun Image.toEntity() = ImageEntity(
		id = id,
		imgUri = imgUri,
		globalId = globalId,
		globalOwner = globalOwner
	)

}