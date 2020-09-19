package com.lucidsoftworksllc.sabotcommunity.fragments

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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.lucidsoftworksllc.sabotcommunity.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.SharedPrefManager.Companion.getInstance
import com.lucidsoftworksllc.sabotcommunity.adapters.UserListAdapter
import com.lucidsoftworksllc.sabotcommunity.models.UserListRecycler
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class UserListFragment : Fragment() {
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
    private var userRecyclerList: MutableList<UserListRecycler>? = null
    private var userlistAdapter: UserListAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val userListRootView = inflater.inflate(R.layout.fragment_user_list, null)
        backButton = userListRootView.findViewById(R.id.backButton)
        queryName = userListRootView.findViewById(R.id.queryName)
        nothingToShow = userListRootView.findViewById(R.id.nothingToShow)
        progressBar = userListRootView.findViewById(R.id.progressBar)
        profileErrorScreen = userListRootView.findViewById(R.id.profileErrorScreen)
        recyclerSeeAll = userListRootView.findViewById(R.id.recyclerSeeAll)
        mContext = activity
        deviceUserID = getInstance(mContext!!)!!.userID
        deviceUsername = getInstance(mContext!!)!!.username
        userRecyclerList = ArrayList()
        recyclerSeeAll?.setHasFixedSize(true)
        recyclerSeeAll?.layoutManager = LinearLayoutManager(mContext)
        if (arguments != null) {
            query = requireArguments().getString("query")
            queryID = requireArguments().getString("queryID")
        } else {
            profileErrorScreen?.visibility = View.VISIBLE
        }
        when (query) {
            "connections" -> queryName?.text = getString(R.string.connections)
            "followers" -> queryName?.text = getString(R.string.label_followers)
            "following" -> queryName?.text = getString(R.string.followingTextProfile)
            "comment" -> queryName?.text = getString(R.string.comment_likes)
            "post" -> queryName?.text = getString(R.string.post_likes)
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
                    if (getInstance(mContext!!)!!.isUserBlocked(username)) continue
                    val id = profilenewsObject.getString("id")
                    val userId = profilenewsObject.getString("user_id")
                    val profilePic = profilenewsObject.getString("profile_pic")
                    val nickname = profilenewsObject.getString("nickname")
                    val verified = profilenewsObject.getString("verified")
                    val online = profilenewsObject.getString("online")
                    val desc = profilenewsObject.getString("desc")
                    val publicsTopicResult = UserListRecycler(id, userId, profilePic, nickname, username, verified, online, desc)
                    userRecyclerList!!.add(publicsTopicResult)
                }
                if (profilepublicsnews.length() == 0) {
                    nothingToShow!!.visibility = View.VISIBLE
                }
                userlistAdapter = UserListAdapter(userRecyclerList!!, mContext!!)
                recyclerSeeAll!!.adapter = userlistAdapter
                progressBar!!.visibility = View.GONE
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) {
            profileErrorScreen!!.visibility = View.VISIBLE
            progressBar!!.visibility = View.GONE
        }
        Volley.newRequestQueue(mContext).add(stringRequest)
    }

    companion object {
        private const val SEE_ALL_URL = Constants.ROOT_URL + "user_list_query.php"
    }
}