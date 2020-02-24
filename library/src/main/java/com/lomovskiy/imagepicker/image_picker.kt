package com.lomovskiy.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.lang.UnsupportedOperationException
import java.text.SimpleDateFormat
import java.util.*

class ImagePicker(

    private val context: Context,
    private val destinationPath: String,
    private val compressor: ImageCompressor?

) {

    private val rcGallery = 997
    private val rcCamera = 991
    private val authority: String = "${context.packageName}.imagepicker.fileprovider"

    private val dateTimeFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    private var tempPhotoFile: File? = null

    @Throws(UnsupportedOperationException::class)
    fun pickFromCamera(fragment: Fragment) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context.packageManager) != null) {
            tempPhotoFile = getNewTempFile()
            val photoUri = FileProvider.getUriForFile(context, authority, tempPhotoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            fragment.startActivityForResult(intent, rcCamera)
        } else {
            throw UnsupportedOperationException("Activity for doing this action is not find")
        }
    }

    @Throws(UnsupportedOperationException::class)
    fun pickFromCamera(activity: Activity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context.packageManager) != null) {
            tempPhotoFile = getNewTempFile()
            val photoUri = FileProvider.getUriForFile(activity, authority, tempPhotoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            activity.startActivityForResult(intent, rcCamera)
        } else {
            throw UnsupportedOperationException("Activity for doing this action is not find")
        }
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
            handleGalleryResult(resultCode, data, callback)
        } else if (requestCode == rcCamera) {
            handleCameraResult(resultCode, callback)
        }
    }

    private fun handleGalleryResult(resultCode: Int,
                                    data: Intent?,
                                    callback: Callback) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                try {
                    tempPhotoFile = getNewTempFile()
                    val uri: Uri = data!!.data!!
                    tempPhotoFile!!.writeBytes(context.contentResolver.openInputStream(uri)!!.readBytes())
                    val photoFile: File = getNewPhotoFile()
                    if (compressor == null) {
                        photoFile.writeBytes(tempPhotoFile!!.readBytes())
                    } else {
                        compressor.compress(tempPhotoFile!!, photoFile)
                    }
                    tempPhotoFile!!.delete()
                    callback.onSuccess(photoFile, PickType.Gallery)
                } catch (e: Exception) {
                    callback.onFailure(e, PickType.Gallery)
                }
            }
            Activity.RESULT_CANCELED -> callback.onCancel(PickType.Gallery)
        }
    }

    private fun handleCameraResult(resultCode: Int, callback: Callback) {
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
                    callback.onSuccess(photoFile, PickType.Camera)
                } catch (e: Exception) {
                    callback.onFailure(e, PickType.Camera)
                }
            }
            Activity.RESULT_CANCELED -> callback.onCancel(PickType.Camera)
        }
    }

    private fun getGalleryIntent(): Intent {
        return Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
    }

    private fun getNewTempFile(): File {
        return File("${context.cacheDir}/${UUID.randomUUID()}")
    }

    private fun getNewPhotoFile(): File {
        return File("${destinationPath}/${UUID.randomUUID()}.jpg")
    }

    interface Callback {

        fun onCancel(pickType: PickType)
        fun onFailure(e: Exception, pickType: PickType)
        fun onSuccess(file: File, pickType: PickType)

    }

}

sealed class PickType {
    object Gallery : PickType()
    object Camera : PickType()
}

class ImagePickerFileProvider : FileProvider()