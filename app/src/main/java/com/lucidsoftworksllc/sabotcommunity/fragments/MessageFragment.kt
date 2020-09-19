package com.lucidsoftworksllc.sabotcommunity.fragments

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.MessagesThreadAdapter
import com.lucidsoftworksllc.sabotcommunity.models.MessagesHelper
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class MessageFragment : Fragment() {
    private var mCtx: Context? = null
    private var thisUserID: String? = null
    private var thisUsername: String? = null
    private var userTo: String? = null
    private val profilePic: String? = null
    private val nickname: String? = null
    private var imageUploaded: String? = null
    private var lastId: String? = null
    private var profileMessageToName: TextView? = null
    private var lastOnline: TextView? = null
    private var sendButton: ImageView? = null
    private var imgAttachment: ImageView? = null
    private var userMessageMenu: ImageView? = null
    private var backMessageButton: ImageView? = null
    private var profileMessageToImage: CircleImageView? = null
    private var verifiedView: CircleImageView? = null
    private var onlineView: CircleImageView? = null
    private var messageEditText: EditText? = null
    private var cannotRespondLayout: LinearLayout? = null
    private var usernameLayout: LinearLayout? = null
    private var layoutManager: LinearLayoutManager? = null
    private var messages: ArrayList<MessagesHelper>? = null
    private var messagesRecyclerView: RecyclerView? = null
    private var adapter: MessagesThreadAdapter? = null
    private var messageProgress: ProgressBar? = null
    private var sendProgress: ProgressBar? = null
    private var imageToUpload: Bitmap? = null
    var rQueue: RequestQueue? = null
    var jsonObject: JSONObject? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val messageRootView = inflater.inflate(R.layout.content_chat, null)
        messageEditText = messageRootView.findViewById(R.id.et_message)
        onlineView = messageRootView.findViewById(R.id.online)
        verifiedView = messageRootView.findViewById(R.id.verified)
        messageProgress = messageRootView.findViewById(R.id.messageProgress)
        sendButton = messageRootView.findViewById(R.id.img_send)
        imgAttachment = messageRootView.findViewById(R.id.img_attachment)
        profileMessageToName = messageRootView.findViewById(R.id.profileMessageToName)
        profileMessageToImage = messageRootView.findViewById(R.id.profileMessageToImage)
        userMessageMenu = messageRootView.findViewById(R.id.userMessageMenu)
        backMessageButton = messageRootView.findViewById(R.id.backMessageButton)
        sendProgress = messageRootView.findViewById(R.id.sendProgress)
        cannotRespondLayout = messageRootView.findViewById(R.id.cannotRespondLayout)
        usernameLayout = messageRootView.findViewById(R.id.usernameLayout)
        lastOnline = messageRootView.findViewById(R.id.lastOnline)
        imageUploaded = ""
        mCtx = activity
        messagesRecyclerView = messageRootView.findViewById(R.id.recycler_chat_list)
        messagesRecyclerView?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mCtx)
        //layoutManager.setReverseLayout(true); FIX THIS
        //layoutManager.setStackFromEnd(true);  AND THIS TOO
        messagesRecyclerView?.layoutManager = layoutManager
        userTo = requireArguments().getString("user_to")
        thisUserID = SharedPrefManager.getInstance(mCtx!!)!!.userID
        thisUsername = SharedPrefManager.getInstance(mCtx!!)!!.username
        messages = ArrayList()
        backMessageButton?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        imgAttachment?.setOnClickListener {
            requestMultiplePermissions()
            openCropper()
        }
        loadUserInfo(userTo)
        getMessages()
        return messageRootView
    }

    private fun openCropper() {
        CropImage.activity()
                .start(requireContext(), this)
    }

    private fun getMessages() {
        val stringRequest = StringRequest(Request.Method.GET, "$URL_FETCH_MESSAGES?this_user=$thisUsername&username=$userTo&userid=$thisUserID",
                { response: String? ->
                    try {
                        val res = JSONObject(response!!)
                        val thread = res.getJSONArray("messages")
                        for (i in 0 until thread.length()) {
                            val obj = thread.getJSONObject(i)
                            val id = obj.getString("id")
                            val userTo1 = obj.getString("user_to")
                            val userFrom = obj.getString("user_from")
                            val body = obj.getString("body")
                            val date = obj.getString("date")
                            val image = obj.getString("image")
                            val messageObject = MessagesHelper(id, userTo1, userFrom, body, date, image)
                            messages!!.add(messageObject)
                            lastId = id
                        }
                        newChats()
                        messageProgress!!.visibility = View.GONE
                        messagesRecyclerView!!.visibility = View.VISIBLE
                        adapter = MessagesThreadAdapter(mCtx!!, messages!!, thisUsername!!)
                        messagesRecyclerView!!.adapter = adapter
                        scrollToBottomNow()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        ) { }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    //create thread
    // start thread
    private val messagesFromID: Unit
        get() {
            val messagesIDThread: Thread = object : Thread() {
                //create thread
                override fun run() {
                    val stringRequest = StringRequest(Request.Method.GET, "$URL_FETCH_MORE_MESSAGES?this_user=$thisUsername&username=$userTo&userid=$thisUserID&last_id=$lastId",
                            { response: String? ->
                                try {
                                    val res = JSONObject(response!!)
                                    val lastOnline1 = res.getString("userLastOnline")
                                    if (lastOnline1 != "null") {
                                        lastOnline!!.text = lastOnline1
                                        if (lastOnline1 == "Online now") {
                                            onlineView!!.visibility = View.VISIBLE
                                        } else {
                                            onlineView!!.visibility = View.GONE
                                        }
                                    }
                                    val thread = res.getJSONArray("messages")
                                    for (i in 0 until thread.length()) {
                                        val obj = thread.getJSONObject(i)
                                        val id = obj.getString("id")
                                        val userFrom = obj.getString("user_from")
                                        val body = obj.getString("body")
                                        val image = obj.getString("image")
                                        processMessage(userFrom, body, image)
                                        lastId = id
                                    }
                                    newChats()
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                    ) { }
                    (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
                }
            }
            messagesIDThread.start() // start thread
        }

    private fun processMessage(user_string: String, message: String, image: String) {
        val m = MessagesHelper(null.toString(), thisUsername!!, user_string, message, "Just now", image)
        messages!!.add(m)
        scrollToBottom()
    }

    private fun sendMessage(user_string: String, message: String) {
        sendButton!!.visibility = View.GONE
        sendProgress!!.visibility = View.VISIBLE
        if (message.equals("", ignoreCase = true)) return
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_SEND_MESSAGE, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    if (imageToUpload != null) {
                        val messageId = jsonObject.getString("messageid")
                        messageImageUpload(imageToUpload!!, messageId, thisUsername, user_string, message)
                    } else {
                        val m = MessagesHelper(null.toString(), user_string, thisUsername!!, message, "Just now", "")
                        messages!!.add(m)
                        adapter!!.notifyDataSetChanged()
                        scrollToBottom()
                        sendButton!!.visibility = View.VISIBLE
                        sendProgress!!.visibility = View.GONE
                    }
                    messageEditText!!.setText("")
                } else {
                    Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    sendButton!!.visibility = View.VISIBLE
                    sendProgress!!.visibility = View.GONE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
                Response.ErrorListener { }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user_to"] = user_string
                params["message"] = message
                params["user_from"] = thisUsername!!
                params["user_id"] = thisUserID!!
                return params
            }
        }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadUserInfo(user_to: String?) {
        val stringRequest = StringRequest(Request.Method.GET, "$URL_USER_INFO?username=$user_to&deviceuser=$thisUsername&deviceuserid=$thisUserID", { response: String? ->
            try {
                val res = JSONObject(response!!)
                val thread = res.getJSONArray("messages")
                val messageObject = thread.getJSONObject(0)
                val profilePic1 = messageObject.getString("profile_pic")
                val nickname = messageObject.getString("nickname")
                val verified = messageObject.getString("verified")
                val lastOnline1 = messageObject.getString("last_online")
                val lastOnlineText = messageObject.getString("last_online_text")
                val blockedArray1 = messageObject.getString("blocked_array")
                val userToId = messageObject.getString("user_id")
                lastOnline!!.text = lastOnlineText
                var blocked = ""
                val blockedArray = blockedArray1.split(",".toRegex()).toTypedArray()
                for (usernameBlocked in blockedArray) {
                    if (usernameBlocked == thisUsername && usernameBlocked != "") {
                        blocked = "yes"
                        break
                    }
                }
                if (blocked != "yes" && !SharedPrefManager.getInstance(mCtx!!)!!.isUserBlocked(user_to!!)) {
                    sendButton!!.setOnClickListener { view: View ->
                        val body = messageEditText!!.text.toString().trim { it <= ' ' }
                        if (messageEditText!!.text.toString().isNotEmpty() || imageUploaded != null && imageUploaded!!.isNotEmpty()) {
                            sendMessage(user_to, body)
                            hideKeyboardFrom(mCtx, view)
                        } else {
                            Toast.makeText(mCtx, "You must enter text before submitting!", Toast.LENGTH_LONG).show()
                        }
                    }
                    usernameLayout!!.setOnClickListener { startActivity(Intent(mCtx, FragmentContainer::class.java).putExtra("user_to_id", userToId)) }
                    if (lastOnline1 == "yes") {
                        onlineView!!.visibility = View.VISIBLE
                    }
                    if (verified == "yes") {
                        verifiedView!!.visibility = View.VISIBLE
                    }
                    userMessageMenu!!.setOnClickListener { view: View? ->
                        val popup = PopupMenu(mCtx, view)
                        val inflater = popup.menuInflater
                        inflater.inflate(R.menu.message_top_menu, popup.menu)
                        popup.setOnMenuItemClickListener { item: MenuItem ->
                            if (item.itemId == R.id.menuPlayerReport) {
                                val ldf = ReportFragment()
                                val args = Bundle()
                                args.putString("context", "message")
                                args.putString("type", "user")
                                args.putString("id", userToId)
                                ldf.arguments = args
                                (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.chat_fragment_container, ldf).addToBackStack(null).commit()
                            }
                            if (item.itemId == R.id.menuPlayerBlock) {
                                SharedPrefManager.getInstance(mCtx!!)!!.blockUser(user_to)
                                val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
                                if (currentFragment is MessageFragment) {
                                    val fragTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                    fragTransaction.detach(currentFragment)
                                    fragTransaction.attach(currentFragment)
                                    fragTransaction.commit()
                                }
                            }
                            true
                        }
                        popup.show()
                    }
                } else {
                    cannotRespondLayout!!.visibility = View.VISIBLE
                    userMessageMenu!!.visibility = View.GONE
                }
                profileMessageToName!!.text = nickname
                val profilePic2 = profilePic1.substring(0, profilePic1.length - 4) + "_r.JPG"
                Glide.with(mCtx!!)
                        .load(Constants.BASE_URL + profilePic2)
                        .into(profileMessageToImage!!)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    fun scrollToBottom() {
        adapter!!.notifyDataSetChanged()
        if (adapter!!.itemCount > 1) Objects.requireNonNull(messagesRecyclerView!!.layoutManager!!).smoothScrollToPosition(messagesRecyclerView, null, adapter!!.itemCount - 1)
    }

    private fun scrollToBottomNow() {
        adapter!!.notifyDataSetChanged()
        if (adapter!!.itemCount > 1) Objects.requireNonNull(messagesRecyclerView!!.layoutManager!!).scrollToPosition(adapter!!.itemCount - 1)
    }

    private fun messageImageUpload(bitmap: Bitmap, message_id: String, username: String?, user_string: String, message: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        try {
            jsonObject = JSONObject()
            val imgname = Calendar.getInstance().timeInMillis.toString()
            jsonObject!!.put("name", imgname)
            jsonObject!!.put("message_id", message_id)
            jsonObject!!.put("image", encodedImage)
            jsonObject!!.put("user_from", username)
        } catch (e: JSONException) {
            Log.e("JSONObject Here", e.toString())
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, UPLOAD_IMAGE_URL, jsonObject,
                { jsonObject: JSONObject ->
                    rQueue!!.cache.clear()
                    try {
                        if (jsonObject.getString("error") != "true") {
                            imgAttachment!!.setImageResource(R.drawable.ic_attach_file_grey_24dp)
                            imageToUpload = null
                            imageUploaded = jsonObject.getString("imagepath")
                            val m = MessagesHelper(null.toString(), user_string, thisUsername!!, message, "Just now", imageUploaded!!)
                            messages!!.add(m)
                            adapter!!.notifyDataSetChanged()
                            scrollToBottom()
                            sendButton!!.visibility = View.VISIBLE
                            sendProgress!!.visibility = View.GONE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(mCtx, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }) { volleyError: VolleyError -> Log.e("UploadCoverFragment", volleyError.toString()) }
        rQueue = Volley.newRequestQueue(mCtx)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        rQueue?.add(jsonObjectRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                var bitmap1: Bitmap? = null
                try {
                    if (Build.VERSION.SDK_INT >= 29) {
                        val source: ImageDecoder.Source = ImageDecoder.createSource(mCtx!!.contentResolver, resultUri)
                        try {
                            bitmap1 = ImageDecoder.decodeBitmap(source)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            bitmap1 = MediaStore.Images.Media.getBitmap(mCtx!!.contentResolver, resultUri)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    imgAttachment!!.setImageBitmap(bitmap1)
                    imageToUpload = bitmap1
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(mCtx, "Failed!", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(mCtx, "Failed! Error: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(mCtx!!.applicationContext, "No permissions granted!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { Toast.makeText(mCtx!!.applicationContext, "Error!", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    fun newChats() {
        if (!shouldGetNotification(mCtx)) {
            val chatHandler = Handler()
            val runnableCode = Runnable { messagesFromID }
            chatHandler.postDelayed(runnableCode, 5000)
        } else {
            val chatHandler = Handler()
            val runnableCode = Runnable { newChats() }
            chatHandler.postDelayed(runnableCode, 10000)
        }
    }

    companion object {
        const val URL_FETCH_MESSAGES: String = ROOT_URL + "messages.php/messages"
        const val URL_FETCH_MORE_MESSAGES: String = ROOT_URL + "messages.php/get_new_messages"
        const val URL_SEND_MESSAGE: String = ROOT_URL + "messages.php/send"
        private const val URL_USER_INFO = Constants.ROOT_URL + "messages.php/user"
        private const val UPLOAD_IMAGE_URL = Constants.ROOT_URL + "message_image_upload.php"
        fun hideKeyboardFrom(context: Context?, view: View) {
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun shouldGetNotification(context: Context?): Boolean {
            val myProcess = RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(myProcess)
            if (myProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) return true
            val km = context!!.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            return km.inKeyguardRestrictedInputMode()
        }
    }
}