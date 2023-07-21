package com.uogames.remembercards.models

import android.graphics.drawable.AnimationDrawable
import com.uogames.dto.local.LocalPhraseView
import com.uogames.dto.local.LocalPronunciationView
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ObservableMediaPlayer
import kotlinx.coroutines.Deferred

class LocalPhraseModel(
	val phrase: LocalPhraseView,
	private val player: ObservableMediaPlayer,
	private val loader: (LocalPronunciationView) -> Deferred<ByteArray>
) {
	private val pData by lazy { phrase.pronounce?.let { loader(it) } }
	suspend fun playPhrase(anim: AnimationDrawable) = player.play(MediaBytesSource(pData?.await()), anim)
}