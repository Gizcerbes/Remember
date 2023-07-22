package com.uogames.remembercards.ui.card.cardFragment

import android.content.Context
import android.view.ViewGroup
import androidx.core.net.toUri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.uogames.dto.global.*
import com.uogames.dto.local.LocalCardView
import com.uogames.remembercards.R
import com.uogames.remembercards.models.GlobalCardModel
import com.uogames.remembercards.models.LocalCardModel
import com.uogames.remembercards.models.SearchingState
import com.uogames.remembercards.ui.dialogs.ShareAttentionDialog
import com.uogames.remembercards.ui.views.CardView
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class CardAdapter(
	private val model: Model
) : ClosableAdapter() {

	interface Model {

		val size: Flow<Int>
		val isSearching: Flow<SearchingState>

		suspend fun getLocal(position: Int): LocalCardModel?

		suspend fun getGlobal(position: Int): GlobalCardModel?

		fun isUploadFlow(v: LocalCardView): Flow<Boolean>

		fun isChangedFlow(v: LocalCardView): Flow<Boolean?>

		suspend fun isChanged(v: LocalCardView): Boolean

		fun isUploadNoticed(): Boolean

		fun setUploadNotice(b: Boolean)

		fun setUpload(v: LocalCardView)

		fun save(v: GlobalCardView)

		fun getPicasso(contest: Context): Picasso

		fun isCloud(): Boolean

		fun onEditAction(v: LocalCardView)

		fun onReportAction(v: GlobalCardView)

		fun isExistFlow(v: GlobalCardView): Flow<Boolean>

	}

	private val recyclerScope = CoroutineScope(Dispatchers.Main)
	private val auth = Firebase.auth
	private var size = 0

	init {
		model.size.observe(recyclerScope) {
			size = it
			notifyDataSetChanged()
		}

//		model.isSearching.observe(recyclerScope) {
//			if (it != SearchingState.SEARCHED) notifyDataSetChanged()
//		}
	}

	inner class LocalCardHolder(val view: CardView) : ClosableHolder(view) {

		override fun show() {
			observer = recyclerScope.launch {
				val cardView = model.getLocal(adapterPosition) ?: return@launch
				view.clue = cardView.card.reason
				view.setOnClickEdit { model.onEditAction(cardView.card) }
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

				addShareAction(cardView)

				model.isUploadFlow(cardView.card).observeLaunching(this) {
					runCatching {
						view.showProgressLoading = it
						view.showButtonShare = !it && isAvailableToShare(cardView, model.isChanged(cardView.card))
						view.showButtonEdit = !it
					}
				}

				model.isChangedFlow(cardView.card).observeLaunching(this) {
					runCatching { view.showButtonShare = isAvailableToShare(cardView, it == true) }
				}

				view.showButtons = true
			}

		}

		private fun isAvailableToShare(cardView: LocalCardModel, changed: Boolean): Boolean {
			if (!changed) return false
			if (auth.currentUser == null) return false
			if (cardView.card.globalOwner != null && cardView.card.globalOwner != auth.currentUser?.uid) return false
			return true
		}

		private fun addShareAction(cardView: LocalCardModel) {
			if (!isAvailableToShare(cardView, changed = cardView.card.changed)) return
			view.setOnClickButtonShare {
				if (model.isUploadNoticed()) {
					model.setUpload(cardView.card)
				} else {
					ShareAttentionDialog.show(itemView.context) {
						if (it) model.setUploadNotice(false)
						model.setUpload(cardView.card)
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

				view.setOnClickButtonReport(auth.currentUser != null) { model.onReportAction(cardView.card) }

				view.setOnClickButtonDownload {
					model.save(cardView.card)
				}
				model.isExistFlow(cardView.card).observe(this) { view.showButtonDownload = !it }

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