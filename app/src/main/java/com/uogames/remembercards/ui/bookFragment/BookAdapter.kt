package com.uogames.remembercards.ui.bookFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import com.uogames.dto.Image
import com.uogames.dto.Pronunciation
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

class BookAdapter(
	private val model: BookViewModel,
	private val player: ObservableMediaPlayer,
	val lifecycleScope: LifecycleCoroutineScope,
	private val editPhraseCall: (Int) -> Unit
) : ChangeableAdapter<BookAdapter.CardHolder>(lifecycleScope) {

	companion object {
		private const val INFO_MODE = 0
	}

	inner class CardHolder(
		view: LinearLayout,
		private val viewGrope: ViewGroup,
		lifecycleScope: LifecycleCoroutineScope
	) : ChangeableViewHolder(view, viewGrope, lifecycleScope) {


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

		private suspend fun showInfoMode() {
			infoBind.root.visibility = View.INVISIBLE

			model.get(adapterPosition).observeWhenStarted(lifecycleScope) { bookView ->
				bookView?.phrase?.let { phrase ->
					infoBind.txtPhrase.text = phrase.phrase
					infoBind.txtDefinition.text = phrase.definition.orEmpty()
					showImage(bookView.image)
					showPronounce(bookView.pronounce)
					infoBind.txtLang.text = bookView.lang
					infoBind.btnEdit.setOnClickListener {
						editPhraseCall(phrase.id)
					}

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

	}

	override fun onShow(
		parent: ViewGroup,
		view: LinearLayout,
		viewType: Int,
		scope: LifecycleCoroutineScope
	): CardHolder {
		return CardHolder(view, parent, scope)
	}

	override fun getItemCount() = model.size.value


}