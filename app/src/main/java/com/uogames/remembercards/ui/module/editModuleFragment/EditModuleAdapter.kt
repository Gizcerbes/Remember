package com.uogames.remembercards.ui.module.editModuleFragment

import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import com.uogames.dto.local.LocalModuleCard
import com.uogames.map.CardMap.toLocalCard
import com.uogames.map.ModuleCardMap.toLocalModuleCard
import com.uogames.remembercards.R
import com.uogames.remembercards.ui.views.CardView
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import java.util.*

class EditModuleAdapter(
    private val model: EditModuleViewModel,
    private val player: ObservableMediaPlayer
) :  ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)

    private var listItems: List<LocalModuleCard> = arrayListOf()

    private var size = 0

    init {
        model.size.observe(recyclerScope){
            size = it
            notifyDataSetChanged()
        }
    }

    fun setListItems(list: List<LocalModuleCard>) {
        listItems = list
        notifyDataSetChanged()
    }

    inner class CardHolder(val view: CardView) : ClosableHolder(view) {

        private var cardObserver: Job? = null

        override fun show() {
            view.reset()
            observer = recyclerScope.launch {
                val moduleCardView = model.getModuleCardViewAsync(adapterPosition).await().ifNull { return@launch }
                val cardView = moduleCardView.card
                view.clue = cardView.reason
                cardView.phrase.let { phrase ->
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
                cardView.translate.let { translate ->
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

                view.setOnClickRemove { Toast.makeText(itemView.context, itemView.context.getText(R.string.press_to_remove), Toast.LENGTH_SHORT).show()}
                view.setOnLongClickRemove {
                    model.removeModuleCard(moduleCardView.toLocalModuleCard()){}
                    true
                }

                view.showButtons = true
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            view.reset()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosableHolder {
        return CardHolder(CardView(parent.context))
    }

    override fun onBindViewHolder(holder: ClosableHolder, position: Int) = holder.show()

    override fun getItemCount() = size

    override fun close() {
        recyclerScope.cancel()
    }

    override fun onViewRecycled(holder: ClosableHolder) {
        super.onViewRecycled(holder)
        holder.onDestroy()
    }

    fun onDestroy() {
        recyclerScope.cancel()
    }
}
