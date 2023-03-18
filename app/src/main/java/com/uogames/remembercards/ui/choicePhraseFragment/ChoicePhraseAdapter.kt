package com.uogames.remembercards.ui.choicePhraseFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import com.uogames.dto.global.GlobalImage
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*

class ChoicePhraseAdapter(
    private val model: ChoicePhraseViewModel,
    private val player: ObservableMediaPlayer,
    private val reportCall: ((GlobalPhrase) -> Unit)? = null,
    private val choiceCall: (LocalPhrase) -> Unit
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private var size = 0

    init {
        model.size.observe(recyclerScope) {
            size = it
            notifyDataSetChanged()
        }
    }

    inner class LocalPhraseHolder(val bind: CardPhraseBinding) : ClosableHolder(bind.root) {

        private var full = false

        private val startAction: () -> Unit = {
            bind.progressLoading.visibility = View.VISIBLE
            bind.btnStop.visibility = View.VISIBLE
            bind.btnShare.visibility = View.GONE
            bind.btnEdit.visibility = View.GONE
        }

        private val endAction: (String) -> Unit = {
            bind.progressLoading.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.btnShare.visibility = View.VISIBLE
            bind.btnEdit.visibility = View.VISIBLE
            Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
        }

        override fun show() {
            clear()
            observer = recyclerScope.safeLaunch {
                val bookView = model.getLocalBookModel(adapterPosition).ifNull { return@safeLaunch }
                val phrase = bookView.phrase
                bind.txtPhrase.text = phrase.phrase
                bind.txtDefinition.text = phrase.definition.orEmpty()
                showImage(bookView.image)
                showPronounce(bookView.pronounce)
                bind.txtLang.text = bookView.lang
                model.setShareAction(phrase, endAction).ifTrue(startAction)
                bind.btnEdit.setOnClickListener { choiceCall(phrase) }
            }
            bind.btnAction.setOnClickListener {
                full = !full
                bind.btns.visibility = if (full) View.VISIBLE else View.GONE
                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                bind.imgAction.setImageResource(img)
            }
        }

        private fun clear() {
            full = false
            bind.btns.visibility = View.GONE
            bind.mcvImgPhrase.visibility = View.GONE
            bind.btnSound.visibility = View.GONE
            bind.txtPhrase.text = ""
            bind.txtLang.text = ""
            bind.txtDefinition.text = ""
            bind.progressLoading.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.btnDownload.visibility = View.GONE
            bind.btnShare.visibility = View.GONE
            bind.imgEdit.setImageResource(R.drawable.ic_baseline_add_24)
            bind.imgAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        }

        private suspend fun showImage(image: Deferred<LocalImage?>) = showImage(image.await())

        private fun showImage(image: LocalImage?) {
            image?.let {
                bind.mcvImgPhrase.visibility = View.VISIBLE
                val uri = it.imgUri.toUri()
                Picasso.get().load(uri).placeholder(R.drawable.noise).into(bind.imgPhrase)
            }.ifNull {
                bind.mcvImgPhrase.visibility = View.GONE
            }
        }

        private suspend fun showPronounce(pronounce: Deferred<LocalPronunciation?>) = showPronounce(pronounce.await())

        private fun showPronounce(pronunciation: LocalPronunciation?) {
            pronunciation?.let { pron ->
                bind.btnSound.visibility = View.VISIBLE
                bind.btnSound.setOnClickListener {
                    player.play(
                        itemView.context,
                        pron.audioUri.toUri(),
                        bind.imgBtnSound.background.asAnimationDrawable()
                    )
                }
            }.ifNull {
                bind.btnSound.visibility = View.GONE
            }
        }

    }

    inner class GlobalPhraseHolder(val bind: CardPhraseBinding) : ClosableHolder(bind.root) {

        private var full = false

        override fun show() {
            clear()
            observer = recyclerScope.launch {
                val phraseView = model.getByPosition(adapterPosition.toLong()).ifNull { return@launch }
                val phrase = phraseView.phrase
                bind.txtPhrase.text = phrase.phrase
                bind.txtDefinition.text = phrase.definition.orEmpty()
                showImage(phraseView.image)
                showPronounce(phraseView)
                bind.txtLang.text = phraseView.lang

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
                        model.getByGlobalId(phraseView.phrase.globalId)?.let { phrase -> choiceCall(phrase) }
                    }
                }

                model.setDownloadAction(phrase.globalId, endAction).ifTrue(startAction)

                bind.btnDownload.setOnClickListener {
                    startAction()
                    model.save(phraseView, endAction)
                }
                bind.btnStop.setOnClickListener {
                    model.stopDownloading(phrase.globalId)
                }
            }
            bind.btnAction.setOnClickListener {
                full = !full
                bind.btns.visibility = if (full) View.VISIBLE else View.GONE
                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                bind.imgAction.setImageResource(img)
            }
        }

        fun clear() {
            full = false
            bind.btns.visibility = View.GONE
            bind.imgPhrase.visibility = View.GONE
            bind.btnSound.visibility = View.GONE
            bind.txtPhrase.text = ""
            bind.txtLang.text = ""
            bind.txtDefinition.text = ""
            bind.progressLoading.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.btnEdit.visibility = View.GONE
            bind.btnShare.visibility = View.GONE
            bind.imgAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        }

        private suspend fun showImage(image: Deferred<GlobalImage?>) {
            image.await()?.let {
                val uri = it.imageUri.toUri()
                model.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(bind.imgPhrase)
                bind.imgPhrase.visibility = View.VISIBLE
            }.ifNull {
                bind.imgPhrase.visibility = View.GONE
            }
        }

        private suspend fun showPronounce(phraseModel: ChoicePhraseViewModel.GlobalPhraseModel) {
            phraseModel.phrase.idPronounce?.let {
                bind.btnSound.visibility = View.VISIBLE
                bind.btnSound.setOnClickListener {
                    recyclerScope.launch {
                        player.play(
                            MediaBytesSource(phraseModel.pronounceData.await()),
                            bind.imgBtnSound.background.asAnimationDrawable()
                        )
                    }
                }
            }.ifNull {
                bind.btnSound.visibility = View.GONE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (model.cloud.value) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosableHolder {
        val bind = CardPhraseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            0 -> LocalPhraseHolder(bind)
            else -> GlobalPhraseHolder(bind)
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