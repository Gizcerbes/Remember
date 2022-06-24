package com.uogames.remembercards.ui.cardFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.uogames.remembercards.databinding.CardCardBinding
import com.uogames.remembercards.utils.ChangeableAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow

class CardAdapter(private val model: CardViewModel) : ChangeableAdapter<CardAdapter.CardHolder>() {

	inner class CardHolder(view: LinearLayout, viewGrope: ViewGroup) :
		ChangeableViewHolder(view, viewGrope) {

		private val bind by lazy {
			CardCardBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false)
		}

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return bind.root
		}

		override fun CoroutineScope.show(typeFragment: Int) {

		}

	}

	override fun onShow(parent: ViewGroup, view: LinearLayout, viewType: Int): CardHolder {
		return CardHolder(view, parent)
	}

	override fun getItemCount(): Int = model.size.value

}