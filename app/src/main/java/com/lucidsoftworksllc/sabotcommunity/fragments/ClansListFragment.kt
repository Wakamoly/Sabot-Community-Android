package com.lucidsoftworksllc.sabotcommunity.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.JoinedClansAdapter
import com.lucidsoftworksllc.sabotcommunity.models.ClansRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ClansListFragment : Fragment() {
    private var noClans: TextView? = null
    private var clansSwipe: SwipeRefreshLayout? = null
    private var clansProgressBar: ProgressBar? = null
    private var clansLayout: RelativeLayout? = null
    private var mContext: Context? = null
    private var dialog: ProgressDialog? = null
    private var deviceUserID: String? = null
    private var deviceUsername: String? = null
    private var clansMenu: ImageView? = null
    private var recyclerView: RecyclerView? = null
    private val layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: JoinedClansAdapter? = null
    private var clans: MutableList<ClansRecycler>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val clansRootView = inflater.inflate(R.layout.fragment_clans_list, null)
        noClans = clansRootView.findViewById(R.id.noClans)
        clansSwipe = clansRootView.findViewById(R.id.clansSwipe)
        clansProgressBar = clansRootView.findViewById(R.id.clansProgressBar)
        clansLayout = clansRootView.findViewById(R.id.clansLayout)
        recyclerView = clansRootView.findViewById(R.id.recyclerClans)
        clansMenu = clansRootView.findViewById(R.id.clansMenu)
        mContext = activity
        deviceUserID = SharedPrefManager.getInstance(mContext!!)!!.userID
        deviceUsername = SharedPrefManager.getInstance(mContext!!)!!.username
        clans = ArrayList()
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(mContext)
        dialog = ProgressDialog(mContext)
        dialog!!.setMessage("Loading joined clans...")
        dialog!!.show()
        adapter = JoinedClansAdapter(mContext!!)
        recyclerView?.adapter = adapter
        clansMenu?.setOnClickListener { view: View? ->
            val popup = PopupMenu(mContext, view)
            val inflater1 = popup.menuInflater
            inflater1.inflate(R.menu.clans_list_top_options_menu, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                if (item.itemId == R.id.menuNewClan) {
                    val ldf = NewClanFragment()
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                true
            }
            popup.show()
        }
        clansSwipe?.setOnRefreshListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commitNowAllowingStateLoss()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commitAllowingStateLoss()
            } else {
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
            }
            clansSwipe?.isRefreshing = false
        }
        loadJoinedClans()
        return clansRootView
    }

    private fun loadJoinedClans() {
        val stringRequest = StringRequest(Request.Method.GET, "$URL_JOINED_CLANS?username=$deviceUsername&userid=$deviceUserID",
                { response: String? ->
                    try {
                        val res = JSONObject(response!!)
                        val thread = res.getJSONArray("clans")
                        for (i in 0 until thread.length()) {
                            val obj = thread.getJSONObject(i)
                            val position = obj.getString("position")
                            val tag = obj.getString("tag")
                            val name = obj.getString("name")
                            val numMembers = obj.getString("num_members")
                            val insignia = obj.getString("insignia")
                            val games = obj.getString("games")
                            val id = obj.getString("id")
                            val avg = obj.getString("avg")
                            val clansObject = ClansRecycler(position, tag, name, numMembers, insignia, games, id, avg)
                            clans!!.add(clansObject)
                        }
                        if (thread.length() == 0) {
                            noClans!!.visibility = View.VISIBLE
                            noClans!!.setOnClickListener {
                                val ldf = NewClanFragment()
                                (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                        }else{
                            adapter?.addItems(clans)
                        }
                        dialog!!.dismiss()
                        clansProgressBar!!.visibility = View.GONE
                        clansLayout!!.visibility = View.VISIBLE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        ) { }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        const val URL_JOINED_CLANS = Constants.ROOT_URL + "joined_clans.php"
    }
}