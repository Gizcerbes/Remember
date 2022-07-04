package com.uogames.remembercards.ui.bookFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.findNavController
import com.uogames.dto.Phrase
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class BookAdapter(
	private val model: BookViewModel,
	private val player: ObservableMediaPlayer,
	lifecycleScope: LifecycleCoroutineScope
) : ChangeableAdapter<BookAdapter.CardHolder>(lifecycleScope) {

	companion object {
		private const val INFO_MODE = 0
	}

	private val recyclerScope = CoroutineScope(Dispatchers.Main)

	init {
		CoroutineScope(Dispatchers.IO).launch {

		}
		model.size.onEach {
			notifyDataSetChanged()
		}.launchIn(recyclerScope)
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

		override fun LifecycleCoroutineScope.show(typeFragment: Int) {
			when (typeFragment) {
				INFO_MODE -> this.showInfoMode()
			}
		}

		private fun LifecycleCoroutineScope.showInfoMode() {
			infoBind.root.visibility = View.INVISIBLE
			val position = adapterPosition
			model.get(position).observeWhenStarted(this) { res ->
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
					infoBind.root.visibility = View.VISIBLE
				}
			}
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

		private fun LifecycleCoroutineScope.showPronounce(phrase: Phrase) {
			phrase.idPronounce?.let {
				infoBind.btnSound.visibility = View.VISIBLE
				infoBind.btnSound.setOnClickListener {
					play(phrase)
				}
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
			//player.stop()
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