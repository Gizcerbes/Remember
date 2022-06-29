package com.uogames.remembercards.ui.editCardFragment

import android.app.ActionBar
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.view.setMargins
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.Image
import com.uogames.remembercards.utils.observeWhenStarted

class ImageAdapter(
	lifecycleCoroutineScope: LifecycleCoroutineScope,
	editCardViewModel: EditCardViewModel,
	val call: (Image) -> Unit
) :
	RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

	private var list = listOf<Image>()

	init {
		editCardViewModel.listImageFlow.observeWhenStarted(lifecycleCoroutineScope){
			list = it
			notifyDataSetChanged()
		}
	}

	inner class ImageHolder(val view: MaterialCardView) : RecyclerView.ViewHolder(view){

		fun show(){
			val uri = list[adapterPosition].imgUri.toUri()
			//view.setImageURI(uri)
			view.setOnClickListener { call(list[adapterPosition]) }
			view.addView(ImageView(view.context).apply {
				setImageURI(uri)
				scaleType = ImageView.ScaleType.CENTER_CROP
			})
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
		val imView = MaterialCardView(parent.context).apply {
			//scaleType = ImageView.ScaleType.CENTER_CROP
			val layParams = ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,250)
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


}