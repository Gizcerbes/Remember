package com.uogames.remembercards.ui.phrasesFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.uogames.dto.global.GlobalImage
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*

class PhraseAdapter(
        private val vm: PhraseViewModel,
        private val player: ObservableMediaPlayer,
        private val reportCall: ((GlobalPhrase) -> Unit)? = null,
        private val editCall: ((Int) -> Unit)? = null
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private val auth = Firebase.auth
    private var size = 0

    init {
        vm.size.observe(recyclerScope) {
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
                val bookView = vm.getLocalBookModel(adapterPosition).ifNull { return@safeLaunch }
                val phrase = bookView.phrase
                if (auth.currentUser == null || (phrase.globalOwner != null && phrase.globalOwner != auth.currentUser?.uid)) {
                    bind.btnShare.visibility = View.GONE
                }
                bind.txtPhrase.text = phrase.phrase
                bind.txtDefinition.text = phrase.definition.orEmpty()
                showImage(bookView.image)
                showPronounce(bookView.pronounce)
                bind.txtLang.text = bookView.lang
                bind.btnEdit.setOnClickListener { editCall?.let { it1 -> it1(phrase.id) } }
                vm.setShareAction(phrase, endAction).ifTrue(startAction)
                bind.btnShare.setOnClickListener {
                    startAction()
                    vm.share(phrase, endAction)
                }
                bind.btnStop.setOnClickListener { vm.stopSharing(phrase) }
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
            bind.btnReport.visibility = View.GONE
            auth.currentUser.ifNull { bind.btnShare.visibility = View.GONE }
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

        private suspend fun showPronounce(pronounce: Deferred<Pronunciation?>) = showPronounce(pronounce.await())

        private fun showPronounce(pronunciation: Pronunciation?) {
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

        private val startAction: () -> Unit = {
            bind.progressLoading.visibility = View.VISIBLE
            bind.btnStop.visibility = View.VISIBLE
            bind.btnDownload.visibility = View.GONE
        }

        private val endAction: (String) -> Unit = {
            bind.progressLoading.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.btnDownload.visibility = View.VISIBLE
            Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
        }

        override fun show() {
            clear()
            observer = recyclerScope.launch {
                val phraseView = vm.getByPosition(adapterPosition.toLong()).ifNull { return@launch }
                val phrase = phraseView.phrase
                bind.txtPhrase.text = phrase.phrase
                bind.txtDefinition.text = phrase.definition.orEmpty()
                showImage(phraseView.image)
                showPronounce(phraseView)
                bind.txtLang.text = phraseView.lang
                vm.setDownloadAction(phrase.globalId, endAction).ifTrue(startAction)

                bind.btnReport.setOnClickListener { reportCall?.let { it(phrase) } }

                bind.btnDownload.setOnClickListener {
                    startAction()
                    vm.save(phraseView, endAction)
                }
                bind.btnStop.setOnClickListener {
                    vm.stopDownloading(phrase.globalId)
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
            auth.currentUser.ifNull { bind.btnReport.visibility = View.GONE }
        }

        private suspend fun showImage(image: Deferred<GlobalImage?>) {
            image.await()?.let {
                val uri = it.imageUri.toUri()
                vm.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(bind.imgPhrase)
                bind.imgPhrase.visibility = View.VISIBLE
            }.ifNull {
                bind.imgPhrase.visibility = View.GONE
            }
        }

        private suspend fun showPronounce(phraseModel: PhraseViewModel.GlobalPhraseModel) {
            phraseModel.phrase.idPronounce?.let {
                bind.btnSound.visibility = View.VISIBLE
                bind.btnSound.setOnClickListener {
                    recyclerScope.launch {
                        val data = phraseModel.pronounceData.await()
                        player.play(
                                MediaBytesSource(data),
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
        return if (vm.cloud.value) 1 else 0
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

    override fun onViewRecycled(holder: ClosableHolder) {
        super.onViewRecycled(holder)
        holder.onDestroy()
    }

    override fun close() {
        recyclerScope.cancel()
    }


}