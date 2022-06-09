package com.uogames.remembercards.ui.bookFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.uogames.flags.Countries
import com.uogames.remembercards.databinding.CardEditBinding
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.utils.ChangeableAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BookAdapter(
	private val model: BookViewModel
) : ChangeableAdapter<BookAdapter.CardHolder>() {

	companion object {
		private const val INFO_MODE = 0
		private const val EDIT_MODE = 1
	}

	inner class CardHolder(
		view: LinearLayout,
		private val viewGrope: ViewGroup,
		dataChange: MutableStateFlow<Int>
	) : ChangeableViewHolder(view, viewGrope, dataChange) {


		private val infoBind: CardPhraseBinding by lazy {
			CardPhraseBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
		}

		private val editBind: CardEditBinding by lazy {
			CardEditBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
		}

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return when (typeFragment) {
				0 -> infoBind.root
				1 -> editBind.root
				else -> null
			}
		}

		override fun show(typeFragment: Int) {
			when (typeFragment) {
				INFO_MODE -> showInfoMode()
//				EDIT_MODE -> showEditMode()
			}
		}

		private fun showInfoMode() {
			model.get(adapterPosition).onEach { res ->
				res?.let { phrase ->
					infoBind.txtPhrase.text = phrase.phrase
					phrase.idImage?.let {
						infoBind.imgPhrase.visibility = View.VISIBLE

					} ?: run { infoBind.imgPhrase.visibility = View.GONE }

					phrase.idPronounce?.let {
						infoBind.btnSound.visibility = View.GONE
						infoBind.btnSound.setOnClickListener {

						}
					} ?: run { infoBind.btnSound.visibility = View.GONE }

					phrase.lang?.let {

					}

					phrase.definition?.let {
						infoBind.txtDefinition.text = it
					}

					infoBind.btnEdit.setOnClickListener {

					}

				}
			}.launchIn(cardScope)
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