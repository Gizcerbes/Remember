package com.uogames.remembercards.ui.choiceCardFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.uogames.dto.global.GlobalImage
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import java.util.*

class ChoiceCardAdapter(
    private val model: ChoiceCardViewModel,
    private val player: ObservableMediaPlayer,
    private val callChoiceCardID: (Int) -> Unit
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private var size = 0

    init {
        model.size.observe(recyclerScope) {
            size = it
            notifyDataSetChanged()
        }
    }

    inner class LocalCardHolder(val bind: CardCardBinding) : ClosableHolder(bind.root) {

        private var cardObserver: Job? = null
        private var full = false

        override fun show() {
            clear()
            cardObserver = recyclerScope.launch(Dispatchers.IO) {
                val cardView = model.get(adapterPosition).ifNull { return@launch }
                launch(Dispatchers.Main) {
                    bind.txtReason.text = cardView.card.reason
                    bind.btnDownload.setOnClickListener { callChoiceCardID(cardView.card.id) }
                    cardView.card.phrase.let { phrase ->
                        bind.txtLangFirst.text = Locale.forLanguageTag(phrase.lang).displayLanguage
                        bind.txtPhraseFirst.text = phrase.phrase
                        phrase.image?.let { image ->
                            bind.imgCardFirst.visibility = View.VISIBLE
                            Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(bind.imgCardFirst)
                        }.ifNull {
                            bind.imgCardFirst.visibility = View.GONE
                        }
                        phrase.pronounce?.let { pronounce ->
                            bind.imgSoundFirst.visibility = View.VISIBLE
                            bind.mcvFirst.setOnClickListener {
                                player.play(
                                    itemView.context,
                                    pronounce.audioUri.toUri(),
                                    bind.imgSoundFirst.background.asAnimationDrawable()
                                )
                            }
                        }.ifNull { bind.imgSoundFirst.visibility = View.GONE }
                        bind.txtDefinitionFirst.text = phrase.definition.orEmpty()
                    }
                    cardView.card.translate.let { translate ->
                        bind.txtLangSecond.text = Locale.forLanguageTag(translate.lang).displayLanguage
                        bind.txtPhraseSecond.text = translate.phrase
                        translate.image?.let { image ->
                            bind.imgCardSecond.visibility = View.VISIBLE
                            Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(bind.imgCardSecond)
                        }.ifNull {
                            bind.imgCardSecond.visibility = View.GONE
                        }
                        translate.pronounce?.let { pronounce ->
                            bind.imgSoundSecond.visibility = View.VISIBLE
                            bind.mcvSecond.setOnClickListener {
                                player.play(
                                    itemView.context,
                                    pronounce.audioUri.toUri(),
                                    bind.imgSoundSecond.background.asAnimationDrawable()
                                )
                            }
                        }.ifNull { bind.imgSoundSecond.visibility = View.GONE }
                        bind.txtDefinitionSecond.text = translate.definition.orEmpty()
                    }
                }
            }
            bind.btnCardAction.setOnClickListener {
                full = !full
                bind.llBtns.visibility = if (full) View.VISIBLE else View.GONE
                bind.txtDefinitionFirst.visibility = if (full && bind.txtDefinitionFirst.text.isNotEmpty()) View.VISIBLE else View.GONE
                bind.txtDefinitionSecond.visibility = if (full && bind.txtDefinitionSecond.text.isNotEmpty()) View.VISIBLE else View.GONE
                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                bind.imgBtnAction.setImageResource(img)
            }
        }

        private fun clear() {
            full = false
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
            bind.llBtns.visibility = View.GONE
            bind.progressLoading.visibility = View.GONE
            bind.btnEdit.visibility = View.GONE
            bind.btnShare.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            //bind.imgBtnDownload.setImageResource(R.drawable.ic_baseline_add_24)
            bind.btnReport.visibility = View.GONE
        }

        override fun onDestroy() {
            cardObserver?.cancel()
        }
    }

    inner class GlobalCardHolder(val bind: CardCardBinding) : ClosableHolder(bind.root) {

        private var cardObserver: Job? = null
        private var full = false

        override fun show() {
            clear()
            cardObserver = recyclerScope.launch {
                val cardView = model.getByPosition(adapterPosition.toLong()).ifNull { return@launch }
                bind.txtReason.text = cardView.card.reason
                cardView.card.phrase.let { phrase ->
                    bind.txtLangFirst.text = Locale.forLanguageTag(phrase.lang).displayLanguage
                    bind.txtPhraseFirst.text = phrase.phrase
                    phrase.image?.let { image ->
                        bind.imgCardFirst.visibility = View.VISIBLE
                        model.getPicasso(itemView.context).load(image.imageUri.toUri()).placeholder(R.drawable.noise).into(bind.imgCardFirst)
                    }.ifNull {
                        bind.imgCardFirst.visibility = View.GONE
                    }
                    phrase.pronounce?.let { pronounce ->
                        bind.imgSoundFirst.visibility = View.VISIBLE
                        bind.mcvFirst.setOnClickListener {
                            player.play(
                                itemView.context,
                                pronounce.audioUri.toUri(),
                                bind.imgSoundFirst.background.asAnimationDrawable()
                            )
                        }
                    }.ifNull { bind.imgSoundFirst.visibility = View.GONE }
                    bind.txtDefinitionFirst.text = phrase.definition.orEmpty()
                }
                cardView.card.translate.let { translate ->
                    bind.txtLangSecond.text = Locale.forLanguageTag(translate.lang).displayLanguage
                    bind.txtPhraseSecond.text = translate.phrase
                    translate.image?.let { image ->
                        bind.imgCardSecond.visibility = View.VISIBLE
                        model.getPicasso(itemView.context).load(image.imageUri.toUri()).placeholder(R.drawable.noise).into(bind.imgCardSecond)
                    }.ifNull {
                        bind.imgCardSecond.visibility = View.GONE
                    }
                    translate.pronounce?.let { pronounce ->
                        bind.imgSoundSecond.visibility = View.VISIBLE
                        bind.mcvSecond.setOnClickListener {
                            player.play(
                                itemView.context,
                                pronounce.audioUri.toUri(),
                                bind.imgSoundSecond.background.asAnimationDrawable()
                            )
                        }
                    }.ifNull { bind.imgSoundSecond.visibility = View.GONE }
                    bind.txtDefinitionSecond.text = translate.definition.orEmpty()
                }
                bind.root.visibility = View.VISIBLE

                val startAction: () -> Unit = {
                    bind.progressLoading.visibility = View.VISIBLE
                    bind.btnStop.visibility = View.VISIBLE
                    bind.btnDownload.visibility = View.GONE
                }

                val endAction: (String) -> Unit = {
                    bind.progressLoading.visibility = View.GONE
                    bind.btnStop.visibility = View.GONE
                    bind.btnDownload.visibility = View.VISIBLE
                    recyclerScope.launch {
                        model.getByGlobalId(cardView.card.globalId)?.let { card -> callChoiceCardID(card.id) }
                    }
                }

                model.setDownloadAction(cardView.card.globalId, endAction).ifTrue(startAction)

                bind.btnDownload.setOnClickListener {
                    startAction()
                    model.save(cardView, endAction)
                }

                bind.btnStop.setOnClickListener {
                    model.stopDownloading(cardView.card.globalId)
                }

            }

            bind.btnCardAction.setOnClickListener {
                full = !full
                bind.llBtns.visibility = if (full) View.VISIBLE else View.GONE
                bind.txtDefinitionFirst.visibility = if (full && bind.txtDefinitionFirst.text.isNotEmpty()) View.VISIBLE else View.GONE
                bind.txtDefinitionSecond.visibility = if (full && bind.txtDefinitionSecond.text.isNotEmpty()) View.VISIBLE else View.GONE
                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                bind.imgBtnAction.setImageResource(img)
            }
        }

        private fun clear() {
            full = false
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
            bind.llBtns.visibility = View.GONE
            bind.progressLoading.visibility = View.GONE
            bind.btnEdit.visibility = View.GONE
            bind.btnShare.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        }

        override fun onDestroy() {
            cardObserver?.cancel()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (model.cloud.value) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosableHolder {
        val bind = CardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            0 -> LocalCardHolder(bind)
            1 -> GlobalCardHolder(bind)
            else -> LocalCardHolder(bind)
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