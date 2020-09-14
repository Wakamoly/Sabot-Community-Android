package com.lucidsoftworksllc.sabotcommunity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
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
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class ClanAdminPanel : Fragment() {
    private var topPanelLayout: RelativeLayout? = null
    private var clanAdminPanelCenter: RelativeLayout? = null
    private var errorLayout: RelativeLayout? = null
    private var setClanCoverButton: RelativeLayout? = null
    private var setClanPhotoButton: RelativeLayout? = null
    private var manageMembersLayout: MaterialRippleLayout? = null
    private var progressDialog: ProgressDialog? = null
    private var clanAdminPanelProgress: ProgressBar? = null
    private var saveChanges: ImageView? = null
    private var backArrow: ImageView? = null
    private var backgroundPanel: ImageView? = null
    private var insigniaPanel: ImageView? = null
    private val galleryCover = 1
    private val galleryInsignia = 2
    private var newClanCoverBitmap: Bitmap? = null
    private var newClanInsigniabitmap: Bitmap? = null
    var jsonObject: JSONObject? = null
    var rQueue: RequestQueue? = null
    private var editTVclanname: TextView? = null
    private var clanTagView: TextView? = null
    private var editClanname: EditText? = null
    private var descriptionET: EditText? = null
    private var facebookInput: EditText? = null
    private var instaInput: EditText? = null
    private var twitterInput: EditText? = null
    private var youtubeInput: EditText? = null
    private var discordInput: EditText? = null
    private var websiteInput: EditText? = null
    private var mContext: Context? = null
    private var userID: String? = null
    private var username: String? = null
    private var clanID: String? = null
    private var mClanname: String? = null
    private var mClantag: String? = null
    private var manageMembers: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val clanAdminRootView = inflater.inflate(R.layout.fragment_clanadminpanel, null)
        topPanelLayout = clanAdminRootView.findViewById(R.id.topPanelLayout)
        clanAdminPanelCenter = clanAdminRootView.findViewById(R.id.clanAdminPanelCenter)
        clanAdminPanelProgress = clanAdminRootView.findViewById(R.id.clanAdminPanelProgress)
        saveChanges = clanAdminRootView.findViewById(R.id.saveChanges)
        backArrow = clanAdminRootView.findViewById(R.id.backArrow)
        insigniaPanel = clanAdminRootView.findViewById(R.id.insigniaPanel)
        editTVclanname = clanAdminRootView.findViewById(R.id.editTVclanname)
        editClanname = clanAdminRootView.findViewById(R.id.edit_clanname)
        manageMembersLayout = clanAdminRootView.findViewById(R.id.manageMembersLayout)
        errorLayout = clanAdminRootView.findViewById(R.id.errorLayout)
        facebookInput = clanAdminRootView.findViewById(R.id.facebookInput)
        instaInput = clanAdminRootView.findViewById(R.id.instaInput)
        twitterInput = clanAdminRootView.findViewById(R.id.twitterInput)
        youtubeInput = clanAdminRootView.findViewById(R.id.youtubeInput)
        discordInput = clanAdminRootView.findViewById(R.id.discordInput)
        websiteInput = clanAdminRootView.findViewById(R.id.websiteInput)
        descriptionET = clanAdminRootView.findViewById(R.id.descriptionET)
        clanTagView = clanAdminRootView.findViewById(R.id.clanTagView)
        backgroundPanel = clanAdminRootView.findViewById(R.id.backgroundPanel)
        manageMembers = clanAdminRootView.findViewById(R.id.manageMembers)
        setClanCoverButton = clanAdminRootView.findViewById(R.id.setClanCoverButton)
        setClanPhotoButton = clanAdminRootView.findViewById(R.id.setClanPhotoButton)
        progressDialog = ProgressDialog(activity)
        mContext = activity
        userID = SharedPrefManager.getInstance(mContext).userID
        username = SharedPrefManager.getInstance(mContext).username
        clanID = requireArguments().getString("ClanId")
        saveChanges?.setOnClickListener { saveChangesClick() }
        backArrow?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        setClanCoverButton?.setOnClickListener {
            requestMultiplePermissions()
            val galleryIntent = CropImage.activity().getIntent(requireContext())
            startActivityForResult(galleryIntent, galleryCover)
        }
        setClanPhotoButton?.setOnClickListener {
            requestMultiplePermissions()
            val galleryIntent = CropImage.activity().setAspectRatio(1, 1).getIntent(requireContext())
            startActivityForResult(galleryIntent, galleryInsignia)
        }
        loadClanSettings()
        return clanAdminRootView
    }

    private fun loadClanSettings() {
        val stringRequest = StringRequest(Request.Method.GET, "$LOAD_CLAN_SETTINGS?userid=$userID&username=$username&clanid=$clanID", { response: String? ->
            try {
                val profiletop = JSONArray(response)
                val profiletopObject = profiletop.getJSONObject(0)
                val banned = profiletopObject.getString("banned")
                val deleted = profiletopObject.getString("deleted")
                if (banned == "yes" || deleted == "yes") {
                    clanAdminPanelProgress!!.visibility = View.GONE
                    errorLayout!!.visibility = View.VISIBLE
                } else {
                    //String owner = profiletopObject.getString("owner");
                    val id = profiletopObject.getString("id")
                    val clanname = profiletopObject.getString("clanname")
                    val clantag = profiletopObject.getString("clantag")
                    val description = profiletopObject.getString("description")
                    val insignia = profiletopObject.getString("insignia")
                    val background = profiletopObject.getString("background")
                    val facebook = profiletopObject.getString("facebook")
                    val instagram = profiletopObject.getString("instagram")
                    val twitter = profiletopObject.getString("twitter")
                    val youtube = profiletopObject.getString("youtube")
                    val website = profiletopObject.getString("website")
                    val discord = profiletopObject.getString("discord")
                    //String gamesarray = profiletopObject.getString("games");
                    mClanname = clanname
                    mClantag = clantag
                    facebookInput!!.setText(facebook)
                    instaInput!!.setText(instagram)
                    twitterInput!!.setText(twitter)
                    youtubeInput!!.setText(youtube)
                    websiteInput!!.setText(website)
                    descriptionET!!.setText(description)
                    clanTagView!!.text = String.format("[%s]", clantag)
                    discordInput!!.setText(discord)
                    editTVclanname!!.text = clanname
                    editClanname!!.setText(clanname)
                    Glide.with(mContext!!)
                            .load(Constants.BASE_URL + insignia)
                            .error(R.mipmap.ic_launcher)
                            .into(insigniaPanel!!)
                    Glide.with(mContext!!)
                            .load(Constants.BASE_URL + background)
                            .error(R.mipmap.ic_launcher)
                            .into(backgroundPanel!!)
                    clanAdminPanelProgress!!.visibility = View.GONE
                    clanAdminPanelCenter!!.visibility = View.VISIBLE
                    manageMembers!!.setOnClickListener { view: View? ->
                        val ldf = ClanManageMembers()
                        val args = Bundle()
                        args.putString("ClanId", id)
                        args.putString("Clanname", clanname)
                        args.putString("Clantag", clantag)
                        ldf.arguments = args
                        (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) {
            errorLayout!!.visibility = View.VISIBLE
            clanAdminPanelProgress!!.visibility = View.GONE
        }
        Volley.newRequestQueue(mContext).add(stringRequest)
    }

    private fun saveChangesClick() {
        progressDialog!!.setMessage("Saving your settings...")
        progressDialog!!.show()
        val facebook = facebookInput!!.text.toString().trim { it <= ' ' }
        val insta = instaInput!!.text.toString().trim { it <= ' ' }
        val twitter = twitterInput!!.text.toString().trim { it <= ' ' }
        val youtube = youtubeInput!!.text.toString().trim { it <= ' ' }
        val website = websiteInput!!.text.toString().trim { it <= ' ' }
        val description = descriptionET!!.text.toString().trim { it <= ' ' }
        val discord = discordInput!!.text.toString().trim { it <= ' ' }
        val clanname = editClanname!!.text.toString().trim { it <= ' ' }
        val username = SharedPrefManager.getInstance(activity).username
        if (clanname.isNotEmpty() && username.isNotEmpty()) {
            val stringRequest: StringRequest = object : StringRequest(Method.POST,
                    CLAN_SAVE_SETTINGS,
                    Response.Listener { response: String? ->
                        progressDialog!!.dismiss()
                        try {
                            val jsonObject = JSONObject(response!!)
                            Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                            if (jsonObject.getString("error") == "false") {
                                requireActivity().supportFragmentManager.popBackStackImmediate()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { error: VolleyError ->
                        progressDialog!!.hide()
                        Toast.makeText(activity, error.message, Toast.LENGTH_LONG).show()
                    }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["username"] = username
                    params["userid"] = userID!!
                    params["clanid"] = clanID!!
                    params["facebook"] = facebook
                    params["insta"] = insta
                    params["twitter"] = twitter
                    params["youtube"] = youtube
                    params["website"] = website
                    params["description"] = description
                    params["discord"] = discord
                    params["clanname"] = clanname
                    return params
                }
            }
            RequestHandler.getInstance(mContext).addToRequestQueue(stringRequest)
        } else {
            progressDialog!!.dismiss()
            Toast.makeText(mContext, "Please fill in clan name field!", Toast.LENGTH_LONG).show()
        }
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
                    newClanCoverBitmap = bitmap1
                    uploadCoverImage(newClanCoverBitmap, mClanname, mClantag)
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
                    newClanInsigniabitmap = bitmap1
                    uploadInsigniaImage(newClanInsigniabitmap, mClanname, mClantag)
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

    private fun uploadInsigniaImage(bitmap: Bitmap?, clanname: String?, clantag: String?) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 65, byteArrayOutputStream)
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
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, UPLOAD_INSIGNIA_URL, jsonObject,
                { jsonObject: JSONObject ->
                    rQueue!!.cache.clear()
                    try {
                        if (jsonObject.getString("error") == "false") {
                            val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
                            if (currentFragment is ClanAdminPanel) {
                                val fragTransaction = requireActivity().supportFragmentManager.beginTransaction()
                                fragTransaction.detach(currentFragment)
                                fragTransaction.attach(currentFragment)
                                fragTransaction.commit()
                            }
                        } else {
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

    private fun uploadCoverImage(bitmap: Bitmap?, clanname: String?, clantag: String?) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
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
                        if (jsonObject.getString("error") == "false") {
                            val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
                            if (currentFragment is ClanAdminPanel) {
                                val fragTransaction = requireActivity().supportFragmentManager.beginTransaction()
                                fragTransaction.detach(currentFragment)
                                fragTransaction.attach(currentFragment)
                                fragTransaction.commit()
                            }
                        } else {
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
                            Toast.makeText(mContext, "No permissions are granted by user!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    companion object {
        private const val CLAN_SAVE_SETTINGS = Constants.ROOT_URL + "save_clan_settings.php"
        private const val LOAD_CLAN_SETTINGS = Constants.ROOT_URL + "load_clan_settings.php"
        const val UPLOAD_COVER_URL = Constants.ROOT_URL + "uploadCoverClan.php"
        const val UPLOAD_INSIGNIA_URL = Constants.ROOT_URL + "uploadInsigniaClan.php"
    }
}