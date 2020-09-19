package com.lucidsoftworksllc.sabotcommunity.fragments

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.ChatroomMessagesAdapter
import com.lucidsoftworksllc.sabotcommunity.models.ChatroomMessagesHelper
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PublicsChatRoom : Fragment() {
    private var mCtx: Context? = null
    private var thisUserID: String? = null
    private var thisUsername: String? = null
    private var gameID: String? = null
    private val gamename: String? = null
    private val gameimage: String? = null
    private var thisNickname: String? = null
    private var thisprofilePic: String? = null
    private var lastId: String? = null
    private var isChatRoomFollowed: String? = null
    private var gameName: TextView? = null
    private var lastReply: TextView? = null
    private var sendButton: ImageView? = null
    private var chatroomGameImage: ImageView? = null
    private var backMessageButton: ImageView? = null
    private var userMessageMenu: ImageView? = null
    private var imgSendDisabled: ImageView? = null
    private var chatroomFollowBtn: ImageView? = null
    private var messageEditText: EditText? = null
    private var cannotRespondLayout: LinearLayout? = null
    private var chatroomTopLayout: LinearLayout? = null
    private var layoutManager: LinearLayoutManager? = null
    private var chatroomFollowProg: ProgressBar? = null
    private var messages: ArrayList<ChatroomMessagesHelper>? = null
    private var messagesRecyclerView: RecyclerView? = null
    private var adapter: ChatroomMessagesAdapter? = null
    private var messageProgress: ProgressBar? = null
    private var sendProgress: ProgressBar? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val messageRootView = inflater.inflate(R.layout.fragment_chatroom, null)
        messageEditText = messageRootView.findViewById(R.id.et_message)
        messageProgress = messageRootView.findViewById(R.id.messageProgress)
        sendButton = messageRootView.findViewById(R.id.img_send)
        backMessageButton = messageRootView.findViewById(R.id.backMessageButton)
        sendProgress = messageRootView.findViewById(R.id.sendProgress)
        cannotRespondLayout = messageRootView.findViewById(R.id.cannotRespondLayout)
        gameName = messageRootView.findViewById(R.id.gameName)
        lastReply = messageRootView.findViewById(R.id.lastReply)
        chatroomGameImage = messageRootView.findViewById(R.id.chatroomGameImage)
        chatroomTopLayout = messageRootView.findViewById(R.id.chatroomTopLayout)
        userMessageMenu = messageRootView.findViewById(R.id.userMessageMenu)
        imgSendDisabled = messageRootView.findViewById(R.id.img_send_disabled)
        chatroomFollowBtn = messageRootView.findViewById(R.id.chatroomFollowBtn)
        chatroomFollowProg = messageRootView.findViewById(R.id.chatroomFollowProg)
        mCtx = activity
        messagesRecyclerView = messageRootView.findViewById(R.id.recycler_chat_list)
        messagesRecyclerView?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mCtx)
        //layoutManager.setReverseLayout(true); FIX THIS
        //layoutManager.setStackFromEnd(true);  AND THIS TOO
        messagesRecyclerView?.layoutManager = layoutManager
        gameID = requireArguments().getString("GameId")
        thisUserID = SharedPrefManager.getInstance(mCtx!!)!!.userID
        thisUsername = SharedPrefManager.getInstance(mCtx!!)!!.username
        thisNickname = SharedPrefManager.getInstance(mCtx!!)!!.nickname
        thisprofilePic = SharedPrefManager.getInstance(mCtx!!)!!.profilePic
        messages = ArrayList()
        backMessageButton?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        loadGameInfo()
        getMessages()
        return messageRootView
    }

    private fun getMessages() {
        val messagesIDThread: Thread = object : Thread() {
            //create thread
            override fun run() {
                val stringRequest = StringRequest(Request.Method.GET, "$URL_FETCH_MESSAGES?this_user=$thisUsername&gameid=$gameID&userid=$thisUserID",
                        { response: String? ->
                            try {
                                val res = JSONObject(response!!)
                                val isUserFollow = res.getString("isUserFollow")
                                isChatRoomFollowed = if (isUserFollow == "no") {
                                    "no"
                                } else {
                                    "yes"
                                }
                                chatroomFollowBtn!!.setOnClickListener { promptNoti() }
                                val thread = res.getJSONArray("messages")
                                for (i in 0 until thread.length()) {
                                    val obj = thread.getJSONObject(i)
                                    val id = obj.getString("id")
                                    val message = obj.getString("message")
                                    val username = obj.getString("username")
                                    val timeMessage = obj.getString("time_message")
                                    val nickname = obj.getString("nickname")
                                    val profilePic = obj.getString("profile_pic")
                                    val lastOnline = obj.getString("last_online")
                                    val userLevel = obj.getString("user_level")
                                    val verified = obj.getString("verified")
                                    val userid = obj.getString("userid")
                                    val messageObject = ChatroomMessagesHelper(id, message, username, timeMessage, nickname, profilePic, lastOnline, userLevel, verified, userid)
                                    messages!!.add(messageObject)
                                    lastId = id
                                }
                                newChats()
                                messageProgress!!.visibility = View.GONE
                                messagesRecyclerView!!.visibility = View.VISIBLE
                                adapter = ChatroomMessagesAdapter(mCtx!!, messages!!, thisUsername!!)
                                messagesRecyclerView!!.adapter = adapter
                                scrollToBottomNow()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                ) { }
                (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
            }
        }
        messagesIDThread.start() // start thread
    }

    private fun promptNoti() {
        if (isChatRoomFollowed == "yes") {
            LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.notify_reply)
                    .setTitle("Remove chatroom push notifications for $gamename?")
                    .setPositiveButton(R.string.yes) {
                        chatroomFollowProg!!.visibility = View.VISIBLE
                        userChatroomNoti("unfollow")
                    }
                    .setNegativeButton(R.string.no, null)
                    .show()
        } else if (isChatRoomFollowed == "no") {
            LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.notify_reply)
                    .setTitle("Receive chatroom push notifications for $gamename?")
                    .setMessage("Warning: it may get annoying once this feature becomes more popular!")
                    .setPositiveButton(R.string.yes) {
                        chatroomFollowProg!!.visibility = View.VISIBLE
                        userChatroomNoti("follow")
                    }
                    .setNegativeButton(R.string.no, null)
                    .show()
        }
    }

    //create thread
    // start thread
    private val messagesFromID: Unit
        get() {
            val messagesIDThread: Thread = object : Thread() {
                //create thread
                override fun run() {
                    val stringRequest = StringRequest(Request.Method.GET, "$URL_FETCH_MESSAGES_FROM_ID?this_user=$thisUsername&gameid=$gameID&userid=$thisUserID&start_id=$lastId",
                            { response: String? ->
                                try {
                                    val res = JSONObject(response!!)
                                    val thread = res.getJSONArray("messages")
                                    for (i in 0 until thread.length()) {
                                        val obj = thread.getJSONObject(i)
                                        val id = obj.getString("id")
                                        val message = obj.getString("message")
                                        val username = obj.getString("username")
                                        val timeMessage = obj.getString("time_message")
                                        val nickname = obj.getString("nickname")
                                        val profilePic = obj.getString("profile_pic")
                                        val lastOnline = obj.getString("last_online")
                                        val userLevel = obj.getString("user_level")
                                        val verified = obj.getString("verified")
                                        val userid = obj.getString("userid")
                                        processMessage(id, message, username, timeMessage, nickname, profilePic, lastOnline, userLevel, verified, userid)
                                        lastId = id
                                        lastReply!!.text = timeMessage
                                    }
                                    newChats()
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                    ) { }
                    (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
                }
            }
            messagesIDThread.start() // start thread
        }

    private fun processMessage(id: String, message: String, username: String, time_message: String, nickname: String, profile_pic: String, last_online: String, user_level: String, verified: String, userid: String) {
        val m = ChatroomMessagesHelper(id, message, username, time_message, nickname, profile_pic, last_online, user_level, verified, userid)
        messages!!.add(m)
        scrollToBottom()
    }

    private fun sendMessage(gameID: String?, message: String) {
        sendButton!!.visibility = View.GONE
        sendProgress!!.visibility = View.VISIBLE
        if (message.equals("", ignoreCase = true)) return
        messageEditText!!.setText("")
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_SEND_MESSAGE,
                Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.getString("error") == "false") {
                            val m = ChatroomMessagesHelper(null.toString(), message, thisUsername!!, "Just now", thisNickname!!, thisprofilePic!!, "yes", "0", "no", thisUserID!!)
                            messages!!.add(m)
                            adapter!!.notifyDataSetChanged()
                            scrollToBottom()
                        } else {
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                        sendButton!!.visibility = View.VISIBLE
                        sendProgress!!.visibility = View.GONE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["gameid"] = gameID!!
                params["message"] = message
                params["user_from"] = thisUsername!!
                return params
            }
        }
        (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadGameInfo() {
        val stringRequest = StringRequest(Request.Method.GET, "$URL_USER_INFO?gameid=$gameID&deviceuser=$thisUsername&deviceuserid=$thisUserID", { response: String? ->
            try {
                val res = JSONObject(response!!)
                val thread = res.getJSONArray("messages")
                val messageObject = thread.getJSONObject(0)
                val lastReplyText = messageObject.getString("last_reply_text")
                val gamename = messageObject.getString("gamename")
                val catImage = messageObject.getString("image")
                gameName!!.text = gamename
                Glide.with(mCtx!!)
                        .load(Constants.BASE_URL + catImage)
                        .into(chatroomGameImage!!)
                lastReply!!.text = lastReplyText
                sendButton!!.setOnClickListener { view: View ->
                    val body = messageEditText!!.text.toString().trim { it <= ' ' }
                    if (messageEditText!!.text.toString().isNotEmpty()) {
                        sendMessage(gameID, body)
                        hideKeyboardFrom(mCtx, view)
                    } else {
                        Toast.makeText(mCtx, "You must enter text before submitting!", Toast.LENGTH_LONG).show()
                    }
                }
                userMessageMenu!!.setOnClickListener { }
                /*
                    if user is banned from chat
                    cannotRespondLayout.setVisibility(View.VISIBLE);
                    userMessageMenu.setVisibility(GONE);*/
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { }
        (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun userChatroomNoti(method: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_NOTI_FOLLOW,
                Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response!!)
                        Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                        chatroomFollowProg!!.visibility = View.GONE
                        if (method == "follow") {
                            isChatRoomFollowed = "yes"
                        } else if (method == "unfollow") {
                            isChatRoomFollowed = "no"
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["gameid"] = gameID!!
                params["gamename"] = gamename!!
                params["method"] = method
                params["username"] = thisUsername!!
                params["userid"] = thisUserID!!
                return params
            }
        }
        (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    fun scrollToBottom() {
        adapter!!.notifyDataSetChanged()
        if (adapter!!.itemCount > 1) Objects.requireNonNull(messagesRecyclerView!!.layoutManager!!).smoothScrollToPosition(messagesRecyclerView, null, adapter!!.itemCount - 1)
    }

    private fun scrollToBottomNow() {
        adapter!!.notifyDataSetChanged()
        if (adapter!!.itemCount > 1) Objects.requireNonNull(messagesRecyclerView!!.layoutManager!!).scrollToPosition(adapter!!.itemCount - 1)
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
        const val URL_FETCH_MESSAGES: String = ROOT_URL + "chatrooms.php/messages"
        const val URL_SEND_MESSAGE: String = ROOT_URL + "chatrooms.php/send"
        const val URL_NOTI_FOLLOW: String = ROOT_URL + "chatrooms.php/noti"
        private const val URL_USER_INFO = ROOT_URL + "chatrooms.php/game"
        private const val URL_FETCH_MESSAGES_FROM_ID = ROOT_URL + "chatrooms.php/messagesFromID"
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