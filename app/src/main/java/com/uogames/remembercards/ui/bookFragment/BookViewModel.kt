package com.uogames.remembercards.ui.bookFragment

import androidx.lifecycle.ViewModel
import com.uogames.dto.Card

class BookViewModel : ViewModel() {


    fun size(): Long {
        return 0
    }

    fun get(number: Long): Card {
        return Card(0, "", "")
    }


}