package com.lomovskiy.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImagePicker(

    private val destinationFolder: File,
    private val compressor: ImageCompressor?

) {

    private val rcGallery = 997
    private val rcCamera = 991

    private val dateTimeFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    private var tempPhotoFile: File? = null

    fun pickFromCamera() {

    }

    fun pickFromGallery(fragment: Fragment) {
        fragment.startActivityForResult(getGalleryIntent(), rcGallery)
    }

    fun pickFromGallery(activity: Activity) {
        activity.startActivityForResult(getGalleryIntent(), rcGallery)
    }

    fun handleOnActivityResult(requestCode: Int,
                               resultCode: Int,
                               data: Intent?,
                               context: Context,
                               callback: Callback) {
        if (requestCode == rcGallery) {
            handleGalleryResult(resultCode, data, context, callback)
        } else if (requestCode == rcCamera) {
            handleCameraResult(resultCode, context, callback)
        }
    }

    private fun handleGalleryResult(resultCode: Int,
                                    data: Intent?,
                                    context: Context,
                                    callback: Callback) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                try {
                    tempPhotoFile = getNewTempFile(context)
                    val uri: Uri = data!!.data!!
                    tempPhotoFile!!.writeBytes(context.contentResolver.openInputStream(uri)!!.readBytes())
                    val photoFile: File = getNewPhotoFile()
                    if (compressor == null) {
                        photoFile.writeBytes(tempPhotoFile!!.readBytes())
                    } else {
                        compressor.compress(tempPhotoFile!!, photoFile)
                    }
                    tempPhotoFile!!.delete()
                    callback.onSuccess(photoFile, PickType.GALLERY)
                } catch (e: Exception) {
                    callback.onFailure(e, PickType.GALLERY)
                }
            }
            Activity.RESULT_CANCELED -> callback.onCancel(PickType.GALLERY)
        }
    }

    private fun handleCameraResult(resultCode: Int,
                                   context: Context,
                                   callback: Callback) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                try {
                    val photoFile: File = getNewPhotoFile()
                    if (compressor == null) {
                        photoFile.writeBytes(tempPhotoFile!!.readBytes())
                    } else {
                        compressor.compress(tempPhotoFile!!, photoFile)
                    }
                    tempPhotoFile!!.delete()
                    callback.onSuccess(photoFile, PickType.CAMERA)
                } catch (e: Exception) {
                    callback.onFailure(e, PickType.CAMERA)
                }
            }
            Activity.RESULT_CANCELED -> callback.onCancel(PickType.CAMERA)
        }
    }

    private fun getGalleryIntent(): Intent {
        return Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
    }

    private fun getNewTempFile(context: Context): File {
        return File("${context.cacheDir}/${UUID.randomUUID()}")
    }

    private fun getNewPhotoFile(): File {
        val timestamp: String = dateTimeFormatter.format(Date())
        val uuid: String = UUID.randomUUID().toString()
        return File("${destinationFolder.absolutePath}/${timestamp}_$uuid.jpg")
    }

    interface Callback {

        fun onCancel(pickType: PickType)
        fun onFailure(e: Exception, pickType: PickType)
        fun onSuccess(file: File, pickType: PickType)

    }

}

enum class PickType {
    CAMERA, GALLERY
}