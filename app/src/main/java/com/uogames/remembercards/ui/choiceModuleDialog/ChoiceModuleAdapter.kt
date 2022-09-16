package com.uogames.remembercards.ui.choiceModuleDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uogames.dto.local.Module
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardModuleBinding
import com.uogames.remembercards.ui.libraryFragment.LibraryViewModel
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observeWhile
import kotlinx.coroutines.*

class ChoiceModuleAdapter(
    val model: LibraryViewModel,
    val selectModule: (Module) -> Unit
) : RecyclerView.Adapter<ChoiceModuleAdapter.ModuleHolder>() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private var size = 0

    init {
        model.size.observeWhile(recyclerScope) {
            size = it
            notifyDataSetChanged()
        }
    }

    inner class ModuleHolder(val bind: CardModuleBinding) : RecyclerView.ViewHolder(bind.root) {

        private var moduleObserver: Job? = null

        private var full = false

        fun onShow() {
            clear()
            recyclerScope.launch(Dispatchers.IO) {
                val module = model.getModuleByPosition(adapterPosition).ifNull { return@launch }
                launch(Dispatchers.Main) {
                    bind.txtName.text = module.name
                    moduleObserver = recyclerScope.launch(Dispatchers.IO) {
                        val count = model.getCountByModule(module)
                        launch(Dispatchers.Main) {
                            bind.txtCountItems.text = itemView.context.getString(R.string.count_items).replace("||COUNT||", count.toString())
                        }
                    }
                    bind.txtCountItems.text = ""
                    bind.txtOwner.text = module.owner
                    bind.btnAction.setOnClickListener { selectModule(module) }
                }
            }
        }

        private fun clear() {
            full = false
            bind.llBar.visibility = View.GONE
            bind.imgAction.setImageResource(R.drawable.ic_baseline_add_24)
        }

        fun onDestroy() {
            moduleObserver?.cancel()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleHolder {
        return ModuleHolder(
            CardModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ModuleHolder, position: Int) {
        holder.onShow()
    }

    override fun getItemCount(): Int {
        return model.size.value
    }

    override fun onViewRecycled(holder: ModuleHolder) {
        super.onViewRecycled(holder)
        holder.onDestroy()
    }

    fun onDestroy() {
        recyclerScope.cancel()
    }
}
