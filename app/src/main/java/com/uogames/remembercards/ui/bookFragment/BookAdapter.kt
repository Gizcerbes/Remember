package com.uogames.remembercards.ui.bookFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.uogames.dto.Card
import com.uogames.remembercards.BuildConfig
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardEditBinding
import com.uogames.remembercards.databinding.CardInfoBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.log


class BookAdapter(
	private val model: BookViewModel
) : RecyclerView.Adapter<BookAdapter.CardViewHolder>() {
	companion object {
		private const val INFO_FRAGMENT = "INFO_FRAGMENT"
		private const val EDIT_FRAGMENT = "EDIT_FRAGMENT"
		private val setLay: (LinearLayout) -> Unit = {
			val param = it.layoutParams
			param.height = ViewGroup.LayoutParams.WRAP_CONTENT
			it.layoutParams = param
		}
	}

	private val recyclerScope = CoroutineScope(Dispatchers.Main)
	private val stopAll = MutableStateFlow(false)
	private val changeAll = MutableStateFlow(0)

	init {
		model.size {
			notifyDataSetChanged()
			if (BuildConfig.DEBUG) {
				if (it == 0) {
					CoroutineScope(Dispatchers.IO).launch {
						for (i in 0..100) {
							model.add(Card(phrase = i.toString(), translate = i.toString())) {
								Log.e("TAG", ": $it")
							}
							delay(100)
						}
					}
				}
			}
		}
		model.size.onEach {
			changeAll.value++
		}.launchIn(recyclerScope)
	}

	inner class CardViewHolder(view: View, private val viewGrope: ViewGroup) :
		RecyclerView.ViewHolder(view) {

		private val holderScope = CoroutineScope(Dispatchers.Main)
		private var cardScope = CoroutineScope(Dispatchers.Main)

		private val infoBind: CardInfoBinding by lazy {
			CardInfoBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
		}
		private val editBind: CardEditBinding by lazy {
			CardEditBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
		}

		private val typeFragment = MutableStateFlow(INFO_FRAGMENT)

		init {
			typeFragment.onEach { draw(it) }.launchIn(holderScope)
			changeAll.onEach { changePosition() }.launchIn(holderScope)
			stopAll.onEach {
				if (it) {
					cardScope.cancel()
				}
			}.launchIn(holderScope)
		}


		fun changePosition() {
			cardScope.cancel()
			cardScope = CoroutineScope(Dispatchers.Main)
			typeFragment.value = INFO_FRAGMENT
			if (adapterPosition != -1) show()
		}


		private fun draw(type: String) {
			val lay = itemView as LinearLayout
			lay.removeAllViews()
			when (type) {
				INFO_FRAGMENT -> lay.addView(infoBind.root)
				EDIT_FRAGMENT -> lay.addView(editBind.root)
			}
			setLay(lay)
			show()
		}

		private fun show() {
			when (typeFragment.value) {
				INFO_FRAGMENT -> showInfoMode()
				EDIT_FRAGMENT -> showEditMode()
			}
		}

		private fun showInfoMode() {
			model.get(adapterPosition).onEach { card ->
				infoBind.btnEdit.setOnClickListener {
					typeFragment.value = EDIT_FRAGMENT
				}
				infoBind.progress.visibility = View.VISIBLE
				infoBind.txtPhrase.text = card.phrase
				infoBind.txtTranslate.text = card.translate
				infoBind.progress.visibility = View.GONE
			}.launchIn(cardScope)

			model.updateBlocking.onEach {
				if (!it) model.refresh(adapterPosition)
			}.launchIn(cardScope)
		}

		private fun showEditMode() {
			Log.e("TAG", "showEditMode: $adapterPosition")
			val card = model.get(adapterPosition).value
			editBind.editPhrase.editText?.setText(card.phrase)
			editBind.editTranslate.editText?.setText(card.translate)
			editBind.btnDelete.setOnClickListener {
				model.delete(card) {
					if (it) {
						model.refreshAll()
						typeFragment.value = INFO_FRAGMENT
						notifyItemRemoved(adapterPosition)
						cardScope.cancel()
						changeAll.value++
					}
				}
			}
			editBind.btnBack.setOnClickListener { typeFragment.value = INFO_FRAGMENT }
			editBind.btnSave.setOnClickListener {
				val phrase = editBind.editPhrase.editText?.text.toString()
				val translate = editBind.editTranslate.editText?.text.toString()
				model.updateCard(Card(card.id, phrase, translate))
				typeFragment.value = INFO_FRAGMENT
				notifyItemChanged(adapterPosition)
				cardScope.cancel()
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
		return CardViewHolder(
			LinearLayout(parent.context).apply {
				layoutParams = LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT
				)
				orientation = LinearLayout.VERTICAL
			}, parent
		)
	}

	override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
		holder.changePosition()
	}

	override fun getItemCount() = model.size.value


	fun stop() {
		model.reset()
		stopAll.value = true
		recyclerScope.cancel()
	}

}