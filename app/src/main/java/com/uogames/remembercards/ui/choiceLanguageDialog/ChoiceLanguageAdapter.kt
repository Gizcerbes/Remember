package com.uogames.remembercards.ui.choiceLanguageDialog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.uogames.flags.Languages
import com.uogames.remembercards.databinding.CardLanguageBinding
import com.uogames.remembercards.utils.ChangeableAdapter
import kotlinx.coroutines.flow.MutableStateFlow

class ChoiceLanguageAdapter(val call: (Languages) -> Unit) : ChangeableAdapter<ChoiceLanguageAdapter.LanguageHolder>() {

	private var list: Array<Languages> = Languages.values()

	inner class LanguageHolder(view: LinearLayout, viewGrope: ViewGroup, dataChange: MutableStateFlow<Int>) :
		ChangeableAdapter.ChangeableViewHolder(view, viewGrope, dataChange) {

		private val bind by lazy { CardLanguageBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false) }

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return when (typeFragment) {
				0 -> bind.root
				else -> null
			}
		}

		override fun show(typeFragment: Int) {
			bind.txtLanguage.text = list[adapterPosition].language
			bind.root.setOnClickListener {
				call(list[adapterPosition])
			}
		}

	}

	override fun onShow(parent: ViewGroup, view: LinearLayout, viewType: Int, changeListener: MutableStateFlow<Int>): LanguageHolder {
		return LanguageHolder(view, parent, changeListener)
	}

	override fun getItemCount(): Int {
		return list.size
	}

	fun setMask(string: String) {
		list = Languages.values().filter {
			it.language.uppercase().contains(string.uppercase())
		}.toTypedArray()
		notifyDataSetChanged()
	}

}