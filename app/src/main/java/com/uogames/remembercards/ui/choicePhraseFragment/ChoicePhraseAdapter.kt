package com.uogames.remembercards.ui.choicePhraseFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
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

	inner class PhraseHolder(view: View) : RecyclerView.ViewHolder(view) {

		private var modelObserver: Job? = null

		private var _bind: CardPhraseBinding? = null
		private val bind get() = _bind!!

		fun onShow() {
			_bind = CardPhraseBinding.inflate(LayoutInflater.from(itemView.context), itemView as ViewGroup, false)
			val linearLayout = itemView as LinearLayout
			linearLayout.removeAllViews()
			linearLayout.addView(bind.root)
			bind.btnEdit.visibility = View.GONE
			bind.root.visibility = View.INVISIBLE
			modelObserver = model.get(adapterPosition).observeWhile(recyclerScope) { bookView ->
				bookView?.phrase?.let { phrase ->
					bind.root.setOnClickListener {
						selectedCall(phrase)
					}
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
			_bind = null
		}

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseHolder {
		return PhraseHolder(LinearLayout(parent.context).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT
			)
			orientation = LinearLayout.VERTICAL
		})
	}

	override fun onBindViewHolder(holder: PhraseHolder, position: Int) {
		holder.onShow()
		(holder.itemView as LinearLayout).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			)
		}
	}

	override fun getItemCount(): Int = model.size.value

	override fun onViewRecycled(holder: PhraseHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	fun onDestroy() {
		recyclerScope.cancel()
	}

}