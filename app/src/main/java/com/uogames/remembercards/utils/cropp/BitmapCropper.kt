package com.uogames.remembercards.utils.cropp

import android.graphics.*
import android.view.MotionEvent
import kotlin.math.absoluteValue
import kotlin.math.sqrt

class BitmapCropper {

	private val src: Bitmap

	private val previewWidth = 800f

	private var area: Area
	private var preview: Bitmap
	private var canvas: Canvas

	private var crop: Bitmap? = null
	private var previewCrop: Bitmap? = null

	private var cursorX = 0f
	private var cursorY = 0f
	private var diagonal = 0f

	private var cropChange = true
	private var previewChange = true

	constructor(src: Bitmap) {
		this.src = src
		val newHeight = src.height * previewWidth / src.width
		preview = Bitmap.createScaledBitmap(src, previewWidth.toInt(), newHeight.toInt(), true)
		area = Area(preview.width.toFloat(), preview.height.toFloat(), 0.1f, 16f / 9f)
		previewCrop =
			Bitmap.createBitmap(preview.width, preview.height, Bitmap.Config.ARGB_8888)
		canvas = Canvas(previewCrop!!)
	}



	fun getCrop(): Bitmap {
		area.resize()
		val scaleSize = src.width / area.wight
		if (cropChange) {
			crop = Bitmap.createBitmap(
				src,
				(area.left * scaleSize).toInt(),
				(area.top * scaleSize).toInt(),
				((area.right - area.left) * scaleSize).toInt(),
				((area.bottom - area.top) * scaleSize).toInt()
			)
			cropChange = false
		}
		return crop!!
	}

	fun getPreview(): Bitmap {
		area.resize()
		if (previewChange) {
			canvas.drawBitmap(preview, 0f, 0f, null)
			val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
				style = Paint.Style.FILL
				color = Color.argb(200, 0, 0, 0)
			}
			canvas.drawRect(0f, 0f, preview.width.toFloat(), area.top.toInt().toFloat(), paint)
			canvas.drawRect(
				0f,
				area.bottom.toInt().toFloat(),
				preview.width.toFloat(),
				preview.height.toFloat(),
				paint
			)
			canvas.drawRect(
				0f,
				area.top.toInt().toFloat(),
				area.left.toInt().toFloat(),
				area.bottom.toInt().toFloat(),
				paint
			)
			canvas.drawRect(
				area.right.toInt().toFloat(),
				area.top.toInt().toFloat(),
				preview.width.toFloat(),
				area.bottom.toInt().toFloat(),
				paint
			)

			val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
				style = Paint.Style.STROKE
				color = Color.WHITE
			}
			canvas.drawRect(area.left, area.top, area.right, area.bottom, linePaint)
			previewChange = false
		}
		return previewCrop!!
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
						area.changePosition(
							(event.getX(0) - cursorX) * scaleX,
							(event.getY(0) - cursorY) * scaleY
						)
						cursorX = event.getX(0)
						cursorY = event.getY(0)
					}
					2 -> {
						val firstX = event.getX(0)
						val secondX = event.getX(1)
						val firstY = event.getY(0)
						val secondY = event.getY(1)
						val difX = ((firstX - secondX) * scaleX).absoluteValue
						val difY = ((firstY - secondY) * scaleY).absoluteValue
						val rez = sqrt((difX * difX + difY * difY).toDouble()).toFloat()
						area.changeDiagonal(rez - diagonal)
						diagonal = rez
					}
				}
				cropChange = true
				previewChange = true
			}
			MotionEvent.ACTION_POINTER_DOWN -> {
				val firstX = event.getX(0)
				val secondX = event.getX(1)
				val firstY = event.getY(0)
				val secondY = event.getY(1)
				val difX = ((firstX - secondX) * scaleX).absoluteValue
				val difY = ((firstY - secondY) * scaleY).absoluteValue
				diagonal = sqrt((difX * difX + difY * difY).toDouble()).toFloat()
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

	fun getArea(): Area = area
}


