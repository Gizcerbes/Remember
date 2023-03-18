package com.uogames.map

import com.uogames.dto.global.GlobalPronunciation
import com.uogames.dto.global.GlobalPronunciationView
import com.uogames.dto.local.LocalPronunciation

object PronunciationMap {

	fun LocalPronunciation.update(pronounce: GlobalPronunciation) = LocalPronunciation(
		id = id,
		audioUri = audioUri,
		globalId = pronounce.globalId,
		globalOwner = pronounce.globalOwner
	)

	fun LocalPronunciation.update(view: GlobalPronunciationView) = LocalPronunciation(
		id = id,
		audioUri = audioUri,
		globalId = view.globalId,
		globalOwner = view.user.globalOwner
	)
}