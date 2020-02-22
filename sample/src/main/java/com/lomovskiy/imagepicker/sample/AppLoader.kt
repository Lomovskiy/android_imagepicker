package com.lomovskiy.imagepicker.sample

import android.app.Application
import android.os.Environment
import com.lomovskiy.imagepicker.ImagePicker
import java.io.File

class AppLoader : Application() {

    companion object {

        lateinit var imagePicker: ImagePicker

    }

    override fun onCreate() {
        super.onCreate()
        imagePicker = ImagePicker(
            destinationFolder = getDestinationFolderForImagePicker(),
            compressor = null
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