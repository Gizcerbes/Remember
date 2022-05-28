package com.uogames.remembercards.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.io.InputStream


class FileChooser(private val fragment: Fragment, private val typeContent: String) {

    private var uriListener: (Uri) -> Unit = {}

    private val launcher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uriListener(it) }
            }
        }

    fun getUri(call: (Uri) -> Unit) {
        uriListener = call
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = typeContent
        launcher.launch(intent)
    }


    fun getParcelFileDescription(call: (ParcelFileDescriptor) -> Unit) =
        getUri() { uri ->
            val desc = fragment.requireActivity().contentResolver.openFileDescriptor(uri, "r")
            desc?.let { call(it) }
        }


    fun getInputStream(call: (InputStream) -> Unit) =
        getParcelFileDescription() { call(ParcelFileDescriptor.AutoCloseInputStream(it)) }


    fun getByteArray(call: (ByteArray) -> Unit) =
        getInputStream() { call(it.readBytes()) }


    fun getBitmap(call: (Bitmap) -> Unit) =
        getInputStream() { call(BitmapFactory.decodeStream(it)) }


}