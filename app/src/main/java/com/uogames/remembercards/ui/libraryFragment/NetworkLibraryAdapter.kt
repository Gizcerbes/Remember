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

class NetworkLibraryAdapter(
	val model: NetworkLibraryViewModel,
	val selectID: (Module) -> Unit
) : ClosableAdapter<NetworkLibraryAdapter.ModuleHolder>() {

	private val recyclerScope = CoroutineScope(Dispatchers.Main)
	private var size = 0

	init {
		model.size.observeWhile(recyclerScope) {
			size = it.toInt()
			notifyDataSetChanged()
		}
	}

	inner class ModuleHolder(val bind: CardModuleBinding) : RecyclerView.ViewHolder(bind.root) {

		private var moduleObserver: Job? = null

		private var full = false

		fun onShow() {
			clear()
			moduleObserver = recyclerScope.launch(Dispatchers.IO) {
				val module = model.getByPosition(adapterPosition.toLong()).ifNull { return@launch }
				launch(Dispatchers.Main) { bind.txtName.text = module.name }
				val count = model.getModuleCardCount(module)
				launch(Dispatchers.Main) {
					bind.txtCountItems.text = itemView.context.getString(R.string.count_items).replace("||COUNT||", count.toString())
				}

				val startAction: () -> Unit = {
					bind.progressLoading.visibility = View.VISIBLE
					bind.btnStop.visibility = View.VISIBLE
					bind.btnDownload.visibility = View.GONE
				}

				val endAction: (String) -> Unit = {
					bind.progressLoading.visibility = View.GONE
					bind.btnStop.visibility = View.GONE
					bind.btnDownload.visibility = View.VISIBLE
					Toast.makeText(itemView.context, it, Toast.LENGTH_SHORT).show()
				}

				launch(Dispatchers.Main) {
					model.setDownloadAction(module.globalId, endAction).ifTrue(startAction)
				}

				bind.btnDownload.setOnClickListener {
					startAction()
					model.download(module.globalId, endAction)
				}

				bind.btnStop.setOnClickListener {
					model.stopDownloading(module.globalId)
				}

			}

			bind.btnAction.setOnClickListener {
				full = !full
				bind.llBar.visibility = if (full) View.VISIBLE else View.GONE
				val img = if (full) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24
				bind.imgAction.setImageResource(img)
				if (adapterPosition == size - 1 && !full) notifyItemChanged(adapterPosition)
			}
		}

		private fun clear() {
			full = false
			bind.llBar.visibility = View.GONE
			bind.progressLoading.visibility = View.GONE
			bind.txtCountItems.text = ""
			bind.btnEdit.visibility = View.GONE
			bind.btnShare.visibility = View.GONE
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

	override fun getItemCount() = size

	override fun onViewRecycled(holder: ModuleHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	override fun close() {
		recyclerScope.cancel()
	}
}
