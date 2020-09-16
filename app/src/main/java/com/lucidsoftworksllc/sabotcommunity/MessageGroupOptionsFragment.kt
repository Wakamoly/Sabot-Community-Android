package com.lucidsoftworksllc.sabotcommunity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class MessageGroupOptionsFragment : Fragment() {
    private var newMessageInsignia: ImageView? = null
    private var groupOptionsLayout: LinearLayout? = null
    private var messageName: TextView? = null
    private var setInsigniaButton: RelativeLayout? = null
    private var etNewGroupMessageName: EditText? = null
    private var allUsersCanInvite: CheckBox? = null
    private var adminsCanInvite: CheckBox? = null
    private var allUsersCanChangeGroupImage: CheckBox? = null
    private var adminsCanChangeGroupImage: CheckBox? = null
    private var allUsersCanChangeGroupName: CheckBox? = null
    private var adminsCanChangeGroupName: CheckBox? = null
    private var allUsersCanMessage: CheckBox? = null
    private var adminsCanMessage: CheckBox? = null
    private var adminsCanRemoveUsers: CheckBox? = null
    private var btnSubmit: Button? = null
    private var submitGroupOptionsProg: ProgressBar? = null
    private var groupOptionsLoading: ProgressBar? = null
    private var mContext: Context? = null
    private val galleryInsignia = 2
    private var newGroupInsigniabitmap: Bitmap? = null
    var jsonObject: JSONObject? = null
    var rQueue: RequestQueue? = null
    private var deviceUserID: String? = null
    private var deviceUsername: String? = null
    private var canChangeGroupImage: String? = null
    private var canChangeGroupName: String? = null
    private var canChangeOptions: String? = null
    private var group_id: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val groupOptionsRootView = inflater.inflate(R.layout.fragment_group_message_options, null)
        newMessageInsignia = groupOptionsRootView.findViewById(R.id.new_message_insignia)
        setInsigniaButton = groupOptionsRootView.findViewById(R.id.setInsigniaButton)
        etNewGroupMessageName = groupOptionsRootView.findViewById(R.id.etNewGroupMessageName)
        allUsersCanInvite = groupOptionsRootView.findViewById(R.id.allUsersCanInvite)
        adminsCanInvite = groupOptionsRootView.findViewById(R.id.adminsCanInvite)
        allUsersCanChangeGroupImage = groupOptionsRootView.findViewById(R.id.allUsersCanChangeGroupImage)
        adminsCanChangeGroupImage = groupOptionsRootView.findViewById(R.id.adminsCanChangeGroupImage)
        allUsersCanChangeGroupName = groupOptionsRootView.findViewById(R.id.allUsersCanChangeGroupName)
        adminsCanChangeGroupName = groupOptionsRootView.findViewById(R.id.adminsCanChangeGroupName)
        allUsersCanMessage = groupOptionsRootView.findViewById(R.id.allUsersCanMessage)
        adminsCanMessage = groupOptionsRootView.findViewById(R.id.adminsCanMessage)
        adminsCanRemoveUsers = groupOptionsRootView.findViewById(R.id.adminsCanRemoveUsers)
        btnSubmit = groupOptionsRootView.findViewById(R.id.btnSubmit)
        submitGroupOptionsProg = groupOptionsRootView.findViewById(R.id.submitGroupOptionsProg)
        groupOptionsLoading = groupOptionsRootView.findViewById(R.id.groupOptionsLoading)
        groupOptionsLayout = groupOptionsRootView.findViewById(R.id.groupOptionsLayout)
        messageName = groupOptionsRootView.findViewById(R.id.messageName)
        mContext = activity
        if (arguments != null) {
            canChangeGroupImage = requireArguments().getString("canChangeGroupImage")
            canChangeGroupName = requireArguments().getString("canChangeGroupName")
            canChangeOptions = requireArguments().getString("canChangeOptions")
            group_id = requireArguments().getString("group_id")
            deviceUserID = SharedPrefManager.getInstance(mContext!!)!!.userID
            deviceUsername = SharedPrefManager.getInstance(mContext!!)!!.username
        }
        groupOptionsInfo
        return groupOptionsRootView
    }

    private val groupOptionsInfo: Unit
        get() {
            val stringRequest = StringRequest(Request.Method.GET, "$URL_LOAD_GROUP_OPTIONS?group_id=$group_id&deviceuser=$deviceUsername&deviceuserid=$deviceUserID", { response: String? ->
                try {
                    val jsonObject = JSONObject(response!!)
                    if (jsonObject.getString("error") == "false") {
                        val thread = jsonObject.getJSONArray("messages")
                        val messageObject = thread.getJSONObject(0)
                        val name = messageObject.getString("name")
                        val image = messageObject.getString("image")
                        val options = messageObject.getString("options")
                        etNewGroupMessageName!!.setText(name)
                        messageName!!.text = name
                        val optionsSplit = options.split(", ".toRegex()).toTypedArray()
                        for (option in optionsSplit) {
                            when (option) {
                                "allUsersCanChangeGroupImage" -> allUsersCanChangeGroupImage!!.isChecked = true
                                "allUsersCanChangeGroupName" -> allUsersCanChangeGroupName!!.isChecked = true
                                "allUsersCanInvite" -> allUsersCanInvite!!.isChecked = true
                                "allUsersCanMessage" -> allUsersCanMessage!!.isChecked = true
                                "adminsCanChangeGroupImage" -> adminsCanChangeGroupImage!!.isChecked = true
                                "adminsCanChangeGroupName" -> adminsCanChangeGroupName!!.isChecked = true
                                "adminsCanInvite" -> adminsCanInvite!!.isChecked = true
                                "adminsCanMessage" -> adminsCanMessage!!.isChecked = true
                                "adminsCanRemoveUsers" -> adminsCanRemoveUsers!!.isChecked = true
                            }
                        }
                        if (canChangeGroupImage == "yes" || canChangeGroupName == "yes" || canChangeOptions == "yes") {
                            btnSubmit!!.visibility = View.VISIBLE
                            if (canChangeGroupName == "yes" && canChangeOptions == "yes") {
                                btnSubmit!!.setOnClickListener {
                                    if (etNewGroupMessageName!!.text.toString().length in 3..25) {
                                        submitGroupOptionsProg!!.visibility = View.VISIBLE
                                        btnSubmit!!.visibility = View.GONE
                                        val optionArray = groupOptions
                                        updateGroupMessage(etNewGroupMessageName!!.text.toString(), optionArray)
                                    } else {
                                        Toast.makeText(mContext, "Group name must be 3-25 characters long", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else if (canChangeGroupName == "yes") {
                                btnSubmit!!.setOnClickListener {
                                    submitGroupOptionsProg!!.visibility = View.VISIBLE
                                    btnSubmit!!.visibility = View.GONE
                                    updateGroupMessage(etNewGroupMessageName!!.text.toString(), "")
                                }
                            } else if (canChangeGroupImage == "yes") {
                                btnSubmit!!.setOnClickListener {
                                    submitGroupOptionsProg!!.visibility = View.VISIBLE
                                    btnSubmit!!.visibility = View.GONE
                                    uploadInsigniaImage(newGroupInsigniabitmap, group_id, messageName!!.text.toString())
                                }
                            }
                        }
                        if (canChangeOptions == "yes") {
                            allUsersCanChangeGroupImage!!.isEnabled = true
                            allUsersCanChangeGroupName!!.isEnabled = true
                            allUsersCanInvite!!.isEnabled = true
                            allUsersCanMessage!!.isEnabled = true
                            adminsCanChangeGroupImage!!.isEnabled = true
                            adminsCanChangeGroupName!!.isEnabled = true
                            adminsCanInvite!!.isEnabled = true
                            adminsCanMessage!!.isEnabled = true
                            adminsCanRemoveUsers!!.isEnabled = true
                            allUsersCanInvite!!.setOnClickListener {
                                if (allUsersCanInvite!!.isChecked) {
                                    adminsCanInvite!!.isChecked = false
                                    adminsCanInvite!!.visibility = View.GONE
                                } else {
                                    adminsCanInvite!!.visibility = View.VISIBLE
                                }
                            }
                            allUsersCanChangeGroupImage!!.setOnClickListener {
                                if (allUsersCanChangeGroupImage!!.isChecked) {
                                    adminsCanChangeGroupImage!!.isChecked = false
                                    adminsCanChangeGroupImage!!.visibility = View.GONE
                                } else {
                                    adminsCanChangeGroupImage!!.visibility = View.VISIBLE
                                }
                            }
                            allUsersCanChangeGroupName!!.setOnClickListener {
                                if (allUsersCanChangeGroupName!!.isChecked) {
                                    adminsCanChangeGroupName!!.isChecked = false
                                    adminsCanChangeGroupName!!.visibility = View.GONE
                                } else {
                                    adminsCanChangeGroupName!!.visibility = View.VISIBLE
                                }
                            }
                            allUsersCanMessage!!.setOnClickListener {
                                if (allUsersCanMessage!!.isChecked) {
                                    adminsCanMessage!!.isChecked = false
                                    adminsCanMessage!!.visibility = View.GONE
                                } else {
                                    adminsCanMessage!!.visibility = View.VISIBLE
                                }
                            }
                        }
                        if (canChangeGroupName == "yes") {
                            etNewGroupMessageName!!.visibility = View.VISIBLE
                        }
                        if (canChangeGroupImage == "yes") {
                            setInsigniaButton!!.visibility = View.VISIBLE
                            setInsigniaButton!!.setOnClickListener {
                                requestMultiplePermissions()
                                val galleryIntent = Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                startActivityForResult(galleryIntent, galleryInsignia)
                            }
                        }
                        Glide.with(mContext!!)
                                .load(Constants.BASE_URL + image)
                                .into(newMessageInsignia!!)
                        groupOptionsLoading!!.visibility = View.GONE
                        groupOptionsLayout!!.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show()
                        btnSubmit!!.visibility = View.GONE
                        submitGroupOptionsProg!!.visibility = View.GONE
                        groupOptionsLoading!!.visibility = View.GONE
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show()
                    btnSubmit!!.visibility = View.GONE
                    submitGroupOptionsProg!!.visibility = View.GONE
                    groupOptionsLoading!!.visibility = View.GONE
                }
            }) {
                Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show()
                btnSubmit!!.visibility = View.GONE
                submitGroupOptionsProg!!.visibility = View.GONE
                groupOptionsLoading!!.visibility = View.GONE
            }
            (mContext as ChatActivity?)!!.addToRequestQueue(stringRequest)
        }
    private val groupOptions: String
        get() {
            val optionsList = ArrayList<String>()
            if (allUsersCanChangeGroupImage!!.isChecked) {
                optionsList.add("allUsersCanChangeGroupImage")
            }
            if (allUsersCanChangeGroupName!!.isChecked) {
                optionsList.add("allUsersCanChangeGroupName")
            }
            if (allUsersCanInvite!!.isChecked) {
                optionsList.add("allUsersCanInvite")
            }
            if (allUsersCanMessage!!.isChecked) {
                optionsList.add("allUsersCanMessage")
            }
            if (adminsCanChangeGroupImage!!.isChecked) {
                optionsList.add("adminsCanChangeGroupImage")
            }
            if (adminsCanChangeGroupName!!.isChecked) {
                optionsList.add("adminsCanChangeGroupName")
            }
            if (adminsCanInvite!!.isChecked) {
                optionsList.add("adminsCanInvite")
            }
            if (adminsCanMessage!!.isChecked) {
                optionsList.add("adminsCanMessage")
            }
            if (adminsCanRemoveUsers!!.isChecked) {
                optionsList.add("adminsCanRemoveUsers")
            }
            return optionsList.toString()
        }

    private fun updateGroupMessage(groupMessageName: String, optionArray: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_GROUP_OPTIONS_SUBMIT, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    if (newGroupInsigniabitmap != null) {
                        uploadInsigniaImage(newGroupInsigniabitmap, group_id, etNewGroupMessageName!!.text.toString())
                    }
                    val ldf = ConvosFragment()
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
                } else {
                    Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show()
                    btnSubmit!!.visibility = View.VISIBLE
                    submitGroupOptionsProg!!.visibility = View.GONE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            submitGroupOptionsProg!!.visibility = View.GONE
            Toast.makeText(mContext, "Network Error!", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["group_name"] = groupMessageName
                params["option_array"] = optionArray
                params["group_id"] = group_id!!
                params["username"] = SharedPrefManager.getInstance(mContext!!)!!.username!!
                params["userid"] = SharedPrefManager.getInstance(mContext!!)!!.userID!!
                return params
            }
        }
        (mContext as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (requestCode == galleryInsignia) {
            if (data != null) {
                val contentURI = data.data
                var bitmap1: Bitmap? = null
                try {
                    if (Build.VERSION.SDK_INT >= 29) {
                        val source: ImageDecoder.Source = ImageDecoder.createSource(mContext!!.contentResolver, contentURI!!)
                        try {
                            bitmap1 = ImageDecoder.decodeBitmap(source)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            bitmap1 = MediaStore.Images.Media.getBitmap(mContext!!.contentResolver, contentURI)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    newMessageInsignia!!.setImageBitmap(bitmap1)
                    newGroupInsigniabitmap = bitmap1
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadInsigniaImage(bitmap: Bitmap?, group_id: String?, groupname: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        try {
            jsonObject = JSONObject()
            val imgname = Calendar.getInstance().timeInMillis.toString()
            jsonObject!!.put("name", imgname)
            jsonObject!!.put("group_id", group_id)
            jsonObject!!.put("group_name", groupname)
            jsonObject!!.put("image", encodedImage)
            jsonObject!!.put("username", SharedPrefManager.getInstance(mContext!!)!!.username)
        } catch (e: JSONException) {
            e.printStackTrace()
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
                }) { }
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
        const val URL_LOAD_GROUP_OPTIONS = Constants.ROOT_URL + "messages.php/loadgroupoptions"
        const val URL_GROUP_OPTIONS_SUBMIT = Constants.ROOT_URL + "messages.php/updategroupoptions"
        const val UPLOAD_INSIGNIA_URL = Constants.ROOT_URL + "uploadInsigniaGroupMessage.php"
    }
}