package com.lomovskiy.imagepicker.sample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lomovskiy.imagepicker.ImagePicker
import com.lomovskiy.imagepicker.PickType
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener, ImagePicker.Callback {

    private val imagePicker: ImagePicker = AppLoader.imagePicker

    private lateinit var pickFromGalleryButton: Button
    private lateinit var pickFromCameraButton: Button
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pickFromGalleryButton = findViewById(R.id.pick_from_gallery_button)
        pickFromCameraButton = findViewById(R.id.pick_from_camera_button)
        imageView = findViewById(R.id.image_view)
        pickFromGalleryButton.setOnClickListener(this)
        pickFromCameraButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.pick_from_gallery_button -> {
                imagePicker.pickFromGallery(this)
            }
            R.id.pick_from_camera_button -> {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagePicker.handleOnActivityResult(requestCode, resultCode, data, this, this)
    }

    override fun onCancel(pickType: PickType) {
        showToast("onCancel ${pickType.name}")
    }

    override fun onFailure(e: Exception, pickType: PickType) {
        showToast("onFailure: $e, ${pickType.name}")
    }

    override fun onSuccess(file: File, pickType: PickType) {
        when (pickType) {
            PickType.GALLERY -> {
                showToast("onSuccess: ${pickType.name}")
                imageView.setImageURI(Uri.fromFile(file))
            }
            PickType.CAMERA -> {
                showToast("onSuccess: ${pickType.name}")
                imageView.setImageURI(Uri.fromFile(file))
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
