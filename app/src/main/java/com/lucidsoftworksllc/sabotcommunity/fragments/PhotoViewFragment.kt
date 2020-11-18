package com.lucidsoftworksllc.sabotcommunity.fragments

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.ZoomableImageView
import com.lucidsoftworksllc.sabotcommunity.others.snackbarShort
import com.lucidsoftworksllc.sabotcommunity.others.toastShort
import kotlinx.android.synthetic.main.fragment_photo.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class PhotoViewFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val imageRootView = inflater.inflate(R.layout.fragment_photo, null)
        val imageFromProfile: ZoomableImageView = imageRootView.findViewById(R.id.imageFromProfile)
        val downloadButton: ImageView = imageRootView.findViewById(R.id.downloadBtn)
        val imageToView = requireArguments().getString("image")
        Glide.with(requireActivity())
                .load(Constants.BASE_URL + imageToView)
                .error(R.mipmap.ic_launcher)
                .into(imageFromProfile)

        downloadButton.setOnClickListener {
            println("Attempting to download image... ${Constants.BASE_URL + imageToView}")
            downloadImage(Constants.BASE_URL + imageToView)
        }

        return imageRootView
    }

    private fun downloadImage(imageURL: String) {
        if (!verifyPermissions()) {
            return
        }
        Glide.with(this)
                .load(imageURL)
                .into(object : CustomTarget<Drawable?>() {
                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                    override fun onLoadFailed(@Nullable errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        requireActivity().toastShort(resources.getString(R.string.image_failed_downloading))
                    }
                    override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable?>?) {
                        val bitmap = (resource as BitmapDrawable).bitmap
                        requireView().snackbarShort(resources.getString(R.string.image_saving), "")
                        saveImage(bitmap, getString(R.string.app_name))
                    }
                })
    }

    private fun verifyPermissions(): Boolean {
        // This will return the current Status
        val permissionExternalMemory = ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            val STORAGE_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            // If permission not granted then ask for permission in real time.
            requireView().snackbarShort(resources.getString(R.string.crop_image_activity_no_permissions), "")
            ActivityCompat.requestPermissions(requireActivity(), STORAGE_PERMISSIONS, 1)
            return false
        }
        return true
    }

    private fun saveImage(bitmap: Bitmap, folderName: String) {
        if (Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, requireActivity().contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                requireActivity().contentResolver.update(uri, values, null, null)
                requireView().snackbarShort(resources.getString(R.string.image_saved), "")
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + "/" + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                requireView().snackbarShort(resources.getString(R.string.image_saved), "")
            }
        }
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        //values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis()); <--- App API level too low
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}