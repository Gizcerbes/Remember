package com.uogames.remembercards.ui.bookFragment

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.uogames.dto.Phrase
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.utils.ChangeableAdapter
import com.uogames.remembercards.utils.asAnimationDrawable
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observeWhile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class BookAdapter(
	private val model: BookViewModel
) : ChangeableAdapter<BookAdapter.CardHolder>() {

	companion object {
		private const val INFO_MODE = 0
	}

	private val recyclerScope = CoroutineScope(Dispatchers.Main)

	private var player: MediaPlayer? = null

	init {
		model.size.onEach {
			notifyDataSetChanged()
		}.launchIn(recyclerScope)
	}

	inner class CardHolder(
		view: LinearLayout,
		private val viewGrope: ViewGroup,
	) : ChangeableViewHolder(view, viewGrope) {


		private val infoBind: CardPhraseBinding by lazy {
			CardPhraseBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
		}

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return when (typeFragment) {
				0 -> infoBind.root
				else -> null
			}
		}

		override fun CoroutineScope.show(typeFragment: Int) {
			when (typeFragment) {
				INFO_MODE -> showInfoMode()
			}
		}

		private fun CoroutineScope.showInfoMode() {
			infoBind.btnEdit.visibility = View.GONE
			infoBind.root.visibility = View.INVISIBLE
			val position = adapterPosition
			model.get(position).observeWhile(this) { res ->
				res?.let { phrase ->
					infoBind.txtPhrase.text = phrase.phrase
					infoBind.txtDefinition.text = phrase.definition.orEmpty()
					showImage(phrase)
					showPronounce(phrase)
					showLang(phrase)
					infoBind.btnEdit.setOnClickListener {
						val activity = itemView.context as AppCompatActivity
						activity.findNavController(R.id.nav_host_fragment).navigate(R.id.addPhraseFragment, Bundle().apply {
							putInt(EditPhraseFragment.ID_PHRASE, phrase.id)
						})
					}
				}
			}
			infoBind.root.visibility = View.VISIBLE
		}

		private fun showImage(phrase: Phrase) {
			infoBind.imgPhrase.visibility = View.INVISIBLE
			infoBind.imgPhrase.visibility = phrase.idImage?.let {
				model.getImage(phrase) {
					infoBind.imgPhrase.setImageURI(it)
				}
				View.VISIBLE
			}.ifNull { View.GONE }
		}

		private fun showPronounce(phrase: Phrase) {
			infoBind.btnSound.visibility = phrase.idPronounce?.let {
				infoBind.btnSound.setOnClickListener {
					cardScope.launch(Dispatchers.IO) {
						val audio = model.getAudio(phrase).first()
						player?.stop()
						val player = MediaPlayer()
						player.setDataSource(itemView.context, audio)
						try {
							player.prepare()
						} catch (e: Exception) {
							Toast.makeText(itemView.context, e.toString(), Toast.LENGTH_SHORT).show()
						}
						player.start()
						this@BookAdapter.player = player
						launch(Dispatchers.Main) {
							infoBind.imgBtnSound.background.asAnimationDrawable().start()
							while (player.isPlaying) delay(100)
							infoBind.imgBtnSound.background.asAnimationDrawable().stop()
							infoBind.imgBtnSound.background.asAnimationDrawable().selectDrawable(0)
						}
					}
				}
				View.VISIBLE
			}.ifNull { View.GONE }
		}

		private fun showLang(phrase: Phrase) {
			phrase.lang?.let {
				val data = it.split("-")
				if (data.isNotEmpty()) try {
					infoBind.txtLang.text = Locale(data[0]).displayLanguage
				} catch (e: Exception) {
				}
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
	): CardHolder {
		return CardHolder(view, parent)
	}

	override fun getItemCount() = model.size.value



}