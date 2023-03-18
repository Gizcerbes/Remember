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
): ClosableAdapter() {

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
                    setData(
                        cardView.phrase.await(),
                        cardView.phrasePronounce.await(),
                        cardView.phraseImage.await(),
                        bind.txtLangFirst,
                        bind.txtPhraseFirst,
                        bind.imgSoundFirst,
                        bind.mcvFirst,
                        bind.imgCardFirst
                    )
                    setData(
                        cardView.translate.await(),
                        cardView.translatePronounce.await(),
                        cardView.translateImage.await(),
                        bind.txtLangSecond,
                        bind.txtPhraseSecond,
                        bind.imgSoundSecond,
                        bind.mcvSecond,
                        bind.imgCardSecond
                    )
                }
            }
            bind.btnCardAction.setOnClickListener {
                full = !full
                bind.btns.visibility = if (full) View.VISIBLE else View.GONE
                bind.txtDefinitionFirst.visibility = if (full && bind.txtDefinitionFirst.text.isNotEmpty()) View.VISIBLE else View.GONE
                bind.txtDefinitionSecond.visibility = if (full && bind.txtDefinitionSecond.text.isNotEmpty()) View.VISIBLE else View.GONE
                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                bind.imgBtnAction.setImageResource(img)
                if (!full) notifyItemChanged(adapterPosition)
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
            bind.btns.visibility = View.GONE
            bind.progressLoading.visibility = View.GONE
            bind.btnEdit.visibility = View.GONE
            bind.btnShare.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            bind.imgBtnDownload.setImageResource(R.drawable.ic_baseline_add_24)
        }

        private fun setData(
            phrase: LocalPhrase?,
            pronunciation: LocalPronunciation?,
            image: LocalImage?,
            langView: TextView,
            phraseView: TextView,
            soundImg: ImageView,
            button: MaterialCardView,
            phraseImage: ImageView
        ) {
            phrase?.let {
                langView.text = Locale.forLanguageTag(phrase.lang).displayLanguage
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
                setData(
                    cardView.phrase.await(),
                    cardView.phrasePronounceData,
                    cardView.phraseImage.await(),
                    bind.txtLangFirst,
                    bind.txtPhraseFirst,
                    bind.imgSoundFirst,
                    bind.mcvFirst,
                    bind.imgCardFirst,
                    bind.txtDefinitionFirst
                )
                setData(
                    cardView.translate.await(),
                    cardView.translatePronounceData,
                    cardView.translateImage.await(),
                    bind.txtLangSecond,
                    bind.txtPhraseSecond,
                    bind.imgSoundSecond,
                    bind.mcvSecond,
                    bind.imgCardSecond,
                    bind.txtDefinitionSecond
                )
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
                bind.btns.visibility = if (full) View.VISIBLE else View.GONE
                bind.txtDefinitionFirst.visibility = if (full && bind.txtDefinitionFirst.text.isNotEmpty()) View.VISIBLE else View.GONE
                bind.txtDefinitionSecond.visibility = if (full && bind.txtDefinitionSecond.text.isNotEmpty()) View.VISIBLE else View.GONE
                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                bind.imgBtnAction.setImageResource(img)
                if (adapterPosition == size - 1 && !full) notifyItemChanged(adapterPosition)
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
            bind.btns.visibility = View.GONE
            bind.progressLoading.visibility = View.GONE
            bind.btnEdit.visibility = View.GONE
            bind.btnShare.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        }

        private fun setData(
            phrase: GlobalPhrase?,
            pronunciationData: Deferred<ByteArray?>,
            image: GlobalImage?,
            langView: TextView,
            phraseView: TextView,
            soundImg: ImageView,
            button: MaterialCardView,
            phraseImage: ImageView,
            definition: TextView
        ) {
            phrase?.let {
                langView.text = Locale.forLanguageTag(phrase.lang).displayLanguage
                phraseView.text = phrase.phrase
                definition.text = phrase.definition.orEmpty()
            }

            image?.let {
                model.getPicasso(itemView.context).load(it.imageUri.toUri()).placeholder(R.drawable.noise).into(phraseImage)
                phraseImage.visibility = View.VISIBLE
            }.ifNull { phraseImage.visibility = View.GONE }

            phrase?.idPronounce?.let { _ ->
                soundImg.visibility = View.VISIBLE
                button.setOnClickListener {
                    recyclerScope.launch {
                        player.play(MediaBytesSource(pronunciationData.await()), soundImg.background.asAnimationDrawable())
                    }
                }
            }.ifNull {
                soundImg.visibility = View.GONE
                button.setOnClickListener(null)
            }
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
        return when(viewType) {
            0 -> LocalCardHolder(bind)
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