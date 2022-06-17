package com.uogames.remembercards.utils

import android.graphics.*
import android.view.MotionEvent
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class BitmapCropper(private val src: Bitmap) {

    private val wight = 1920f
    private val height = 1080f

    private var startWidth = 0f
    private var startHeight = 0f

    private var left = 0f
    private var right = 0f
    private var top = 0f
    private var bottom = 0f

    private var cursorX = 0f
    private var cursorY = 0f

    private var cursorXY = 0f

    private var preview: Bitmap
    private var maxScale: Float
    private var minScale: Float

    private var scale: Float

    init {
        val newWidth = 1024
        val newHeight = src.height * newWidth / src.width
        preview = Bitmap.createScaledBitmap(src, newWidth, newHeight, true)

        maxScale = min(preview.width / wight, preview.height / height)
        minScale = maxScale * 0.1f
        scale = maxScale * 0.5f

        startWidth = preview.width / 2f
        startHeight = preview.height / 2f
        resize()
    }

    private fun resize() {
        val wid = wight * scale
        val hei = height * scale
        if (wid > preview.width || hei > preview.height) {
            scale /= max(wid / preview.width, hei / preview.height)
            resize()
            return
        }
        var l = startWidth - wid / 2
        var r = startWidth + wid / 2
        var t = startHeight - hei / 2
        var b = startHeight + hei / 2
        if (l < 0) {
            r -= l
            l = 0f
            startWidth = wid / 2
        }
        if (r > preview.width.toFloat()) {
            l -= r - preview.width
            r = preview.width.toFloat()
            startWidth = preview.width - wid / 2
        }
        if (t < 0) {
            b -= t
            t = 0f
            startHeight = hei / 2
        }
        if (b > preview.height.toFloat()) {
            t -= b - preview.height.toFloat()
            b = preview.height.toFloat()
            startHeight = preview.height.toFloat() - hei / 2
        }
        left = l
        right = r
        top = t
        bottom = b
    }

    fun getCrop(): Bitmap {
        val scaleSize = src.width / preview.width.toFloat()
        return Bitmap.createBitmap(
            src,
            (left * scaleSize).toInt(),
            (top * scaleSize).toInt(),
            ((right - left) * scaleSize).toInt(),
            ((bottom - top) * scaleSize).toInt()
        )
    }

    fun getPreview(): Bitmap {
        val bitmap = Bitmap.createBitmap(preview.width, preview.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawBitmap(preview, 0f, 0f, null)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.argb(200, 0, 0, 0)
        }
        canvas.drawRect(0f,0f,preview.width.toFloat(), top.toInt().toFloat(), paint)
        canvas.drawRect(0f,bottom.toInt().toFloat(), preview.width.toFloat(), preview.height.toFloat(),paint)
        canvas.drawRect(0f,top.toInt().toFloat(), left.toInt().toFloat(), bottom.toInt().toFloat(), paint)
        canvas.drawRect(right.toInt().toFloat(),top.toInt().toFloat(), preview.width.toFloat(), bottom.toInt().toFloat(),paint)

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.WHITE
        }
        canvas.drawRect(left, top, right, bottom, linePaint)

        return bitmap
    }

    fun crop(event: MotionEvent, viewWight: Int, viewHeight: Int) {
        val scaleX = preview.width / viewWight.toFloat()
        val scaleY = preview.height / viewHeight.toFloat()
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                cursorX = event.x
                cursorY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                when (event.pointerCount) {
                    1 -> {
                        startWidth -= (cursorX - event.getX(0)) * scaleX
                        startHeight -= (cursorY - event.getY(0)) * scaleY
                        cursorX = event.getX(0)
                        cursorY = event.getY(0)
                        if (right <= 0 || bottom <= 0) {
                            right = 100f * scaleX
                            bottom = 100f * scaleY
                            startWidth = preview.width / 2f - 50f * scaleX
                            startHeight = preview.height / 2f - 50f * scaleY
                        }
                        resize()
                    }
                    2 -> {
                        val firstX = event.getX(0)
                        val secondX = event.getX(1)
                        val firstY = event.getY(0)
                        val secondY = event.getY(1)
                        val difX = ((firstX - secondX) * scaleX).absoluteValue
                        val difY = ((firstY - secondY) * scaleY).absoluteValue
                        val rez = sqrt((difX * difX + difY * difY).toDouble()).toFloat()
                        val newScale = (rez / cursorXY) * scale
                        cursorXY = rez

                        scale = if (newScale > minScale) newScale else minScale
                        resize()
                    }
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val firstX = event.getX(0)
                val secondX = event.getX(1)
                val firstY = event.getY(0)
                val secondY = event.getY(1)
                val difX = ((firstX - secondX) * scaleX).absoluteValue
                val difY = ((firstY - secondY) * scaleY).absoluteValue
                cursorXY = sqrt ((difX * difX + difY * difY).toDouble()).toFloat()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                when (event.actionIndex) {
                    0 -> {
                        cursorX = event.getX(1)
                        cursorY = event.getY(1)
                    }
                    1 -> {
                        cursorX = event.getX(0)
                        cursorY = event.getY(0)
                    }
                }
            }
        }
    }
}


