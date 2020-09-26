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
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.ClanMembersAdapter
import com.lucidsoftworksllc.sabotcommunity.models.ClanMembersRecycler
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class ClanManageMembers : Fragment() {
    private var clanNameTop: TextView? = null
    private var noMemberRequests: TextView? = null
    private var noMembers: TextView? = null
    private var clanID: String? = null
    private var clanname: String? = null
    private var userID: String? = null
    private var username: String? = null
    private var clantag: String? = null
    private var mContext: Context? = null
    private var backArrow: ImageView? = null
    private var recyclerMembers: RecyclerView? = null
    private var recyclerMembersJoined: RecyclerView? = null
    private var manageMembersSwipe: SwipeRefreshLayout? = null
    private var progressBar: ProgressBar? = null
    private var memberRequestsLayout: RelativeLayout? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: ClanMembersAdapter? = null
    private var requestsRecyclerList: MutableList<ClanMembersRecycler>? = null
    private var membersRecyclerList: MutableList<ClanMembersRecycler>? = null
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
        clanID = requireArguments().getString("ClanId")
        clanname = requireArguments().getString("Clanname")
        clantag = requireArguments().getString("Clantag")
        mContext = activity
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        memberRequestsLayout?.visibility = View.VISIBLE
        clanNameTop?.text = clanname
        requestsRecyclerList = ArrayList()
        recyclerMembers?.setHasFixedSize(true)
        recyclerMembers?.layoutManager = LinearLayoutManager(mContext)
        membersRecyclerList = ArrayList()
        recyclerMembersJoined?.setHasFixedSize(true)
        recyclerMembersJoined?.layoutManager = LinearLayoutManager(mContext)
        backArrow?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        loadMemberRequests()
        loadMembers()
        return manageMembersRootView
    }

    private fun loadMemberRequests() {
        val stringRequest = StringRequest(Request.Method.GET, "$CLAN_MEMBER_REQUESTS?userid=$userID&clanid=$clanID&username=$username", { response: String? ->
            try {
                val members = JSONArray(response)
                for (i in 0 until members.length()) {
                    val membersObject = members.getJSONObject(i)
                    val username = membersObject.getString("username")
                    if (SharedPrefManager.getInstance(mContext!!)!!.isUserBlocked(username)) continue
                    val id = membersObject.getString("id")
                    val profilePic = membersObject.getString("profile_pic")
                    val nickname = membersObject.getString("nickname")
                    val userid = membersObject.getString("userid")
                    val membersResult = ClanMembersRecycler(id, profilePic, nickname, userid, username, clanID!!, clantag!!, "request")
                    requestsRecyclerList!!.add(membersResult)
                    progressBar!!.visibility = View.GONE
                    manageMembersSwipe!!.isRefreshing = false
                }
                if (members.length() == 0) {
                    noMemberRequests!!.visibility = View.VISIBLE
                    progressBar!!.visibility = View.GONE
                }
                linearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                recyclerMembers!!.layoutManager = linearLayoutManager
                adapter = ClanMembersAdapter(mContext!!, requestsRecyclerList!!)
                recyclerMembers!!.adapter = adapter
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadMembers() {
        val stringRequest = StringRequest(Request.Method.GET, "$CLAN_MEMBERS?userid=$userID&clanid=$clanID&username=$username", { response: String? ->
            try {
                val members = JSONArray(response)
                for (i in 0 until members.length()) {
                    val membersObject = members.getJSONObject(i)
                    val username = membersObject.getString("username")
                    if (SharedPrefManager.getInstance(mContext!!)!!.isUserBlocked(username)) continue
                    val id = membersObject.getString("id")
                    val profilePic = membersObject.getString("profile_pic")
                    val nickname = membersObject.getString("nickname")
                    val userid = membersObject.getString("userid")
                    val position = membersObject.getString("position")
                    val membersResult = ClanMembersRecycler(id, profilePic, nickname, userid, username, clanID!!, clantag!!, position)
                    membersRecyclerList!!.add(membersResult)
                    progressBar!!.visibility = View.GONE
                    manageMembersSwipe!!.isRefreshing = false
                }
                if (members.length() == 0) {
                    noMembers!!.visibility = View.VISIBLE
                    progressBar!!.visibility = View.GONE
                }
                linearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                recyclerMembersJoined!!.layoutManager = linearLayoutManager
                adapter = ClanMembersAdapter(mContext!!, membersRecyclerList!!)
                recyclerMembersJoined!!.adapter = adapter
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val CLAN_MEMBER_REQUESTS = Constants.ROOT_URL + "clan_member_requests.php"
        private const val CLAN_MEMBERS = Constants.ROOT_URL + "clan_members.php"
    }
}