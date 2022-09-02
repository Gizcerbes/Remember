package com.uogames.remembercards.ui.libraryFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.uogames.dto.local.Module
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardModuleBinding
import com.uogames.remembercards.utils.ClosableAdapter
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observeWhile
import kotlinx.coroutines.*

class LibraryAdapter(
    val model: LibraryViewModel,
    val selectID: (Module) -> Unit
) : ClosableAdapter<LibraryAdapter.ModuleHolder>() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)

    init {
        model.size.observeWhile(recyclerScope) {
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
                    bind.btnEdit.setOnClickListener { selectID(module) }

                    bind.btnAction.setOnClickListener {
                        full = !full
                        bind.llBar.visibility = if (full) View.VISIBLE else View.GONE
                        val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                        bind.imgAction.setImageResource(img)
                    }

                    val startAction: () -> Unit = {
                        bind.progressLoading.visibility = View.VISIBLE
                        bind.btnStop.visibility = View.VISIBLE
                        bind.btnShare.visibility = View.GONE
                        bind.btnEdit.visibility = View.GONE
                    }

                    val endAction: (String) -> Unit = {
                        bind.progressLoading.visibility = View.GONE
                        bind.btnStop.visibility = View.GONE
                        bind.btnShare.visibility = View.VISIBLE
                        bind.btnEdit.visibility = View.VISIBLE
                        Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
                    }

                    model.setShareAction(module, endAction).ifTrue(startAction)

                    bind.btnShare.setOnClickListener {
                        startAction()
                        model.share(module, endAction)
                    }

                    bind.btnStop.setOnClickListener {
                        model.stopSharing(module)
                    }
                }
            }
        }

        private fun clear() {
            full = false
            bind.llBar.visibility = View.GONE
            bind.progressLoading.visibility = View.GONE
            bind.btnDownload.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.imgAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
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

    override fun close() {
        recyclerScope.cancel()
    }
}
