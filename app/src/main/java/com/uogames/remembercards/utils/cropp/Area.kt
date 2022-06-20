package com.uogames.remembercards.utils.cropp

import kotlin.math.sqrt

class Area(val wight: Float, val height: Float, minArea: Float, val ratio: Float) {

	private var minDiagonal = sqrt(wight* wight + height * height)  * minArea

	var left = 0f
		private set
	var right = 0f
		private set
	var top = 0f
		private set
	var bottom = 0f
		private set

	var cursorX = wight / 2f
		private set
	var cursorY = height / 2f
		private set
	var diagonal = sqrt(wight* wight + height * height)/2
		private set

	init {
		resize()
	}

	fun resize() {
		if (diagonal < minDiagonal) diagonal = minDiagonal

		var hei = sqrt((diagonal * diagonal) / (1 + ratio * ratio))
		var wig = hei * ratio

		if (hei > height) {
			hei = height
			wig = height * ratio
			diagonal = sqrt(hei * hei + wig * wig)
		}
		if (wig > wight) {
			wig = wight
			hei = wight / ratio
			diagonal = sqrt(hei * hei + wig * wig)
		}

		val minCursorX = wig / 2f
		val maxCursorX = wight - minCursorX
		val minCursorY = hei / 2f
		val maxCursorY = height - minCursorY

		if (cursorX < minCursorX) cursorX = minCursorX
		if (cursorX > maxCursorX) cursorX = maxCursorX
		if (cursorY < minCursorY) cursorY = minCursorY
		if (cursorY > maxCursorY) cursorY = maxCursorY

		left = cursorX - wig / 2f
		right = cursorX + wig / 2f
		top = cursorY - hei / 2f
		bottom = cursorY + hei / 2f
	}

	fun changePosition(vectorCursorX: Float, vectorCursorY: Float) {
		cursorX += vectorCursorX
		cursorY += vectorCursorY
	}

	fun changeDiagonal(vectorDiagonal: Float) {
		diagonal += vectorDiagonal
	}

}