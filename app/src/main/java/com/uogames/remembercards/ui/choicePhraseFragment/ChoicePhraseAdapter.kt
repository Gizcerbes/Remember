package com.uogames.remembercards.ui.choicePhraseFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import com.uogames.dto.Image
import com.uogames.dto.Phrase
import com.uogames.dto.Pronunciation
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.ui.bookFragment.BookViewModel
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*

class ChoicePhraseAdapter(
	val scope: LifecycleCoroutineScope,
	val model: BookViewModel,
	val player: ObservableMediaPlayer,
	val editCall: (Phrase) -> Unit,
	val selectedCall: (Phrase) -> Unit
) : ChangeableAdapter<ChoicePhraseAdapter.PhraseHolder>(scope) {

	companion object {
		private const val INFO_MODE = 0
	}

	inner class PhraseHolder(
		layout: LinearLayout,
		viewGrope: ViewGroup,
		val scope: LifecycleCoroutineScope
	) :
		ChangeableAdapter.ChangeableViewHolder(layout, viewGrope, scope) {

		private val infoBind: CardPhraseBinding by lazy {
			CardPhraseBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
		}

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return when (typeFragment) {
				0 -> infoBind.root
				else -> null
			}
		}

		override suspend fun CoroutineScope.show(typeFragment: Int, end: () -> Unit) {
			when (typeFragment) {
				INFO_MODE -> showInfoMode()
			}
		}

		private fun showInfoMode() {
			infoBind.btnEdit.visibility = View.GONE
			infoBind.root.visibility = View.INVISIBLE
			model.get(adapterPosition).observeWhenStarted(scope) { bookView ->
				bookView?.phrase?.let { phrase ->
					infoBind.root.setOnClickListener {
						selectedCall(phrase)
					}
					infoBind.txtPhrase.text = phrase.phrase
					infoBind.txtDefinition.text = phrase.definition.orEmpty()
					showImage(bookView.image)
					showPronounce(bookView.pronounce)
					infoBind.txtLang.text = bookView.lang
					infoBind.btnEdit.setOnClickListener { editCall(phrase) }
					infoBind.root.visibility = View.VISIBLE
				}
			}
		}

		private suspend fun showImage(image: Deferred<Image?>) {
			infoBind.imgPhrase.visibility = View.INVISIBLE
			image.await()?.let {
				val uri = it.imgUri.toUri()
				infoBind.imgPhrase.setImageURI(uri)
				infoBind.imgPhrase.visibility = View.VISIBLE
			}.ifNull {
				infoBind.imgPhrase.visibility = View.GONE
			}
		}

		private suspend fun showPronounce(pronounce: Deferred<Pronunciation?>) {
			pronounce.await()?.let { pron ->
				infoBind.btnSound.visibility = View.VISIBLE
				infoBind.btnSound.setOnClickListener {
					player.play(
						itemView.context,
						pron.audioUri.toUri(),
						infoBind.imgBtnSound.background.asAnimationDrawable()
					)
				}
			}.ifNull {
				infoBind.btnSound.visibility = View.GONE
			}
		}

		override fun onDetached() {
			player.stop()
		}
	}

	override fun onShow(parent: ViewGroup, view: LinearLayout, viewType: Int, scope: LifecycleCoroutineScope): PhraseHolder {
		return PhraseHolder(view, parent, scope)
	}

	override fun getItemCount() = model.size.value


}