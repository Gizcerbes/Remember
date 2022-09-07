package com.uogames.remembercards.ui.choiceCardFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.uogames.dto.local.Image
import com.uogames.dto.local.Phrase
import com.uogames.dto.local.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.ui.cardFragment.CardViewModel
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class ChoiceCardAdapter(
	private val model: CardViewModel,
	private val player: ObservableMediaPlayer,
	private val callChoiceCardID: (Int) -> Unit
) : RecyclerView.Adapter<ChoiceCardAdapter.CardHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)

	init {
		model.size.observeWhile(recyclerScope) {
			notifyDataSetChanged()
		}
	}

	inner class CardHolder(val bind: CardCardBinding) : RecyclerView.ViewHolder(bind.root) {

		private var cardObserver: Job? = null

		fun onShow() {
			clear()
			cardObserver = model.get(adapterPosition).observeWhile(recyclerScope) {
				val cardView = it.ifNull { return@observeWhile }
				bind.txtReason.text = cardView.card.reason
				bind.btnCardAction.setOnClickListener { callChoiceCardID(cardView.card.id) }
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

		private fun clear() {
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
			bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_add_24)
		}

		private fun setData(
			phrase: Phrase?,
			pronunciation: Pronunciation?,
			image: Image?,
			langView: TextView,
			phraseView: TextView,
			soundImg: ImageView,
			button: MaterialCardView,
			phraseImage: ImageView
		) {
			phrase?.let {
				langView.text = Lang.parse(phrase.lang).locale.displayLanguage
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

		fun onDestroy() {
			cardObserver?.cancel()
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
		return CardHolder(
			CardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		)
	}

	override fun onBindViewHolder(holder: CardHolder, position: Int) = holder.onShow()

	override fun getItemCount() = model.size.value

	override fun onViewRecycled(holder: CardHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	fun onDestroy() {
		recyclerScope.cancel()
	}
}
