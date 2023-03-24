package com.uogames.remembercards.ui.editModuleFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.uogames.dto.local.LocalModuleCard
import com.uogames.dto.local.LocalPhrase
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.utils.*
import com.uogames.repository.DataProvider.Companion.toCard
import com.uogames.repository.DataProvider.Companion.toImage
import com.uogames.repository.DataProvider.Companion.toPhrase
import com.uogames.repository.DataProvider.Companion.toPronounce
import com.uogames.repository.DataProvider.Companion.toTranslate
import kotlinx.coroutines.*
import java.util.*

class EditModuleAdapter(
    private val model: EditModuleViewModel,
    private val player: ObservableMediaPlayer
) : RecyclerView.Adapter<EditModuleAdapter.CardHolder>() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)

    private var listItems: List<LocalModuleCard> = arrayListOf()

    fun setListItems(list: List<LocalModuleCard>) {
        listItems = list
        notifyDataSetChanged()
    }

    inner class CardHolder(val bind: CardCardBinding) : RecyclerView.ViewHolder(bind.root) {

        private var cardObserver: Job? = null

        fun onShow() {
            clear()
            val moduleCard = listItems[adapterPosition]
            cardObserver = recyclerScope.launch(Dispatchers.IO) {
                val card = moduleCard.toCard().ifNull { return@launch }
                launch(Dispatchers.Main) {
                    bind.txtReason.text = card.reason
                    bind.btnCardAction.setOnClickListener {
                        Toast.makeText(itemView.context, itemView.context.getText(R.string.press_to_delete), Toast.LENGTH_SHORT).show()
                    }
                    bind.btnCardAction.setOnLongClickListener {
                        model.removeModuleCard(moduleCard) {}
                        true
                    }
                    card.toPhrase()?.let { phrase ->
                        setData(phrase, bind.txtLangFirst, bind.txtPhraseFirst, bind.imgSoundFirst, bind.mcvFirst, bind.imgCardFirst)
                    }
                    card.toTranslate()?.let { phrase ->
                        setData(phrase, bind.txtLangSecond, bind.txtPhraseSecond, bind.imgSoundSecond, bind.mcvSecond, bind.imgCardSecond)
                    }
                }
            }
        }

        private fun clear() {
            bind.txtDefinitionFirst.visibility = View.GONE
            bind.txtDefinitionSecond.visibility = View.GONE
            bind.imgCardFirst.visibility = View.GONE
            bind.imgSoundFirst.visibility = View.GONE
            bind.txtDefinitionFirst.text = ""
            bind.txtLangFirst.text = ""
            bind.txtPhraseFirst.text = ""
            bind.imgCardSecond.visibility = View.GONE
            bind.imgSoundSecond.visibility = View.GONE
            bind.txtDefinitionSecond.text = ""
            bind.txtLangSecond.text = ""
            bind.txtPhraseSecond.text = ""
            bind.btns.visibility = View.GONE
            bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_remove_24)
        }

        private suspend fun setData(
            phrase: LocalPhrase,
            langView: TextView,
            phraseView: TextView,
            soundImg: ImageView,
            button: MaterialCardView,
            phraseImage: ImageView
        ) {
            langView.text = Locale.forLanguageTag(phrase.lang).displayLanguage
            phraseView.text = phrase.phrase
            phrase.toImage()?.let {
                Picasso.get().load(it.imgUri.toUri()).placeholder(R.drawable.noise).into(phraseImage)
                phraseImage.visibility = View.VISIBLE
            }.ifNull {
                phraseImage.visibility = View.GONE
            }

            phrase.toPronounce()?.let { audio ->
                soundImg.visibility = View.VISIBLE
                button.setOnClickListener {
                    player.play(itemView.context, audio.audioUri.toUri(), soundImg.background.asAnimationDrawable())
                }
            }.ifNull {
                soundImg.visibility = View.GONE
            }
        }

        fun onDestroy() {
            cardObserver?.cancel()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        return CardHolder(
            CardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) = holder.onShow()

    override fun getItemCount() = listItems.size

    override fun onViewRecycled(holder: CardHolder) {
        super.onViewRecycled(holder)
        holder.onDestroy()
    }

    fun onDestroy() {
        recyclerScope.cancel()
    }
}
