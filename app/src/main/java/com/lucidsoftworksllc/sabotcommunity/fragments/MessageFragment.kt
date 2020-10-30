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
import com.lucidsoftworksllc.sabotcommunity.databinding.ContentChatBinding
import com.lucidsoftworksllc.sabotcommunity.db.SabotDatabase
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesEntity
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.UserMessageRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.UserMessageVM
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.SentMessageResponse
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UserMessageData
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UserMessagesFromID
import com.lucidsoftworksllc.sabotcommunity.network.UserMessageApi
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.lucidsoftworksllc.sabotcommunity.others.Constants.ROOT_URL
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseFragment
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.content_chat.*
import kotlinx.coroutines.CoroutineScope
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

class MessageFragment : BaseFragment<UserMessageVM, ContentChatBinding, UserMessageRepo>() {
    private lateinit var userTo: String
    private var imageUploaded: String = ""
    private var lastId = 0
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: MessagesThreadAdapter
    private var imageToUpload: Bitmap? = null
    private lateinit var messageUserInfoDao: MessageUserInfoDao
    private lateinit var userMessagesDao: UserMessagesDao
    private lateinit var typedMessageDao: TypedMessageDao
    private var rQueue: RequestQueue? = null
    private var jsonObject: JSONObject? = null
    private var canUpdate: Boolean = false
    private var recyclerInit: Boolean = false

    override fun onPause() {
        super.onPause()
        canUpdate = false
    }

    override fun onResume() {
        super.onResume()
        canUpdate = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCtx = requireContext()
        messageUserInfoDao = SabotDatabase(mCtx).getMessageUserInfo()
        userMessagesDao = SabotDatabase(mCtx).getUserMessagesDao()
        typedMessageDao = SabotDatabase(mCtx).getTypedMessageDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        canUpdate = true
        userTo = requireArguments().getString("user_to").toString()

        initRecycler()
        subscribeObservers()

        // TODO: 10/26/20    v REMOVE THESE v
        //initTypedMessage()
        //initUserInfoDatabase(true)
        //getMessages(true)

        et_message.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                viewModel.setTypedMessage(s.toString(), userTo)
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        backMessageButton.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        img_attachment.setOnClickListener {
            requestMultiplePermissions()
            openCropper()
        }
    }

    private fun subscribeObservers(){
        viewModel.getTypedMessage(userTo)
        viewModel.typedMessage.observe(viewLifecycleOwner, {
            if(it.isNotBlank()) et_message.setText(it)
        })

        viewModel.getUserInfo(userTo, deviceUsername, deviceUserID)
        viewModel.userInfo.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success -> {
                    updateUserInfoUI(it.data)
                }
            }
        })

        viewModel.getUserMessages(userTo, deviceUsername, deviceUserID)
        viewModel.userMessages.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success -> {
                    appendRecycler(it.data)
                    messageProgress?.visible(false)
                    recycler_chat_list?.visible(true)
                }
                is DataState.Loading -> {
                    messageProgress?.visible(true)
                    recycler_chat_list?.visible(false)
                    requireView().snackbarShort("Updating messages...", "")
                }
                is DataState.Failure -> handleApiError(it) { errorRetry() }
            }
        })


        viewModel.newUserMessages.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success -> {
                    updateNewMessages(it.data)
                }
                is DataState.Failure -> handleApiError(it) { errorRetry() }
            }
        })

        viewModel.sentMessage.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Loading -> {
                    img_send.visible(false)
                    sendProgress.visible(true)
                }
                is DataState.Success -> {
                    updateSentMessageUI(it.data)
                }
                // TODO: 10/30/20 ADD RETRY FOR SENDING MESSAGES
                is DataState.Failure -> handleApiError(it) { errorRetry() }
            }
        })

    }

    // TODO: 10/30/20 UPDATE MODEL FILE
    private fun updateSentMessageUI(data: UserMessageData){
        if (!data.error){
            et_message.setText("")
            img_attachment?.setImageResource(R.drawable.ic_attach_file_grey_24dp)
            imageToUpload = null
            processMessages(data.messages)
            img_send.visible(true)
            sendProgress?.visible(false)
            viewModel.setTypedMessage("", userTo)
        }
    }

    private fun errorRetry(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commitNowAllowingStateLoss()
            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commitAllowingStateLoss()
        } else {
            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }
    }

    private fun openCropper() {
        CropImage.activity()
                .start(mCtx, this)
    }

    /*private fun initTypedMessage(){
        launch {
            if (typedMessageDao.isRowExistUser(userTo)){
                val typedMessage = typedMessageDao.getTypedMessageUser(userTo)
                if (typedMessage.isNotBlank()) withContext(Main){ et_message.setText(typedMessage) }
            }
        }
    }*/

    /*private fun initUserInfoDatabase(init: Boolean) {
        launch {
            val userInfoEntity = withContext(Default) { messageUserInfoDao.getUserInfo(userTo) }
            if (init) {
                if(messageUserInfoDao.isRowExistUsername(userTo)){
                    updateUserInfoView(userInfoEntity)
                }
                loadUserInfo(userTo)
            } else {
                updateUserInfoView(userInfoEntity)
            }
        }
    }*/

    private fun updateUserInfoUI(userInfoEntity: MessageUserInfoEntity) {
        val profilePic1 = userInfoEntity.profile_pic
        val nickname = userInfoEntity.nickname
        val verified = userInfoEntity.verified
        val lastOnline1 = userInfoEntity.last_online
        val lastOnlineText = userInfoEntity.last_online_text
        val userToId = userInfoEntity.user_id
        val username = userInfoEntity.username
        lastOnline?.text = lastOnlineText
        img_send.setOnClickListener { view: View ->
            val body = et_message.text.toString().trim { it <= ' ' }
            if (et_message.text.toString().isNotEmpty() || imageUploaded.isNotEmpty()) {
                //sendMessage(username, body)
                viewModel.sendMessage(userTo, deviceUsername, deviceUserID, body, imageToUpload)
                hideKeyboardFrom(mCtx, view)
            } else {
                mCtx.toastLong("You must enter text before submitting!")
            }
        }
        usernameLayout.setOnClickListener { startActivity(Intent(mCtx, FragmentContainer::class.java).putExtra("user_to_id", userToId.toString())) }
        if (lastOnline1 == "yes") {
            online_view.visible(true)
        }
        if (verified == "yes") {
            verified_view.visible(true)
        }
        profileMessageToName.text = nickname
        val profilePic2 = profilePic1.substring(0, profilePic1.length - 4) + "_r.JPG"
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profilePic2)
                .into(profileMessageToImage)
        userMessageMenu.setOnClickListener { view: View? ->
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
                    SharedPrefManager.getInstance(mCtx)!!.blockUser(username)
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

    /*private fun updateUserInfoView(userInfoEntity: MessageUserInfoEntity){
        CoroutineScope(Main).launch {
            val profilePic1 = userInfoEntity.profile_pic
            val nickname = userInfoEntity.nickname
            val verified = userInfoEntity.verified
            val lastOnline1 = userInfoEntity.last_online
            val lastOnlineText = userInfoEntity.last_online_text
            val userToId = userInfoEntity.user_id
            val username = userInfoEntity.username
            lastOnline?.text = lastOnlineText
            img_send.setOnClickListener { view: View ->
                val body = et_message.text.toString().trim { it <= ' ' }
                if (et_message.text.toString().isNotEmpty() || imageUploaded.isNotEmpty()) {
                    sendMessage(username, body)
                    hideKeyboardFrom(mCtx, view)
                } else {
                    Toast.makeText(mCtx, "You must enter text before submitting!", Toast.LENGTH_LONG).show()
                }
            }
            usernameLayout.setOnClickListener { startActivity(Intent(mCtx, FragmentContainer::class.java).putExtra("user_to_id", userToId.toString())) }
            if (lastOnline1 == "yes") {
                online_view.visible(true)
            }
            if (verified == "yes") {
                verified_view.visible(true)
            }
            profileMessageToName.text = nickname
            val profilePic2 = profilePic1.substring(0, profilePic1.length - 4) + "_r.JPG"
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + profilePic2)
                    .into(profileMessageToImage)
            userMessageMenu.setOnClickListener { view: View? ->
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
                        SharedPrefManager.getInstance(mCtx)!!.blockUser(username)
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
    }*/

    private fun initRecycler(){
        recycler_chat_list.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mCtx)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recycler_chat_list.layoutManager = layoutManager
        adapter = MessagesThreadAdapter(mCtx, deviceUsername)
        recycler_chat_list.adapter = adapter
    }

    private fun updateNewMessages(data: UserMessagesFromID){
        if (!data.error){
            processMessages(data.messages)
            if (data.userLastOnline != "null") {
                lastOnline?.text = data.userLastOnline
                if (data.userLastOnline == "Online now") {
                    online_view?.visible(true)
                } else {
                    online_view?.visible(false)
                }
            }
        }
    }

    private fun appendRecycler(data: UserMessageData){
        val messageList = data.messages
        if (messageList.isNotEmpty()){
            lastId = messageList[0].message_id
            adapter.addItems(messageList)
            if (!recyclerInit){
                scrollToBottomNow()
                recyclerInit = true
            }
        }
        messageProgress?.visible(false)
        recycler_chat_list?.visible(true)
        rllt_text_box?.visible(true)
        newChats()
    }

    private fun processMessages(entityList: List<UserMessagesEntity>) {
        if (entityList.isNotEmpty()){
            for (data in entityList){
                lastId = data.message_id
            }
            val currentPost = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (currentPost == 0){
                adapter.addItems(entityList)
                scrollToBottom()
            }else{
                adapter.addItems(entityList)
                requireView().snackbar("New messages!", "Scroll down") { scrollToBottom() }
            }
        }
        newChats()
    }

    /*private fun sendMessage(user_string: String, message: String) {
        img_send.visibility = View.GONE
        sendProgress.visibility = View.VISIBLE
        if (message.equals("", ignoreCase = true)) return
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_SEND_MESSAGE, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    val messageId = jsonObject.getInt("messageid")
                    if (imageToUpload != null) {
                        messageImageUpload(imageToUpload!!, messageId, deviceUsername, user_string, message)
                    } else {
                        val m = UserMessagesEntity(messageId, userTo, deviceUsername, message, "Just now", "")
                        processMessages(listOf(m))
                        img_send.visibility = View.VISIBLE
                        sendProgress.visibility = View.GONE
                        viewModel.setTypedMessage("", userTo)
                    }
                    et_message.setText("")
                    launch { typedMessageDao.updateTypedMessageUser("",userTo) }
                } else {
                    Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    img_send.visibility = View.VISIBLE
                    sendProgress.visibility = View.GONE
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
                params["user_from"] = deviceUsername
                params["user_id"] = deviceUserID.toString()
                return params
            }
        }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }*/

    private fun scrollToBottom() {
        if (adapter.itemCount > 1) Objects.requireNonNull(recycler_chat_list?.layoutManager!!).smoothScrollToPosition(recycler_chat_list, null, 0)
    }

    private fun scrollToBottomNow() {
        if (adapter.itemCount > 1) Objects.requireNonNull(recycler_chat_list?.layoutManager!!).scrollToPosition(0)
    }

    /*private fun messageImageUpload(bitmap: Bitmap, message_id: Int, username: String?, user_string: String, message: String) {
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
                            img_attachment?.setImageResource(R.drawable.ic_attach_file_grey_24dp)
                            imageToUpload = null
                            imageUploaded = jsonObject.getString("imagepath")
                            val m = UserMessagesEntity(message_id, userTo, deviceUsername, message, "Just now", imageUploaded)
                            processMessages(listOf(m))
                            img_send.visibility = View.VISIBLE
                            sendProgress?.visibility = View.GONE
                            viewModel.setTypedMessage("", userTo)
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
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                var bitmap1: Bitmap? = null
                try {
                    if (Build.VERSION.SDK_INT >= 29) {
                        val source: ImageDecoder.Source = ImageDecoder.createSource(mCtx.contentResolver, resultUri)
                        try {
                            bitmap1 = ImageDecoder.decodeBitmap(source)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            bitmap1 = MediaStore.Images.Media.getBitmap(mCtx.contentResolver, resultUri)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    if (Build.VERSION.SDK_INT < 29){
                        img_attachment.setImageBitmap(bitmap1)
                    }else{
                        img_attachment.setImageResource(R.drawable.icons8_question_mark_64)
                        mCtx.toastLong("Cannot display image cropped! (Android 10+ temporary issue, upload should work as usual.)")
                    }

                    imageToUpload = bitmap1
                } catch (e: IOException) {
                    e.printStackTrace()
                    mCtx.toastShort("Failed!")
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
                            Toast.makeText(mCtx.applicationContext, "No permissions granted!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { Toast.makeText(mCtx.applicationContext, "Error!", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    private fun newChats() {
        if (canUpdate) {
            val chatHandler = Handler(Looper.getMainLooper())
            val runnableCode = Runnable { viewModel.getNewUserMessages(userTo, deviceUsername, deviceUserID, lastId) }
            chatHandler.postDelayed(runnableCode, 3000)
        } else {
            val chatHandler = Handler(Looper.getMainLooper())
            val runnableCode = Runnable { newChats() }
            chatHandler.postDelayed(runnableCode, 3000)
        }
    }

    companion object {
        //const val URL_SEND_MESSAGE: String = ROOT_URL + "messages.php/send"
        //private const val UPLOAD_IMAGE_URL = ROOT_URL + "message_image_upload.php"
        fun hideKeyboardFrom(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun getViewModel() = UserMessageVM::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = ContentChatBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserMessageRepo {
        val api = remoteDataSource.buildApi(UserMessageApi::class.java, mCtx.fcmToken)
        return UserMessageRepo(api, typedMessageDao, userMessagesDao, messageUserInfoDao)
    }
}