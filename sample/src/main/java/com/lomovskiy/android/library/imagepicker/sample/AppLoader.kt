package com.lomovskiy.android.library.imagepicker.sample

import android.app.Application
import android.graphics.Bitmap
import android.os.Environment
import com.lomovskiy.android.library.imagepicker.ImageCompressor
import com.lomovskiy.android.library.imagepicker.ImagePicker
import java.io.File

class AppLoader : Application() {

    companion object {

        lateinit var imagePicker: ImagePicker

    }

    override fun onCreate() {
        super.onCreate()
        imagePicker = ImagePicker(
            context = this,
            destinationPath = getDestinationFolderForImagePicker().absolutePath,
            compressor = ImageCompressor(
                640,
                480,
                50,
                Bitmap.CompressFormat.JPEG
            )
        )
    }

    private fun getDestinationFolderForImagePicker(): File {
        if (isExternalMediaMounted()) {
            return getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        } else {
            return filesDir
        }
    }

    private fun isExternalMediaMounted(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

}