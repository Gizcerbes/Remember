package com.uogames.remembercards.ui.bookFragment

import android.icu.util.Measure
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uogames.remembercards.databinding.FragmentBookRecyclerBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class BookRecyclerFragment : DaggerFragment() {

    @Inject
    lateinit var bookViewModel: BookViewModel

    lateinit var bind: FragmentBookRecyclerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentBookRecyclerBinding.inflate(inflater, null, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BookAdapter(bookViewModel)

        bind.recycler.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        //Log.e("TAG", "onViewCreated: ${bind.recycler.layoutParams.height} ${bind.recycler.}")
        bind.recycler.adapter = adapter

        bind.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                bookViewModel.setSpeedScrolling(dy)
//                bind.btnAdd.visibility = if (dy > 0 || isSearch.value) View.GONE else View.VISIBLE
//                bind.btnSearch.visibility = if (dy > 0) View.GONE else View.VISIBLE
            }
        })

    }

}