package com.uogames.remembercards.ui.cropFragment

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.databinding.FragmentCropBinding
import com.uogames.remembercards.utils.cropp.BitmapCropper
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import javax.inject.Inject

class CropFragment : DaggerFragment() {

	@Inject
	lateinit var cropViewModel: CropViewModel

	private lateinit var bind: FragmentCropBinding

	private var cropper: BitmapCropper? = null

	private var rotateJob: Job? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentCropBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		bind.btnBack.setOnClickListener {
			cropViewModel.reset()
			findNavController().popBackStack()
		}

		cropViewModel.getData()?.let {
			cropper = BitmapCropper(it)
			bind.img.setImageBitmap(cropper!!.getPreview())
		}

		bind.btnSave.setOnClickListener {
			rotateJob?.cancel()
			cropper?.getCrop()?.let { bitmap ->
				cropViewModel.putData(bitmap)
			}
			findNavController().popBackStack()
		}

		bind.btnRotateLeft.setOnClickListener {
			cropViewModel.leftRotate()
		}

		bind.btnRotateRight.setOnClickListener {
			cropViewModel.rightRotate()
		}

		bind.img.setOnTouchListener { _, event ->
			cropper?.crop(event, bind.img.width, bind.img.height)
			cropper?.let { bind.img.setImageBitmap(it.getPreview()) }
			bind.img.performClick()
			true
		}

		rotateJob = cropViewModel.rotateStat.observeWhenStarted(lifecycleScope) {
			val bitmap = cropViewModel.getData()
			val area = cropper?.getArea()
			if (bitmap != null && area != null) when (it % 4) {
				0 -> cropper = BitmapCropper(rotateBitmap(bitmap, 0f))
				1 -> cropper = BitmapCropper(rotateBitmap(bitmap, 90f))
				2 -> cropper = BitmapCropper(rotateBitmap(bitmap, 180f))
				3 -> cropper = BitmapCropper(rotateBitmap(bitmap, 270f))
			}
			cropper?.getPreview().let { preview -> bind.img.setImageBitmap(preview) }
		}
	}

	private fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
		val matrix = Matrix()
		matrix.setRotate(angle)
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
	}

}