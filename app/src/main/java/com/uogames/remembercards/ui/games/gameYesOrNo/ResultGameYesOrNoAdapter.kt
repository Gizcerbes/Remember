package com.uogames.remembercards.ui.games.gameYesOrNo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import com.google.android.material.divider.MaterialDivider
import com.squareup.picasso.Picasso
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardResultBinding
import com.uogames.remembercards.utils.ClosableAdapter
import com.uogames.remembercards.utils.asAnimationDrawable
import com.uogames.remembercards.utils.ifNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale

class ResultGameYesOrNoAdapter(
    private val model: GameYesOrNotViewModel
) : ClosableAdapter() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)

    private var list: List<Pair<GameYesOrNotViewModel.LocalCardModel, ArrayList<GameYesOrNotViewModel.LocalCardModel>>> = listOf()

    init {
        list = model.getAnswers()
    }

    inner class ResultHolder(val bind: CardResultBinding) : ClosableHolder(bind.root) {
        private val iconsArrows = listOf(R.drawable.ic_baseline_keyboard_arrow_up_24, R.drawable.ic_baseline_keyboard_arrow_down_24)
        private var full = false
        private var mistakes = false

        override fun show() {
            clear()
            val ans = list[adapterPosition]
            val cardModel = ans.first
            bind.txtReason.text = cardModel.card.reason
            bind.txtLangFirst.text = Locale.forLanguageTag(cardModel.card.phrase.lang).displayLanguage
            cardModel.card.phrase.pronounce?.let {
                bind.imgSoundFirst.visibility = View.VISIBLE
                bind.mcvFirst.setOnClickListener { recyclerScope.launch { cardModel.playPhrase(bind.imgSoundFirst.background.asAnimationDrawable()) } }
            }.ifNull { bind.mcvFirst.setOnClickListener(null) }
            bind.txtPhraseFirst.text = cardModel.card.phrase.phrase
            bind.txtDefinitionFirst.text = cardModel.card.phrase.definition
            cardModel.card.phrase.image?.let {
                bind.imgCardFirst.visibility = View.GONE
                Picasso.get().load(it.imgUri.toUri()).placeholder(R.drawable.noise).into(bind.imgCardFirst)
            }
            bind.txtLangSecond.text = Locale.forLanguageTag(cardModel.card.translate.lang).displayLanguage
            cardModel.card.translate.pronounce?.let {
                bind.imgSoundSecond.visibility = View.VISIBLE
                bind.mcvSecond.setOnClickListener { recyclerScope.launch { cardModel.playTranslate(bind.imgSoundSecond.background.asAnimationDrawable()) } }
            }.ifNull { bind.mcvSecond.setOnClickListener(null) }
            bind.txtPhraseSecond.text = cardModel.card.translate.phrase
            bind.txtDefinitionSecond.text = cardModel.card.translate.definition
            cardModel.card.translate.image?.let {
                bind.imgCardSecond.visibility = View.GONE
                Picasso.get().load(it.imgUri.toUri()).placeholder(R.drawable.noise).into(bind.imgCardSecond)
            }

            bind.llAnswers.addView(MaterialDivider(itemView.context))

            ans.second.forEach {
                if (ans.first.card.id == it.card.id) return@forEach
                val txt = TextView(itemView.context).apply {
                    setTextAppearance(R.style.Theme_RememberCards_Body2)
                    text = it.card.translate.phrase
                }
                bind.llAnswers.addView(txt)
            }

            bind.txtCountOfMistakes.text = ans.second.size.toString()

            bind.btnCardAction.setOnClickListener {
                full = !full
                bind.txtDefinitionFirst.visibility = if (full && !cardModel.card.phrase.definition.isNullOrEmpty()) View.VISIBLE else View.GONE
                bind.txtDefinitionSecond.visibility = if (full && !cardModel.card.translate.definition.isNullOrEmpty()) View.VISIBLE else View.GONE
                bind.imgBtnAction.setImageResource(iconsArrows[if (full) 0 else 1])
            }

            bind.btnShowMistakes.setOnClickListener {
                mistakes = !mistakes
                bind.imgBtnMistakes.setImageResource(iconsArrows[if (mistakes) 0 else 1])
                bind.llAnswers.visibility = if (mistakes) View.VISIBLE else View.GONE
            }

        }

        fun clear() {
            bind.txtReason.text = ""
            bind.imgBtnAction.setImageResource(iconsArrows[1])
            bind.txtLangFirst.text = ""
            bind.imgSoundFirst.visibility = View.GONE
            bind.txtPhraseFirst.text = ""
            bind.txtDefinitionFirst.text = ""
            bind.txtDefinitionFirst.visibility = View.GONE
            bind.imgCardFirst.visibility = View.GONE
            bind.txtLangSecond.text = ""
            bind.imgSoundSecond.visibility = View.GONE
            bind.txtPhraseSecond.text = ""
            bind.txtDefinitionSecond.text = ""
            bind.txtDefinitionSecond.visibility = View.GONE
            bind.imgCardSecond.visibility = View.GONE
            bind.txtCountOfMistakes.text = "0"
            bind.imgBtnMistakes.setImageResource(iconsArrows[1])
            bind.llAnswers.visibility = View.GONE
            bind.llAnswers.removeAllViews()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultHolder {
        return ResultHolder(CardResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ClosableHolder, position: Int) {
        holder.show()
    }

    override fun getItemCount() = list.size

    fun onDestroy() {
        recyclerScope.cancel()
    }

    override fun close() {
        recyclerScope.cancel()
    }
}
