package com.uogames.remembercards.ui.cardFragment

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.findNavController
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.Phrase
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.ui.editCardFragment.EditCardFragment
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class CardAdapter(
	private val model: CardViewModel,
	private val player: ObservableMediaPlayer,
	scope: LifecycleCoroutineScope
) : ChangeableAdapter<CardAdapter.CardHolder>(scope) {

	init {
		model.size.observeWhenStarted(scope) {
			notifyDataSetChanged()
		}
	}

	inner class CardHolder(view: LinearLayout, viewGrope: ViewGroup, private val scope: LifecycleCoroutineScope) :
		ChangeableViewHolder(view, viewGrope, scope) {

		private val bind by lazy {
			CardCardBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
		}

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return bind.root
		}

		override fun LifecycleCoroutineScope.show(typeFragment: Int) {
			model.get(adapterPosition).observeWhenStarted(scope) {
				it?.let { card ->
					launch {
						bind.txtReason.text = card.reason
						model.getImage(card)?.imgUri?.toUri()?.let { uri ->
							bind.imgCard.visibility = View.VISIBLE
							bind.imgCard.setImageURI(uri)
						}.ifNull {
							bind.imgCard.visibility = View.GONE
						}
						bind.btnEditCard.setOnClickListener {
							(itemView.context as AppCompatActivity).findNavController(R.id.nav_host_fragment)
								.navigate(R.id.editCardFragment, Bundle().apply {
									putInt(EditCardFragment.EDIT_ID, card.id)
								})
						}
						model.getPhrase(card.idPhrase)?.let { phrase ->
							setData(phrase, bind.txtLangFirst, bind.txtPhraseFirst, bind.imgSoundFirst, bind.mcvFirst)
						}
						model.getPhrase(card.idTranslate)?.let { phrase ->
							setData(phrase, bind.txtLangSecond, bind.txtPhraseSecond, bind.imgSoundSecond, bind.mcvSecond)
						}
					}
				}
			}
		}

		private suspend fun setData(phrase: Phrase, langView: TextView, phraseView: TextView, soundImg: ImageView, button: MaterialCardView) {
			langView.text = showLang(phrase)
			phraseView.text = phrase.phrase
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

	override fun onShow(parent: ViewGroup, view: LinearLayout, viewType: Int, scope: LifecycleCoroutineScope): CardHolder {
		return CardHolder(view, parent, scope)
	}

	override fun getItemCount(): Int = model.size.value


}