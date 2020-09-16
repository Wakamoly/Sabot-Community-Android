package com.lucidsoftworksllc.sabotcommunity

import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class NewGroupMessage : Fragment(), NewGroupMessageUserAdapter.AdapterCallback, UserListGroupMessageAdapter.AdapterCallback {
    private var newMessageInsignia: ImageView? = null
    private var setInsigniaButton: RelativeLayout? = null
    private var etNewGroupMessageName: EditText? = null
    private var newMessageSearch: SearchView? = null
    private var recyclerSearch: RecyclerView? = null
    private var recyclerUsersToInvite: RecyclerView? = null
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
    private var apiInterface: UserApiInterface? = null
    private var users: List<User?>? = null
    private var adapter: NewGroupMessageUserAdapter? = null
    private var adapter2: UserListGroupMessageAdapter? = null
    private var newGroupMessageProgressBar: ProgressBar? = null
    private var mContext: Context? = null
    private var searchRecyclerList: List<SearchRecycler>? = null
    private var userListRecycler: MutableList<UserListRecycler>? = null
    private var usersToInvite: ArrayList<String>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var layoutManager2: RecyclerView.LayoutManager? = null
    private val galleryInsignia = 2
    private var newGroupInsigniabitmap: Bitmap? = null
    var jsonObject: JSONObject? = null
    var rQueue: RequestQueue? = null

    override fun onMethodCallbackUserList(position: Int, username: String?) {
        userListRecycler!!.removeAt(position)
        adapter2!!.notifyDataSetChanged()
        usersToInvite!!.remove(username)
        Toast.makeText(mContext, "Removed @$username", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newGroupRootView = inflater.inflate(R.layout.fragment_new_group_message, null)
        newMessageInsignia = newGroupRootView.findViewById(R.id.new_message_insignia)
        setInsigniaButton = newGroupRootView.findViewById(R.id.setInsigniaButton)
        etNewGroupMessageName = newGroupRootView.findViewById(R.id.etNewGroupMessageName)
        newMessageSearch = newGroupRootView.findViewById(R.id.newMessageSearch)
        recyclerSearch = newGroupRootView.findViewById(R.id.recyclerSearch)
        recyclerUsersToInvite = newGroupRootView.findViewById(R.id.recyclerUsersToInvite)
        allUsersCanInvite = newGroupRootView.findViewById(R.id.allUsersCanInvite)
        adminsCanInvite = newGroupRootView.findViewById(R.id.adminsCanInvite)
        allUsersCanChangeGroupImage = newGroupRootView.findViewById(R.id.allUsersCanChangeGroupImage)
        adminsCanChangeGroupImage = newGroupRootView.findViewById(R.id.adminsCanChangeGroupImage)
        allUsersCanChangeGroupName = newGroupRootView.findViewById(R.id.allUsersCanChangeGroupName)
        adminsCanChangeGroupName = newGroupRootView.findViewById(R.id.adminsCanChangeGroupName)
        allUsersCanMessage = newGroupRootView.findViewById(R.id.allUsersCanMessage)
        adminsCanMessage = newGroupRootView.findViewById(R.id.adminsCanMessage)
        adminsCanRemoveUsers = newGroupRootView.findViewById(R.id.adminsCanRemoveUsers)
        btnSubmit = newGroupRootView.findViewById(R.id.btnSubmit)
        newGroupMessageProgressBar = newGroupRootView.findViewById(R.id.newGroupMessageProgressBar)
        mContext = activity
        newGroupInsigniabitmap = null
        Glide.with(mContext!!)
                .load(Constants.BASE_URL + "assets/images/profile_pics/defaults/sabotblack.gif")
                .into(newMessageInsignia!!)
        searchRecyclerList = ArrayList()
        userListRecycler = ArrayList()
        usersToInvite = ArrayList()
        layoutManager = LinearLayoutManager(mContext)
        recyclerSearch?.layoutManager = layoutManager
        layoutManager2 = LinearLayoutManager(mContext)
        recyclerUsersToInvite?.layoutManager = layoutManager2
        adapter2 = UserListGroupMessageAdapter(userListRecycler!!, mContext!!, this@NewGroupMessage)
        recyclerUsersToInvite?.adapter = adapter2
        setInsigniaButton?.setOnClickListener {
            requestMultiplePermissions()
            val galleryIntent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, galleryInsignia)
        }
        btnSubmit?.setOnClickListener {
            if (etNewGroupMessageName?.text.toString().length in 3..25) {
                newGroupMessageProgressBar?.visibility = View.VISIBLE
                btnSubmit?.visibility = View.GONE
                val optionArray = groupOptions
                val inviteArray = usersToInvite.toString()
                submitGroupMessage(etNewGroupMessageName?.text.toString(), optionArray, inviteArray)
            } else {
                Toast.makeText(mContext, "Group name must be 3-25 characters long", Toast.LENGTH_SHORT).show()
            }
        }
        allUsersCanInvite?.setOnClickListener {
            if (allUsersCanInvite?.isChecked!!) {
                adminsCanInvite?.isChecked = false
                adminsCanInvite?.visibility = View.GONE
            } else {
                adminsCanInvite?.visibility = View.VISIBLE
            }
        }
        allUsersCanChangeGroupImage?.setOnClickListener {
            if (allUsersCanChangeGroupImage?.isChecked!!) {
                adminsCanChangeGroupImage?.isChecked = false
                adminsCanChangeGroupImage?.visibility = View.GONE
            } else {
                adminsCanChangeGroupImage?.visibility = View.VISIBLE
            }
        }
        allUsersCanChangeGroupName?.setOnClickListener {
            if (allUsersCanChangeGroupName?.isChecked!!) {
                adminsCanChangeGroupName?.isChecked = false
                adminsCanChangeGroupName?.visibility = View.GONE
            } else {
                adminsCanChangeGroupName?.visibility = View.VISIBLE
            }
        }
        allUsersCanMessage?.setOnClickListener {
            if (allUsersCanMessage?.isChecked!!) {
                adminsCanMessage?.isChecked = false
                adminsCanMessage?.visibility = View.GONE
            } else {
                adminsCanMessage?.visibility = View.VISIBLE
            }
        }
        newMessageSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                fetchUser("users_only", query)
                if (query.isNotEmpty()) {
                    recyclerSearch?.visibility = View.VISIBLE
                } else {
                    recyclerSearch?.visibility = View.GONE
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val handler = Handler()
                handler.postDelayed({
                    fetchUser("users_only", newText)
                    if (newText.isNotEmpty()) {
                        recyclerSearch?.visibility = View.VISIBLE
                    } else {
                        recyclerSearch?.visibility = View.GONE
                    }
                }, 100)
                return false
            }
        })
        return newGroupRootView
    }

    fun fetchUser(type: String?, key: String?) {
        apiInterface = UsersApiClient.apiClient?.create(UserApiInterface::class.java)
        val call = apiInterface?.getUsers(type, key)
        call?.enqueue(object : Callback<List<User?>?> {
            override fun onResponse(call: Call<List<User?>?>, response: Response<List<User?>?>) {
                users = response.body()
                adapter = NewGroupMessageUserAdapter(users!! as MutableList<User>, mContext!!, this@NewGroupMessage)
                recyclerSearch!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<User?>?>, t: Throwable) {
                recyclerSearch!!.visibility = View.INVISIBLE
                Toast.makeText(context, "Error\n$t", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.searchView).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.isSubmitButtonEnabled = true
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                fetchUser("users_only", query)
                if (query.isNotEmpty()) {
                    recyclerSearch!!.visibility = View.VISIBLE
                } else {
                    recyclerSearch!!.visibility = View.GONE
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val handler = Handler()
                handler.postDelayed({
                    fetchUser("users_only", newText)
                    if (newText.isNotEmpty()) {
                        recyclerSearch!!.visibility = View.VISIBLE
                    } else {
                        recyclerSearch!!.visibility = View.GONE
                    }
                }, 100)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
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

    private fun submitGroupMessage(groupMessageName: String, optionArray: String, inviteArray: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_GROUP_SUBMIT, com.android.volley.Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    val groupId = jsonObject.getString("groupid")
                    if (newGroupInsigniabitmap != null) {
                        uploadInsigniaImage(newGroupInsigniabitmap!!, groupId, etNewGroupMessageName!!.text.toString())
                    }
                    val ldf = ConvosFragment()
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().replace(R.id.chat_fragment_container, ldf).commit()
                } else {
                    Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show()
                    btnSubmit!!.visibility = View.VISIBLE
                    newGroupMessageProgressBar!!.visibility = View.GONE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, com.android.volley.Response.ErrorListener {
            newGroupMessageProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Network Error!", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["group_name"] = groupMessageName
                params["option_array"] = optionArray
                params["invite_array"] = inviteArray
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
                            bitmap1 = MediaStore.Images.Media.getBitmap(mContext!!.contentResolver, contentURI!!)
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

    private fun uploadInsigniaImage(bitmap: Bitmap, group_id: String, groupname: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 65, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        try {
            jsonObject = JSONObject()
            val imgname = Calendar.getInstance().timeInMillis.toString()
            jsonObject!!.put("name", imgname)
            jsonObject!!.put("group_id", group_id)
            jsonObject!!.put("group_name", groupname)
            jsonObject!!.put("image", encodedImage)
            jsonObject!!.put("username", SharedPrefManager.getInstance(mContext!!)!!.username)
        } catch (ignored: JSONException) {
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
        const val URL_GROUP_SUBMIT = Constants.ROOT_URL + "messages.php/newgroup"
        const val UPLOAD_INSIGNIA_URL = Constants.ROOT_URL + "uploadInsigniaGroupMessage.php"
    }

    override fun onNewGroupMethodCallback(user_id: String?, profile_pic: String?, nickname: String?, username: String?, verified: String?, online: String?) {
        var isObjectExist = false
        for (i in userListRecycler!!.indices) {
            val thingy = userListRecycler!![i].user_id
            if (thingy == user_id) {
                isObjectExist = true
                break
            }
        }
        if (!isObjectExist) {
            val userToAdd = UserListRecycler(null.toString(), user_id!!, profile_pic!!, nickname!!, username!!, verified!!, "", null.toString())
            userListRecycler!!.add(userToAdd)
            adapter2!!.notifyDataSetChanged()
            usersToInvite!!.add(username!!)
            Toast.makeText(mContext, "Adding @$username", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(mContext, "Already added @$username!", Toast.LENGTH_SHORT).show()
        }
    }

}