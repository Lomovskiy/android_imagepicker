package com.lomovskiy.android.library.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

class PhotoFile(val uri: Uri, val file: File)

class ImagePicker(
    private val context: Context,
    private val workerExecutor: Executor,
    private val uiExecutor: Executor,
    private val destinationPath: String,
    private val compressor: ImageCompressor?
) {

    private val rcGallery = ViewCompat.generateViewId()
    private val rcCamera = ViewCompat.generateViewId()
    private val authority: String = "${context.packageName}.imagepicker.fileprovider"

    private val galleryIntent = Intent(Intent.ACTION_PICK).apply {
        type = "image/*"
    }

    private val dateTimeFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    private var tempPhotoFile: PhotoFile? = null

    fun pickFromCamera(caller: Fragment) {
        tempPhotoFile = getNewMediaFile()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoFile!!.uri)
        grantWritePermission(context, intent, tempPhotoFile!!.uri)
        caller.startActivityForResult(intent, rcCamera)
    }

    fun pickFromCamera(caller: Activity) {
        tempPhotoFile = getNewMediaFile()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoFile!!.uri)
        grantWritePermission(caller, intent, tempPhotoFile!!.uri)
        caller.startActivityForResult(intent, rcCamera)
    }

    fun pickFromGallery(caller: Fragment) {
        caller.startActivityForResult(galleryIntent, rcGallery)
    }

    fun pickFromGallery(caller: Activity) {
        caller.startActivityForResult(galleryIntent, rcGallery)
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?, resultTarget: ResultTarget) {
        when (requestCode) {
            rcGallery -> handleGalleryResult(resultCode, data, resultTarget)
            rcCamera -> handleCameraResult(resultCode, resultTarget)
        }
    }

    private fun handleGalleryResult(resultCode: Int,
                                            data: Intent?,
                                            resultTarget: ResultTarget) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                workerExecutor.execute {
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
                        uiExecutor.execute {
                            resultTarget.onPickImageSuccess(photoFile,
                                    PickType.GALLERY
                            )
                        }
                    } catch (e: Exception) {
                        uiExecutor.execute {
                            resultTarget.onPickImageError(e, PickType.GALLERY)
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> resultTarget.onPickImageCancelled(PickType.GALLERY)
        }
    }

    private fun handleCameraResult(resultCode: Int, resultTarget: ResultTarget) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                workerExecutor.execute {
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
                        uiExecutor.execute {
                            resultTarget.onPickImageSuccess(
                                    photoFile,
                                    PickType.CAMERA
                            )
                        }
                    } catch (e: Exception) {
                        uiExecutor.execute {
                            resultTarget.onPickImageError(e, PickType.CAMERA)
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> resultTarget.onPickImageCancelled(PickType.CAMERA)
        }
    }

    private fun getNewMediaFile(): PhotoFile {
        val file = File("${context.cacheDir}/${UUID.randomUUID()}.jpg")
        val uri: Uri = FileProvider.getUriForFile(context, authority, file)
        return PhotoFile(uri, file)
    }

    private fun getNewPhotoFile(): File {
        return File("${destinationPath}/${UUID.randomUUID()}.jpg")
    }

    interface ResultTarget {

        fun onPickImageSuccess(file: File, pickType: PickType) {}

        fun onPickImageError(e: Exception, pickType: PickType) {}

        fun onPickImageCancelled(pickType: PickType) {}

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

internal class ImagePickerFileProvider : FileProvider()