package com.uogames.remembercards.ui.choicePhraseFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uogames.dto.local.Image
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.ui.bookFragment.BookViewModel
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*

class ChoicePhraseAdapter(
        val model: BookViewModel,
        val player: ObservableMediaPlayer,
        val editCall: (LocalPhrase) -> Unit,
        val selectedCall: (LocalPhrase) -> Unit
) : ClosableAdapter<ChoicePhraseAdapter.PhraseHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)
	private var size = 0

	init {
		model.size.observeWhile(recyclerScope) {
			size = it
			notifyDataSetChanged()
		}
	}

	inner class PhraseHolder(val bind: CardPhraseBinding) : RecyclerView.ViewHolder(bind.root) {

		private var modelObserver: Job? = null

		fun onShow() {
			clear()
			modelObserver = recyclerScope.launch(Dispatchers.IO) {
				val bookView = model.getBookModel(adapterPosition).ifNull { return@launch }
				val phrase = bookView.phrase
				launch(Dispatchers.Main) {
					bind.btnAction.setOnClickListener { selectedCall(phrase) }
					bind.txtPhrase.text = phrase.phrase
					bind.txtDefinition.text = phrase.definition.orEmpty()
					showImage(bookView.image)
					showPronounce(bookView.pronounce)
					bind.txtLang.text = bookView.lang
					bind.btnEdit.setOnClickListener { editCall(phrase) }

				}
			}
		}

		fun clear() {
			bind.btns.visibility = View.GONE
			bind.mcvImgPhrase.visibility = View.GONE
			bind.btnSound.visibility = View.GONE
			bind.txtPhrase.text = ""
			bind.txtLang.text = ""
			bind.txtDefinition.text = ""
			bind.imgAction.setImageResource(R.drawable.ic_baseline_add_24)
		}

		private suspend fun showImage(image: Deferred<Image?>) = showImage(image.await())

		private fun showImage(image: Image?) {
			image?.let {
				bind.mcvImgPhrase.visibility = View.VISIBLE
				val uri = it.imgUri.toUri()
				Picasso.get().load(uri).placeholder(R.drawable.noise).into(bind.imgPhrase)
			}.ifNull {
				bind.mcvImgPhrase.visibility = View.GONE
			}
		}

		private suspend fun showPronounce(pronounce: Deferred<Pronunciation?>) {
			pronounce.await()?.let { pron ->
				bind.btnSound.visibility = View.VISIBLE
				bind.btnSound.setOnClickListener {
					player.play(
						itemView.context,
						pron.audioUri.toUri(),
						bind.imgBtnSound.background.asAnimationDrawable()
					)
				}
			}.ifNull {
				bind.btnSound.visibility = View.GONE
			}
		}

		fun onDestroy() {
			modelObserver?.cancel()
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
