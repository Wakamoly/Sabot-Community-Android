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
import com.lucidsoftworksllc.sabotcommunity.adapters.PublicsTopicAdapter
import com.lucidsoftworksllc.sabotcommunity.models.PublicsTopicRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class PublicsPrevious : Fragment() {
    private var publicsnewsadapter: PublicsTopicAdapter? = null
    private var profilepublicsnewsRecyclerList: MutableList<PublicsTopicRecycler>? = null
    private var profileErrorScreen: RelativeLayout? = null
    private var progressBar: ProgressBar? = null
    private var recyclerSeeAll: RecyclerView? = null
    private var backButton: ImageView? = null
    private var gameID: String? = null
    private var thisUserID: String? = null
    private var thisUsername: String? = null
    private var gamename: String? = null
    private var mCtx: Context? = null
    private var queryName: TextView? = null
    private var nothingToShow: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val seeAllRootView = inflater.inflate(R.layout.fragment_see_all, null)
        backButton = seeAllRootView.findViewById(R.id.backButton)
        recyclerSeeAll = seeAllRootView.findViewById(R.id.recyclerSeeAll)
        progressBar = seeAllRootView.findViewById(R.id.progressBar)
        queryName = seeAllRootView.findViewById(R.id.queryName)
        profileErrorScreen = seeAllRootView.findViewById(R.id.profileErrorScreen)
        nothingToShow = seeAllRootView.findViewById(R.id.nothingToShow)
        mCtx = activity
        gameID = requireArguments().getString("GameId")
        gamename = requireArguments().getString("gamename")
        thisUserID = SharedPrefManager.getInstance(mCtx!!)!!.userID
        thisUsername = SharedPrefManager.getInstance(mCtx!!)!!.username
        profilepublicsnewsRecyclerList = ArrayList()
        recyclerSeeAll?.setHasFixedSize(true)
        recyclerSeeAll?.layoutManager = LinearLayoutManager(mCtx)
        queryName?.text = gamename
        loadPublics()
        backButton?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        return seeAllRootView
    }

    private fun loadPublics() {
        val stringRequest = StringRequest(Request.Method.GET, "$PREVIOUS_PUBLICS?publicsid=$gameID&userid=$thisUserID&username=$thisUsername", { response: String? ->
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
                    val publicsTopicResult = PublicsTopicRecycler(id, numposts, subject, date, cat, topicBy, type, userId, profilePic, nickname, username, eventDate, zone, context, numPlayers, numAdded, gamename!!)
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
        }) {
            Toast.makeText(mCtx, "Network error!", Toast.LENGTH_SHORT).show()
            progressBar!!.visibility = View.GONE
            profileErrorScreen!!.visibility = View.VISIBLE
        }
        (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val PREVIOUS_PUBLICS = Constants.ROOT_URL + "previous_publics.php"
    }
}