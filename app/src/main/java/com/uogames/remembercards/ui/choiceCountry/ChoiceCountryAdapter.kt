package com.uogames.remembercards.ui.choiceCountry

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uogames.flags.Countries
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardCountryBinding

class ChoiceCountryAdapter(
    val call: (Countries) -> Unit
) : RecyclerView.Adapter<ChoiceCountryAdapter.CountryHolder>() {

    private var list: List<Countries> = arrayListOf()

    fun setData(list: List<Countries>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class CountryHolder(val bind: CardCountryBinding) : RecyclerView.ViewHolder(bind.root) {

        @SuppressLint("ResourceType")
        fun show() {
            val country = list[adapterPosition]
            bind.imgFlag.setImageResource(country.res)
            bind.llLanguages.removeAllViews()
            country.country.forEach {
                val tv = TextView(itemView.context)
                tv.setTextAppearance(R.attr.textAppearanceBody2)
                tv.text = it.value
                bind.llLanguages.addView(tv)
            }
            bind.root.setOnClickListener { call(country) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryHolder {
        val bind = CardCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryHolder(bind)
    }

    override fun onBindViewHolder(holder: CountryHolder, position: Int) {
        holder.show()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
