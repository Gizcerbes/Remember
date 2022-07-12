package com.uogames.remembercards.ui.choiceLanguageDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleCoroutineScope
import com.uogames.flags.Languages
import com.uogames.remembercards.databinding.CardLanguageBinding
import com.uogames.remembercards.utils.ChangeableAdapter
import kotlinx.coroutines.CoroutineScope
import java.util.*

class ChoiceLanguageAdapter(scope: LifecycleCoroutineScope, val callIso: (String) -> Unit) :
	ChangeableAdapter<ChoiceLanguageAdapter.LanguageHolder>(scope) {

	private var list: Array<String> = Locale.getISOLanguages()

	inner class LanguageHolder(view: LinearLayout, viewGrope: ViewGroup, scope: LifecycleCoroutineScope) :
		ChangeableAdapter.ChangeableViewHolder(view, viewGrope, scope) {

		private val bind by lazy { CardLanguageBinding.inflate(LayoutInflater.from(itemView.context), viewGrope, false) }

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return when (typeFragment) {
				0 -> bind.root
				else -> null
			}
		}

		override suspend fun CoroutineScope.show(typeFragment: Int, end: () -> Unit) {
//			bind.txtLanguage.text = list[adapterPosition].language
//			bind.root.setOnClickListener {
//				call(list[adapterPosition])
//			}
		}

	}

	override fun onShow(parent: ViewGroup, view: LinearLayout, viewType: Int, scope: LifecycleCoroutineScope): LanguageHolder {
		return LanguageHolder(view, parent, scope)
	}

	override fun getItemCount(): Int {
		return list.size
	}

//	fun setMask(string: String) {
//		list = Languages.values().filter {
//			it.language.uppercase().contains(string.uppercase())
//		}.toTypedArray()
//		notifyDataSetChanged()
//	}

}