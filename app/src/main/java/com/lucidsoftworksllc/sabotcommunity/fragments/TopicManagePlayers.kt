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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.SharedPrefManager.Companion.getInstance
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.PublicsPlayersAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.PublicsPlayersNonAdminAdapter
import com.lucidsoftworksllc.sabotcommunity.models.PublicsPlayersRecycler
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class TopicManagePlayers : Fragment() {
    private var clanNameTop: TextView? = null
    private var noMemberRequests: TextView? = null
    private var noMembers: TextView? = null
    private var topicID: String? = null
    private var userID: String? = null
    private var username: String? = null
    private var permission: String? = null
    private var mContext: Context? = null
    private var backArrow: ImageView? = null
    private var recyclerMembers: RecyclerView? = null
    private var recyclerMembersJoined: RecyclerView? = null
    private var manageMembersSwipe: SwipeRefreshLayout? = null
    private var progressBar: ProgressBar? = null
    private var memberRequestsLayout: RelativeLayout? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: PublicsPlayersAdapter? = null
    private var adapterNonAdmin: PublicsPlayersNonAdminAdapter? = null
    private var requestsRecyclerList: MutableList<PublicsPlayersRecycler>? = null
    private var membersRecyclerList: MutableList<PublicsPlayersRecycler>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val manageMembersRootView = inflater.inflate(R.layout.fragment_clan_member_manage, null)
        clanNameTop = manageMembersRootView.findViewById(R.id.clanNameTop)
        backArrow = manageMembersRootView.findViewById(R.id.backArrow)
        recyclerMembers = manageMembersRootView.findViewById(R.id.recyclerMembers)
        manageMembersSwipe = manageMembersRootView.findViewById(R.id.manageMembersSwipe)
        progressBar = manageMembersRootView.findViewById(R.id.progressBar)
        noMemberRequests = manageMembersRootView.findViewById(R.id.noMemberRequests)
        recyclerMembersJoined = manageMembersRootView.findViewById(R.id.recyclerMembersJoined)
        noMembers = manageMembersRootView.findViewById(R.id.noMembers)
        memberRequestsLayout = manageMembersRootView.findViewById(R.id.memberRequestsLayout)
        clanNameTop?.setText(R.string.nav_publics)
        topicID = requireArguments().getString("topic_id")
        permission = requireArguments().getString("permission")
        mContext = activity
        userID = getInstance(mContext!!)!!.userID
        username = getInstance(mContext!!)!!.username
        requestsRecyclerList = ArrayList()
        recyclerMembers?.setHasFixedSize(true)
        recyclerMembers?.layoutManager = LinearLayoutManager(mContext)
        membersRecyclerList = ArrayList()
        recyclerMembersJoined?.setHasFixedSize(true)
        recyclerMembersJoined?.layoutManager = LinearLayoutManager(mContext)
        backArrow?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        loadMembers()
        return manageMembersRootView
    }

    private fun loadMembers() {
        val stringRequest = StringRequest(Request.Method.GET, "$PUBLICS_MEMBERS?topicid=$topicID&username=$username&permission=$permission", { response: String? ->
            val obj: JSONObject
            try {
                obj = JSONObject(response!!)
                val members = obj.getJSONArray("members")
                for (i in 0 until members.length()) {
                    val membersObject = members.getJSONObject(i)
                    val username = membersObject.getString("username")
                    if (getInstance(mContext!!)!!.isUserBlocked(username)) continue
                    val id = membersObject.getString("id")
                    val profilePic = membersObject.getString("profile_pic")
                    val nickname = membersObject.getString("nickname")
                    val userid = membersObject.getString("userid")
                    val position = membersObject.getString("position")
                    val membersResult = PublicsPlayersRecycler(id, profilePic, nickname, userid, username, topicID!!, position)
                    membersRecyclerList!!.add(membersResult)
                    progressBar!!.visibility = View.GONE
                    manageMembersSwipe!!.isRefreshing = false
                }
                if (members.length() == 0) {
                    noMembers!!.visibility = View.VISIBLE
                }
                linearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                recyclerMembersJoined!!.layoutManager = linearLayoutManager
                if (permission == "admin") {
                    adapter = PublicsPlayersAdapter(mContext!!, membersRecyclerList!!)
                    recyclerMembersJoined!!.adapter = adapter
                } else {
                    adapterNonAdmin = PublicsPlayersNonAdminAdapter(mContext!!, membersRecyclerList!!)
                    recyclerMembersJoined!!.adapter = adapterNonAdmin
                }
                if (!obj.isNull("memberrequests") && permission == "admin") {
                    val memberrequests = obj.getJSONArray("memberrequests")
                    for (i in 0 until memberrequests.length()) {
                        val membersObject = memberrequests.getJSONObject(i)
                        val username = membersObject.getString("username")
                        if (getInstance(mContext!!)!!.isUserBlocked(username)) continue
                        val id = membersObject.getString("id")
                        val profilePic = membersObject.getString("profile_pic")
                        val nickname = membersObject.getString("nickname")
                        val userid = membersObject.getString("userid")
                        val position = membersObject.getString("position")
                        val membersResult = PublicsPlayersRecycler(id, profilePic, nickname, userid, username, topicID!!, position)
                        requestsRecyclerList!!.add(membersResult)
                    }
                    progressBar!!.visibility = View.GONE
                    manageMembersSwipe!!.isRefreshing = false
                    if (memberrequests.length() > 0) {
                        memberRequestsLayout!!.visibility = View.VISIBLE
                    }
                    linearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                    recyclerMembers!!.layoutManager = linearLayoutManager
                    adapter = PublicsPlayersAdapter(mContext!!, requestsRecyclerList!!)
                    recyclerMembers!!.adapter = adapter
                }
                progressBar!!.visibility = View.GONE
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val PUBLICS_MEMBERS = Constants.ROOT_URL + "publics_players.php"
    }
}