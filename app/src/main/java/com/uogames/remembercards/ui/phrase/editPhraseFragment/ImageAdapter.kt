package com.uogames.remembercards.ui.phrase.editPhraseFragment

import android.app.ActionBar
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.uogames.dto.local.LocalImage
import com.uogames.remembercards.R
import com.uogames.remembercards.utils.observe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class ImageAdapter(
    editCardViewModel: EditPhraseViewModel,
    val call: (LocalImage) -> Unit
) :	RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    private val recyclerScope = CoroutineScope(Dispatchers.Main)

    private var list = listOf<LocalImage>()

    init {
        editCardViewModel.listImageFlow.observe(recyclerScope) {
            list = it
            notifyDataSetChanged()
        }
    }

    inner class ImageHolder(val view: MaterialCardView) : RecyclerView.ViewHolder(view) {

        fun show() {
            val image = list[adapterPosition]
            view.setOnClickListener { call(image) }
            val iv = ImageView(view.context).apply {
                //setImageURI(image.imgUri.toUri())
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            view.addView(
                iv
//                ImageView(view.context).apply {
//                    setImageURI(image.imgUri.toUri())
//                    scaleType = ImageView.ScaleType.CENTER_CROP
//                }
            )
            Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(iv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imView = MaterialCardView(parent.context).apply {
            val layParams = ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250)
            layParams.setMargins(7)
            layoutParams = layParams
        }
        return ImageHolder(imView)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.show()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun onDestroy() {
        recyclerScope.cancel()
    }
}
