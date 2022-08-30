package com.uogames.remembercards.ui.choicePhraseFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uogames.dto.local.Image
import com.uogames.dto.local.Phrase
import com.uogames.dto.local.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.ui.bookFragment.BookViewModel
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*

class ChoicePhraseAdapter(
	val model: BookViewModel,
	val player: ObservableMediaPlayer,
	val editCall: (Phrase) -> Unit,
	val selectedCall: (Phrase) -> Unit
) : RecyclerView.Adapter<ChoicePhraseAdapter.PhraseHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)

	init {
		model.size.observeWhile(recyclerScope) { notifyDataSetChanged() }
	}

	inner class PhraseHolder(val bind: CardPhraseBinding) : RecyclerView.ViewHolder(bind.root) {

		private var modelObserver: Job? = null

		fun onShow() {
			clear()
			modelObserver = recyclerScope.launch(Dispatchers.IO) {
				val bookView = model.getBookModel(adapterPosition).ifNull { return@launch }
				val phrase = bookView.phrase
				launch(Dispatchers.Main) {
					bind.root.setOnClickListener { selectedCall(phrase) }
					bind.txtPhrase.text = phrase.phrase
					bind.txtDefinition.text = phrase.definition.orEmpty()
					showImage(bookView.image)
					showPronounce(bookView.pronounce)
					bind.txtLang.text = bookView.lang
					bind.btnEdit.setOnClickListener { editCall(phrase) }
					bind.root.visibility = View.VISIBLE
				}
			}

		}

		private fun clear() {
			bind.btns.visibility = View.GONE
			bind.imgAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
			bind.imgPhrase.visibility = View.GONE
			bind.btnSound.visibility = View.GONE
			bind.txtPhrase.text = ""
			bind.txtLang.text = ""
			bind.txtDefinition.text = ""
		}

		private suspend fun showImage(image: Deferred<Image?>) {
			bind.imgPhrase.visibility = View.INVISIBLE
			image.await()?.let {
				val uri = it.imgUri.toUri()
				Picasso.get().load(uri).placeholder(R.drawable.noise).into(bind.imgPhrase)
				bind.imgPhrase.visibility = View.VISIBLE
			}.ifNull {
				bind.imgPhrase.visibility = View.GONE
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

	override fun getItemCount(): Int = model.size.value

	override fun onViewRecycled(holder: PhraseHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	fun onDestroy() {
		recyclerScope.cancel()
	}

}