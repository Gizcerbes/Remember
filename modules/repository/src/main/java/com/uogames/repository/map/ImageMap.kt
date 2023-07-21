package com.uogames.repository.map

import com.uogames.database.entity.ImageEntity
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.LocalImageView
import java.util.UUID

object ImageMap {

	fun ImageEntity.toViewDTO() = LocalImageView(
		id = id,
		imgUri = imgUri,
		globalId = UUID.fromString(globalId),
		globalOwner = globalOwner
	)

	fun LocalImageView.toEntity() = ImageEntity(
		id = id,
		imgUri = imgUri,
		globalId = globalId.toString(),
		globalOwner = globalOwner
	)

	fun ImageEntity.toDTO() = LocalImage(
		id = id,
		imgUri = imgUri,
		globalId = UUID.fromString(globalId) ?: UUID.randomUUID(),
		globalOwner = globalOwner
	)

	fun LocalImage.toEntity() = ImageEntity(
		id = id,
		imgUri = imgUri,
		globalId = globalId.toString(),
		globalOwner = globalOwner
	)

}