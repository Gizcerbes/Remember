package com.uogames.remembercards.ui.bookFragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.uogames.dto.Phrase
import com.uogames.flags.Countries
import com.uogames.flags.Languages
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.ui.addPhraseFragment.AddPhraseFragment
import com.uogames.remembercards.utils.ChangeableAdapter
import com.uogames.remembercards.utils.observeWhile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BookAdapter(
	private val model: BookViewModel
) : ChangeableAdapter<BookAdapter.CardHolder>() {

	companion object {
		private const val INFO_MODE = 0
	}

	private var player: MediaPlayer? = null

	inner class CardHolder(
		view: LinearLayout,
		private val viewGrope: ViewGroup,
		dataChange: MutableStateFlow<Int>
	) : ChangeableViewHolder(view, viewGrope, dataChange) {


		private val infoBind: CardPhraseBinding by lazy {
			CardPhraseBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
		}

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return when (typeFragment) {
				0 -> infoBind.root
				else -> null
			}
		}

		override fun show(typeFragment: Int) {
			when (typeFragment) {
				INFO_MODE -> showInfoMode()
			}
		}

		private fun showInfoMode() {
			model.get(adapterPosition).observeWhile(cardScope) { res ->
				res?.let { phrase ->
					infoBind.txtPhrase.text = phrase.phrase
					infoBind.txtDefinition.text = phrase.definition.orEmpty()
					showImage(phrase)
					showPronounce(phrase)
					showLang(phrase)
					infoBind.btnEdit.setOnClickListener {
						val activity = itemView.context as AppCompatActivity
						activity.findNavController(R.id.nav_host_fragment).navigate(R.id.addPhraseFragment, Bundle().apply {
							putInt(AddPhraseFragment.ID_PHRASE, phrase.id)
						})
					}
				}
			}
		}

		private fun showImage(phrase: Phrase) {
			phrase.idImage?.let {
				infoBind.imgPhrase.visibility = View.VISIBLE
				cardScope.launch {
					infoBind.imgPhrase.setImageBitmap(model.getImage(phrase).first())
				}
			} ?: run { infoBind.imgPhrase.visibility = View.GONE }
		}

		private fun showPronounce(phrase: Phrase) {
			phrase.idPronounce?.let {
				infoBind.btnSound.visibility = View.VISIBLE
				infoBind.btnSound.setOnClickListener {
					cardScope.launch {
						val audio = model.getAudio(phrase).first()
						player?.stop()
						player = MediaPlayer()
						player?.setDataSource(audio)
						player?.prepare()
						player?.start()
					}
				}
			} ?: run { infoBind.btnSound.visibility = View.GONE }
		}

		private fun showLang(phrase: Phrase) {
			phrase.lang?.let {
				val data = it.split("-")
				if (data.isNotEmpty()) try {
					infoBind.txtLang.text = Languages.valueOf(data[0]).language
				} catch (e: Exception) {
				}
				if (data.size > 1) infoBind.imgCountry.setImageResource(Countries.valueOf(data[1]).res)
			}
		}

		override fun onDetached() {
			player?.stop()
		}

	}

	override fun onShow(
		parent: ViewGroup,
		view: LinearLayout,
		viewType: Int,
		changeListener: MutableStateFlow<Int>
	): CardHolder {
		return CardHolder(view, parent, changeListener)
	}

	override fun getItemCount() = model.size.value



}