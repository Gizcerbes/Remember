package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class ImageResponse(
	@SerializedName("id")
	val id: Long = 0,
	@SerializedName("image_uri")
	val imageUri: String,
	@SerializedName("global_id")
	val globalId: Long = 0,
	@SerializedName("global_owner")
	val globalOwner: String? = null
)