package com.uogames.remembercards.utils

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow


abstract class ChangeableAdapter<VH : ChangeableAdapter.ChangeableViewHolder>(private val scope: LifecycleCoroutineScope) :
	RecyclerView.Adapter<VH>() {

	private val dataChange = MutableStateFlow(0)

	abstract class ChangeableViewHolder(
		view: LinearLayout,
		private val viewGrope: ViewGroup,
		private val scope: LifecycleCoroutineScope
	) : RecyclerView.ViewHolder(view) {

		private val typeFragment = MutableStateFlow(0)
		private var cardJob: Job? = null
		private var oldPos = -1

		init {
			typeFragment.observeWhenStarted(scope) { draw(it) }
		}

		fun changePosition() {
			cardJob?.cancel()
			val type = itemViewType()
			if (typeFragment.value == type) {
				if (adapterPosition != -1) {
					cardJob = scope.launchWhenStarted { draw(typeFragment.value) }
				}
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
			}
			if (adapterPosition != -1) {
				 scope.show(typeFragment)
			}
			val param = view.layoutParams
			param.height = ViewGroup.LayoutParams.WRAP_CONTENT
			view.layoutParams = param
			oldPos = adapterPosition
		}

		abstract fun LifecycleCoroutineScope.show(typeFragment: Int)

		fun onDetach() {
			cardJob?.cancel()
			onDetached()
		}

		open fun onDetached() {
		}

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
		return onShow(parent, createLayout(parent), viewType, scope)
	}

	abstract fun onShow(
		parent: ViewGroup,
		view: LinearLayout,
		viewType: Int,
		scope: LifecycleCoroutineScope
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


