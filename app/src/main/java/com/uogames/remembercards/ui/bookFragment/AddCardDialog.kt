package com.uogames.remembercards.ui.bookFragment

import android.content.Context
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.uogames.dto.Card
import com.uogames.remembercards.databinding.CardEditBinding
import com.uogames.remembercards.databinding.CardInfoBinding
import dagger.android.support.DaggerDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddCardDialog : DaggerDialogFragment() {

    companion object {
        const val TAG = "ADD_DIALOG"
    }

    @Inject
    lateinit var bookViewModel: BookViewModel

    private lateinit var bind: CardEditBinding

    private val imm by lazy {
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = CardEditBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bind.btnBack.setOnClickListener {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            dismiss()
        }

        bind.btnDelete.visibility = View.GONE
        bind.btnSave.setOnClickListener {
            val phrase = bind.editPhrase.editText?.text.toString()
            val translate = bind.editTranslate.editText?.text.toString()
            val card = Card(phrase = phrase, translate = translate)
            if (phrase.isNotEmpty() && translate.isNotEmpty()) bookViewModel.add(card) {
                if (it) {
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    dismiss()
                }
            } else {
                bind.editPhrase.error = ""
                bind.editTranslate.error = ""
            }
        }
    }

    override fun onStart() {
        super.onStart()

        bind.editPhrase.requestFocus()
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            imm.showSoftInput(bind.editPhrase.editText, InputMethodManager.SHOW_IMPLICIT)
        }


        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.attributes?.horizontalMargin = 17f
    }


}