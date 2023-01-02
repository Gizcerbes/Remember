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
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.ui.cardFragment.CardViewModel
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import java.util.*

class ChoiceCardAdapter(
	private val model: CardViewModel,
	private val player: ObservableMediaPlayer,
	private val callChoiceCardID: (Int) -> Unit
) : ClosableAdapter<ChoiceCardAdapter.CardHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)
	private var size = 0

	init {
		model.size.observeWhile(recyclerScope) {
			size = it
			notifyDataSetChanged()
		}
	}

	inner class CardHolder(val bind: CardCardBinding) : RecyclerView.ViewHolder(bind.root) {

		private var cardObserver: Job? = null
		private var full = false

		fun onShow() {
			clear()
			cardObserver = recyclerScope.launch(Dispatchers.IO) {
				val cardView = model.get2(adapterPosition).ifNull { return@launch }
				launch(Dispatchers.Main) {
					bind.txtReason.text = cardView.card.reason
					bind.btnDownload.setOnClickListener { callChoiceCardID(cardView.card.id) }
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
			bind.btnCardAction.setOnClickListener {
				full = !full
				bind.btns.visibility = if (full) View.VISIBLE else View.GONE
				bind.txtDefinitionFirst.visibility = if (full && bind.txtDefinitionFirst.text.isNotEmpty()) View.VISIBLE else View.GONE
				bind.txtDefinitionSecond.visibility = if (full && bind.txtDefinitionSecond.text.isNotEmpty()) View.VISIBLE else View.GONE
				val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
				bind.imgBtnAction.setImageResource(img)
				if (!full) notifyItemChanged(adapterPosition)
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
			bind.imgBtnDownload.setImageResource(R.drawable.ic_baseline_add_24)
		}

		private fun setData(
                phrase: LocalPhrase?,
                pronunciation: Pronunciation?,
                image: Image?,
                langView: TextView,
                phraseView: TextView,
                soundImg: ImageView,
                button: MaterialCardView,
                phraseImage: ImageView
		) {
			phrase?.let {
				langView.text = Locale.forLanguageTag(phrase.lang).displayLanguage
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

	override fun getItemCount() = size

	override fun onViewRecycled(holder: CardHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	override fun close() {
		recyclerScope.cancel()
	}
}
