package com.uogames.remembercards.ui.module.choiceModuleDialog

import android.view.ViewGroup
import android.widget.LinearLayout
import com.uogames.remembercards.R
import com.uogames.remembercards.ui.views.CardModuleView
import com.uogames.remembercards.utils.ClosableAdapter
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifNullOrEmpty
import com.uogames.remembercards.utils.observe
import kotlinx.coroutines.*

class ChoiceModuleAdapter(
    val model: ChoiceModuleViewModel
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private var size = 0

    init {
        model.size.observe(recyclerScope) {
            size = it
            notifyDataSetChanged()
        }
    }

    inner class ModuleHolderAll(val view: CardModuleView) : ClosableHolder(view) {
        override fun show() {
            view.reset()
            observer = recyclerScope.launch {
                view.name = itemView.context.getString(R.string.all_cards)
                view.count = model.getCountOfAllCardsAsync().await()
                view.owner = model.getUserName()
                view.setOnClick { model.selectModule(ChoiceModuleViewModel.ChoiceAll()) }
            }
        }
    }

    inner class ModuleHolderLocal(val view: CardModuleView) : ClosableHolder(view) {
        override fun show() {
            view.reset()
            observer = recyclerScope.launch {
                val mm = model.getLocalModel(adapterPosition).ifNull { return@launch }
                val owner = mm.owner.await().userName.ifNullOrEmpty {
                    model.getUserName()
                }

                view.name = mm.module.name
                view.count = mm.count.await()
                view.owner = owner

                view.setOnClick { model.selectModule(ChoiceModuleViewModel.ChoiceLocalModule(mm.module)) }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (model.getType(position)) {
            ChoiceModuleViewModel.ChoiceStat.ALL -> 0
            ChoiceModuleViewModel.ChoiceStat.ID -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosableHolder {
        return when (viewType) {
            0 -> ModuleHolderAll(CardModuleView(parent.context))
            1 -> ModuleHolderLocal(CardModuleView(parent.context))
            else -> ModuleHolderLocal(CardModuleView(parent.context))
        }
    }

    override fun onBindViewHolder(holder: ClosableHolder, position: Int) {
        holder.show()
    }

    override fun getItemCount(): Int {
        return model.size.value
    }

    override fun close() {
        recyclerScope.cancel()
    }

    fun onDestroy() {
        recyclerScope.cancel()
    }
}
