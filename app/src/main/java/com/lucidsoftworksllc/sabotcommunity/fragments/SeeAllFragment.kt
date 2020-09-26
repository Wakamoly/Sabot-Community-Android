package com.lucidsoftworksllc.sabotcommunity.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.ProfilenewsAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.PublicsTopicAdapter
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.PublicsTopicRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class SeeAllFragment : Fragment() {
    private var newsadapter: ProfilenewsAdapter? = null
    private var clanNewsAdapter: ProfilenewsAdapter? = null
    private var publicsnewsadapter: PublicsTopicAdapter? = null
    private var profilenewsRecyclerList: MutableList<ProfilenewsRecycler>? = null
    private var clanNewsRecyclerList: MutableList<ProfilenewsRecycler>? = null
    private var profilepublicsnewsRecyclerList: MutableList<PublicsTopicRecycler>? = null
    private var profileErrorScreen: RelativeLayout? = null
    private var progressBar: ProgressBar? = null
    private var recyclerSeeAll: RecyclerView? = null
    private var backButton: ImageView? = null
    private var queryID: String? = null
    private var thisUserID: String? = null
    private var thisUsername: String? = null
    private var method: String? = null
    private var queryIDextra: String? = null
    private var mCtx: Context? = null
    private var queryText: TextView? = null
    private var queryName: TextView? = null
    private var nothingToShow: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val seeAllRootView = inflater.inflate(R.layout.fragment_see_all, null)
        backButton = seeAllRootView.findViewById(R.id.backButton)
        recyclerSeeAll = seeAllRootView.findViewById(R.id.recyclerSeeAll)
        progressBar = seeAllRootView.findViewById(R.id.progressBar)
        queryText = seeAllRootView.findViewById(R.id.queryText)
        queryName = seeAllRootView.findViewById(R.id.queryName)
        profileErrorScreen = seeAllRootView.findViewById(R.id.profileErrorScreen)
        nothingToShow = seeAllRootView.findViewById(R.id.nothingToShow)
        mCtx = activity
        queryID = requireArguments().getString("queryid")
        queryIDextra = requireArguments().getString("queryidextra")
        method = requireArguments().getString("method")
        thisUserID = SharedPrefManager.getInstance(mCtx!!)!!.userID
        thisUsername = SharedPrefManager.getInstance(mCtx!!)!!.username
        profilenewsRecyclerList = ArrayList()
        profilepublicsnewsRecyclerList = ArrayList()
        clanNewsRecyclerList = ArrayList()
        recyclerSeeAll?.setHasFixedSize(true)
        recyclerSeeAll?.layoutManager = LinearLayoutManager(mCtx)
        if (method == "publics") {
            queryText?.text = getString(R.string.publics_posts_text)
            loadPublics()
        }
        if (method == "posts") {
            queryText?.text = getString(R.string.profile_posts)
            loadPosts()
        }
        if (method == "clan_posts") {
            queryText?.text = getString(R.string.clan_posts)
            loadClanPosts()
        }
        backButton?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        return seeAllRootView
    }

    private fun loadPublics() {
        val stringRequest = StringRequest(Request.Method.GET, "$SEE_ALL_URL?queryid=$queryID&queryidextra=$queryIDextra&method=$method&userid=$thisUserID&deviceusername=$thisUsername", { response: String? ->
            try {
                val profilepublicsnews = JSONArray(response)
                for (i in 0 until profilepublicsnews.length()) {
                    val profilenewsObject = profilepublicsnews.getJSONObject(i)
                    val username = profilenewsObject.getString("username")
                    if (SharedPrefManager.getInstance(mCtx!!)!!.isUserBlocked(username)) continue
                    val id = profilenewsObject.getString("id")
                    val numposts = profilenewsObject.getString("numposts")
                    val subject = profilenewsObject.getString("subject")
                    val date = profilenewsObject.getString("date")
                    val cat = profilenewsObject.getString("cat")
                    val topicBy = profilenewsObject.getString("topic_by")
                    val type = profilenewsObject.getString("type")
                    val userId = profilenewsObject.getString("user_id")
                    val profilePic = profilenewsObject.getString("profile_pic")
                    val nickname = profilenewsObject.getString("nickname")
                    val eventDate = profilenewsObject.getString("event_date")
                    val zone = profilenewsObject.getString("zone")
                    val context = profilenewsObject.getString("context")
                    val numPlayers = profilenewsObject.getString("num_players")
                    val numAdded = profilenewsObject.getString("num_added")
                    val gamename = profilenewsObject.getString("gamename")
                    val publicsTopicResult = PublicsTopicRecycler(id, numposts, subject, date, cat, topicBy, type, userId, profilePic, nickname, username, eventDate, zone, context, numPlayers, numAdded, gamename)
                    profilepublicsnewsRecyclerList!!.add(publicsTopicResult)
                }
                if (profilepublicsnews.length() == 0) {
                    nothingToShow!!.visibility = View.VISIBLE
                }
                publicsnewsadapter = PublicsTopicAdapter(mCtx!!, profilepublicsnewsRecyclerList!!)
                recyclerSeeAll!!.adapter = publicsnewsadapter
                progressBar!!.visibility = View.GONE
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mCtx, "Network error!", Toast.LENGTH_SHORT).show() }
        (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadPosts() {
        val stringRequest = StringRequest(Request.Method.GET, "$SEE_ALL_URL?queryid=$queryID&queryidextra=$queryIDextra&method=$method&userid=$thisUserID&deviceusername=$thisUsername", { response: String? ->
            try {
                val profilenews = JSONArray(response)
                for (i in 0 until profilenews.length()) {
                    val profilenewsObject = profilenews.getJSONObject(i)
                    val id = profilenewsObject.getInt("id")
                    val type = profilenewsObject.getString("type")
                    val likes = profilenewsObject.getString("likes")
                    val body = profilenewsObject.getString("body")
                    val addedBy = profilenewsObject.getString("added_by")
                    val userTo = profilenewsObject.getString("user_to")
                    val dateAdded = profilenewsObject.getString("date_added")
                    val userClosed = profilenewsObject.getString("user_closed")
                    val deleted = profilenewsObject.getString("deleted")
                    val image = profilenewsObject.getString("image")
                    val userId = profilenewsObject.getString("user_id")
                    val profilePic = profilenewsObject.getString("profile_pic")
                    val verified = profilenewsObject.getString("verified")
                    val online = profilenewsObject.getString("online")
                    val nickname = profilenewsObject.getString("nickname")
                    val username = profilenewsObject.getString("username")
                    val commentcount = profilenewsObject.getString("commentcount")
                    val likedbyuserYes = profilenewsObject.getString("likedbyuseryes")
                    val form = profilenewsObject.getString("form")
                    val edited = profilenewsObject.getString("edited")
                    val profilenewsResult = ProfilenewsRecycler(id, type, likes, body, addedBy, userTo, dateAdded, userClosed, deleted, image, userId, profilePic, verified, online, nickname, username, commentcount, likedbyuserYes, form, edited)
                    profilenewsRecyclerList!!.add(profilenewsResult)
                }
                if (profilenews.length() == 0) {
                    nothingToShow!!.visibility = View.VISIBLE
                }
                newsadapter = ProfilenewsAdapter(mCtx!!, profilenewsRecyclerList)
                recyclerSeeAll!!.adapter = newsadapter
                progressBar!!.visibility = View.GONE
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { profileErrorScreen!!.visibility = View.VISIBLE }
        (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadClanPosts() {
        val stringRequest = StringRequest(Request.Method.GET, "$SEE_ALL_URL?queryid=$queryID&queryidextra=$queryIDextra&method=$method&userid=$thisUserID&deviceusername=$thisUsername", { response: String? ->
            try {
                val clannews = JSONArray(response)
                for (i in 0 until clannews.length()) {
                    val clannewsObject = clannews.getJSONObject(i)
                    val id = clannewsObject.getInt("id")
                    val type = clannewsObject.getString("type")
                    val likes = clannewsObject.getString("likes")
                    val body = clannewsObject.getString("body")
                    val addedBy = clannewsObject.getString("added_by")
                    val userTo = clannewsObject.getString("user_to")
                    val dateAdded = clannewsObject.getString("date_added")
                    val userClosed = clannewsObject.getString("user_closed")
                    val deleted = clannewsObject.getString("deleted")
                    val image = clannewsObject.getString("image")
                    val userId = clannewsObject.getString("user_id")
                    val profilePic = clannewsObject.getString("profile_pic")
                    val verified = clannewsObject.getString("verified")
                    val online = clannewsObject.getString("online")
                    val nickname = clannewsObject.getString("nickname")
                    val username = clannewsObject.getString("username")
                    val commentcount = clannewsObject.getString("commentcount")
                    val likedbyuserYes = clannewsObject.getString("likedbyuseryes")
                    val form = clannewsObject.getString("form")
                    val edited = clannewsObject.getString("edited")
                    val clanpostsRecycler = ProfilenewsRecycler(id, type, likes, body, addedBy, userTo, dateAdded, userClosed, deleted, image, userId, profilePic, verified, online, nickname, username, commentcount, likedbyuserYes, form, edited)
                    clanNewsRecyclerList!!.add(clanpostsRecycler)
                }
                clanNewsAdapter = ProfilenewsAdapter(mCtx!!, clanNewsRecyclerList)
                recyclerSeeAll!!.adapter = clanNewsAdapter
                if (clannews.length() == 0) {
                    nothingToShow!!.visibility = View.VISIBLE
                }
                progressBar!!.visibility = View.GONE
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mCtx, "Network error!", Toast.LENGTH_SHORT).show() }
        (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val SEE_ALL_URL = Constants.ROOT_URL + "see_all.php"
    }
}