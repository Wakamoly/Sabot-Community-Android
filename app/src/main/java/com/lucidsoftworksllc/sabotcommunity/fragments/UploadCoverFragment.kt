package com.lucidsoftworksllc.sabotcommunity.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lucidsoftworksllc.sabotcommunity.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.SharedPrefManager.Companion.getInstance
import com.theartofdev.edmodo.cropper.CropImage
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class UploadCoverFragment : Fragment() {
    private var buttonUpload: Button? = null
    private var imageView: ImageView? = null
    private var uploadProgress: LinearLayout? = null
    private var jsonObject: JSONObject? = null
    private var rQueue: RequestQueue? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val uploadCoverView = inflater.inflate(R.layout.fragment_upload_cover, container, false)
        requestMultiplePermissions()
        val buttonChoose = uploadCoverView.findViewById<Button>(R.id.btnChoose)
        uploadProgress = uploadCoverView.findViewById(R.id.uploadProgress)
        buttonUpload = uploadCoverView.findViewById(R.id.btnUpload)
        imageView = uploadCoverView.findViewById(R.id.imageView)
        buttonChoose.setOnClickListener { openCropper() }
        openCropper()
        return uploadCoverView
    }

    private fun openCropper() {
        requestMultiplePermissions()
        CropImage.activity()
                .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                var bitmap1: Bitmap? = null
                try {
                    if (Build.VERSION.SDK_INT >= 29) {
                        val source: ImageDecoder.Source = ImageDecoder.createSource(requireActivity().contentResolver, resultUri)
                        try {
                            bitmap1 = ImageDecoder.decodeBitmap(source)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            bitmap1 = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, resultUri)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    imageView!!.setImageBitmap(bitmap1)
                    buttonUpload!!.visibility = View.VISIBLE
                    buttonUpload!!.setOnClickListener {
                        uploadImage(bitmap1!!)
                        uploadProgress!!.visibility = View.VISIBLE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(activity, "Failed! Error: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        try {
            jsonObject = JSONObject()
            val imgname = Calendar.getInstance().timeInMillis.toString()
            val userID = getInstance(requireActivity())!!.userID
            val username = getInstance(requireActivity())!!.username
            jsonObject!!.put("name", imgname)
            jsonObject!!.put("userid", userID)
            jsonObject!!.put("username", username)
            jsonObject!!.put("image", encodedImage)
        } catch (e: JSONException) {
            Log.e("JSONObject Here", e.toString())
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, UPLOAD_URL, jsonObject,
                {
                    rQueue!!.cache.clear()
                    val asf: Fragment = FragmentProfile()
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.commit()
                    Toast.makeText(requireActivity(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                }) { volleyError: VolleyError -> Log.e("UploadCoverFragment", volleyError.toString()) }
        rQueue = Volley.newRequestQueue(requireActivity())
        rQueue?.add(jsonObjectRequest)
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(requireActivity(), "No permissions are granted by user!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { Toast.makeText(requireActivity(), "Some Error! ", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    companion object {
        const val UPLOAD_URL = Constants.ROOT_URL + "uploadCoverProfile.php"
    }
}