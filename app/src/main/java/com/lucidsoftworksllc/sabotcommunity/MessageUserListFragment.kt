package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class MessageUserListFragment : Fragment(), MessageUserListAdapter.AdapterCallback {
    private var backButton: ImageView? = null
    private var queryName: TextView? = null
    private var nothingToShow: TextView? = null
    private var progressBar: ProgressBar? = null
    private var recyclerSeeAll: RecyclerView? = null
    private var profileErrorScreen: RelativeLayout? = null
    private var mContext: Context? = null
    private var deviceUserID: String? = null
    private var deviceUsername: String? = null
    private var query: String? = null
    private var queryID: String? = null
    private var permission: String? = null
    private var owner: String? = null
    private var canRemove: String? = null
    private var userRecyclerList: MutableList<MessageUserListRecycler>? = null
    private var userlistAdapter: MessageUserListAdapter? = null
    override fun onMethodCallback() {
        val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.chat_fragment_container)
        if (currentFragment is MessageUserListFragment) {
            val fragTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out)
            fragTransaction.detach(currentFragment)
            fragTransaction.attach(currentFragment)
            fragTransaction.commit()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val userListRootView = inflater.inflate(R.layout.fragment_user_list, null)
        backButton = userListRootView.findViewById(R.id.backButton)
        queryName = userListRootView.findViewById(R.id.queryName)
        nothingToShow = userListRootView.findViewById(R.id.nothingToShow)
        progressBar = userListRootView.findViewById(R.id.progressBar)
        profileErrorScreen = userListRootView.findViewById(R.id.profileErrorScreen)
        recyclerSeeAll = userListRootView.findViewById(R.id.recyclerSeeAll)
        mContext = activity
        deviceUserID = SharedPrefManager.getInstance(mContext!!)!!.userID
        deviceUsername = SharedPrefManager.getInstance(mContext!!)!!.username
        userRecyclerList = ArrayList()
        recyclerSeeAll?.setHasFixedSize(true)
        recyclerSeeAll?.layoutManager = LinearLayoutManager(mContext)
        if (arguments != null) {
            query = requireArguments().getString("query")
            queryID = requireArguments().getString("queryID")
            permission = requireArguments().getString("permission")
            owner = requireArguments().getString("owner")
            canRemove = requireArguments().getString("canRemove")
        } else {
            profileErrorScreen?.visibility = View.VISIBLE
        }
        when (query) {
            "players_added" -> queryName?.text = getString(R.string.players_added)
        }
        backButton?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        getQuery()
        return userListRootView
    }

    private fun getQuery() {
        val stringRequest = StringRequest(Request.Method.GET, "$SEE_ALL_URL?queryid=$queryID&query=$query&userid=$deviceUserID&deviceusername=$deviceUsername", { response: String? ->
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
                    val position = profilenewsObject.getString("position")
                    val result = MessageUserListRecycler(id, userId, profilePic, nickname, username, verified, online, desc, position, owner!!, canRemove!!, queryID!!)
                    userRecyclerList!!.add(result)
                }
                if (profilepublicsnews.length() == 0) {
                    nothingToShow!!.visibility = View.VISIBLE
                }
                userlistAdapter = MessageUserListAdapter(userRecyclerList!!, mContext!!, this@MessageUserListFragment)
                recyclerSeeAll!!.adapter = userlistAdapter
                progressBar!!.visibility = View.GONE
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) {
            profileErrorScreen!!.visibility = View.VISIBLE
            progressBar!!.visibility = View.GONE
        }
        (mContext as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val SEE_ALL_URL = Constants.ROOT_URL + "message_user_list_query.php"
    }
}