package com.uogames.remembercards.ui.bookFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uogames.dto.Image
import com.uogames.dto.Pronunciation
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.CardPhraseBinding
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*

class BookAdapter(
    private val model: BookViewModel,
    private val player: ObservableMediaPlayer,
    private val editPhraseCall: (Int) -> Unit
) : RecyclerView.Adapter<BookAdapter.CardHolder>() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)

    inner class CardHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var bookViewObserver: Job? = null

        private var _bind: CardPhraseBinding? = null
        private val bind get() = _bind!!

        fun onShow() {
            _bind = CardPhraseBinding.inflate(LayoutInflater.from(itemView.context), itemView as ViewGroup, false)
            val linearLayout = itemView as LinearLayout
            linearLayout.removeAllViews()
            linearLayout.addView(bind.root)
            bind.imgPhrase.visibility = View.GONE
            bookViewObserver = model.get(adapterPosition).observeWhile(recyclerScope) { bookView ->
                bookView?.phrase?.let { phrase ->
                    bind.txtPhrase.text = phrase.phrase
                    bind.txtDefinition.text = phrase.definition.orEmpty()
                    showImage(bookView.image)
                    showPronounce(bookView.pronounce)
                    bind.txtLang.text = bookView.lang
                    bind.btnEdit.setOnClickListener {
                        editPhraseCall(phrase.id)
                    }
                }
            }
        }

        private suspend fun showImage(image: Deferred<Image?>) {
            image.await()?.let {
                val uri = it.imgUri.toUri()
                Picasso.get().load(uri).placeholder(R.drawable.noise).into(bind.imgPhrase)
                bind.imgPhrase.visibility = View.VISIBLE
            }.ifNull {
                bind.imgPhrase.visibility = View.GONE
            }
        }

        private suspend fun showPronounce(pronounce: Deferred<Pronunciation?>) {
            pronounce.await()?.let { pron ->
                bind.btnSound.visibility = View.VISIBLE
                bind.btnSound.setOnClickListener {
                    player.play(
                        itemView.context,
                        pron.audioUri.toUri(),
                        bind.imgBtnSound.background.asAnimationDrawable()
                    )
                }
            }.ifNull {
                bind.btnSound.visibility = View.GONE
            }
        }

        fun onDestroy() {
            bookViewObserver?.cancel()
            _bind = null
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        return CardHolder(LinearLayout(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
        })
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.onShow()
        (holder.itemView as LinearLayout).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun getItemCount(): Int {
        return model.size.value
    }

    override fun onViewRecycled(holder: CardHolder) {
        super.onViewRecycled(holder)
        holder.onDestroy()
    }

    fun onDestroy(){
        recyclerScope.cancel()
    }





}