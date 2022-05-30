package com.uogames.remembercards.ui.bookFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.uogames.dto.Card
import com.uogames.remembercards.databinding.CardEditBinding
import com.uogames.remembercards.databinding.CardInfoBinding
import com.uogames.remembercards.utils.ChangeableAdapter
import kotlinx.coroutines.*
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

		private val infoBind: CardInfoBinding by lazy {
			CardInfoBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
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
				EDIT_MODE -> showEditMode()
			}
		}

		private fun showInfoMode() {
			model.get(adapterPosition).onEach { card ->
				infoBind.btnEdit.setOnClickListener { changeType(EDIT_MODE) }
				infoBind.progress.visibility = View.VISIBLE
				infoBind.txtPhrase.text = card?.phrase
				infoBind.txtTranslate.text = card?.translate
				infoBind.progress.visibility = View.GONE
			}.launchIn(cardScope)
		}

		private fun showEditMode() {
			model.get(adapterPosition).onEach { card ->
				card?.let {
					editBind.editPhrase.editText?.setText(card.phrase)
					editBind.editTranslate.editText?.setText(card.translate)
					editBind.btnDelete.setOnClickListener {
						model.delete(card) {
							if (it) {
								changeType(INFO_MODE)
								notifyItemRemoved(adapterPosition)
								cardScope.cancel()
							}
						}
					}
					editBind.btnBack.setOnClickListener { changeType(INFO_MODE) }
					editBind.btnSave.setOnClickListener {
						val phrase = editBind.editPhrase.editText?.text.toString()
						val translate = editBind.editTranslate.editText?.text.toString()
						model.updateCard(Card(card.id, phrase, translate))
						changeType(INFO_MODE)
						notifyItemChanged(adapterPosition)
						cardScope.cancel()
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