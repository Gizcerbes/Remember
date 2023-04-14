package com.uogames.remembercards.ui.dialogs.choiceLanguageDialog

import android.view.LayoutInflater
import android.view.ViewGroup
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

    inner class LanguageHolder(val bind: CardLanguageBinding) : RecyclerView.ViewHolder(bind.root) {

        fun onShow() {
            clear()
            val item = list[adapterPosition]
            bind.txtLanguage.text = item.displayLanguage
            bind.root.setOnClickListener { call(item) }
        }

        private fun clear() {
            bind.txtLanguage.text = ""
        }

        fun onDestroy() {
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageHolder {
        return LanguageHolder(
            CardLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LanguageHolder, position: Int) = holder.onShow()

    override fun onViewRecycled(holder: LanguageHolder) {
        super.onViewRecycled(holder)
        holder.onDestroy()
    }

    fun onDestroy() {
        recyclerScope.cancel()
    }
}
