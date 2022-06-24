package com.uogames.remembercards.ui.gameYesOrNo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardGameResultBinding
import com.uogames.remembercards.utils.observeWhile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GameYesOrNoAdapter(
	private val model: GameYesOrNotViewModel
) : RecyclerView.Adapter<GameYesOrNoAdapter.ResultHolder>() {

	companion object {
		private const val RESULT_FRAGMENT = "RESULT_FRAGMENT"
	}

	inner class ResultHolder(view: View, private val viewGroup: ViewGroup) :
		RecyclerView.ViewHolder(view) {

		private val holderScope = CoroutineScope(Dispatchers.Main)

		private val typeFragment = MutableStateFlow(RESULT_FRAGMENT)

		private val resultBind: CardGameResultBinding by lazy {
			CardGameResultBinding.inflate(LayoutInflater.from(view.context), viewGroup, false)
		}
		init {
			typeFragment.observeWhile(holderScope) { draw(it) }
		}

		fun changePosition() {
			if (adapterPosition != -1) show()
		}

		private fun draw(type:String) {
			val lay = itemView as LinearLayout
			lay.removeAllViews()
			when(type){
				RESULT_FRAGMENT -> lay.addView(resultBind.root)
			}
			val param = lay.layoutParams
			param.height = ViewGroup.LayoutParams.WRAP_CONTENT
			lay.layoutParams = param
		}

		private fun show(){
			when (typeFragment.value){
				RESULT_FRAGMENT -> showResult()
			}
		}

		private fun showResult(){
//			val answer = model.getAnswer(adapterPosition)
//			resultBind.txtPhrase.text = answer.firs.phrase
//			resultBind.txtTranslate.text = answer.firs.translate
//			if (answer.truth){
//				resultBind.txtAnswer.text = answer.firs.translate
//				resultBind.txtAnswer.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGreen))
//			} else {
//				resultBind.txtAnswer.text = answer.second.translate
//				resultBind.txtAnswer.setTextColor(ContextCompat.getColor(itemView.context, R.color.redwood))
//			}
		}



	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultHolder {
		return ResultHolder(
			LinearLayout(parent.context).apply {
				layoutParams = LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT
				)
				orientation = LinearLayout.VERTICAL
			}, parent
		)
	}

	override fun onBindViewHolder(holder: ResultHolder, position: Int) {
		holder.changePosition()
	}

	override fun getItemCount() = model.allAnswers.value


}