package com.lucidsoftworksllc.sabotcommunity.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.MessagesThreadAdapter
import com.lucidsoftworksllc.sabotcommunity.db.SabotDatabase
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesEntity
import com.lucidsoftworksllc.sabotcommunity.models.MessagesHelper
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.lucidsoftworksllc.sabotcommunity.others.Constants.ROOT_URL
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MessageFragment : CoFragment() {
    private var mCtx: Context? = null
    private var deviceUserID: String? = null
    private var deviceUsername: String? = null
    private var userTo: String? = null
    private val profilePic: String? = null
    private val nickname: String? = null
    private var imageUploaded: String = ""
    private var lastId = 0
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
    private var messages: List<UserMessagesEntity>? = null
    private var messagesRecyclerView: RecyclerView? = null
    private var adapter: MessagesThreadAdapter? = null
    private var messageProgress: ProgressBar? = null
    private var sendProgress: ProgressBar? = null
    private var imageToUpload: Bitmap? = null
    private var messageUserInfoDao: MessageUserInfoDao? = null
    private var userMessagesDao: UserMessagesDao? = null
    private var typedMessageDao: TypedMessageDao? = null
    var rQueue: RequestQueue? = null
    var jsonObject: JSONObject? = null
    var canUpdate: Boolean = true

    override fun onPause() {
        super.onPause()
        canUpdate = false
    }

    override fun onResume() {
        super.onResume()
        canUpdate = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val messageRootView = inflater.inflate(R.layout.content_chat, null)
        mCtx = activity
        canUpdate = true
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
        messagesRecyclerView = messageRootView.findViewById(R.id.recycler_chat_list)
        userTo = requireArguments().getString("user_to")
        deviceUserID = mCtx?.deviceUserID
        deviceUsername = mCtx?.deviceUsername
        messageUserInfoDao = SabotDatabase(mCtx!!).getMessageUserInfo()
        userMessagesDao = SabotDatabase(mCtx!!).getUserMessagesDao()
        typedMessageDao = SabotDatabase(mCtx!!).getTypedMessageDao()

        initRecycler()
        initTypedMessage()
        initUserInfoDatabase(true)
        getMessages(true)

        messageEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                launch {
                    if (typedMessageDao?.isRowExistUser(userTo!!)!!){
                        typedMessageDao?.updateTypedMessageUser(s.toString(),userTo!!)
                    }else{
                        val typedMessage = TypedMessageEntity("user",userTo!!,0,s.toString())
                        typedMessageDao?.insertTypedMessage(typedMessage)
                    }
                }
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        backMessageButton?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        imgAttachment?.setOnClickListener {
            requestMultiplePermissions()
            openCropper()
        }
        return messageRootView
    }

    private fun openCropper() {
        CropImage.activity()
                .start(requireContext(), this)
    }

    private fun initTypedMessage(){
        launch {
            if (typedMessageDao?.isRowExistUser(userTo!!)!!){
                val typedMessage = typedMessageDao?.getTypedMessageUser(userTo!!)!!
                if (typedMessage.isNotBlank()) withContext(Main){messageEditText?.setText(typedMessage)}
            }
        }
    }

    private fun initUserInfoDatabase(init: Boolean) {
        launch {
            val userInfoEntity = withContext(Default) { messageUserInfoDao?.getUserInfo(userTo!!) }
            if (init) {
                if (userInfoEntity != null) {
                    withContext(Main){
                        updateUserInfoView(userInfoEntity)
                        loadUserInfo(userTo)
                    }
                } else {
                    withContext(Main){
                        loadUserInfo(userTo)
                    }
                }
            } else {
                withContext(Main){
                    updateUserInfoView(userInfoEntity!!)
                }
            }
        }
    }

    private fun updateUserInfoView(userInfoEntity: MessageUserInfoEntity){
        CoroutineScope(Main).launch {
            val profilePic1 = userInfoEntity.profile_pic
            val nickname = userInfoEntity.nickname
            val verified = userInfoEntity.verified
            val lastOnline1 = userInfoEntity.last_online
            val lastOnlineText = userInfoEntity.last_online_text
            val userToId = userInfoEntity.user_id
            val username = userInfoEntity.username
            lastOnline!!.text = lastOnlineText
            sendButton!!.setOnClickListener { view: View ->
                val body = messageEditText!!.text.toString().trim { it <= ' ' }
                if (messageEditText!!.text.toString().isNotEmpty() || imageUploaded.isNotEmpty()) {
                    sendMessage(username, body)
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
            profileMessageToName!!.text = nickname
            val profilePic2 = profilePic1.substring(0, profilePic1.length - 4) + "_r.JPG"
            Glide.with(mCtx!!)
                    .load(Constants.BASE_URL + profilePic2)
                    .into(profileMessageToImage!!)
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
                        args.putString("id", userToId.toString())
                        ldf.arguments = args
                        (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.chat_fragment_container, ldf).addToBackStack(null).commit()
                    }
                    if (item.itemId == R.id.menuPlayerBlock) {
                        SharedPrefManager.getInstance(mCtx!!)!!.blockUser(username)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            requireActivity().supportFragmentManager.beginTransaction().detach(this@MessageFragment).commitNowAllowingStateLoss()
                            requireActivity().supportFragmentManager.beginTransaction().attach(this@MessageFragment).commitAllowingStateLoss()
                        } else {
                            requireActivity().supportFragmentManager.beginTransaction().detach(this@MessageFragment).attach(this@MessageFragment).commit()
                        }
                    }
                    true
                }
                popup.show()
            }
        }
    }

    private fun initRecycler(){
        messagesRecyclerView?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mCtx)
        messages = ArrayList()
        layoutManager?.reverseLayout = true
        layoutManager?.stackFromEnd = true
        messagesRecyclerView?.layoutManager = layoutManager
        adapter = MessagesThreadAdapter(mCtx!!, messages!! as MutableList<UserMessagesEntity>, deviceUsername!!)
        messagesRecyclerView?.adapter = adapter
    }

    private fun appendRecycler(messageList: List<UserMessagesEntity>){
        if (messageList.isNotEmpty()){
            lastId = messageList[0].message_id
            adapter?.addItems(messageList)
            messageProgress!!.visibility = View.GONE
            messagesRecyclerView!!.visibility = View.VISIBLE
            scrollToBottomNow()
        }else{
            messageProgress!!.visibility = View.GONE
            messagesRecyclerView!!.visibility = View.VISIBLE
        }
    }

    private fun getMessagesInit(init: Boolean){
        val stringRequest = StringRequest(Request.Method.GET, "$URL_FETCH_MESSAGES?this_user=$deviceUsername&username=$userTo&userid=$deviceUserID",
                { response: String? ->
                    launch {
                        try {
                            val res = JSONObject(response!!)
                            val thread = res.getJSONArray("messages")
                            for (i in 0 until thread.length()) {
                                val obj = thread.getJSONObject(i)
                                val id = obj.getInt("id")
                                val userTo1 = obj.getString("user_to")
                                val userFrom = obj.getString("user_from")
                                val body = obj.getString("body")
                                val date = obj.getString("date")
                                val image = obj.getString("image")
                                val messageObject = UserMessagesEntity(id, userTo1, userFrom, body, date, image)
                                withContext(Default){
                                    if (userMessagesDao!!.isRowExist(id)){
                                        userMessagesDao!!.updateMessage(messageObject)
                                    }else{
                                        userMessagesDao!!.addMessage(messageObject)
                                    }
                                }
                            }
                            if (init){
                                getMessages(false)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
        ) { }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    private fun getMessages(init: Boolean) {
        launch {
            if (init){
                val userMessages = withContext(IO){ userMessagesDao?.getMessages(userTo!!,deviceUsername!!) }
                if (userMessages!!.isEmpty()){
                    getMessagesInit(true)
                }else{
                    withContext(Main){ appendRecycler(userMessages) }
                    getMessagesInit(false)
                    messagesFromID()
                }
            }else{
                val userMessages = withContext(IO){ userMessagesDao?.getMessages(userTo!!,deviceUsername!!) }
                withContext(Main){ appendRecycler(userMessages!!) }
                messagesFromID()
            }
        }

    }

    private suspend fun messagesFromID(){
        val stringRequest = StringRequest(Request.Method.GET, "$URL_FETCH_MORE_MESSAGES?this_user=$deviceUsername&username=$userTo&userid=$deviceUserID&last_id=$lastId",
                { response: String? ->
                    launch {
                        try {
                            val res = JSONObject(response!!)
                            val lastOnline1 = res.getString("userLastOnline")
                            if (lastOnline1 != "null") {
                                withContext(Main){
                                    lastOnline!!.text = lastOnline1
                                    if (lastOnline1 == "Online now") {
                                        onlineView!!.visibility = View.VISIBLE
                                    } else {
                                        onlineView!!.visibility = View.GONE
                                    }
                                }
                            }
                            val thread = res.getJSONArray("messages")
                            for (i in 0 until thread.length()) {
                                val obj = thread.getJSONObject(i)
                                val id = obj.getInt("id")
                                val userFrom = obj.getString("user_from")
                                val body = obj.getString("body")
                                val time = obj.getString("time")
                                val image = obj.getString("image")
                                withContext(Main){ processMessage(userFrom, body, image, id, deviceUsername!!, time) }
                            }
                            newChats()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
        ) { }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    private fun processMessage(user_from: String, message: String, image: String, message_id: Int, user_to: String, date_text: String) {
        CoroutineScope(Main).launch {
            lastId = message_id
            println("LAST ID: $lastId")
            val m = UserMessagesEntity(message_id, user_to, user_from, message, date_text, image)
            withContext(IO){ userMessagesDao?.addMessage(m) }
            adapter!!.add(m)
            scrollToBottom()
        }
    }

    private fun sendMessage(user_string: String, message: String) {
        sendButton!!.visibility = View.GONE
        sendProgress!!.visibility = View.VISIBLE
        if (message.equals("", ignoreCase = true)) return
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_SEND_MESSAGE, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    val messageId = jsonObject.getInt("messageid")
                    if (imageToUpload != null) {
                        messageImageUpload(imageToUpload!!, messageId, deviceUsername, user_string, message)
                    } else {
                        processMessage(deviceUsername!!,message,"",messageId,user_string,"Just now")
                        sendButton!!.visibility = View.VISIBLE
                        sendProgress!!.visibility = View.GONE
                    }
                    messageEditText!!.setText("")
                    launch { typedMessageDao?.updateTypedMessageUser("",userTo!!) }
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
                params["user_from"] = deviceUsername!!
                params["user_id"] = deviceUserID!!
                return params
            }
        }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadUserInfo(user_to: String?) {
        val stringRequest = StringRequest(Request.Method.GET, "$URL_USER_INFO?username=$user_to&deviceuser=$deviceUsername&deviceuserid=$deviceUserID", { response: String? ->
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
                val username = messageObject.getString("username")
                var blocked = ""
                val blockedArray = blockedArray1.split(",".toRegex()).toTypedArray()
                for (usernameBlocked in blockedArray) {
                    if (usernameBlocked == deviceUsername && usernameBlocked != "") {
                        blocked = "yes"
                        break
                    }
                }
                if (blocked != "yes" && !SharedPrefManager.getInstance(mCtx!!)!!.isUserBlocked(user_to!!)) {

                    launch {
                        val userEntity = MessageUserInfoEntity(userToId.toInt(),
                                profilePic1,
                                nickname,
                                verified,lastOnline1,
                                lastOnlineText,
                                blockedArray1,
                                username)
                        withContext(Default) {
                            if (messageUserInfoDao!!.isRowExist(userToId.toInt())) {
                                messageUserInfoDao!!.updateUser(userEntity)
                            } else {
                                messageUserInfoDao!!.addUser(userEntity)
                            }
                        }
                        initUserInfoDatabase(false)
                    }

                } else {
                    cannotRespondLayout!!.visibility = View.VISIBLE
                    userMessageMenu!!.visibility = View.GONE
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    private fun scrollToBottom() {
        //adapter!!.notifyDataSetChanged()
        if (adapter!!.itemCount > 1) Objects.requireNonNull(messagesRecyclerView!!.layoutManager!!).smoothScrollToPosition(messagesRecyclerView, null, 0)
    }

    private fun scrollToBottomNow() {
        //adapter!!.notifyDataSetChanged()
        if (adapter!!.itemCount > 1) Objects.requireNonNull(messagesRecyclerView!!.layoutManager!!).scrollToPosition(0)
    }

    private fun messageImageUpload(bitmap: Bitmap, message_id: Int, username: String?, user_string: String, message: String) {
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
                            processMessage(deviceUsername!!,message,imageUploaded,message_id,user_string,"Just now")
                            sendButton?.visibility = View.VISIBLE
                            sendProgress?.visibility = View.GONE
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

                    if (Build.VERSION.SDK_INT < 29){
                        imgAttachment!!.setImageBitmap(bitmap1)
                    }else{
                        imgAttachment?.setImageResource(R.drawable.icons8_question_mark_64)
                        activity?.toastLong("Cannot display image cropped! (Android 10+ temporary issue, upload should work as usual.)")
                    }

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

    private suspend fun newChats() {
        if (canUpdate) {
            val chatHandler = Handler(Looper.getMainLooper())
            val runnableCode = Runnable { launch { messagesFromID() } }
            chatHandler.postDelayed(runnableCode, 3000)
        } else {
            val chatHandler = Handler(Looper.getMainLooper())
            val runnableCode = Runnable { launch { newChats() } }
            chatHandler.postDelayed(runnableCode, 3000)
        }
    }

    companion object {
        const val URL_FETCH_MESSAGES: String = ROOT_URL + "messages.php/messages"
        const val URL_FETCH_MORE_MESSAGES: String = ROOT_URL + "messages.php/get_new_messages"
        const val URL_SEND_MESSAGE: String = ROOT_URL + "messages.php/send"
        private const val URL_USER_INFO = ROOT_URL + "messages.php/user"
        private const val UPLOAD_IMAGE_URL = ROOT_URL + "message_image_upload.php"
        fun hideKeyboardFrom(context: Context?, view: View) {
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}