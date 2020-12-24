package com.lomovskiy.android.library.imagepicker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.File

class ImageCompressor(
    private val requiredWidth: Int,
    private val requiredHeight: Int,
    private val requiredQuality: Int,
    private val compressFormat: Bitmap.CompressFormat
) {

    fun compress(photoFile: File, destinationFile: File) {
        val fos = destinationFile.outputStream()
        try {
            decodeScaledBitmapFromFile(photoFile, requiredHeight, requiredWidth)
                .compress(compressFormat, requiredQuality, fos)
        } finally {
            fos.flush()
            fos.close()
        }
    }

    private fun decodeScaledBitmapFromFile(photoFile: File, requiredHeight: Int, requiredWidth: Int): Bitmap {
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(photoFile.absolutePath, options)
        options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight)
        options.inJustDecodeBounds = false
        var scaledBitmap: Bitmap = BitmapFactory.decodeFile(photoFile.absolutePath, options)
        val exifInterface = ExifInterface(photoFile.absolutePath)
        val orientation: Int = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        val matrix = Matrix()
        when (orientation) {
            6 -> matrix.postRotate(90F)
            3 -> matrix.postRotate(180F)
            8 -> matrix.postRotate(270F)
        }
        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        return scaledBitmap
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options,
                                      requiredWidth: Int,
                                      requiredHeight: Int): Int {
        val bitmapHeight: Int = options.outHeight
        val bitmapWidth: Int = options.outWidth
        var inSampleSize = 1
        if (bitmapHeight > requiredHeight || bitmapWidth > requiredWidth) {
            val halfHeight: Int = bitmapHeight / 2
            val halfWidth: Int = bitmapWidth / 2
            while ((halfHeight / inSampleSize) >= requiredHeight && (halfWidth / inSampleSize) >= requiredWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}