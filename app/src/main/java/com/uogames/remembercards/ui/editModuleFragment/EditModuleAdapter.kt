package com.uogames.remembercards.ui.editModuleFragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.findNavController
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.ModuleCard
import com.uogames.dto.Phrase
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.ui.editCardFragment.EditCardFragment
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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

	inner class CardsHolder(view: LinearLayout, viewGroup: ViewGroup,val scope: LifecycleCoroutineScope) :
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
					launch {
						bind.txtReason.text = card.reason
						bind.imgBtnAction.setImageResource(R.drawable.ic_baseline_remove_24)
						bind.btnCardAction.setOnClickListener {
							model.removeModuleCard(moduleCard){}
						}
						model.getPhrase(card.idPhrase)?.let { phrase ->
							setData(phrase, bind.txtLangFirst, bind.txtPhraseFirst, bind.imgSoundFirst, bind.mcvFirst, bind.imgCardFirst)
						}
						model.getPhrase(card.idTranslate)?.let { phrase ->
							setData(phrase, bind.txtLangSecond, bind.txtPhraseSecond, bind.imgSoundSecond, bind.mcvSecond, bind.imgCardSecond)
						}
						end()
						bind.root.visibility = View.VISIBLE
					}
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
			model.getImage(phrase)?.let {
				phraseImage.visibility = View.VISIBLE
				phraseImage.setImageURI(it.imgUri.toUri())
			}.ifNull {
				phraseImage.visibility = View.GONE
			}
			phrase.idPronounce?.let {
				soundImg.visibility = View.VISIBLE
				model.getPronounce(phrase)?.let { audio ->
					button.setOnClickListener {
						play(soundImg, audio.audioUri.toUri())
					}
				}
			}.ifNull {
				soundImg.visibility = View.GONE
			}
		}

		private fun play(soundImg: ImageView, audioUri: Uri) {
			player.setStatListener {
				when (it) {
					ObservableMediaPlayer.Status.PLAY -> soundImg.background.asAnimationDrawable().start()
					else -> {
						soundImg.background.asAnimationDrawable().stop()
						soundImg.background.asAnimationDrawable().selectDrawable(0)
					}
				}
			}
			player.play(itemView.context, audioUri)
		}

		private fun showLang(phrase: Phrase): String {
			phrase.lang?.let {
				val data = it.split("-")
				if (data.isNotEmpty()) try {
					return Locale(data[0]).displayLanguage
				} catch (e: Exception) {
				}
			}
			return ""
		}
	}

	override fun onShow(parent: ViewGroup, view: LinearLayout, viewType: Int, scope: LifecycleCoroutineScope): CardsHolder {
		return CardsHolder(view, parent, scope)
	}

	override fun getItemCount(): Int {
		return listItems.size
	}


}