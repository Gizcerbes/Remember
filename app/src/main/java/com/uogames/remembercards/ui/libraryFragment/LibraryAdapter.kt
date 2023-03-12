package com.uogames.remembercards.ui.libraryFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.dto.global.GlobalModule
import com.uogames.dto.local.LocalModule
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardModuleBinding
import com.uogames.remembercards.databinding.DialogShareAttentionBinding
import com.uogames.remembercards.utils.ClosableAdapter
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifTrue
import com.uogames.remembercards.utils.observe
import kotlinx.coroutines.*

class LibraryAdapter(
    private val model: LibraryViewModel,
    private val reportCall: ((GlobalModule) -> Unit)? = null,
    private val selectCall: (LocalModule) -> Unit
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)
    private var size = 0
    private val auth = Firebase.auth

    init {
        model.size.observe(recyclerScope) {
            size = it
            notifyDataSetChanged()
        }
    }

    inner class LocalModuleHolder(private val bind: CardModuleBinding) : ClosableHolder(bind.root) {

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
                val owner = mm.module.globalOwner
                val user = auth.currentUser
                val uid = user?.uid
                if (user == null || (owner != null && owner != uid)) bind.btnShare.visibility = View.GONE

                bind.txtName.text = mm.module.name
                val count = mm.count.await().toString()
                bind.txtCountItems.text = itemView.context.getString(R.string.count_items).replace("||COUNT||", count)

                bind.txtOwner.text = mm.owner.await().userName

                bind.btnEdit.setOnClickListener { selectCall(mm.module) }

                model.setShareAction(mm.module, endAction).ifTrue(startAction)

                bind.btnShare.setOnClickListener {
                    startAction()
                    model.share(mm.module, endAction)
                }

                bind.btnShare.setOnClickListener {
                    model.shareNotice.value?.let {
                        startAction()
                        model.share(mm.module, endAction)
                    }.ifNull {
                        val viewBin = DialogShareAttentionBinding.inflate(LayoutInflater.from(itemView.context))
                        MaterialAlertDialogBuilder(itemView.context)
                            .setView(viewBin.root)
                            .setPositiveButton("Apply") { _, _ ->
                                startAction()
                                model.share(mm.module, endAction)
                                if (viewBin.cbDnshow.isChecked) model.showShareNotice(false)
                            }.setNegativeButton("Cancel") { _, _ ->
                            }.show()
                    }
                }

                bind.btnStop.setOnClickListener {
                    model.stopSharing(mm.module)
                }

            }
            bind.btnAction.setOnClickListener {
                full = !full
                bind.llBar.visibility = if (full) View.VISIBLE else View.GONE
                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                bind.imgAction.setImageResource(img)
            }

        }

        private fun clear() {
            full = false
            bind.llBar.visibility = View.GONE
            bind.progressLoading.visibility = View.GONE
            bind.btnDownload.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.btnReport.visibility = View.GONE
            bind.imgAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        }
    }

    inner class GlobalModuleHolder(private val bind: CardModuleBinding) : ClosableHolder(bind.root) {

        private var full = false

        private val startAction: () -> Unit = {
            bind.progressLoading.visibility = View.VISIBLE
            bind.btnStop.visibility = View.VISIBLE
            bind.btnDownload.visibility = View.GONE
        }

        private val endAction: (String) -> Unit = {
            bind.progressLoading.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.btnDownload.visibility = View.GONE
            Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
        }

        override fun show() {
            clear()
            observer = recyclerScope.launch {
                val mm = model.getByPosition(adapterPosition).ifNull { return@launch }

                bind.txtName.text = mm.module.name
                val count = mm.count.await().toString()
                bind.txtCountItems.text = itemView.context.getString(R.string.count_items).replace("||COUNT||", count)

                bind.txtOwner.text = mm.owner.await().userName

                bind.btnReport.setOnClickListener { reportCall?.let { it(mm.module) } }

                model.setDownloadAction(mm.module.globalId, endAction).ifTrue(startAction)

                bind.btnDownload.setOnClickListener {
                    startAction()
                    model.download(mm.module.globalId, endAction)
                }

                bind.btnStop.setOnClickListener {
                    model.stopDownloading(mm.module.globalId)
                }
            }
            bind.btnAction.setOnClickListener {
                full = !full
                bind.llBar.visibility = if (full) View.VISIBLE else View.GONE
                val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
                bind.imgAction.setImageResource(img)
            }
        }

        private fun clear() {
            full = false
            bind.llBar.visibility = View.GONE
            bind.progressLoading.visibility = View.GONE
            bind.btnDownload.visibility = View.GONE
            bind.btnStop.visibility = View.GONE
            bind.btnShare.visibility = View.GONE
            bind.imgAction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            auth.currentUser.ifNull { bind.btnReport.visibility = View.GONE }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (model.cloud.value) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosableHolder {
        val bind = CardModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            0 -> LocalModuleHolder(bind)
            1 -> GlobalModuleHolder(bind)
            else -> LocalModuleHolder(bind)
        }
    }

    override fun onBindViewHolder(holder: ClosableHolder, position: Int) = holder.show()

    override fun getItemCount() = size

    override fun close() {
        recyclerScope.cancel()
    }
}