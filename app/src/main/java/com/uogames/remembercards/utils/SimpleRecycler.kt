package com.uogames.remembercards.utils

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.uogames.remembercards.ui.bookFragment.BookAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SimpleRecycler : RecyclerView {

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
		context,
		attrs,
		defStyleAttr
	)

	override fun setAdapter(adapter: Adapter<*>?) {
		if (height <= 0 || width <= 0)
			CoroutineScope(Dispatchers.Main).launch {
				delay(500)
				addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
					val rect = Rect(left, top, right, bottom)
					val oldRect = Rect(oldLeft, oldTop, oldRight, oldBottom)
					if (rect.width() != oldRect.width() || rect.height() != oldRect.height()) {
						val params = layoutParams
						params.height = rect.height()
						params.width = rect.width()
						layoutParams = params
						Log.e("TAG", "onViewCreated: ${rect.width()} ${rect.height()}", )
					}
				}
				super.setAdapter(adapter)
			}
		else
			super.setAdapter(adapter)



	}

	abstract class SimpleAdapter<VH : SimpleAdapter.SimpleViewHolder> : RecyclerView.Adapter<VH>() {

		private val stopAll = MutableStateFlow(false)
		private val changeAll = MutableStateFlow(0)

		abstract class SimpleViewHolder(private val view: LinearLayout, private val viewGrope: ViewGroup) :
			ViewHolder(view) {

			private val defHolderScope = CoroutineScope(Dispatchers.Main)
			var cardScope = CoroutineScope(Dispatchers.Main)

			private val typeFragment = MutableStateFlow(0)

			init {
				typeFragment.onEach { draw(it) }.launchIn(defHolderScope)
			}

			fun changePosition(){
				cardScope.cancel()
				cardScope = CoroutineScope(Dispatchers.Main)
				if(adapterPosition != -1) show(typeFragment.value, viewGrope)
			}

			abstract fun onCreateView(typeFragment: Int): View

			private fun draw(typeFragment: Int){
				view.removeAllViews()
				view.addView(onCreateView(typeFragment))
				val param = view.layoutParams
				param.height = ViewGroup.LayoutParams.WRAP_CONTENT
				view.layoutParams = param
				show(typeFragment, viewGrope)
			}

			abstract fun show(typeFragment: Int, viewGrope: ViewGroup)

		}

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
			return onCreateViewGolder(parent, createLayout(parent), viewType)
		}

		abstract fun onCreateViewGolder(parent: ViewGroup, view: LinearLayout, viewType: Int): VH

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

	}


}