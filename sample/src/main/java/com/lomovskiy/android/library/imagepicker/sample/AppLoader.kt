package com.lomovskiy.android.library.imagepicker.sample

import android.app.Application
import android.graphics.Bitmap
import android.os.Environment
import androidx.core.content.ContextCompat
import com.lomovskiy.android.library.imagepicker.Compressor
import com.lomovskiy.android.library.imagepicker.ImagePicker
import java.io.File
import java.util.concurrent.Executors

class AppLoader : Application() {

    companion object {

        lateinit var imagePicker: ImagePicker

    }

    override fun onCreate() {
        super.onCreate()
        imagePicker = ImagePicker(
            context = this,
            destinationPath = getDestinationFolderForImagePicker().absolutePath,
            compressor = Compressor(
                640,
                480,
                50,
                Bitmap.CompressFormat.JPEG
            ),
            workerExecutor = Executors.newSingleThreadExecutor(),
            uiExecutor = ContextCompat.getMainExecutor(this)
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