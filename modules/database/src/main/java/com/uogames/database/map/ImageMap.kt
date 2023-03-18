package com.uogames.database.map

import com.uogames.database.entity.ImageEntity
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.LocalImageView

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

class ImageViewMap(): ViewMap<ImageEntity, LocalImageView> {
	override suspend fun toDTO(entity: ImageEntity) = LocalImageView(
		id = entity.id,
		imgUri = entity.imgUri,
		globalId = entity.globalId,
		globalOwner = entity.globalOwner
	)

	override suspend fun toEntity(dto: LocalImageView) = ImageEntity(
		id = dto.id,
		imgUri = dto.imgUri,
		globalId = dto.globalId,
		globalOwner = dto.globalOwner
	)
}