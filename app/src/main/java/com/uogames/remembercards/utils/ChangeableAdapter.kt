package com.uogames.remembercards.utils

import android.util.Log
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
	) : RecyclerView.ViewHolder(view) {

		private val defHolderScope = CoroutineScope(Dispatchers.Main)
		private var _cardScope = CoroutineScope(Dispatchers.Main)
		val cardScope: CoroutineScope get() = _cardScope
		private var oldPos = -1

		private val typeFragment = MutableStateFlow(0)

		init {
			typeFragment.onEach { draw(it) }.launchIn(defHolderScope)
		}

		fun changePosition() {
			_cardScope.cancel()
			_cardScope = CoroutineScope(Dispatchers.Main)
			val type = itemViewType()
			if (typeFragment.value == type) {
				if (adapterPosition != -1) cardScope.show(typeFragment.value)
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
			if (adapterPosition != oldPos) {
				view.removeAllViews()
				view.addView(onCreateView(typeFragment, viewGrope))
			} else {
				Log.e("TAG", "draw: $adapterPosition $oldPosition $oldPos")
			}
			if (adapterPosition != -1) cardScope.launch { show(typeFragment) }
			val param = view.layoutParams
			param.height = ViewGroup.LayoutParams.WRAP_CONTENT
			view.layoutParams = param
			oldPos = adapterPosition
		}

		abstract fun CoroutineScope.show(typeFragment: Int)

		fun onDetach() {
			cardScope.cancel()
			onDetached()
		}

		open fun onDetached() {
		}

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
		return onShow(parent, createLayout(parent), viewType)
	}

	abstract fun onShow(
		parent: ViewGroup,
		view: LinearLayout,
		viewType: Int,
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
		holder.onDetach()
	}


}


