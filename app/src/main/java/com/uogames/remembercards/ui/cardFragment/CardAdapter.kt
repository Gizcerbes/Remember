package com.uogames.remembercards.ui.cardFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.uogames.dto.local.Card
import com.uogames.dto.local.Image
import com.uogames.dto.local.Phrase
import com.uogames.dto.local.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class CardAdapter(
	private val model: CardViewModel,
	private val player: ObservableMediaPlayer,
	private val cardAction: (Card) -> Unit
) : ClosableAdapter<CardAdapter.CardHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)
	var size = 0
		private set
	private val auth = Firebase.auth

	init {
//		model.size.observeWhile(recyclerScope) {
//			notifyDataSetChanged()
//		}
		recyclerScope.launch {
			model.size.stateIn(this).onEach {
				size = it
				notifyDataSetChanged()
			}.launchIn(this)
		}
	}

	inner class CardHolder(val bind: CardCardBinding) : RecyclerView.ViewHolder(bind.root) {

		private var cardObserver: Job? = null

		private var full = false

		fun show() {
			clear()
			cardObserver = recyclerScope.launch(Dispatchers.IO) {
				val cardView = model.get2(adapterPosition).ifNull { return@launch }
				if (auth.currentUser == null || (cardView.card.globalOwner != null && cardView.card.globalOwner != auth.currentUser?.uid)) {
					bind.btnShare.visibility = View.GONE
				}
				launch(Dispatchers.Main) {
					bind.txtReason.text = cardView.card.reason
					bind.btnEdit.setOnClickListener { cardAction(cardView.card) }
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

					val startAction: () -> Unit = {
						bind.progressLoading.visibility = View.VISIBLE
						bind.btnStop.visibility = View.VISIBLE
						bind.btnShare.visibility = View.GONE
						bind.btnEdit.visibility = View.GONE
					}

					val endAction: (String) -> Unit = {
						bind.progressLoading.visibility = View.GONE
						bind.btnStop.visibility = View.GONE
						bind.btnShare.visibility = View.VISIBLE
						bind.btnEdit.visibility = View.VISIBLE
						Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
					}

					model.setShareAction(cardView.card, endAction).ifTrue(startAction)

					bind.btnShare.setOnClickListener {
						startAction()
						model.share(cardView.card, endAction)
					}

					bind.btnStop.setOnClickListener {
						model.stopSharing(cardView.card)
					}
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
			bind.btnDownload.visibility = View.GONE
			bind.btnStop.visibility = View.GONE
			bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
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
				langView.text = Lang.parse(phrase.lang).locale.displayLanguage
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
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
		return CardHolder(
			CardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		)
	}

	override fun onBindViewHolder(holder: CardHolder, position: Int) = holder.show()

	override fun getItemCount() = size

	override fun onViewRecycled(holder: CardHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	override fun close() {
		recyclerScope.cancel()
	}
}
