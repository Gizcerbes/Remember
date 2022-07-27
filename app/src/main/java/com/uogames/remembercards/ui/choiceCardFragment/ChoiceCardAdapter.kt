package com.uogames.remembercards.ui.choiceCardFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.uogames.dto.Image
import com.uogames.dto.Phrase
import com.uogames.dto.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.ui.cardFragment.CardViewModel
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class ChoiceCardAdapter(
    private val model: CardViewModel,
    private val player: ObservableMediaPlayer,
    private val callChoiceCardID: (Int) -> Unit
):RecyclerView.Adapter<ChoiceCardAdapter.CardHolder>() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)

    inner class CardHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var cardObserver: Job? = null

        private var _bind: CardCardBinding? = null
        private val bind get() = _bind!!

        fun onShow(){
            _bind = CardCardBinding.inflate(LayoutInflater.from(itemView.context), itemView as ViewGroup, false)
            val linearLayout = itemView as LinearLayout
            linearLayout.removeAllViews()
            linearLayout.addView(bind.root)
            cardObserver = model.get(adapterPosition).observeWhile(recyclerScope) {
                it?.let { cardView ->
                    bind.txtReason.text = cardView.card.reason
                    bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_add_24)
                    bind.btnCardAction.setOnClickListener {
                        callChoiceCardID(cardView.card.id)
                    }
                    setData(
                        cardView.phrase.await(),
                        cardView.phrasePronounce?.await(),
                        cardView.phraseImage?.await(),
                        bind.txtLangFirst,
                        bind.txtPhraseFirst,
                        bind.imgSoundFirst,
                        bind.mcvFirst,
                        bind.imgCardFirst
                    )
                    setData(
                        cardView.translate.await(),
                        cardView.translatePronounce?.await(),
                        cardView.translateImage?.await(),
                        bind.txtLangSecond,
                        bind.txtPhraseSecond,
                        bind.imgSoundSecond,
                        bind.mcvSecond,
                        bind.imgCardSecond
                    )
                }
                bind.root.visibility = View.VISIBLE
            }
        }

        private fun setData(
            phrase: Phrase?,
            pronunciation: Pronunciation?,
            image: Image?,
            langView: TextView,
            phraseView: TextView,
            soundImg: ImageView,
            button: MaterialCardView,
            phraseImage: ImageView
        ) {
            phrase?.let {
                langView.text = model.getDisplayLang(phrase)
                phraseView.text = phrase.phrase
            }

            image?.let {
                Picasso.get().load(it.imgUri.toUri()).placeholder(R.drawable.noise).into(phraseImage)
                phraseImage.visibility = View.VISIBLE
            }.ifNull { phraseImage.visibility = View.GONE }

            pronunciation?.let { pronounce ->
                soundImg.visibility = View.VISIBLE
                button.setOnClickListener {
                    player.play(itemView.context, pronounce.audioUri.toUri(), soundImg.background.asAnimationDrawable())
                }
            }.ifNull { soundImg.visibility = View.GONE }
        }

        fun onDestroy(){
            cardObserver?.cancel()
            _bind = null
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        return CardHolder(LinearLayout(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
        })
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.onShow()
        (holder.itemView as LinearLayout).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun getItemCount(): Int {
        return model.size.value
    }

    override fun onViewRecycled(holder: CardHolder) {
        super.onViewRecycled(holder)
        holder.onDestroy()
    }

    fun onDestroy(){
        recyclerScope.cancel()
    }

}