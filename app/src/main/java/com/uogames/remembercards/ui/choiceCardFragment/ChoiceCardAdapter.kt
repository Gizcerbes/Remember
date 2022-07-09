package com.uogames.remembercards.ui.choiceCardFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.Image
import com.uogames.dto.Phrase
import com.uogames.dto.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.ui.cardFragment.CardViewModel
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*

class ChoiceCardAdapter(
	private val model: CardViewModel,
	private val player: ObservableMediaPlayer,
	scope: LifecycleCoroutineScope,
	val callChoiceCardID: (Int) -> Unit
) : ChangeableAdapter<ChoiceCardAdapter.CardHolder>(scope) {

	init {
		model.size.observeWhenStarted(scope) {
			notifyDataSetChanged()
		}
	}

	inner class CardHolder(view: LinearLayout, viewGrope: ViewGroup, private val scope: LifecycleCoroutineScope) :
		ChangeableViewHolder(view, viewGrope, scope) {

		private val bind by lazy {
			CardCardBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
		}

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return bind.root
		}


		override suspend fun CoroutineScope.show(typeFragment: Int, end: () -> Unit) {
			bind.root.visibility = View.INVISIBLE
			model.get(adapterPosition).observeWhenStarted(scope) {
				it?.let { cardView ->
					bind.txtReason.text = cardView.card.reason
					bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_add_24)
					bind.btnCardAction.setOnClickListener {
						callChoiceCardID(cardView.card.id)
					}
					setData(
						cardView.phrase.await(),
						cardView.phrasePronounce?.await(),
						cardView.phraseImage?.await(),
						bind.txtLangFirst,
						bind.txtPhraseFirst,
						bind.imgSoundFirst,
						bind.mcvFirst,
						bind.imgCardFirst
					)
					setData(
						cardView.translate.await(),
						cardView.translatePronounce?.await(),
						cardView.translateImage?.await(),
						bind.txtLangSecond,
						bind.txtPhraseSecond,
						bind.imgSoundSecond,
						bind.mcvSecond,
						bind.imgCardSecond
					)
				}
				bind.root.visibility = View.VISIBLE
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
			phraseImage: ImageView
		) {
			phrase?.let {
				langView.text = model.getDisplayLang(phrase)
				phraseView.text = phrase.phrase
			}

			image?.let {
				phraseImage.visibility = View.VISIBLE
				phraseImage.setImageURI(it.imgUri.toUri())
			}.ifNull { phraseImage.visibility = View.GONE }

			pronunciation?.let { pronounce ->
				soundImg.visibility = View.VISIBLE
				button.setOnClickListener {
					player.play(itemView.context, pronounce.audioUri.toUri(), soundImg.background.asAnimationDrawable())
				}
			}.ifNull { soundImg.visibility = View.GONE }
		}

	}


	override fun onShow(parent: ViewGroup, view: LinearLayout, viewType: Int, scope: LifecycleCoroutineScope): CardHolder {
		return CardHolder(view, parent, scope)
	}

	override fun getItemCount(): Int = model.size.value


}