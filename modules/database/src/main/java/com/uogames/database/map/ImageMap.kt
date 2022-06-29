package com.uogames.database.map

import com.uogames.database.entity.ImageEntity
import com.uogames.dto.Image

object ImageMap : Map<ImageEntity, Image> {
	override fun ImageEntity.toDTO(): Image {
		return Image(
			id = id,
			imgUri = imgUri
		)
	}

	override fun Image.toEntity(): ImageEntity {
		return ImageEntity(
			id = id,
			imgUri = imgUri
		)
	}
}