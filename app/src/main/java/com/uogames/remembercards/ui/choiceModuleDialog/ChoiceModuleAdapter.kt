package com.uogames.remembercards.ui.choiceModuleDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.uogames.dto.local.LocalModule
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardModuleBinding
import com.uogames.remembercards.ui.module.library.LibraryViewModel
import com.uogames.remembercards.utils.ClosableAdapter
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observeWhile
import kotlinx.coroutines.*

class ChoiceModuleAdapter(
    val model: LibraryViewModel,
    val selectModule: (LocalModule) -> Unit
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private var size = 0

    init {
        model.size.observeWhile(recyclerScope) {
            size = it
            notifyDataSetChanged()
        }
    }

    inner class ModuleHolder(val bind: CardModuleBinding) : ClosableHolder(bind.root) {

        private var full = false

        private val startAction: () -> Unit = {
            bind.progressLoading.visibility = View.VISIBLE
            bind.btnStop.visibility = View.VISIBLE
            bind.btnShare.visibility = View.GONE
            bind.btnEdit.visibility = View.GONE
        }

        private val endAction: (String) -> Unit = {
            bind.progressLoading.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.btnShare.visibility = View.VISIBLE
            bind.btnEdit.visibility = View.VISIBLE
            Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
        }

        override fun show() {
            clear()
            observer = recyclerScope.launch {
                val mm = model.get(adapterPosition).ifNull { return@launch }
                val owner = mm.owner.await().userName

                bind.txtName.text = mm.module.name
                val count = mm.count.await().toString()
                bind.txtCountItems.text = itemView.context.getString(R.string.count_items).replace("||COUNT||", count)

                bind.txtOwner.text = owner

                bind.btnAction.setOnClickListener { selectModule(mm.module) }

                model.setShareAction(mm.module, endAction).ifTrue(startAction)

                bind.btnShare.setOnClickListener {
                    startAction()
                    model.share(mm.module, endAction)
                }

                bind.btnStop.setOnClickListener {
                    model.stopSharing(mm.module)
                }

            }
//            bind.btnAction.setOnClickListener {
//                full = !full
//                bind.llBar.visibility = if (full) View.VISIBLE else View.GONE
//                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
//                bind.imgAction.setImageResource(img)
//                if (!full) notifyItemChanged(adapterPosition)
//            }

        }

        private fun clear() {
            full = false
            bind.llBar.visibility = View.GONE
            bind.progressLoading.visibility = View.GONE
            bind.btnDownload.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.btnReport.visibility = View.GONE
            bind.btnShare.visibility = View.GONE
            bind.imgAction.setImageResource(R.drawable.ic_baseline_add_24)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleHolder {
        return ModuleHolder(
            CardModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
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
