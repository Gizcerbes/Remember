package com.uogames.remembercards.ui.module.watch

import android.view.ViewGroup
import androidx.core.net.toUri
import com.uogames.remembercards.R
import com.uogames.remembercards.ui.views.CardView
import com.uogames.remembercards.utils.ClosableAdapter
import com.uogames.remembercards.utils.asAnimationDrawable
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*

class WatchModuleAdapter(
    private val model: WatchModuleViewModel
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private var size = 0

    init {
        model.size.observe(recyclerScope) {
            size = it
            notifyDataSetChanged()
        }
    }

    inner class LocalHolder(val view: CardView) : ClosableHolder(view) {

        override fun show() {
            observer = recyclerScope.launch {
                val moduleCardView = model.getLocalAsync(adapterPosition).await().ifNull { return@launch }
                val cardView = moduleCardView.mc.card
                view.clue = cardView.reason
                cardView.phrase.let { phrase ->
                    view.languageTagFirst = Locale.forLanguageTag(phrase.lang)
                    view.phraseFirst = phrase.phrase
                    phrase.image?.let { image ->
                        model.getPicasso(itemView.context).load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(view.getFirstImageView())
                        view.showImageFirst = true
                    }.ifNull { view.showImageFirst = false }
                    phrase.pronounce?.let { _ ->
                        view.showAudioFirst = true
                        view.setOnClickButtonCardFirst {
                            recyclerScope.launch { moduleCardView.playFirst(it.background.asAnimationDrawable()) }
                        }
                    }.ifNull { view.showAudioFirst = false }
                    view.definitionFirst = phrase.definition.orEmpty()
                }
                cardView.translate.let { translate ->
                    view.languageTagSecond = Locale.forLanguageTag(translate.lang)
                    view.phraseSecond = translate.phrase
                    translate.image?.let { image ->
                        view.showImageSecond = true
                        model.getPicasso(itemView.context).load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(view.getSecondImageView())
                    }.ifNull { view.showImageSecond = false }
                    translate.pronounce?.let { _ ->
                        view.showAudioSecond = true
                        view.setOnClickButtonCardSecond {
                            recyclerScope.launch { moduleCardView.playSecond(it.background.asAnimationDrawable()) }
                        }
                    }.ifNull { view.showAudioSecond = false }
                    view.definitionSecond = translate.definition.orEmpty()
                }
                view.showButtons = true
            }
        }

    }

    inner class GlobalHolder(val view: CardView) : ClosableHolder(view){

        override fun show() {
            observer = recyclerScope.launch {
                val moduleCardView = model.getGlobalAsync(adapterPosition).await().ifNull { return@launch }
                val cardView = moduleCardView.mc.card
                view.clue = cardView.reason
                cardView.phrase.let { phrase ->
                    view.languageTagFirst = Locale.forLanguageTag(phrase.lang)
                    view.phraseFirst = phrase.phrase
                    phrase.image?.let { image ->
                        model.getPicasso(itemView.context).load(image.imageUri.toUri()).placeholder(R.drawable.noise).into(view.getFirstImageView())
                        view.showImageFirst = true
                    }.ifNull { view.showImageFirst = false }
                    phrase.pronounce?.let { _ ->
                        view.showAudioFirst = true
                        view.setOnClickButtonCardFirst {
                            recyclerScope.launch { moduleCardView.playFirst(it.background.asAnimationDrawable()) }
                        }
                    }.ifNull { view.showAudioFirst = false }
                    view.definitionFirst = phrase.definition.orEmpty()
                }
                cardView.translate.let { translate ->
                    view.languageTagSecond = Locale.forLanguageTag(translate.lang)
                    view.phraseSecond = translate.phrase
                    translate.image?.let { image ->
                        view.showImageSecond = true
                        model.getPicasso(itemView.context).load(image.imageUri.toUri()).placeholder(R.drawable.noise).into(view.getSecondImageView())
                    }.ifNull { view.showImageSecond = false }
                    translate.pronounce?.let { _ ->
                        view.showAudioSecond = true
                        view.setOnClickButtonCardSecond {
                            recyclerScope.launch { moduleCardView.playSecond(it.background.asAnimationDrawable()) }
                        }
                    }.ifNull { view.showAudioSecond = false }
                    view.definitionSecond = translate.definition.orEmpty()
                }
                view.showButtons = true
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (model.type.value) 1
        else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosableHolder {
        return when (viewType) {
            0 -> LocalHolder(CardView(parent.context))
            1 -> GlobalHolder(CardView(parent.context))
            else -> LocalHolder(CardView(parent.context))
        }
    }

    override fun onBindViewHolder(holder: ClosableHolder, position: Int) = holder.show()

    override fun getItemCount(): Int = size

    override fun close() = recyclerScope.cancel()

}