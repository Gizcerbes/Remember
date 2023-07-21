package com.uogames.remembercards.ui.phrase.choicePhraseFragment

import android.content.Context
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalPhraseView
import com.uogames.remembercards.R
import com.uogames.remembercards.models.GlobalPhraseModel
import com.uogames.remembercards.models.LocalPhraseModel
import com.uogames.remembercards.ui.views.PhraseView
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class ChoicePhraseAdapter(
    private val vm: Model
) : ClosableAdapter() {

    interface Model {

        val size: Flow<Int>

        suspend fun getLocal(position: Int): LocalPhraseModel?

        suspend fun getGlobal(position: Int): GlobalPhraseModel?

        fun getPicasso(context: Context): Picasso

        fun onSave(v: GlobalPhraseView)

        fun isCloud(): Boolean

        fun onAddAction(v: LocalPhraseView)

        fun onReportAction(v: GlobalPhraseView)

    }

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

        override fun show() {
            view.reset()
            view.showButtonAction = false
            observer = recyclerScope.safeLaunch {
                val phraseView = vm.getLocal(adapterPosition).ifNull { return@safeLaunch }
                val phrase = phraseView.phrase
                view.phrase = phrase.phrase
                view.definition = phrase.definition.orEmpty()
                phrase.image?.imgUri?.let { uri ->
                    vm.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(view.getImageView())
                    view.showImage = true
                }.ifNull { view.showImage = false }
                phrase.pronounce?.audioUri?.let { _ ->
                    view.setOnClickButtonSound {
                        launch { phraseView.playPhrase(it.background.asAnimationDrawable()) }
                    }
                }.ifNull { view.setOnClickButtonSound(false,null) }
                view.language = Locale.forLanguageTag(phraseView.phrase.lang)
               // view.setOnClickButtonAdd { choiceCall(phrase.toLocalPhrase()) }
                view.setOnClickButtonAdd { vm.onAddAction(phrase) }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            view.reset()
        }

    }

    inner class GlobalPhraseHolder(val view: PhraseView) : ClosableHolder(view) {

        override fun show() {
            view.reset()
            observer = recyclerScope.launch {
                val phraseView = vm.getGlobal(adapterPosition).ifNull { return@launch }
                val phrase = phraseView.phrase
                view.phrase = phrase.phrase
                view.definition = phrase.definition.orEmpty()
                phraseView.phrase.image?.imageUri?.let { uri ->
                    vm.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(view.getImageView())
                    view.showImage = true
                }.ifNull { view.showImage = false }
                phrase.pronounce?.let {
                    view.setOnClickButtonSound { v ->
                        launch { phraseView.playPhrase(v.background.asAnimationDrawable()) }
                    }
                }.ifNull { view.setOnClickButtonSound(false,null) }
                view.language = Locale.forLanguageTag(phrase.lang)

                //view.setOnClickButtonReport(auth.currentUser != null) { reportCall?.let { it(phrase.toGlobalPhrase()) } }
                view.setOnClickButtonReport(auth.currentUser != null) { vm.onReportAction(phrase) }

                view.setOnClickButtonDownload {
                    //startAction()
                    //vm.save(phraseView.phraseView, endAction)
                    vm.onSave(phrase)
                }

                //view.setOnClickButtonStop(false) { vm.stopDownloading(phrase.globalId) }

                //vm.setDownloadAction(phrase.globalId, endAction).ifTrue(startAction)
            }

        }

        override fun onDestroy() {
            super.onDestroy()
            view.reset()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (vm.isCloud()) 1 else 0
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