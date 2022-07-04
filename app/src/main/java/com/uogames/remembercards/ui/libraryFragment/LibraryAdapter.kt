package com.uogames.remembercards.ui.libraryFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleCoroutineScope
import com.uogames.dto.Module
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardModuleBinding
import com.uogames.remembercards.utils.ChangeableAdapter
import com.uogames.remembercards.utils.observeWhenStarted
import kotlinx.coroutines.launch

class LibraryAdapter(scope: LifecycleCoroutineScope, val model: LibraryViewModel, val selectID: (Int) -> Unit) :
	ChangeableAdapter<LibraryAdapter.ModuleHolder>(scope) {

	private var list: List<Module> = listOf()

	init {
		model.list.observeWhenStarted(scope) {
			list = it
			notifyDataSetChanged()
		}
	}


	inner class ModuleHolder(view: LinearLayout, viewGrope: ViewGroup, scope: LifecycleCoroutineScope) :
		ChangeableAdapter.ChangeableViewHolder(view, viewGrope, scope) {

		private val bind by lazy { CardModuleBinding.inflate(LayoutInflater.from(viewGrope.context), viewGrope, false) }

		override fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View? {
			return bind.root
		}

		override fun LifecycleCoroutineScope.show(typeFragment: Int) {
			bind.root.visibility = View.INVISIBLE
			launch {
				val module = list[adapterPosition]
				bind.txtName.text = module.name
				model.getCountByModuleID(module.id).observeWhenStarted(this@show) {
					bind.txtCountItems.text = itemView.context.getString(R.string.count_items).replace("||COUNT||", it.toString())
				}
				bind.txtCountItems.text = ""
				bind.txtLikes.text = "${(module.like / (module.like + module.dislike).toDouble() * 100).toInt()}%"
				bind.txtOwner.text = module.owner
				bind.root.setOnClickListener {
					selectID(module.id)
				}
				bind.root.visibility = View.VISIBLE
			}
		}
	}

	override fun onShow(parent: ViewGroup, view: LinearLayout, viewType: Int, scope: LifecycleCoroutineScope): ModuleHolder {
		return ModuleHolder(view, parent, scope)
	}

	override fun getItemCount(): Int {
		return list.size
	}


}