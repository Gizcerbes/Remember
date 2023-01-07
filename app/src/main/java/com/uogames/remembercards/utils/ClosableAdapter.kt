package com.uogames.remembercards.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import java.io.Closeable

abstract class ClosableAdapter : RecyclerView.Adapter<ClosableAdapter.ClosableHolder>(), Closeable {

    abstract class ClosableHolder(view: View): RecyclerView.ViewHolder(view){
        var observer: Job? = null
        abstract fun show()

        open fun onDestroy() {
            observer?.cancel()
        }
    }


    override fun onViewRecycled(holder: ClosableHolder) {
        super.onViewRecycled(holder)
        holder.onDestroy()
    }



}
