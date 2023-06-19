package com.uogames.remembercards.utils

import android.media.MediaDataSource

class MediaBytesSource(private var data: ByteArray?) : MediaDataSource() {

    override fun close() {
        data = null
    }

    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        data?.let {
            synchronized(it) {
                val len = it.size
                if (position >= len) {
                    return -1
                }
                var si: Long = size.toLong()
                if (position + size > len) {
                    si -= (position + size) - len
                }
                System.arraycopy(it, position.toInt(), buffer, offset, si.toInt())
                return si.toInt()
            }
        } ?: return -1
    }

    override fun getSize(): Long {
        return data?.size?.toLong() ?: -1
    }
}
