package com.uogames.remembercards.ui.libraryFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uogames.flags.Countries
import com.uogames.remembercards.databinding.CardPhraseBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Adapter : RecyclerView.Adapter<Adapter.Holder>() {


	inner class Holder(private val bind: CardPhraseBinding) : RecyclerView.ViewHolder(bind.root) {

		fun show() {
			CoroutineScope(Dispatchers.Main).launch {


				val country = Countries.values()[adapterPosition]

				bind.imgPhrase.setImageResource(country.res)
				bind.imgCountry.setImageResource(country.res)
				bind.txtPhrase.text = country.country[0].value

			}
		}

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
		val bind = CardPhraseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return Holder(bind)
	}

	override fun onBindViewHolder(holder: Holder, position: Int) {
		holder.show()
	}

	override fun getItemCount(): Int {
		return Countries.values().size
	}


}