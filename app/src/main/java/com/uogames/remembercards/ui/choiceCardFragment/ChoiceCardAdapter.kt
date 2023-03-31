package com.uogames.remembercards.ui.choiceCardFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import com.uogames.dto.global.GlobalCard
import com.uogames.dto.local.LocalCard
import com.uogames.map.CardMap.toGlobalCard
import com.uogames.map.CardMap.toLocalCard
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.ui.cardFragment.CardViewModel
import com.uogames.remembercards.ui.dialogs.ShareAttentionDialog
import com.uogames.remembercards.ui.views.CardView
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import java.util.*

class ChoiceCardAdapter(
    private val model: ChoiceCardViewModel,
    private val player: ObservableMediaPlayer,
    private val reportCall: ((GlobalCard) -> Unit)? = null,
    private val cardAction: (LocalCard) -> Unit
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private var size = 0

    init {
        model.size.observe(recyclerScope) {
            size = it
            notifyDataSetChanged()
        }
    }

    inner class LocalCardHolder(val view: CardView) : ClosableHolder(view) {

        override fun show() {
            view.reset()
            observer = recyclerScope.launch {
                val cardView = model.getViewAsync(adapterPosition).await().ifNull { return@launch }
                view.clue = cardView.card.reason
                cardView.card.phrase.let { phrase ->
                    view.languageTagFirst = Locale.forLanguageTag(phrase.lang)
                    view.phraseFirst = phrase.phrase
                    phrase.image?.let { image ->
                        Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(view.getFirstImageView())
                        view.showImageFirst = true
                    }.ifNull { view.showImageFirst = false }
                    phrase.pronounce?.let { pronounce ->
                        view.showAudioFirst = true
                        view.setOnClickButtonCardFirst {
                            player.play(itemView.context, pronounce.audioUri.toUri(), it.background.asAnimationDrawable())
                        }
                    }.ifNull { view.showAudioFirst = false }
                    view.definitionFirst = phrase.definition.orEmpty()
                }
                cardView.card.translate.let { translate ->
                    view.languageTagSecond = Locale.forLanguageTag(translate.lang)
                    view.phraseSecond = translate.phrase
                    translate.image?.let { image ->
                        view.showImageSecond = true
                        Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(view.getSecondImageView())
                    }.ifNull { view.showImageSecond = false }
                    translate.pronounce?.let { pronounce ->
                        view.showAudioSecond = true
                        view.setOnClickButtonCardSecond {
                            player.play(itemView.context, pronounce.audioUri.toUri(), it.background.asAnimationDrawable())
                        }
                    }.ifNull { view.showAudioSecond = false }
                    view.definitionSecond = translate.definition.orEmpty()
                }

                view.setOnClickButtonAddListener { cardAction(cardView.card.toLocalCard()) }

                view.showButtons = true
            }

        }
    }

    inner class GlobalCardHolder(val view: CardView) : ClosableHolder(view) {

        private val startAction: () -> Unit = {
            view.showProgressLoading = true
            view.showButtonStop = true
            view.showButtonDownload = false
        }

        private val endAction: (String, LocalCard?) -> Unit = { _, lc ->
            view.showProgressLoading = false
            view.showButtonStop = false
            view.showButtonDownload = true
            lc?.let { recyclerScope.launch { cardAction(it) } }
        }

        override fun show() {
            view.reset()
            observer = recyclerScope.launch {
                val cardView = model.getGlobalViewAsync(adapterPosition.toLong()).await().ifNull { return@launch }
                view.clue = cardView.card.reason
                cardView.card.phrase.let { phrase ->
                    view.languageTagFirst = phrase.lang.let { Locale.forLanguageTag(it) }
                    view.phraseFirst = phrase.phrase
                    phrase.pronounce?.let {
                        view.showAudioFirst = true
                        view.setOnClickButtonCardFirst { v ->
                            launch {
                                cardView.phrasePronounceData.await()?.let {
                                    player.play(MediaBytesSource(it), v.background.asAnimationDrawable())
                                }
                            }
                        }
                    }.ifNull { view.showAudioFirst = false }
                    phrase.image?.let {
                        model.getPicasso(itemView.context).load(it.imageUri.toUri()).placeholder(R.drawable.noise).into(view.getFirstImageView())
                        view.showImageFirst = true
                    }.ifNull { view.showImageFirst = false }
                    view.definitionFirst = phrase.definition.orEmpty()
                }
                cardView.card.translate.let { translate ->
                    view.languageTagSecond = translate.lang.let { Locale.forLanguageTag(it) }
                    view.phraseSecond = translate.phrase
                    translate.pronounce?.let {
                        view.showAudioSecond = true
                        view.setOnClickButtonCardSecond { v ->
                            launch {
                                cardView.translatePronounceData.await()?.let {
                                    player.play(MediaBytesSource(it), v.background.asAnimationDrawable())
                                }
                            }
                        }
                    }.ifNull { view.showAudioSecond = false }
                    translate.image?.let {
                        model.getPicasso(itemView.context).load(it.imageUri.toUri()).placeholder(R.drawable.noise).into(view.getSecondImageView())
                        view.showImageSecond = true
                    }.ifNull { view.showImageSecond = false }
                    view.definitionSecond = translate.definition.orEmpty()
                }

                view.setOnClickButtonReport { reportCall?.let { it(cardView.card.toGlobalCard()) } }

                model.setDownloadAction(cardView.card.globalId, endAction).ifTrue(startAction)

                view.setOnClickButtonDownload {
                    startAction()
                    model.save(cardView, endAction)
                }

                view.setOnClickButtonStop { model.stopDownloading(cardView.card.globalId) }

            }
        }

        override fun onDestroy() {
            super.onDestroy()
            view.reset()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (model.cloud.value) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosableHolder {
        val bind = CardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            0 -> LocalCardHolder(CardView(parent.context))
            1 -> GlobalCardHolder(CardView(parent.context))
            else -> LocalCardHolder(CardView(parent.context))
        }
    }

    override fun onBindViewHolder(holder: ClosableHolder, position: Int) {
        holder.show()
    }

    override fun getItemCount() = size

    override fun close() {
        recyclerScope.cancel()
    }


}