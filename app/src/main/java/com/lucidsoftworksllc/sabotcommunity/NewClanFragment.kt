package com.lucidsoftworksllc.sabotcommunity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL
import com.theartofdev.edmodo.cropper.CropImage
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class NewClanFragment : Fragment() {
    private var newClanCover: ImageView? = null
    private var newClanInsignia: ImageView? = null
    private var setNewClanCoverButton: RelativeLayout? = null
    private var setInsigniaButton: RelativeLayout? = null
    private var newClanTag: TextView? = null
    private var newClanName: TextView? = null
    private var etNewClanTag: EditText? = null
    private var etNewClanName: EditText? = null
    private var etNewClanDescription: EditText? = null
    private var btnSubmit: Button? = null
    private var mContext: Context? = null
    private var userID: String? = null
    private var username: String? = null
    private var tagTakenString: String? = null
    private var newClanProgressBar: ProgressBar? = null
    private val galleryCover = 1
    private val galleryInsignia = 2
    private var newClanCoverBitmap: Bitmap? = null
    private var newClanInsigniabitmap: Bitmap? = null
    var jsonObject: JSONObject? = null
    var rQueue: RequestQueue? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newClanRootView = inflater.inflate(R.layout.fragment_new_clan, null)
        newClanInsignia = newClanRootView.findViewById(R.id.new_clan_insignia)
        btnSubmit = newClanRootView.findViewById(R.id.btnSubmit)
        etNewClanTag = newClanRootView.findViewById(R.id.etNewClanTag)
        etNewClanName = newClanRootView.findViewById(R.id.etNewClanName)
        etNewClanDescription = newClanRootView.findViewById(R.id.etNewClanDescription)
        newClanTag = newClanRootView.findViewById(R.id.newClanTag)
        newClanName = newClanRootView.findViewById(R.id.newClanName)
        setNewClanCoverButton = newClanRootView.findViewById(R.id.setNewClanCoverButton)
        setInsigniaButton = newClanRootView.findViewById(R.id.setInsigniaButton)
        newClanCover = newClanRootView.findViewById(R.id.newClanCover)
        newClanProgressBar = newClanRootView.findViewById(R.id.newClanProgressBar)
        mContext = activity
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        newClanCoverBitmap = null
        newClanInsigniabitmap = null
        tagTakenString = ""
        btnSubmit?.setOnClickListener {
            if (etNewClanTag?.text.toString().length >= 2) {
                if (etNewClanName?.text.toString().length in 3..25) {
                    if (tagTakenString == "") {
                        newClanProgressBar?.visibility = View.VISIBLE
                        btnSubmit?.visibility = View.GONE
                        submitClan(etNewClanTag?.text.toString(), etNewClanName?.text.toString(), etNewClanDescription?.text.toString())
                    } else {
                        etNewClanTag?.requestFocus()
                        Toast.makeText(mContext, "Clan Tag Taken!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(mContext, "Clan name must be 6-25 characters long", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(mContext, "Clan tag must be at least 2 characters long", Toast.LENGTH_SHORT).show()
            }
        }
        setNewClanCoverButton?.setOnClickListener {
            requestMultiplePermissions()
            val galleryIntent = CropImage.activity().getIntent(requireContext())
            startActivityForResult(galleryIntent, galleryCover)
        }
        setInsigniaButton?.setOnClickListener {
            requestMultiplePermissions()
            val galleryIntent = CropImage.activity().setAspectRatio(1, 1).getIntent(requireContext())
            startActivityForResult(galleryIntent, galleryInsignia)
        }
        etNewClanTag?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val clantag = etNewClanTag?.text.toString()
                newClanTag?.text = String.format("[%s]", clantag)
                val handler = Handler()
                handler.postDelayed({
                    val clantag1 = etNewClanTag?.text.toString()
                    val stringRequest = StringRequest(Request.Method.GET, "$URL_TAG_IN_USE?userid=$userID&username=$username&tag=$clantag1", { response: String? ->
                        try {
                            val tagTakenObject = JSONObject(response!!)
                            if (tagTakenObject.getString("error") == "false") {
                                if (tagTakenObject.getString("result") == "yes") {
                                    etNewClanTag?.setBackgroundResource(R.color.pin)
                                    etNewClanTag?.requestFocus()
                                    Toast.makeText(mContext, "Clan Tag Taken!", Toast.LENGTH_SHORT).show()
                                } else {
                                    tagTakenString = ""
                                    etNewClanTag?.setBackgroundResource(R.color.colorPrimary)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }) { Toast.makeText(mContext, "Network error on Response: Is Tag Taken", Toast.LENGTH_SHORT).show() }
                    (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
                }, 1000)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        etNewClanName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val clanname = etNewClanName?.text.toString()
                newClanName?.text = clanname
            }

            override fun afterTextChanged(s: Editable) {}
        })
        return newClanRootView
    }

    private fun submitClan(tag: String, name: String, desc: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_CLAN_SUBMIT, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    val clanID = jsonObject.getString("clanid")
                    if (newClanInsigniabitmap != null) {
                        uploadInsigniaImage(newClanInsigniabitmap!!, etNewClanName!!.text.toString(), etNewClanTag!!.text.toString(), clanID)
                    }
                    if (newClanCoverBitmap != null) {
                        uploadCoverImage(newClanCoverBitmap!!, etNewClanName!!.text.toString(), etNewClanTag!!.text.toString(), clanID)
                    }
                    val ldf = ClansListFragment()
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).addToBackStack(null).replace(R.id.fragment_container, ldf).commit()
                } else {
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    btnSubmit!!.visibility = View.VISIBLE
                    newClanProgressBar!!.visibility = View.GONE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            newClanProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Error on Submit Clan, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["tag"] = tag
                params["name"] = name
                params["desc"] = desc
                params["username"] = username!!
                params["userid"] = userID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (requestCode == galleryCover) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                var bitmap1: Bitmap? = null
                try {
                    if (Build.VERSION.SDK_INT >= 29) {
                        val source: ImageDecoder.Source = ImageDecoder.createSource(mContext!!.contentResolver, resultUri)
                        try {
                            bitmap1 = ImageDecoder.decodeBitmap(source)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            bitmap1 = MediaStore.Images.Media.getBitmap(mContext!!.contentResolver, resultUri)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    newClanCover!!.setImageBitmap(bitmap1)
                    newClanCoverBitmap = bitmap1
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(activity, "Failed! Error: $error", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == galleryInsignia) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                var bitmap2: Bitmap? = null
                try {
                    if (Build.VERSION.SDK_INT >= 29) {
                        val source: ImageDecoder.Source = ImageDecoder.createSource(mContext!!.contentResolver, resultUri)
                        try {
                            bitmap2 = ImageDecoder.decodeBitmap(source)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            bitmap2 = MediaStore.Images.Media.getBitmap(mContext!!.contentResolver, resultUri)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    newClanInsignia!!.setImageBitmap(bitmap2)
                    newClanInsigniabitmap = bitmap2
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(activity, "Failed! Error: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadInsigniaImage(bitmap: Bitmap, clanname: String, clantag: String, clanID: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 65, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        try {
            jsonObject = JSONObject()
            val imgname = Calendar.getInstance().timeInMillis.toString()
            jsonObject!!.put("name", imgname)
            jsonObject!!.put("clantag", clantag)
            jsonObject!!.put("clanname", clanname)
            jsonObject!!.put("image", encodedImage)
            jsonObject!!.put("user", username)
            jsonObject!!.put("clanid", clanID)
        } catch (e: JSONException) {
            Log.e("JSONObject Here", e.toString())
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, UPLOAD_INSIGNIA_URL, jsonObject,
                { jsonObject: JSONObject ->
                    rQueue!!.cache.clear()
                    try {
                        if (jsonObject.getString("error") == "true") {
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }) { volleyError: VolleyError -> Log.e("UploadCoverFragment", volleyError.toString()) }
        rQueue = Volley.newRequestQueue(mContext)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        rQueue?.add(jsonObjectRequest)
    }

    private fun uploadCoverImage(bitmap: Bitmap, clanname: String, clantag: String, clanID: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        try {
            jsonObject = JSONObject()
            val imgname = Calendar.getInstance().timeInMillis.toString()
            jsonObject!!.put("name", imgname)
            jsonObject!!.put("clantag", clantag)
            jsonObject!!.put("clanname", clanname)
            jsonObject!!.put("image", encodedImage)
            jsonObject!!.put("owner", username)
            jsonObject!!.put("clanid", clanID)
        } catch (e: JSONException) {
            Log.e("JSONObject Here", e.toString())
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, UPLOAD_COVER_URL, jsonObject,
                { jsonObject: JSONObject ->
                    rQueue!!.cache.clear()
                    try {
                        if (jsonObject.getString("error") == "true") {
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }) { volleyError: VolleyError -> Log.e("UploadCoverFragment", volleyError.toString()) }
        rQueue = Volley.newRequestQueue(mContext)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
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
                            Toast.makeText(mContext!!.applicationContext, "No permissions are granted by user!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { Toast.makeText(mContext!!.applicationContext, "Error!", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    companion object {
        val URL_TAG_IN_USE: String = ROOT_URL + "clan_tag_used.php"
        const val UPLOAD_COVER_URL = ROOT_URL + "uploadCoverClan.php"
        const val UPLOAD_INSIGNIA_URL = ROOT_URL + "uploadInsigniaClan.php"
        const val URL_CLAN_SUBMIT = ROOT_URL + "submit_new_clan.php"
    }
}