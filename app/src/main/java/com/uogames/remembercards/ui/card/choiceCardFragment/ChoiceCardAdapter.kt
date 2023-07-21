package com.uogames.remembercards.ui.card.choiceCardFragment

import android.content.Context
import android.view.ViewGroup
import androidx.core.net.toUri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.uogames.dto.global.GlobalCardView
import com.uogames.dto.local.LocalCardView
import com.uogames.remembercards.R
import com.uogames.remembercards.models.GlobalCardModel
import com.uogames.remembercards.models.LocalCardModel
import com.uogames.remembercards.models.SearchingState
import com.uogames.remembercards.ui.views.CardView
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class ChoiceCardAdapter(
	private val model: Model
) : ClosableAdapter() {

	interface Model {

		val size: Flow<Int>
		val isSearching: Flow<SearchingState>

		suspend fun getLocal(position: Int): LocalCardModel?

		suspend fun getGlobal(position: Int): GlobalCardModel?

		fun onAddAction(v: LocalCardView)

		fun onReportAction(v: GlobalCardView)

		fun getPicasso(context: Context): Picasso

		fun onSave(v: GlobalCardView)

		fun isCloud(): Boolean

	}

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

		override fun show() {
			view.reset()
			observer = recyclerScope.launch {
				val cardView = model.getLocal(adapterPosition).ifNull { return@launch }
				view.clue = cardView.card.reason
				cardView.card.phrase.let { phrase ->
					view.languageTagFirst = Locale.forLanguageTag(phrase.lang)
					view.phraseFirst = phrase.phrase
					phrase.image?.let { image ->
						Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(view.getFirstImageView())
						view.showImageFirst = true
					}.ifNull { view.showImageFirst = false }
					phrase.pronounce?.let { _ ->
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
					translate.pronounce?.let { _ ->
						view.showAudioSecond = true
						view.setOnClickButtonCardSecond {
							launch { cardView.playTranslate(it.background.asAnimationDrawable()) }
						}
					}.ifNull { view.showAudioSecond = false }
					view.definitionSecond = translate.definition.orEmpty()
				}

				view.setOnClickButtonAddListener { model.onAddAction(cardView.card) }

				view.showButtons = true
			}

		}
	}

	inner class GlobalCardHolder(val view: CardView) : ClosableHolder(view) {

		override fun show() {
			view.reset()
			observer = recyclerScope.launch {
				val cardView = model.getGlobal(adapterPosition).ifNull { return@launch }
				view.clue = cardView.card.reason
				cardView.card.phrase.let { phrase ->
					view.languageTagFirst = phrase.lang.let { Locale.forLanguageTag(it) }
					view.phraseFirst = phrase.phrase
					phrase.pronounce?.let {
						view.showAudioFirst = true
						view.setOnClickButtonCardFirst { v ->
							launch { cardView.playPhrase(v.background.asAnimationDrawable()) }
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
							launch { cardView.playTranslate(v.background.asAnimationDrawable()) }
						}
					}.ifNull { view.showAudioSecond = false }
					translate.image?.let {
						model.getPicasso(itemView.context).load(it.imageUri.toUri()).placeholder(R.drawable.noise).into(view.getSecondImageView())
						view.showImageSecond = true
					}.ifNull { view.showImageSecond = false }
					view.definitionSecond = translate.definition.orEmpty()
				}

				view.setOnClickButtonReport(auth.currentUser != null) { model.onReportAction(cardView.card) }

				view.setOnClickButtonDownload {
					model.onSave(cardView.card)
				}

				view.showButtons = true

			}
		}

		override fun onDestroy() {
			super.onDestroy()
			view.reset()
		}

	}

	override fun getItemViewType(position: Int): Int {
		return if (model.isCloud()) 1 else 0
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