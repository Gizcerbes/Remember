package com.uogames.remembercards.ui.editModuleFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.ModuleCard
import com.uogames.dto.Phrase
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.utils.*
import com.uogames.repository.DataProvider.Companion.toImage
import com.uogames.repository.DataProvider.Companion.toPhrase
import com.uogames.repository.DataProvider.Companion.toPronounce
import com.uogames.repository.DataProvider.Companion.toTranslate
import kotlinx.coroutines.CoroutineScope
import java.util.*

class EditModuleAdapter(
	private val model: EditModuleViewModel,
	private val player: ObservableMediaPlayer,
	scope: LifecycleCoroutineScope
) : ChangeableAdapter<EditModuleAdapter.CardsHolder>(scope) {

	private var listItems: List<ModuleCard> = arrayListOf()

	fun setListItems(list: List<ModuleCard>) {
		listItems = list
		notifyDataSetChanged()
	}

	inner class CardsHolder(view: LinearLayout, viewGroup: ViewGroup, val scope: LifecycleCoroutineScope) :
		ChangeableAdapter.ChangeableViewHolder(view, viewGroup, scope) {

		private val bind by lazy { CardCardBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false) }

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return bind.root
		}

		override suspend fun CoroutineScope.show(typeFragment: Int, end: () -> Unit) {
			val moduleCard = listItems[adapterPosition]
			bind.root.visibility = View.INVISIBLE
			model.getCard(moduleCard).observeWhenStarted(scope) {
				it?.let { card ->
					bind.txtReason.text = card.reason
					bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_remove_24)
					bind.btnCardAction.setOnClickListener {
						model.removeModuleCard(moduleCard) {}
					}
					card.toPhrase()?.let { phrase ->
						setData(phrase, bind.txtLangFirst, bind.txtPhraseFirst, bind.imgSoundFirst, bind.mcvFirst, bind.imgCardFirst)
					}
					card.toTranslate()?.let { phrase ->
						setData(phrase, bind.txtLangSecond, bind.txtPhraseSecond, bind.imgSoundSecond, bind.mcvSecond, bind.imgCardSecond)
					}
					bind.root.visibility = View.VISIBLE
				}
			}
		}

		private suspend fun setData(
			phrase: Phrase,
			langView: TextView,
			phraseView: TextView,
			soundImg: ImageView,
			button: MaterialCardView,
			phraseImage: ImageView
		) {
			langView.text = showLang(phrase)
			phraseView.text = phrase.phrase
			phrase.toImage()?.let {
				phraseImage.visibility = View.VISIBLE
				phraseImage.setImageURI(it.imgUri.toUri())
			}.ifNull {
				phraseImage.visibility = View.GONE
			}

			phrase.toPronounce()?.let { audio ->
				soundImg.visibility = View.VISIBLE
				button.setOnClickListener {
					player.play(itemView.context, audio.audioUri.toUri(), soundImg.background.asAnimationDrawable())
				}
			}.ifNull {
				soundImg.visibility = View.GONE
			}
		}

		private fun showLang(phrase: Phrase): String {
			return safely {
				val data = phrase.lang.split("-")
				Locale(data[0]).displayLanguage
			}.orEmpty()
		}

	}

	override fun onShow(parent: ViewGroup, view: LinearLayout, viewType: Int, scope: LifecycleCoroutineScope): CardsHolder {
		return CardsHolder(view, parent, scope)
	}

	override fun getItemCount(): Int {
		return listItems.size
	}


}