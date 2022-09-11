package com.uogames.remembercards.ui.cardFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.global.Image
import com.uogames.dto.global.Phrase
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*

class NetworkCardAdapter(
	private val model: NetworkCardViewModel,
	private val player: ObservableMediaPlayer
) : ClosableAdapter<NetworkCardAdapter.CardHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)

	init {
		model.size.observeWhile(recyclerScope) {
			notifyDataSetChanged()
		}
	}

	inner class CardHolder(private val bind: CardCardBinding) : RecyclerView.ViewHolder(bind.root) {

		private var cardJob: Job? = null

		private var full = false

		fun show() {
			clear()
			cardJob = recyclerScope.launch {
				val cardView = model.getByPosition(adapterPosition.toLong()).ifNull { return@launch }
				bind.txtReason.text = cardView.card.reason
				setData(
					cardView.phrase.await(),
					cardView.phrasePronounceData,
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
					cardView.translatePronounceData,
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
					bind.btnDownload.visibility = View.GONE
				}

				val endAction: (String) -> Unit = {
					if (cardJob?.isActive == true) {
						bind.progressLoading.visibility = View.GONE
						bind.btnStop.visibility = View.GONE
						bind.btnDownload.visibility = View.VISIBLE
						Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
					}
				}

				model.setDownloadAction(cardView.card.globalId, endAction).ifTrue(startAction)

				bind.btnDownload.setOnClickListener {
					startAction()
					model.save(cardView, endAction)
				}

				bind.btnStop.setOnClickListener {
					model.stopDownloading(cardView.card.globalId)
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
			bind.btnEdit.visibility = View.GONE
			bind.btnShare.visibility = View.GONE
			bind.btnStop.visibility = View.GONE
			bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
		}

		private fun setData(
			phrase: Phrase?,
			pronunciationData: Deferred<ByteArray?>,
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
				model.getPicasso(itemView.context).load(it.imageUri.toUri()).placeholder(R.drawable.noise).into(phraseImage)
				phraseImage.visibility = View.VISIBLE
			}.ifNull { phraseImage.visibility = View.GONE }

			phrase?.idPronounce?.let { _ ->
				soundImg.visibility = View.VISIBLE
				button.setOnClickListener {
					recyclerScope.launch {
						player.play(MediaBytesSource(pronunciationData.await()), soundImg.background.asAnimationDrawable())
					}
				}
			}.ifNull {
				soundImg.visibility = View.GONE
				button.setOnClickListener(null)
			}
		}

		fun onDestroy() {
			cardJob?.cancel()
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
		return CardHolder(
			CardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		)
	}

	override fun onBindViewHolder(holder: CardHolder, position: Int) = holder.show()

	override fun getItemCount() = model.size.value.toInt()

	override fun onViewRecycled(holder: CardHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	override fun close() {
		recyclerScope.cancel()
	}
}
