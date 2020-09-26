package com.lucidsoftworksllc.sabotcommunity.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.adapters.NewGroupMessageUserAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.UserListGroupMessageAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.UserListGroupMessageCurrentAdapter
import com.lucidsoftworksllc.sabotcommunity.models.SearchRecycler
import com.lucidsoftworksllc.sabotcommunity.models.UserListRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MessageGroupInviteFragment : Fragment(), NewGroupMessageUserAdapter.AdapterCallback, UserListGroupMessageAdapter.AdapterCallback {
    private var messageSearch: SearchView? = null
    private var recyclerSearch: RecyclerView? = null
    private var recyclerUsersToInvite: RecyclerView? = null
    private var recyclerUsersInGroup: RecyclerView? = null
    private var btnSubmit: Button? = null
    private var apiInterface: UserApiInterface? = null
    private var users: List<User?>? = null
    private var noPlayers: TextView? = null
    private var adapter: NewGroupMessageUserAdapter? = null
    private var adapter2: UserListGroupMessageAdapter? = null
    private var adapter3: UserListGroupMessageCurrentAdapter? = null
    private var groupMessageInviteProgressBar: ProgressBar? = null
    private var currentPlayersProgress: ProgressBar? = null
    private var mContext: Context? = null
    private var searchRecyclerList: List<SearchRecycler>? = null
    private var userListRecycler: MutableList<UserListRecycler>? = null
    private var playersInGroupRecycler: MutableList<UserListRecycler>? = null
    private var usersToInvite: ArrayList<String>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var layoutManager2: RecyclerView.LayoutManager? = null
    private var layoutManager3: RecyclerView.LayoutManager? = null
    private val userRecyclerList: List<UserListRecycler>? = null
    private var query: String? = null
    private var queryID: String? = null
    private var deviceUserID: String? = null
    private var deviceUsername: String? = null

    fun onMethodCallback(user_id: String, profile_pic: String, nickname: String, username: String, verified: String, online: String) {
        var isObjectExist = false
        for (i in userListRecycler!!.indices) {
            val thingy = userListRecycler!![i].user_id
            if (thingy == user_id) {
                isObjectExist = true
                break
            }
        }
        for (i in playersInGroupRecycler!!.indices) {
            val thingy = playersInGroupRecycler!![i].user_id
            if (thingy == user_id) {
                isObjectExist = true
                break
            }
        }
        if (!isObjectExist) {
            var isObjectExist2 = false
            for (i in playersInGroupRecycler!!.indices) {
                val thingy = playersInGroupRecycler!![i].user_id
                if (thingy == user_id) {
                    isObjectExist2 = true
                    break
                }
            }
            if (!isObjectExist2) {
                val userToAdd = UserListRecycler(null.toString(), user_id, profile_pic, nickname, username, verified, "", null.toString())
                userListRecycler!!.add(userToAdd)
                adapter2!!.notifyDataSetChanged()
                usersToInvite!!.add(username)
                Toast.makeText(mContext, "Adding @$username to invite queue", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(mContext, "@$username is already in the group!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(mContext, "Already added @$username to invite queue!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMethodCallbackUserList(position: Int, username: String?) {
        userListRecycler!!.removeAt(position)
        adapter2!!.notifyDataSetChanged()
        usersToInvite!!.remove(username)
        Toast.makeText(mContext, "Removed @$username", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newGroupInviteRootView = inflater.inflate(R.layout.fragment_groupmessage_invite_players, null)
        messageSearch = newGroupInviteRootView.findViewById(R.id.messageSearch)
        recyclerUsersInGroup = newGroupInviteRootView.findViewById(R.id.recyclerUsersInGroup)
        recyclerSearch = newGroupInviteRootView.findViewById(R.id.recyclerSearch)
        recyclerUsersToInvite = newGroupInviteRootView.findViewById(R.id.recyclerUsersToInvite)
        noPlayers = newGroupInviteRootView.findViewById(R.id.noPlayers)
        btnSubmit = newGroupInviteRootView.findViewById(R.id.btnSubmit)
        currentPlayersProgress = newGroupInviteRootView.findViewById(R.id.currentPlayersProgress)
        groupMessageInviteProgressBar = newGroupInviteRootView.findViewById(R.id.groupMessageInviteProgressBar)
        mContext = activity
        if (arguments != null) {
            query = "group_players_added"
            queryID = requireArguments().getString("group_id")
            deviceUserID = SharedPrefManager.getInstance(mContext!!)!!.userID
            deviceUsername = SharedPrefManager.getInstance(mContext!!)!!.username
        }
        searchRecyclerList = ArrayList()
        userListRecycler = ArrayList()
        playersInGroupRecycler = ArrayList()
        usersToInvite = ArrayList()
        layoutManager = LinearLayoutManager(mContext)
        recyclerSearch?.layoutManager = layoutManager
        layoutManager2 = LinearLayoutManager(mContext)
        recyclerUsersToInvite?.layoutManager = layoutManager2
        adapter2 = UserListGroupMessageAdapter(userListRecycler!!, mContext!!, this@MessageGroupInviteFragment)
        recyclerUsersToInvite?.adapter = adapter2
        layoutManager3 = LinearLayoutManager(mContext)
        btnSubmit?.setOnClickListener {
            if (usersToInvite!!.isNotEmpty()) {
                groupMessageInviteProgressBar?.visibility = View.VISIBLE
                btnSubmit?.visibility = View.GONE
                val inviteArray = usersToInvite.toString()
                inviteUsers(queryID, inviteArray)
            } else {
                Toast.makeText(mContext, "Group name must be 3-25 characters long", Toast.LENGTH_SHORT).show()
            }
        }
        messageSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        players
        return newGroupInviteRootView
    }

    private fun inviteUsers(group_id: String?, invite_array: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_INVITE_SUBMIT,
                com.android.volley.Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.getString("error") == "false") {
                            Toast.makeText(mContext, "Invited player(s)!", Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.popBackStackImmediate()
                        } else {
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                            groupMessageInviteProgressBar!!.visibility = View.GONE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener { }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["group_id"] = group_id!!
                params["invite_array"] = invite_array
                params["username"] = deviceUsername!!
                params["user_id"] = deviceUserID!!
                return params
            }
        }
        (mContext as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    private val players: Unit
        get() {
            val stringRequest = StringRequest(Request.Method.GET, "$URL_CURRENT_PLAYERS?queryid=$queryID&query=$query&userid=$deviceUserID&deviceusername=$deviceUsername", { response: String? ->
                try {
                    val profilepublicsnews = JSONArray(response)
                    for (i in 0 until profilepublicsnews.length()) {
                        val profilenewsObject = profilepublicsnews.getJSONObject(i)
                        val username = profilenewsObject.getString("username")
                        val id = profilenewsObject.getString("id")
                        val userId = profilenewsObject.getString("user_id")
                        val profilePic = profilenewsObject.getString("profile_pic")
                        val nickname = profilenewsObject.getString("nickname")
                        val verified = profilenewsObject.getString("verified")
                        val online = profilenewsObject.getString("online")
                        val desc = profilenewsObject.getString("desc")
                        val result = UserListRecycler(id, userId, profilePic, nickname, username, verified, online, desc)
                        playersInGroupRecycler!!.add(result)
                    }
                    recyclerUsersInGroup!!.layoutManager = layoutManager3
                    adapter3 = UserListGroupMessageCurrentAdapter(playersInGroupRecycler!!, mContext!!)
                    recyclerUsersInGroup!!.adapter = adapter3
                    if (profilepublicsnews.length() == 0) {
                        noPlayers!!.visibility = View.VISIBLE
                    }
                    currentPlayersProgress!!.visibility = View.GONE
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { currentPlayersProgress!!.visibility = View.GONE }
            (mContext as ChatActivity?)!!.addToRequestQueue(stringRequest)
        }

    fun fetchUser(type: String?, key: String?) {
        apiInterface = UsersApiClient.apiClient!!.create(UserApiInterface::class.java)
        val call = apiInterface?.getUsers(type, key)
        call?.enqueue(object : Callback<List<User?>?> {
            override fun onResponse(call: Call<List<User?>?>, response: Response<List<User?>?>) {
                users = response.body()
                adapter = NewGroupMessageUserAdapter(users!! as MutableList<User>, mContext!!, this@MessageGroupInviteFragment)
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

    companion object {
        const val URL_INVITE_SUBMIT = Constants.ROOT_URL + "messages.php/invite_players"
        const val URL_CURRENT_PLAYERS = Constants.ROOT_URL + "message_user_list_query.php"
    }

    override fun onNewGroupMethodCallback(user_id: String?, profile_pic: String?, nickname: String?, username: String?, verified: String?, online: String?) {
        TODO("Not yet implemented")
    }
}