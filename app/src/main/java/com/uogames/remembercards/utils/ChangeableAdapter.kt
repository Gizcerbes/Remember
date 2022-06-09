package com.uogames.remembercards.utils

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.uogames.repository.DataProvider.Companion.get
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


abstract class ChangeableAdapter<VH : ChangeableAdapter.ChangeableViewHolder> :
	RecyclerView.Adapter<VH>() {

	private val dataChange = MutableStateFlow(0)

	abstract class ChangeableViewHolder(
		view: LinearLayout,
		private val viewGrope: ViewGroup,
		dataChange: MutableStateFlow<Int>
	) : RecyclerView.ViewHolder(view) {

		private val defHolderScope = CoroutineScope(Dispatchers.Main)
		private var _cardScope = CoroutineScope(Dispatchers.Main)
		val cardScope: CoroutineScope get() = _cardScope

		private val typeFragment = MutableStateFlow(0)

		init {
			typeFragment.onEach { draw(it) }.launchIn(defHolderScope)
			dataChange.onEach { changePosition() }.launchIn(defHolderScope)
		}

		fun changePosition() {
			_cardScope.cancel()
			_cardScope = CoroutineScope(Dispatchers.Main)
			val type = itemViewType()
			if (typeFragment.value == type) {
				if (adapterPosition != -1) show(typeFragment.value)
			} else {
				if (adapterPosition != -1) typeFragment.value = type
			}
		}

		open fun itemViewType(): Int {
			return 0
		}

		fun changeType(type: Int) {
			typeFragment.value = type
		}

		abstract fun onCreateView(typeFragment: Int, viewGrope: ViewGroup): View?

		private fun draw(typeFragment: Int) {
			val view = itemView as LinearLayout
			view.removeAllViews()
			view.addView(onCreateView(typeFragment, viewGrope))
			val param = view.layoutParams
			param.height = ViewGroup.LayoutParams.WRAP_CONTENT
			view.layoutParams = param
			show(typeFragment)
		}

		abstract fun show(typeFragment: Int)

		fun onDetached() {
			cardScope.cancel()
		}

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
		return onShow(parent, createLayout(parent), viewType, dataChange)
	}

	abstract fun onShow(
		parent: ViewGroup,
		view: LinearLayout,
		viewType: Int,
		changeListener: MutableStateFlow<Int>
	): VH

	private fun createLayout(viewGrope: ViewGroup): LinearLayout {
		return LinearLayout(viewGrope.context).apply {
			layoutParams = LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT
			)
			orientation = LinearLayout.VERTICAL
		}
	}

	override fun onBindViewHolder(holder: VH, position: Int) {
		holder.changePosition()
	}

	override fun onViewRecycled(holder: VH) {
		super.onViewRecycled(holder)
		holder.onDetached()
	}

	fun dataChanged() {
		dataChange.value++
	}


}


