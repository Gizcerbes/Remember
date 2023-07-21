package com.uogames.remembercards.models

import android.graphics.drawable.AnimationDrawable
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.global.GlobalPronunciationView
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ObservableMediaPlayer
import kotlinx.coroutines.Deferred

class GlobalPhraseModel(
	val phrase: GlobalPhraseView,
	private val player: ObservableMediaPlayer,
	private val loader: (GlobalPronunciationView) -> Deferred<ByteArray>
) {
	private val pData by lazy { phrase.pronounce?.let { loader(it) } }

	suspend fun playPhrase(anim: AnimationDrawable) = player.play(MediaBytesSource(pData?.await()), anim)
}