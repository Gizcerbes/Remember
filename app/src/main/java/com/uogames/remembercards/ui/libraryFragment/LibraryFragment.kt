package com.uogames.remembercards.ui.libraryFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import com.google.android.material.card.MaterialCardView
import com.uogames.flags.Countries
import com.uogames.remembercards.databinding.FragmentLbraryBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LibraryFragment : DaggerFragment() {

	private lateinit var bind: FragmentLbraryBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentLbraryBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		bind.recycler.adapter = Adapter()



//		Countries.values().forEach {
//			val ll = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
//			val iv = ImageView(requireContext()).apply { adjustViewBounds = true }
//			it.country.forEach {
//				val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//				lp.setMargins(7)
//				ll.addView(TextView(requireContext()).apply {
//					text = it
//					layoutParams = lp
//				})
//			}
//			ll.addView(iv)
//
//			val card = MaterialCardView(requireContext())
//			val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//			lp.setMargins(45, 45, 45, 45)
//			card.layoutParams = lp
//			card.addView(ll)
//			bind.flags.addView(card)
//			CoroutineScope(Dispatchers.Main).launch {
//				iv.setImageResource(it.res)
//				//Picasso.get().load(it.res).into(iv)
//			}
//
//		}
	}

}