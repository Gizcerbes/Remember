package com.uogames.remembercards.ui.libraryFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.dto.local.LocalModule
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardModuleBinding
import com.uogames.remembercards.utils.ClosableAdapter
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observe
import kotlinx.coroutines.*

class LibraryAdapter(
    private val model: LibraryViewModel,
    private val selectCall: (LocalModule) -> Unit
): ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private var size = 0
    private val auth = Firebase.auth

    init {
        model.size.observe(recyclerScope) {
            size = it
            notifyDataSetChanged()
        }
    }

    inner class LocalModuleHolder(val bind: CardModuleBinding) : ClosableHolder(bind.root) {

        private var full = false

        override fun show() {
            clear()
            observer = recyclerScope.launch(Dispatchers.IO) {
                val module = model.get(adapterPosition).ifNull { return@launch }
                if (auth.currentUser == null || (module.globalOwner != null && module.globalOwner != auth.currentUser?.uid)) {
                    launch(Dispatchers.Main) { bind.btnShare.visibility = View.GONE }
                }
                launch(Dispatchers.Main) {
                    bind.txtName.text = module.name
                    launch(Dispatchers.IO) {
                        val count = model.getCountByModule(module)
                        launch(Dispatchers.Main) {
                            bind.txtCountItems.text = itemView.context.getString(R.string.count_items).replace("||COUNT||", count.toString())
                        }
                    }
                    bind.txtCountItems.text = ""
                    bind.txtOwner.text = module.owner
                    bind.btnEdit.setOnClickListener { selectCall(module) }

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
            bind.btnAction.setOnClickListener {
                full = !full
                bind.llBar.visibility = if (full) View.VISIBLE else View.GONE
                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                bind.imgAction.setImageResource(img)
                if (!full) notifyItemChanged(adapterPosition)
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
    }

    override fun getItemViewType(position: Int): Int {
        return if (model.cloud.value) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosableHolder {
        val bind = CardModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when(viewType){
            1 -> LocalModuleHolder(bind)
            else -> LocalModuleHolder(bind)
        }
    }

    override fun onBindViewHolder(holder: ClosableHolder, position: Int) = holder.show()

    override fun getItemCount() = size

    override fun close() {
        recyclerScope.cancel()
    }
}