package com.uogames.remembercards.ui.libraryFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.uogames.dto.local.Module
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardModuleBinding
import com.uogames.remembercards.utils.observeWhile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class LibraryAdapter(
	val model: LibraryViewModel,
	val selectID: (Module) -> Unit
):RecyclerView.Adapter<LibraryAdapter.ModuleHolder>() {


	private var list: List<Module> = listOf()
	private val recyclerScope = CoroutineScope(Dispatchers.Main)

	init {
		model.list.observeWhile(recyclerScope) {
			list = it
			notifyDataSetChanged()
		}
	}

	inner class ModuleHolder(view:View): RecyclerView.ViewHolder(view){

		private var moduleObserver: Job? = null

		private var _bind: CardModuleBinding? = null
		private val bind get() = _bind!!

		fun onShow(){
			_bind = CardModuleBinding.inflate(LayoutInflater.from(itemView.context), itemView as ViewGroup, false)
			val linearLayout = itemView as LinearLayout
			linearLayout.removeAllViews()
			linearLayout.addView(bind.root)
			bind.root.visibility = View.INVISIBLE
			val module = list[adapterPosition]
			bind.txtName.text = module.name
			moduleObserver = model.getCountByModuleID(module.id).observeWhile(recyclerScope) {
				bind.txtCountItems.text = itemView.context.getString(R.string.count_items).replace("||COUNT||", it.toString())
			}
			bind.txtCountItems.text = ""
			bind.txtLikes.text = "${(module.like / (module.like + module.dislike).toDouble() * 100).toInt()}%"
			bind.txtOwner.text = module.owner
			bind.root.setOnClickListener {
				selectID(module)
			}
			bind.root.visibility = View.VISIBLE
		}

		fun onDestroy(){
			moduleObserver?.cancel()
			_bind = null
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleHolder {
		return ModuleHolder(LinearLayout(parent.context).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT
			)
			orientation = LinearLayout.VERTICAL
		})
	}

	override fun onBindViewHolder(holder: ModuleHolder, position: Int) {
		holder.onShow()
		(holder.itemView as LinearLayout).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			)
		}
	}

	override fun getItemCount(): Int {
		return list.size
	}

	override fun onViewRecycled(holder: ModuleHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	fun onDestroy(){
		recyclerScope.cancel()
	}


}