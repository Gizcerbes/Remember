package com.uogames.remembercards.ui.cardFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.uogames.dto.local.Card
import com.uogames.dto.local.Image
import com.uogames.dto.local.Phrase
import com.uogames.dto.local.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class CardAdapter(
	private val model: CardViewModel,
	private val player: ObservableMediaPlayer,
	private val cardAction: (Card) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)

	inner class CardHolder(view: View) : RecyclerView.ViewHolder(view) {

		private var cardObserver: Job? = null

		private var _bind: CardCardBinding? = null
		private val bind get() = _bind!!

		private var full = false

		fun show() {
			full = false
			_bind = CardCardBinding.inflate(LayoutInflater.from(itemView.context), itemView as ViewGroup, false)
			bind.txtDefinitionFirst.visibility = View.GONE
			bind.txtDefinitionSecond.visibility = View.GONE
			bind.imgCardFirst.visibility = View.GONE
			bind.imgCardSecond.visibility = View.GONE
			bind.btns.visibility = View.GONE
			bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)

			val linearLayout = itemView as LinearLayout
			linearLayout.removeAllViews()
			linearLayout.addView(bind.root)
			cardObserver = model.get(adapterPosition).observeWhile(recyclerScope) {
				it?.let { cardView ->
					bind.txtReason.text = cardView.card.reason
					bind.btnEdit.setOnClickListener { cardAction(cardView.card) }
					bind.btnShare.setOnClickListener {
						model.share(cardView.card.id) { message ->
							Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
						}
					}
					setData(
						cardView.phrase.await(),
						cardView.phrasePronounce.await(),
						cardView.phraseImage.await(),
						bind.txtLangFirst,
						bind.txtPhraseFirst,
						bind.imgSoundFirst,
						bind.mcvFirst,
						bind.imgCardFirst,
						bind.txtDefinitionFirst
					)
					setData(
						cardView.translate.await(),
						cardView.translatePronounce.await(),
						cardView.translateImage.await(),
						bind.txtLangSecond,
						bind.txtPhraseSecond,
						bind.imgSoundSecond,
						bind.mcvSecond,
						bind.imgCardSecond,
						bind.txtDefinitionSecond
					)
					bind.root.visibility = View.VISIBLE
				}
			}
			bind.btnCardAction.setOnClickListener {
				full = !full
				bind.btns.visibility = if (full) View.VISIBLE else View.GONE
				bind.txtDefinitionFirst.visibility = if (full && bind.txtDefinitionFirst.text.isNotEmpty()) View.VISIBLE else View.GONE
				bind.txtDefinitionSecond.visibility = if (full && bind.txtDefinitionSecond.text.isNotEmpty()) View.VISIBLE else View.GONE
				val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
				bind.imgBtnAction.setImageResource(img)
			}

		}

		private fun setData(
			phrase: Phrase?,
			pronunciation: Pronunciation?,
			image: Image?,
			langView: TextView,
			phraseView: TextView,
			soundImg: ImageView,
			button: MaterialCardView,
			phraseImage: ImageView,
			definition: TextView
		) {
			phrase?.let {
				langView.text = model.getDisplayLang(phrase)
				phraseView.text = phrase.phrase
				definition.text = phrase.definition.orEmpty()
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
			_bind = null
		}

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
		return CardHolder(LinearLayout(parent.context).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT
			)
			orientation = LinearLayout.VERTICAL
		})
	}

	override fun onBindViewHolder(holder: CardHolder, position: Int) {
		holder.show()
		(holder.itemView as LinearLayout).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			)
		}
	}

	override fun getItemCount(): Int {
		return model.size.value
	}

	override fun onViewRecycled(holder: CardHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	fun onDestroy() {
		recyclerScope.cancel()
	}

}