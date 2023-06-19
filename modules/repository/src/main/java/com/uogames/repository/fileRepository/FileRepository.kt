package com.uogames.repository.fileRepository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import java.io.Closeable
import java.io.File
import java.lang.ref.WeakReference

class FileRepository private constructor(private val context: WeakReference<Context>) : Closeable {

	companion object {

		private var INSTANCE: FileRepository? = null

		fun getINSTANCE(context: Context): FileRepository {
			if (INSTANCE == null) {
				INSTANCE = FileRepository(WeakReference(context))
			}
			return INSTANCE as FileRepository
		}
	}

	fun saveFile(name: String, bytes: ByteArray): Uri {
		context.get()?.openFileOutput(name, Context.MODE_PRIVATE)?.use {
			it.write(bytes)
			it.flush()
			it.close()
		}
		return File(context.get()?.filesDir, name).toUri()
	}

	fun deleteFile(uri: Uri) {
		context.get()?.let {
			it.deleteFile(uri.toString().removePrefix("${it.filesDir.toUri()}/"))
		}
	}

	fun readFile(uri: Uri): ByteArray?{
		return context.get()?.openFileInput(uri.toFile().name)?.readBytes()
	}

	override fun close() {
		context.clear()
	}


}