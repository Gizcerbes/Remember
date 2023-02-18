package com.uogames.map

import com.uogames.dto.local.Pronunciation

object PronunciationMap {

	fun Pronunciation.update(pronounce: com.uogames.dto.global.GlobalPronunciation) = Pronunciation(
		id = id,
		audioUri = audioUri,
		globalId = pronounce.globalId,
		globalOwner = pronounce.globalOwner
	)

}