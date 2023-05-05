package com.uogames.remembercards.ui.phrase.phrasesFragment

import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import com.uogames.map.PhraseMap.toGlobalPhrase
import com.uogames.remembercards.R
import com.uogames.remembercards.ui.dialogs.ShareAttentionDialog
import com.uogames.remembercards.ui.views.PhraseView
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*

class PhraseAdapter(
    private val vm: PhraseViewModel,
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

    inner class LocalPhraseHolder(val view: PhraseView) : ClosableHolder(view) {

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
            observer = recyclerScope.safeLaunch {
                val phraseView = vm.getLocalModel(adapterPosition).ifNull { return@safeLaunch }
                val phrase = phraseView.phrase
                view.phrase = phrase.phrase
                view.definition = phrase.definition.orEmpty()
                phrase.image?.imgUri?.let { uri ->
                    vm.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(view.getImageView())
                    view.showImage = true
                }.ifNull { view.showImage = false }
                phrase.pronounce?.audioUri?.let { _ ->
                    view.setOnClickButtonSound { v ->
                        launch { phraseView.play(v.background.asAnimationDrawable()) }
                    }
                }.ifNull { view.setOnClickButtonSound(false, null) }
                view.language = Locale.forLanguageTag(phraseView.phrase.lang)
                view.setOnClickListenerButtonEdit { editCall?.let { it1 -> it1(phrase.id) } }
                //vm.setShareAction(phrase, endAction).ifTrue(startAction)

                setShareAction(phrase)

                vm.getShareAction(phrase).observe(this) {
                    view.showProgressLoading = it
                    view.showButtonShare = !it && isAvailableToShare(phrase, vm.isChanged(phrase).first() == true)
                    view.showButtonEdit = !it
                }

                vm.isChanged(phrase).observe(this) { view.showButtonShare = isAvailableToShare(phrase, it == true) }

                //view.setOnClickButtonStop(false) { vm.stopSharing(phrase) }
            }
        }

        private fun isAvailableToShare(phrase: LocalPhraseView, changed: Boolean): Boolean {
            if (!changed) return false
            if (auth.currentUser == null) return false
            if (phrase.globalOwner != null && phrase.globalOwner != auth.currentUser?.uid) return false
            return true
        }

        private fun setShareAction(phrase: LocalPhraseView) {
            if (!isAvailableToShare(phrase, phrase.changed)) return
            view.setOnClickButtonShare {
                vm.shareNotice.value?.let {
                    startAction()
                    vm.share(phrase, endAction)
                }.ifNull {
                    ShareAttentionDialog.show(itemView.context) {
                        startAction()
                        vm.share(phrase, endAction)
                        it.ifTrue { vm.showShareNotice(false) }
                    }
                }
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

        private val endAction: (String, LocalPhrase?) -> Unit = { m, _ ->
            view.showProgressLoading = false
            view.showButtonStop = false
            view.showButtonDownload = true
            Toast.makeText(itemView.context, m, Toast.LENGTH_SHORT).show()
        }

        override fun show() {
            view.reset()
            observer = recyclerScope.launch {
                val phraseView = vm.getGlobalModel(adapterPosition).ifNull { return@launch }
                val phrase = phraseView.phraseView
                view.phrase = phrase.phrase
                view.definition = phrase.definition.orEmpty()

                phrase.image?.imageUri?.let { uri ->
                    vm.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(view.getImageView())
                    view.showImage = true
                }.ifNull { view.showImage = false }
                phrase.pronounce?.let {
                    view.setOnClickButtonSound { v ->
                        launch { phraseView.play(v.background.asAnimationDrawable()) }
                    }
                }.ifNull { view.setOnClickButtonSound(false, null) }
                view.language = Locale.forLanguageTag(phrase.lang)
                vm.setDownloadAction(phrase.globalId, endAction).ifTrue(startAction)

                view.setOnClickButtonReport(auth.currentUser != null) { reportCall?.let { it(phrase.toGlobalPhrase()) } }

                view.setOnClickButtonDownload {
                    startAction()
                    vm.save(phrase, endAction)
                }
                view.setOnClickButtonStop(false) { vm.stopDownloading(phrase.globalId) }
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

    override fun onViewRecycled(holder: ClosableHolder) {
        super.onViewRecycled(holder)
        holder.onDestroy()
    }

    override fun close() {
        recyclerScope.cancel()
    }


}