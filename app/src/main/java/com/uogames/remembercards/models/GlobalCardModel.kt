package com.uogames.remembercards.models

import android.graphics.drawable.AnimationDrawable
import com.uogames.dto.global.GlobalCardView
import com.uogames.dto.global.GlobalPronunciationView
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ObservableMediaPlayer
import kotlinx.coroutines.Deferred

class GlobalCardModel(
	val card: GlobalCardView,
	private val player: ObservableMediaPlayer,
	private val loader: (GlobalPronunciationView) -> Deferred<ByteArray>
) {
	private val pData by lazy { card.phrase.pronounce?.let { loader(it) } }
	private val tData by lazy { card.translate.pronounce?.let { loader(it) } }

	suspend fun playPhrase(anim: AnimationDrawable) = player.play(MediaBytesSource(pData?.await()), anim)
	suspend fun playTranslate(anim: AnimationDrawable) = player.play(MediaBytesSource(tData?.await()), anim)

}