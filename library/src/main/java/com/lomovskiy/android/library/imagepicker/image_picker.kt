package com.lomovskiy.android.library.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MediaFile(val uri: Uri, val file: File)

class ImagePicker(

    private val context: Context,
    private val destinationPath: String,
    private val compressor: ImageCompressor?

) {

    private val rcGallery = 997
    private val rcCamera = 991
    private val authority: String = "${context.packageName}.imagepicker.fileprovider"

    private val dateTimeFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    private var tempPhotoFile: MediaFile? = null

    @Throws(UnsupportedOperationException::class)
    fun pickFromCamera(fragment: Fragment) {
        tempPhotoFile = getNewMediaFile()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoFile!!.uri)
        grantWritePermission(context, intent, tempPhotoFile!!.uri)
        if (intent.resolveActivity(context.packageManager) != null) {
            fragment.startActivityForResult(intent, rcCamera)
        } else {
            throw UnsupportedOperationException("Activity for doing this action is not find")
        }
    }

    @Throws(UnsupportedOperationException::class)
    fun pickFromCamera(activity: Activity) {
        tempPhotoFile = getNewMediaFile()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoFile!!.uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        grantWritePermission(activity, intent, tempPhotoFile!!.uri)
        if (intent.resolveActivity(context.packageManager) != null) {
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
                               callback: Callback
    ) {
        if (requestCode == rcGallery) {
            handleGalleryResult(resultCode, data, callback)
        } else if (requestCode == rcCamera) {
            handleCameraResult(resultCode, callback)
        }
    }

    private fun handleGalleryResult(resultCode: Int,
                                    data: Intent?,
                                    callback: Callback
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                try {
                    tempPhotoFile = getNewMediaFile()
                    val uri: Uri = data!!.data!!
                    tempPhotoFile!!.file.writeBytes(context.contentResolver.openInputStream(uri)!!.readBytes())
                    val photoFile: File = getNewPhotoFile()
                    if (compressor == null) {
                        photoFile.writeBytes(tempPhotoFile!!.file.readBytes())
                    } else {
                        compressor.compress(tempPhotoFile!!.file, photoFile)
                    }
                    tempPhotoFile!!.file.delete()
                    callback.onSuccess(photoFile,
                        PickType.Gallery
                    )
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
                    if (tempPhotoFile!!.uri.toString().isEmpty()) {
                        revokeWritePermission(context, tempPhotoFile!!.uri)
                    }
                    val photoFile: File = getNewPhotoFile()
                    if (compressor == null) {
                        photoFile.writeBytes(tempPhotoFile!!.file.readBytes())
                    } else {
                        compressor.compress(tempPhotoFile!!.file, photoFile)
                    }
                    tempPhotoFile!!.file.delete()
                    callback.onSuccess(photoFile,
                        PickType.Camera
                    )
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

    private fun getNewMediaFile(): MediaFile {
        val file = File("${context.cacheDir}/${UUID.randomUUID()}.jpg")
        val uri: Uri = FileProvider.getUriForFile(context, authority, file)
        return MediaFile(uri, file)
    }

    private fun getNewPhotoFile(): File {
        return File("${destinationPath}/${UUID.randomUUID()}.jpg")
    }

    interface Callback {

        fun onCancel(pickType: PickType)
        fun onFailure(e: Exception, pickType: PickType)
        fun onSuccess(file: File, pickType: PickType)

    }

    private fun grantWritePermission(context: Context, intent: Intent, uri: Uri) {
        val resInfoList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun revokeWritePermission(context: Context, uri: Uri) {
        context.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

}

sealed class PickType {
    object Gallery : PickType()
    object Camera : PickType()
}

internal class ImagePickerFileProvider : FileProvider()