package com.lucidsoftworksllc.sabotcommunity

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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL
import com.theartofdev.edmodo.cropper.CropImage
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class MessageGroupFragment : Fragment() {
    private var mCtx: Context? = null
    private var thisUserID: String? = null
    private var thisUsername: String? = null
    private var groupId: String? = null
    private var profilePic: String? = null
    private var groupName1: String? = null
    private var imageUploaded: String? = null
    private var lastId: String? = null
    private var groupName: TextView? = null
    private var lastReply: TextView? = null
    private var imgSend: ImageView? = null
    private var imgAttachment: ImageView? = null
    private var groupMessageMenu: ImageView? = null
    private var backMessageButton: ImageView? = null
    private var groupImage: CircleImageView? = null
    private val verifiedView: CircleImageView? = null
    private val onlineView: CircleImageView? = null
    private var etMessage: EditText? = null
    private var cannotRespondLayout: LinearLayout? = null
    private val usernameLayout: LinearLayout? = null
    private var layoutManager: LinearLayoutManager? = null
    private var messages: ArrayList<GroupMessagesHelper>? = null
    private var messagesRecyclerView: RecyclerView? = null
    private var adapter: GroupMessagesThreadAdapter? = null
    private var messageProgress: ProgressBar? = null
    private var sendProgress: ProgressBar? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var navHeader: View? = null
    private var imageToUpload: Bitmap? = null
    var rQueue: RequestQueue? = null
    var jsonObject: JSONObject? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val messageRootView = inflater.inflate(R.layout.fragment_group_message, null)
        backMessageButton = messageRootView.findViewById(R.id.backMessageButton)
        groupMessageMenu = messageRootView.findViewById(R.id.groupMessageMenu)
        groupName = messageRootView.findViewById(R.id.groupName)
        lastReply = messageRootView.findViewById(R.id.lastReply)
        messageProgress = messageRootView.findViewById(R.id.messageProgress)
        cannotRespondLayout = messageRootView.findViewById(R.id.cannotRespondLayout)
        imgAttachment = messageRootView.findViewById(R.id.img_attachment)
        etMessage = messageRootView.findViewById(R.id.et_message)
        imgSend = messageRootView.findViewById(R.id.img_send)
        sendProgress = messageRootView.findViewById(R.id.sendProgress)
        groupImage = messageRootView.findViewById(R.id.groupImage)
        mDrawerLayout = messageRootView.findViewById(R.id.drawer_layout)
        navigationView = messageRootView.findViewById(R.id.side_nav_view)
        imageUploaded = ""
        mCtx = activity
        val toggle = ActionBarDrawerToggle(
                activity, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        messagesRecyclerView = messageRootView.findViewById(R.id.recycler_chat_list)
        messagesRecyclerView?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mCtx)
        //layoutManager.setReverseLayout(true); FIX THIS
        //layoutManager.setStackFromEnd(true);  AND THIS TOO
        messagesRecyclerView?.layoutManager = layoutManager
        groupId = requireArguments().getString("group_id")
        thisUserID = SharedPrefManager.getInstance(mCtx!!)!!.userID
        thisUsername = SharedPrefManager.getInstance(mCtx!!)!!.username
        profilePic = SharedPrefManager.getInstance(mCtx!!)!!.profilePic
        groupName1 = ""
        messages = ArrayList()
        backMessageButton?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        imgAttachment?.setOnClickListener {
            requestMultiplePermissions()
            openCropper()
        }
        loadGroupInfo(groupId)
        getMessages()
        return messageRootView
    }

    private fun openCropper() {
        CropImage.activity()
                .start(requireContext(), this)
    }

    private fun getMessages() {
        val stringRequest = StringRequest(Request.Method.GET, "$URL_FETCH_MESSAGES?this_user=$thisUsername&group_id=$groupId&userid=$thisUserID",
                { response: String? ->
                    try {
                        val res = JSONObject(response!!)
                        val thread = res.getJSONArray("messages")
                        for (i in 0 until thread.length()) {
                            val obj = thread.getJSONObject(i)
                            val id = obj.getString("id")
                            val userTo = obj.getString("user_to")
                            val userFrom = obj.getString("user_from")
                            val body = obj.getString("body")
                            val date = obj.getString("date")
                            val image = obj.getString("image")
                            val profilePic1 = obj.getString("profile_pic")
                            val messageObject = GroupMessagesHelper(id, userTo, userFrom, body, date, image, profilePic1)
                            messages!!.add(messageObject)
                            lastId = id
                        }
                        newChats()
                        messageProgress!!.visibility = View.GONE
                        messagesRecyclerView!!.visibility = View.VISIBLE
                        adapter = GroupMessagesThreadAdapter(mCtx!!, messages!!, thisUsername!!)
                        messagesRecyclerView!!.adapter = adapter
                        scrollToBottomNow()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        ) { }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    //TODO fix this v
    // start thread
    // create thread
    private val messagesFromID: Unit
        get() {
            val messagesIDThread: Thread = object : Thread() {
                //create thread
                override fun run() {
                    val stringRequest = StringRequest(Request.Method.GET, "$URL_FETCH_MORE_MESSAGES?this_user=$thisUsername&group_id=$groupId&userid=$thisUserID&last_id=$lastId",
                            { response: String? ->
                                try {
                                    val res = JSONObject(response!!)
                                    val lastReply1 = res.getString("last_reply")
                                    if (lastReply1 == "removed") {
                                        Toast.makeText(mCtx, "Removed from group!", Toast.LENGTH_SHORT).show()
                                        cannotRespondLayout!!.visibility = View.VISIBLE
                                        //TODO fix this v
                                        (mCtx as FragmentActivity?)!!.supportFragmentManager.popBackStackImmediate()
                                    } else {
                                        lastReply!!.text = lastReply1
                                    }
                                    val thread = res.getJSONArray("messages")
                                    for (i in 0 until thread.length()) {
                                        val obj = thread.getJSONObject(i)
                                        val id = obj.getString("id")
                                        val userFrom = obj.getString("user_from")
                                        val body = obj.getString("body")
                                        val image = obj.getString("image")
                                        val time = obj.getString("time")
                                        val profilePic1 = obj.getString("profile_pic")
                                        processMessage(userFrom, body, image, time, profilePic1)
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

    private fun processMessage(user_string: String, message: String, image: String, time: String, profile_pic: String) {
        val m = GroupMessagesHelper(null, thisUsername!!, user_string, message, time, image, profile_pic)
        messages!!.add(m)
        scrollToBottom()
    }

    private fun sendMessage(group_id: String, body: String) {
        imgSend!!.visibility = View.GONE
        sendProgress!!.visibility = View.VISIBLE
        if (body.equals("", ignoreCase = true)) return
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_SEND_MESSAGE,
                Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.getString("error") == "false") {
                            if (imageToUpload != null) {
                                val messageId = jsonObject.getString("messageid")
                                messageImageUpload(imageToUpload!!, messageId, thisUsername, group_id, body)
                            } else {
                                val m = GroupMessagesHelper(null, group_id, thisUsername!!, body, "Just now", "", profilePic!!)
                                messages!!.add(m)
                                adapter!!.notifyDataSetChanged()
                                scrollToBottom()
                                imgSend!!.visibility = View.VISIBLE
                                sendProgress!!.visibility = View.GONE
                            }
                            etMessage!!.setText("")
                        } else {
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                            imgSend!!.visibility = View.VISIBLE
                            sendProgress!!.visibility = View.GONE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["group_id"] = group_id
                params["message"] = body
                params["group_name"] = groupName1!!
                params["user_from"] = thisUsername!!
                return params
            }
        }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadGroupInfo(group_id: String?) {
        val stringRequest = StringRequest(Request.Method.GET, "$URL_GROUP_INFO?group_id=$group_id&deviceuser=$thisUsername&deviceuserid=$thisUserID", { response: String? ->
            //TODO put a refreshing check on ability to message
            try {
                val res = JSONObject(response!!)
                val thread = res.getJSONArray("messages")
                val messageObject = thread.getJSONObject(0)
                val lastReplyText = messageObject.getString("last_reply_text")
                val name = messageObject.getString("name")
                val image = messageObject.getString("image")
                val options = messageObject.getString("options")
                val position = messageObject.getString("position")
                val date = messageObject.getString("date")
                val usersadded = messageObject.getString("users_added")
                val owner = messageObject.getString("owner")
                val muted = arrayOf(messageObject.getString("muted"))
                var allUsersCanChangeGroupImage = "no"
                var allUsersCanChangeGroupName = "no"
                var allUsersCanInvite = "no"
                var allUsersCanMessage = "no"
                var adminsCanChangeGroupImage = "no"
                var adminsCanChangeGroupName = "no"
                var adminsCanInvite = "no"
                var adminsCanMessage = "no"
                var adminsCanRemoveUsers = "no"
                val optionsSplit = options.split(", ".toRegex()).toTypedArray()
                for (option in optionsSplit) {
                    when (option) {
                        "allUsersCanChangeGroupImage" -> allUsersCanChangeGroupImage = "yes"
                        "allUsersCanChangeGroupName" -> allUsersCanChangeGroupName = "yes"
                        "allUsersCanInvite" -> allUsersCanInvite = "yes"
                        "allUsersCanMessage" -> allUsersCanMessage = "yes"
                        "adminsCanChangeGroupImage" -> adminsCanChangeGroupImage = "yes"
                        "adminsCanChangeGroupName" -> adminsCanChangeGroupName = "yes"
                        "adminsCanInvite" -> adminsCanInvite = "yes"
                        "adminsCanMessage" -> adminsCanMessage = "yes"
                        "adminsCanRemoveUsers" -> adminsCanRemoveUsers = "yes"
                    }
                }
                var canChangeGroupImage = "no"
                var canChangeGroupName = "no"
                var canInvite = "no"
                var canMessage = "no"
                var canRemoveUsers = "no"
                var canChangeUserPriv = "no"
                var canChangeOptions = "no"
                when (position) {
                    "owner" -> {
                        canChangeGroupImage = "yes"
                        canChangeGroupName = "yes"
                        canInvite = "yes"
                        canMessage = "yes"
                        canRemoveUsers = "yes"
                        canChangeUserPriv = "yes"
                        canChangeOptions = "yes"
                    }
                    "admin" -> {
                        if (adminsCanChangeGroupImage == "yes") {
                            canChangeGroupImage = "yes"
                        } else if (allUsersCanChangeGroupImage == "yes") {
                            canChangeGroupImage = "yes"
                        }
                        if (adminsCanChangeGroupName == "yes") {
                            canChangeGroupName = "yes"
                        } else if (allUsersCanChangeGroupName == "yes") {
                            canChangeGroupName = "yes"
                        }
                        if (adminsCanInvite == "yes") {
                            canInvite = "yes"
                        } else if (allUsersCanInvite == "yes") {
                            canInvite = "yes"
                        }
                        if (adminsCanMessage == "yes") {
                            canMessage = "yes"
                        } else if (allUsersCanMessage == "yes") {
                            canMessage = "yes"
                        }
                        if (adminsCanRemoveUsers == "yes") {
                            canRemoveUsers = "yes"
                        }
                    }
                    "user" -> {
                        if (allUsersCanMessage == "yes") {
                            canMessage = "yes"
                        }
                        if (allUsersCanChangeGroupImage == "yes") {
                            canChangeGroupImage = "yes"
                        }
                        if (allUsersCanChangeGroupName == "yes") {
                            canChangeGroupName = "yes"
                        }
                        if (allUsersCanInvite == "yes") {
                            canInvite = "yes"
                        }
                    }
                }
                if (canMessage != "yes") {
                    cannotRespondLayout!!.visibility = View.VISIBLE
                }
                groupName!!.setOnClickListener { mDrawerLayout!!.openDrawer(GravityCompat.END) }
                groupImage!!.setOnClickListener { mDrawerLayout!!.openDrawer(GravityCompat.END) }
                val finalCanRemoveUsers = canRemoveUsers
                val finalCanInvite = canInvite
                val finalCanChangeGroupImage = canChangeGroupImage
                val finalCanChangeGroupName = canChangeGroupName
                val finalCanChangeOptions = canChangeOptions
                navigationView!!.setNavigationItemSelectedListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.mute_convo -> {
                            if (muted[0] == "yes") {
                                LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                        .setTopColorRes(R.color.green)
                                        .setButtonsColorRes(R.color.green)
                                        .setIcon(R.drawable.ic_friend_cancel)
                                        .setTitle("Un-mute $name?")
                                        .setPositiveButton(R.string.yes) {
                                            muted[0] = "no"
                                            muteConvo("unmute")
                                        }
                                        .setNegativeButton(R.string.no, null)
                                        .show()
                            } else if (muted[0] == "no") {
                                LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                        .setTopColorRes(R.color.green)
                                        .setButtonsColorRes(R.color.green)
                                        .setIcon(R.drawable.ic_friend_cancel)
                                        .setTitle("Mute $name?")
                                        .setPositiveButton(R.string.yes) {
                                            muted[0] = "yes"
                                            muteConvo("mute")
                                        }
                                        .setNegativeButton(R.string.no, null)
                                        .show()
                            }
                        }
                        R.id.players_added -> {
                            val asf: Fragment = MessageUserListFragment()
                            val args = Bundle()
                            args.putString("query", "players_added")
                            args.putString("queryID", group_id)
                            args.putString("permission", position)
                            args.putString("owner", owner)
                            args.putString("canRemove", finalCanRemoveUsers)
                            asf.arguments = args
                            val fragmentTransaction = (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out)
                            fragmentTransaction.replace(R.id.chat_fragment_container, asf)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                        }
                        R.id.invite_players -> {
                            if (finalCanInvite == "yes") {
                                val asf: Fragment = MessageGroupInviteFragment()
                                val args = Bundle()
                                args.putString("group_id", group_id)
                                asf.arguments = args
                                val fragmentTransaction = (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out)
                                fragmentTransaction.replace(R.id.chat_fragment_container, asf)
                                fragmentTransaction.addToBackStack(null)
                                fragmentTransaction.commit()
                            }
                        }
                        R.id.group_options -> {
                            val asf: Fragment = MessageGroupOptionsFragment()
                            val args = Bundle()
                            args.putString("canChangeGroupImage", finalCanChangeGroupImage)
                            args.putString("canChangeGroupName", finalCanChangeGroupName)
                            args.putString("canChangeOptions", finalCanChangeOptions)
                            args.putString("group_id", group_id)
                            asf.arguments = args
                            val fragmentTransaction = (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out)
                            fragmentTransaction.replace(R.id.chat_fragment_container, asf)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                        }
                        R.id.leave_group -> {
                            LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTopColorRes(R.color.green)
                                    .setButtonsColorRes(R.color.green)
                                    .setIcon(R.drawable.ic_friend_cancel)
                                    .setTitle(R.string.leave_group_message_text)
                                    .setPositiveButton(R.string.yes) { leaveConvo() }
                                    .setNegativeButton(R.string.no, null)
                                    .show()
                        }
                    }
                    mDrawerLayout!!.closeDrawer(GravityCompat.END)
                    true
                }
                navHeader = navigationView!!.getHeaderView(0)
                val navBackground = navHeader?.findViewById<ImageView>(R.id.img_header_bg)
                val headerNickname = navHeader?.findViewById<TextView>(R.id.headerNickname)
                val dateCreated = navHeader?.findViewById<TextView>(R.id.date)
                val usersAdded = navHeader?.findViewById<TextView>(R.id.numAdded)
                val muteConvo = navHeader?.findViewById<TextView>(R.id.mute_convo)
                usersAdded?.text = String.format("Users: %s", usersadded)
                dateCreated?.text = String.format("Created: %s", date)
                headerNickname?.text = name
                Glide.with(mCtx!!).load(Constants.BASE_URL + image)
                        .thumbnail(0.5f)
                        .into(navBackground!!)
                groupName!!.text = name
                groupName1 = name
                Glide.with(mCtx!!)
                        .load(Constants.BASE_URL + image)
                        .into(groupImage!!)
                lastReply!!.text = lastReplyText
                if (canMessage == "yes") {
                    imgSend!!.setOnClickListener { view: View ->
                        val body = etMessage!!.text.toString().trim { it <= ' ' }
                        if (etMessage!!.text.toString().isNotEmpty() || imageUploaded != null && imageUploaded!!.isNotEmpty()) {
                            sendMessage(group_id!!, body)
                            hideKeyboardFrom(mCtx, view)
                        } else {
                            Toast.makeText(mCtx, "You must enter text before submitting!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                groupMessageMenu!!.setOnClickListener { }
                /*
                    if user is banned from chat
                    cannotRespondLayout.setVisibility(View.VISIBLE);
                    userMessageMenu.setVisibility(GONE);*/
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    private fun muteConvo(method: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_MUTE_CONVO,
                Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response!!)
                        Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { Toast.makeText(mCtx, "An error occured!", Toast.LENGTH_SHORT).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["group_id"] = groupId!!
                params["user_muted"] = ""
                params["method"] = method
                params["username"] = thisUsername!!
                params["userid"] = thisUserID!!
                return params
            }
        }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    private fun leaveConvo() {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_LEAVE_CONVO,
                Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.getString("error") == "false") {
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                            val ldf = ConvosFragment()
                            (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).replace(R.id.chat_fragment_container, ldf).commit()
                        } else {
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["group_id"] = groupId!!
                params["username"] = thisUsername!!
                params["userid"] = thisUserID!!
                return params
            }
        }
        //Disabling retry to prevent duplicate messages
        val socketTimeout = 0
        val policy: RetryPolicy = DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.retryPolicy = policy
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
                            val m = GroupMessagesHelper(null, user_string, thisUsername!!, message, "Just now", imageUploaded!!, profilePic!!)
                            messages!!.add(m)
                            adapter!!.notifyDataSetChanged()
                            scrollToBottom()
                            imgSend!!.visibility = View.VISIBLE
                            sendProgress!!.visibility = View.GONE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(mCtx, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }) { }
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
                    Toast.makeText(mCtx, "Image loading failed!", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(activity, "Failed! Error: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
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
            chatHandler.postDelayed(runnableCode, 3000)
        } else {
            val chatHandler = Handler()
            val runnableCode = Runnable { newChats() }
            chatHandler.postDelayed(runnableCode, 10000)
        }
    }

    companion object {
        const val URL_FETCH_MESSAGES: String = ROOT_URL + "messages.php/groupmessages"
        const val URL_FETCH_MORE_MESSAGES: String = ROOT_URL + "messages.php/get_new_group_messages"
        const val URL_SEND_MESSAGE: String = ROOT_URL + "messages.php/send_group"
        private const val URL_GROUP_INFO = ROOT_URL + "messages.php/group"
        private const val URL_MUTE_CONVO = ROOT_URL + "messages.php/mute_convo"
        private const val URL_LEAVE_CONVO = ROOT_URL + "messages.php/leave_convo"
        private const val UPLOAD_IMAGE_URL = ROOT_URL + "message_image_upload.php"
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