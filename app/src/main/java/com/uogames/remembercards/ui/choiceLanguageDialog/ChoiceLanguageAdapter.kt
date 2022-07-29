package com.uogames.remembercards.ui.choiceLanguageDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.uogames.remembercards.databinding.CardLanguageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import java.util.*

class ChoiceLanguageAdapter(
	private val call: (Locale) -> Unit
) : RecyclerView.Adapter<ChoiceLanguageAdapter.LanguageHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)
	private var list: List<Locale> = listOf()

	fun setItemList(list: List<Locale>) {
		this.list = list
		notifyDataSetChanged()
	}


	inner class LanguageHolder(view: View) : RecyclerView.ViewHolder(view) {

		private var _bind: CardLanguageBinding? = null
		private val bind get() =  _bind!!

		fun onShow() {
			_bind = CardLanguageBinding.inflate(LayoutInflater.from(itemView.context), itemView as ViewGroup, false)
			val linearLayout = itemView as LinearLayout
			linearLayout.removeAllViews()
			linearLayout.addView(bind.root)
			val item = list[adapterPosition]
			bind.txtLanguage.text = item.displayLanguage
			bind.root.setOnClickListener { call(item) }
		}

		fun onDestroy() {
			_bind = null
		}

	}

	override fun getItemCount(): Int {
		return list.size
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageHolder {
		return LanguageHolder(LinearLayout(parent.context).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT
			)
			orientation = LinearLayout.VERTICAL
		})
	}

	override fun onBindViewHolder(holder: LanguageHolder, position: Int) {
		holder.onShow()
		(holder.itemView as LinearLayout).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			)
		}
	}

	override fun onViewRecycled(holder: LanguageHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	fun onDestroy() {
		recyclerScope.cancel()
	}


}