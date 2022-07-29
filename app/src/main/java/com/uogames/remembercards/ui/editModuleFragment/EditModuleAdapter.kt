package com.uogames.remembercards.ui.editModuleFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.util.*

class EditModuleAdapter(
	private val model: EditModuleViewModel,
	private val player: ObservableMediaPlayer
) : RecyclerView.Adapter<EditModuleAdapter.CardHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)

	private var listItems: List<ModuleCard> = arrayListOf()

	fun setListItems(list: List<ModuleCard>) {
		listItems = list
		notifyDataSetChanged()
	}

	inner class CardHolder(view: View) : RecyclerView.ViewHolder(view) {

		private var _bind: CardCardBinding? = null
		private val bind get() = _bind!!

		private var cardObserver: Job? = null

		fun onShow() {
			_bind = CardCardBinding.inflate(LayoutInflater.from(itemView.context), itemView as ViewGroup, false)
			val linearLayout = itemView as LinearLayout
			linearLayout.removeAllViews()
			linearLayout.addView(bind.root)
			bind.root.visibility = View.INVISIBLE
			val moduleCard = listItems[adapterPosition]
			model.getCard(moduleCard).observeWhile(recyclerScope) {
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
				Picasso.get().load(it.imgUri.toUri()).placeholder(R.drawable.noise).into(phraseImage)
				phraseImage.visibility = View.VISIBLE
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

		private fun showLang(phrase: Phrase) = safely {
			val data = phrase.lang.split("-")
			Locale(data[0]).displayLanguage
		}.orEmpty()

		fun onDestroy(){
			cardObserver?.cancel()
			_bind = null
		}

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
		return  CardHolder(LinearLayout(parent.context).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT
			)
			orientation = LinearLayout.VERTICAL
		})
	}

	override fun onBindViewHolder(holder: CardHolder, position: Int) {
		holder.onShow()
		(holder.itemView as LinearLayout).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			)
		}
	}

	override fun getItemCount(): Int {
		return listItems.size
	}

	override fun onViewRecycled(holder: CardHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	fun onDestroy(){
		recyclerScope.cancel()
	}


}