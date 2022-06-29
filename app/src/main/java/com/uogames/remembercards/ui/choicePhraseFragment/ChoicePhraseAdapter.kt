package com.uogames.remembercards.ui.choicePhraseFragment

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.findNavController
import com.uogames.dto.Phrase
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.ui.bookFragment.BookViewModel
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*

class ChoicePhraseAdapter(
	scope: LifecycleCoroutineScope,
	val model: BookViewModel,
	val player: ObservableMediaPlayer,
	val selectedCall: (Phrase) -> Unit
) : ChangeableAdapter<ChoicePhraseAdapter.PhraseHolder>(scope) {

	companion object {
		private const val INFO_MODE = 0
	}

	private val recyclerScope = CoroutineScope(Dispatchers.Main)


	init {
		model.size.onEach {
			notifyDataSetChanged()
			Log.e("TAG", "$it")
		}.launchIn(recyclerScope)
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

		override fun LifecycleCoroutineScope.show(typeFragment: Int) {
			when (typeFragment) {
				INFO_MODE -> scope.showInfoMode()
			}
		}

		private fun LifecycleCoroutineScope.showInfoMode() {
			infoBind.btnEdit.visibility = View.GONE
			infoBind.root.visibility = View.INVISIBLE
			model.get(adapterPosition).observeWhile(this) { res ->
				res?.let { phrase ->
					infoBind.root.setOnClickListener {
						selectedCall(phrase)
					}
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
			infoBind.imgPhrase.visibility = phrase.idImage?.let {
				infoBind.imgPhrase.setImageResource(R.drawable.noise)
				model.getImage(phrase) {
					infoBind.imgPhrase.setImageURI(it)
				}
				View.VISIBLE
			}.ifNull { View.GONE }
		}

		private fun LifecycleCoroutineScope.showPronounce(phrase: Phrase) {
			phrase.idPronounce?.let {
				infoBind.btnSound.setOnClickListener {
					play(phrase)
				}
				infoBind.btnSound.visibility = View.VISIBLE
			}.ifNull { infoBind.btnSound.visibility = View.GONE }
		}

		private fun LifecycleCoroutineScope.play(phrase: Phrase) = launch(Dispatchers.IO) {
			val audio = model.getAudio(phrase).first()
			player.setStatListener {
				when (it) {
					ObservableMediaPlayer.Status.PLAY -> infoBind.imgBtnSound.background.asAnimationDrawable().start()
					else -> {
						infoBind.imgBtnSound.background.asAnimationDrawable().stop()
						infoBind.imgBtnSound.background.asAnimationDrawable().selectDrawable(0)
					}
				}
			}
			player.play(itemView.context, audio)
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

	override fun onShow(parent: ViewGroup, view: LinearLayout, viewType: Int, scope: LifecycleCoroutineScope): PhraseHolder {
		return PhraseHolder(view, parent, scope)
	}

	override fun getItemCount() = model.size.value


}