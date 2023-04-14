package com.uogames.remembercards.ui.phrase.choicePhraseFragment

import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.local.LocalPhrase
import com.uogames.map.PhraseMap.toGlobalPhrase
import com.uogames.map.PhraseMap.toLocalPhrase
import com.uogames.remembercards.R
import com.uogames.remembercards.ui.views.PhraseView
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import java.util.*

class ChoicePhraseAdapter(
    private val vm: ChoicePhraseViewModel,
    private val player: ObservableMediaPlayer,
    private val reportCall: ((GlobalPhrase) -> Unit)? = null,
    private val choiceCall: (LocalPhrase) -> Unit
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private var size = 0

    init {
        vm.size.observe(recyclerScope) {
            size = it
            notifyDataSetChanged()
        }
    }

    inner class LocalPhraseHolder(val view: PhraseView) : ClosableHolder(view) {

        private val startAction: () -> Unit = {
            view.showProgressLoading = true
            view.showButtonStop = true
            view.showButtonShare = false
            view.showButtonAdd = false
        }

        private val endAction: (String) -> Unit = {
            view.showProgressLoading = false
            view.showButtonStop = false
            view.showButtonShare = true
            view.showButtonAdd = true
            Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
        }

        override fun show() {
            view.reset()
            view.showButtonAction = false
            observer = recyclerScope.safeLaunch {
                val phraseView = vm.getLocalBookModel(adapterPosition).ifNull { return@safeLaunch }
                val phrase = phraseView.phrase
                view.phrase = phrase.phrase
                view.definition = phrase.definition.orEmpty()
                phrase.image?.imgUri?.let { uri ->
                    vm.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(view.getImageView())
                    view.showImage = true
                }.ifNull { view.showImage = false }
                phrase.pronounce?.audioUri?.let { uri ->
                    view.setOnClickButtonSound {
                        player.play(itemView.context, uri.toUri(), it.background.asAnimationDrawable())
                    }
                }.ifNull { view.setOnClickButtonSound(false,null) }
                view.language = Locale.forLanguageTag(phraseView.phrase.lang)
                vm.setShareAction(phrase, endAction).ifTrue(startAction)
                view.setOnClickButtonAdd { choiceCall(phrase.toLocalPhrase()) }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            view.reset()
        }

    }

    inner class GlobalPhraseHolder(val view: PhraseView) : ClosableHolder(view) {

        private val startAction: () -> Unit = {
            view.showProgressLoading = true
            view.showButtonStop = true
            view.showButtonDownload = false
        }

        private val endAction: (String, LocalPhrase?) -> Unit = { _, phrase ->
            view.showProgressLoading = false
            view.showButtonStop = false
            view.showButtonDownload = true
            phrase?.let{ recyclerScope.launch { choiceCall(phrase) }}
        }

        override fun show() {
            view.reset()
            observer = recyclerScope.launch {
                val phraseView = vm.getGlobalAsync(adapterPosition.toLong()).await().ifNull { return@launch }
                val phrase = phraseView.phraseView
                view.phrase = phrase.phrase
                view.definition = phrase.definition.orEmpty()
                phraseView.image?.imageUri?.let { uri ->
                    vm.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(view.getImageView())
                    view.showImage = true
                }.ifNull { view.showImage = false }
                phrase.pronounce?.let {
                    view.setOnClickButtonSound { v ->
                        recyclerScope.launch {
                            phraseView.pronounceData.await()?.let {
                                player.play(MediaBytesSource(it), v.background.asAnimationDrawable())
                            }
                        }
                    }
                }.ifNull { view.setOnClickButtonSound(false,null) }
                view.language = Locale.forLanguageTag(phrase.lang)

                view.setOnClickButtonReport { reportCall?.let { it(phrase.toGlobalPhrase()) } }

                view.setOnClickButtonDownload {
                    startAction()
                    vm.save(phraseView, endAction)
                }

                view.setOnClickButtonStop(false) { vm.stopDownloading(phrase.globalId) }

                vm.setDownloadAction(phrase.globalId, endAction).ifTrue(startAction)
            }

        }

        override fun onDestroy() {
            super.onDestroy()
            view.reset()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (vm.cloud.value) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosableHolder {
        return when (viewType) {
            0 -> LocalPhraseHolder(PhraseView(parent.context))
            else -> GlobalPhraseHolder(PhraseView(parent.context))
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