package com.uogames.remembercards.ui.card.cardFragment

import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.uogames.dto.global.*
import com.uogames.dto.local.LocalCard
import com.uogames.map.CardMap.toGlobalCard
import com.uogames.map.CardMap.toLocalCard
import com.uogames.remembercards.R
import com.uogames.remembercards.ui.dialogs.ShareAttentionDialog
import com.uogames.remembercards.ui.views.CardView
import com.uogames.remembercards.utils.*
import com.uogames.remembercards.viewmodel.CViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*

class CardAdapter(
    private val model: CardViewModel,
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

    inner class LocalCardHolder(val view: CardView) : ClosableHolder(view) {

        private val startAction: () -> Unit = {
//            view.showProgressLoading = true
//            view.showButtonStop = true
//            view.showButtonShare = false
//            view.showButtonEdit = false
        }

        private val endAction: (String) -> Unit = {
//            view.showProgressLoading = false
//            view.showButtonStop = false
//            view.showButtonShare = true
//            view.showButtonEdit = true
//            Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
        }

        override fun show() {
            view.reset()
            observer = recyclerScope.launch {
                val cardView = model.getLocalModelViewAsync(adapterPosition).await().ifNull { return@launch }
                view.clue = cardView.card.reason
                view.setOnClickEdit { cardAction(cardView.card.toLocalCard()) }
                cardView.card.phrase.let { phrase ->
                    view.languageTagFirst = Locale.forLanguageTag(phrase.lang)
                    view.phraseFirst = phrase.phrase
                    phrase.image?.let { image ->
                        Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(view.getFirstImageView())
                        view.showImageFirst = true
                    }.ifNull { view.showImageFirst = false }
                    phrase.pronounce?.let {
                        view.showAudioFirst = true
                        view.setOnClickButtonCardFirst {
                            launch { cardView.playPhrase(it.background.asAnimationDrawable()) }
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
                    translate.pronounce?.let {
                        view.showAudioSecond = true
                        view.setOnClickButtonCardSecond {
                            launch { cardView.playTranslate(it.background.asAnimationDrawable()) }
                        }
                    }.ifNull { view.showAudioSecond = false }
                    view.definitionSecond = translate.definition.orEmpty()
                }

                //model.setShareAction(cardView.card, endAction).ifTrue(startAction)

                addShareAction(cardView)

//                view.setOnClickButtonStop(false) {
//                    model.stopSharing(cardView.card)
//                }

                model.getShareAction(cardView.card).observe(this) {
                    runCatching{
                        view.showProgressLoading = it
                        view.showButtonShare = !it && isAvailableToShare(cardView, model.isChanged(cardView.card).value == true)
                        view.showButtonEdit = !it
                    }
                }

                model.isChanged(cardView.card).observe(this) {
                    runCatching { view.showButtonShare = isAvailableToShare(cardView, it == true) }
                }

                view.showButtons = true
            }

        }

        private fun isAvailableToShare(cardView: CViewModel.LocalCardModel, changed: Boolean): Boolean {
            if (!changed) return false
            if (auth.currentUser == null) return false
            if (cardView.card.globalOwner != null && cardView.card.globalOwner != auth.currentUser?.uid) return false
            return true
        }

        private fun addShareAction(cardView: CViewModel.LocalCardModel) {
            if (!isAvailableToShare(cardView, changed = cardView.card.changed)) return
            view.setOnClickButtonShare {
                model.shareNotice.value?.let {
                    startAction()
                    model.share(cardView.card, endAction)
                }.ifNull {
                    ShareAttentionDialog.show(itemView.context) {
                        startAction()
                        if (it) model.showShareNotice(false)
                        model.share(cardView.card, endAction)
                    }
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            view.reset()
        }

    }

    inner class GlobalCardHolder(private val view: CardView) : ClosableHolder(view) {

        private val startAction: () -> Unit = {
            view.showProgressLoading = true
            view.showButtonStop = true
            view.showButtonDownload = false
        }

        private val endAction: (String, LocalCard?) -> Unit = { m, _ ->
            view.showProgressLoading = false
            view.showButtonStop = false
            view.showButtonDownload = true
            Toast.makeText(itemView.context, m, Toast.LENGTH_SHORT).show()
        }

        override fun show() {
            view.reset()
            observer = recyclerScope.launch {
                val cardView = model.getGlobalModelViewAsync(adapterPosition.toLong()).await().ifNull { return@launch }
                view.clue = cardView.card.reason
                cardView.card.phrase.let { phrase ->
                    view.languageTagFirst = phrase.lang.let { Locale.forLanguageTag(it) }
                    view.phraseFirst = phrase.phrase
                    phrase.pronounce?.let {
                        view.showAudioFirst = true
                        view.setOnClickButtonCardFirst { v ->
                            launch {
                                cardView.playPhrase(v.background.asAnimationDrawable())
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
                                cardView.playTranslate(v.background.asAnimationDrawable())
                            }
                        }
                    }.ifNull { view.showAudioSecond = false }
                    translate.image?.let {
                        model.getPicasso(itemView.context).load(it.imageUri.toUri()).placeholder(R.drawable.noise).into(view.getSecondImageView())
                        view.showImageSecond = true
                    }.ifNull { view.showImageSecond = false }
                    view.definitionSecond = translate.definition.orEmpty()
                }

                view.setOnClickButtonReport(auth.currentUser != null) { reportCall?.let { it(cardView.card.toGlobalCard()) } }

                model.setDownloadAction(cardView.card.globalId, endAction).ifTrue(startAction)

                view.setOnClickButtonDownload {
                    startAction()
                    model.save(cardView.card, endAction)
                }

                view.setOnClickButtonStop(false) { model.stopDownloading(cardView.card.globalId) }

                view.showButtons = true

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