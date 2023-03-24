package com.uogames.remembercards.ui.cardFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.uogames.dto.global.*
import com.uogames.dto.local.LocalCard
import com.uogames.map.CardMap.toGlobalCard
import com.uogames.map.CardMap.toLocalCard
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.databinding.DialogShareAttentionBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import java.util.*

class CardAdapter(
    private val model: CardViewModel,
    private val player: ObservableMediaPlayer,
    private val reportCall: ((GlobalCard) -> Unit)? = null,
    private val cardAction: (LocalCard) -> Unit
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private val auth = Firebase.auth
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
            cardObserver = recyclerScope.launch(Dispatchers.Main) {
                val cardView = model.get(adapterPosition).ifNull { return@launch }
                if (auth.currentUser == null || (cardView.card.globalOwner != null && cardView.card.globalOwner != auth.currentUser?.uid)) {
                    bind.btnShare.visibility = View.GONE
                }
                bind.txtReason.text = cardView.card.reason
                bind.btnEdit.setOnClickListener { cardAction(cardView.card.toLocalCard()) }
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

                bind.root.visibility = View.VISIBLE

                val startAction: () -> Unit = {
                    bind.progressLoading.visibility = View.VISIBLE
                    bind.btnStop.visibility = View.VISIBLE
                    bind.btnShare.visibility = View.GONE
                    bind.btnEdit.visibility = View.GONE
                }

                val endAction: (String) -> Unit = {
                    bind.progressLoading.visibility = View.GONE
                    bind.btnStop.visibility = View.GONE
                    bind.btnShare.visibility = View.VISIBLE
                    bind.btnEdit.visibility = View.VISIBLE
                    Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
                }

                model.setShareAction(cardView.card, endAction).ifTrue(startAction)

                bind.btnShare.setOnClickListener {
                    startAction()
                    model.share(cardView.card, endAction)
                }

                bind.btnShare.setOnClickListener {
                    model.shareNotice.value?.let {
                        startAction()
                        model.share(cardView.card, endAction)
                    }.ifNull {
                        val viewBin = DialogShareAttentionBinding.inflate(LayoutInflater.from(itemView.context))
                        MaterialAlertDialogBuilder(itemView.context)
                            .setView(viewBin.root)
                            .setPositiveButton("Apply") { _, _ ->
                                startAction()
                                model.share(cardView.card, endAction)
                                if (viewBin.cbDnshow.isChecked) model.showShareNotice(false)
                            }.setNegativeButton("Cancel") { _, _ ->
                            }.show()
                    }
                }

                bind.btnStop.setOnClickListener {
                    model.stopSharing(cardView.card)
                }

            }
            bind.btnCardAction.setOnClickListener {
                full = !full
                bind.btns.visibility = if (full) View.VISIBLE else View.GONE
                bind.txtDefinitionFirst.visibility = if (full && bind.txtDefinitionFirst.text.isNotEmpty()) View.VISIBLE else View.GONE
                bind.txtDefinitionSecond.visibility = if (full && bind.txtDefinitionSecond.text.isNotEmpty()) View.VISIBLE else View.GONE
                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                bind.imgBtnAction.setImageResource(img)
                //if (!full) notifyItemChanged(adapterPosition)
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
            bind.btnDownload.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            bind.btnReport.visibility = View.GONE
        }

        override fun onDestroy() {
            cardObserver?.cancel()
        }
    }

    inner class GlobalCardHolder(private val bind: CardCardBinding) : ClosableHolder(bind.root) {

        private var cardJob: Job? = null

        private var full = false

        override fun show() {
            clear()
            cardJob = recyclerScope.launch {
                val cardView = model.getByPosition(adapterPosition.toLong()).ifNull { return@launch }
                bind.txtReason.text = cardView.card.reason
                setData(
                    cardView.card.phrase,
                    cardView.phrasePronounceData,
                    cardView.card.phrase.image,
                    bind.txtLangFirst,
                    bind.txtPhraseFirst,
                    bind.imgSoundFirst,
                    bind.mcvFirst,
                    bind.imgCardFirst,
                    bind.txtDefinitionFirst
                )
                setData(
                    cardView.card.translate,
                    cardView.translatePronounceData,
                    cardView.card.translate.image,
                    bind.txtLangSecond,
                    bind.txtPhraseSecond,
                    bind.imgSoundSecond,
                    bind.mcvSecond,
                    bind.imgCardSecond,
                    bind.txtDefinitionSecond
                )
                bind.root.visibility = View.VISIBLE

                bind.btnReport.setOnClickListener { reportCall?.let { it(cardView.card.toGlobalCard()) } }

                val startAction: () -> Unit = {
                    bind.progressLoading.visibility = View.VISIBLE
                    bind.btnStop.visibility = View.VISIBLE
                    bind.btnDownload.visibility = View.GONE
                }

                val endAction: (String) -> Unit = {
                    bind.progressLoading.visibility = View.GONE
                    bind.btnStop.visibility = View.GONE
                    bind.btnDownload.visibility = View.VISIBLE
                    Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
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
                //if (!full) notifyItemChanged(adapterPosition)
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
            auth.uid.ifNull { bind.btnReport.visibility = View.GONE }
        }

        private fun setData(
            phrase: GlobalPhraseView?,
            pronunciationData: Deferred<ByteArray?>,
            image: GlobalImageView?,
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

            phrase?.pronounce?.let { _ ->
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
            cardJob?.cancel()
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
            else -> GlobalCardHolder(bind)
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