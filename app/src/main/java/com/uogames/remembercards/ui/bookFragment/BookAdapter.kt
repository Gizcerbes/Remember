package com.uogames.remembercards.ui.bookFragment

import android.graphics.drawable.AnimationDrawable
import android.media.MediaDataSource
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.uogames.dto.Phrase
import com.uogames.flags.Languages
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.utils.ChangeableAdapter
import com.uogames.remembercards.utils.observeWhile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

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
			infoBind.root.visibility = View.INVISIBLE
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
							putInt(EditPhraseFragment.ID_PHRASE, phrase.id)
						})
					}
				}
				infoBind.root.visibility = View.VISIBLE
			}
		}

		private fun showImage(phrase: Phrase) {
			phrase.idImage?.let {
				infoBind.imgPhrase.visibility = View.VISIBLE
				infoBind.imgPhrase.setImageResource(R.drawable.noise)
				model.getImage(phrase){
					//infoBind.imgPhrase.setImageBitmap(it)
					infoBind.imgPhrase.setImageURI(it)
				}
			} ?: run { infoBind.imgPhrase.visibility = View.GONE }
		}

		private fun showPronounce(phrase: Phrase) {
			phrase.idPronounce?.let {
				infoBind.btnSound.visibility = View.VISIBLE
				infoBind.btnSound.setOnClickListener {
					cardScope.launch(Dispatchers.IO) {
						val audio = model.getAudio(phrase).first()
						player?.stop()
						val player = MediaPlayer()
						player.setDataSource(itemView.context, audio)
						try {
							player.prepare()
						} catch (e :Exception){
							Toast.makeText(itemView.context, e.toString(), Toast.LENGTH_SHORT).show()
						}
						player.start()
						this@BookAdapter.player = player
						launch(Dispatchers.Main) {
							(infoBind.imgBtnSound.background as AnimationDrawable).start()
							while (player.isPlaying) delay(100)
							(infoBind.imgBtnSound.background as AnimationDrawable).stop()
							(infoBind.imgBtnSound.background as AnimationDrawable).selectDrawable(0)
						}
					}
				}
			} ?: run { infoBind.btnSound.visibility = View.GONE }
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
		changeListener: MutableStateFlow<Int>
	): CardHolder {
		return CardHolder(view, parent, changeListener)
	}

	override fun getItemCount() = model.size.value



}