package com.uogames.remembercards.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.DialogShareAttentionBinding

class ShareAttentionDialog private constructor(context: Context) : MaterialAlertDialogBuilder(context) {

    companion object {

        fun show(context: Context, onPositive: (Boolean) -> Unit) {
            val viewBin = DialogShareAttentionBinding.inflate(LayoutInflater.from(context))
            ShareAttentionDialog(context)
                .setView(viewBin.root)
                .setPositiveButton(context.getText(R.string.apply)) { _, _ -> onPositive(viewBin.cbDnshow.isChecked) }
                .setNegativeButton(context.getText(R.string.cancel)) { _, _ -> }
                .show()
        }

    }

}