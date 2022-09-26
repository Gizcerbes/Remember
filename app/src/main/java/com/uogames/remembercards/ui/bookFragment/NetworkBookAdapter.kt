package com.uogames.remembercards.ui.bookFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.uogames.dto.global.Image
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*

class NetworkBookAdapter(
	private val model: NetworkBookViewModel,
	private val player: ObservableMediaPlayer
) : ClosableAdapter<NetworkBookAdapter.PhraseHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)
	private var size = 0

	init {
		model.size.observeWhile(recyclerScope) {
			size = it.toInt()
			notifyDataSetChanged()
		}
	}

	inner class PhraseHolder(val bind: CardPhraseBinding) : RecyclerView.ViewHolder(bind.root) {

		private var phraseViewObserver: Job? = null
		private var full = false

		fun onShow() {
			clear()
			phraseViewObserver = recyclerScope.launch {
				val phraseView = model.getByPosition(adapterPosition.toLong()).ifNull { return@launch }
				val phrase = phraseView.phrase
				bind.txtPhrase.text = phrase.phrase
				bind.txtDefinition.text = phrase.definition.orEmpty()
				showImage(phraseView.image)
				showPronounce(phraseView)
				bind.txtLang.text = phraseView.lang

				val startAction: () -> Unit = {
					bind.progressLoading.visibility = View.VISIBLE
					bind.btnStop.visibility = View.VISIBLE
					bind.btnDownload.visibility = View.GONE
				}

				val endAction: (String) -> Unit = {
					bind.progressLoading.visibility = View.GONE
					bind.btnStop.visibility = View.GONE
					bind.btnDownload.visibility = View.VISIBLE
					Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
				}

				model.setDownloadAction(phrase.globalId, endAction).ifTrue(startAction)

				bind.btnDownload.setOnClickListener {
					startAction()
					model.save(phraseView, endAction)
				}
				bind.btnStop.setOnClickListener {
					model.stopDownloading(phrase.globalId)
				}
			}

			bind.btnAction.setOnClickListener {
				full = !full
				bind.btns.visibility = if (full) View.VISIBLE else View.GONE
				val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
				bind.imgAction.setImageResource(img)
				if (!full) notifyItemChanged(adapterPosition)
			}
		}

		private fun clear() {
			full = false
			bind.btns.visibility = View.GONE
			bind.imgPhrase.visibility = View.GONE
			bind.btnSound.visibility = View.GONE
			bind.txtPhrase.text = ""
			bind.txtLang.text = ""
			bind.txtDefinition.text = ""
			bind.progressLoading.visibility = View.GONE
			bind.btnStop.visibility = View.GONE
			bind.btnEdit.visibility = View.GONE
			bind.btnShare.visibility = View.GONE
			bind.imgAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
		}

		private suspend fun showImage(image: Deferred<Image?>) {
			image.await()?.let {
				val uri = it.imageUri.toUri()
				model.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(bind.imgPhrase)
				bind.imgPhrase.visibility = View.VISIBLE
			}.ifNull {
				bind.imgPhrase.visibility = View.GONE
			}
		}

		private suspend fun showPronounce(phraseModel: NetworkBookViewModel.PhraseModel) {
			phraseModel.phrase.idPronounce?.let {
				bind.btnSound.visibility = View.VISIBLE
				bind.btnSound.setOnClickListener {
					recyclerScope.launch {
						player.play(
							MediaBytesSource(phraseModel.pronounceData.await()),
							bind.imgBtnSound.background.asAnimationDrawable()
						)
					}
				}
			}.ifNull {
				bind.btnSound.visibility = View.GONE
			}
		}

		fun onDestroy() {
			phraseViewObserver?.cancel()
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseHolder {
		return PhraseHolder(
			CardPhraseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		)
	}

	override fun onBindViewHolder(holder: PhraseHolder, position: Int) = holder.onShow()

	override fun getItemCount() = size

	override fun onViewRecycled(holder: PhraseHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	override fun close() {
		recyclerScope.cancel()
	}
}
