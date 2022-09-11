package com.uogames.remembercards.ui.bookFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.uogames.dto.local.Image
import com.uogames.dto.local.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*

class BookAdapter(
	private val model: BookViewModel,
	private val player: ObservableMediaPlayer,
	private val editPhraseCall: (Int) -> Unit
) : ClosableAdapter<BookAdapter.CardHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)
	private val auth = Firebase.auth

	init {
		model.size.observeWhile(recyclerScope) {
			notifyDataSetChanged()
		}
	}

	inner class CardHolder(val bind: CardPhraseBinding) : RecyclerView.ViewHolder(bind.root) {

		private var bookViewObserver: Job? = null

		private var full = false

		fun onShow() {
			clear()
			bookViewObserver = recyclerScope.launch(Dispatchers.IO) {
				val bookView = model.getBookModel(adapterPosition).ifNull { return@launch }
				val phrase = bookView.phrase
				launch(Dispatchers.Main) {
					if (auth.currentUser == null || (phrase.globalOwner != null && phrase.globalOwner != auth.currentUser?.uid)) {
						bind.btnShare.visibility = View.GONE
					}

					bind.txtPhrase.text = phrase.phrase
					bind.txtDefinition.text = phrase.definition.orEmpty()
					showImage(bookView.image)
					showPronounce(bookView.pronounce)
					bind.txtLang.text = bookView.lang
					bind.btnEdit.setOnClickListener { editPhraseCall(phrase.id) }

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

					model.setShareAction(phrase, endAction).ifTrue(startAction)

					bind.btnShare.setOnClickListener {
						startAction()
						model.share(phrase, endAction)
					}
					bind.btnStop.setOnClickListener {
						model.stopSharing(phrase)
					}
				}
			}
			bind.btnAction.setOnClickListener {
				full = !full
				bind.btns.visibility = if (full) View.VISIBLE else View.GONE
				val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
				bind.imgAction.setImageResource(img)
			}
		}

		private fun clear() {
			full = false
			bind.btns.visibility = View.GONE
			bind.mcvImgPhrase.visibility = View.GONE
			bind.btnSound.visibility = View.GONE
			bind.txtPhrase.text = ""
			bind.txtLang.text = ""
			bind.txtDefinition.text = ""
			bind.progressLoading.visibility = View.GONE
			bind.btnStop.visibility = View.GONE
			bind.btnDownload.visibility = View.GONE
			auth.currentUser.ifNull { bind.btnShare.visibility = View.GONE }
			bind.imgAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
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

		private suspend fun showPronounce(pronounce: Deferred<Pronunciation?>) = showPronounce(pronounce.await())

		private fun showPronounce(pronunciation: Pronunciation?) {
			pronunciation?.let { pron ->
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
			bookViewObserver?.cancel()
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
		return CardHolder(
			CardPhraseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		)
	}

	override fun onBindViewHolder(holder: CardHolder, position: Int) {
		holder.onShow()
	}

	override fun getItemCount() = model.size.value

	override fun onViewRecycled(holder: CardHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	override fun close() {
		recyclerScope.cancel()
	}
}
