package com.lucidsoftworksllc.sabotcommunity.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.NotificationsAdapter
import com.lucidsoftworksllc.sabotcommunity.models.NotificationsRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.PaginationOnScroll
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class NotificationsFragment : Fragment() {
    private var currentPage = PaginationOnScroll.PAGE_START
    private var isLastPage = false
    private val pageSize = PaginationOnScroll.PAGE_SIZE
    private var isLoading = false
    private var recyclerView: RecyclerView? = null
    private var layoutManager: LinearLayoutManager? = null
    private var notifications: List<NotificationsRecycler>? = null
    private var adapter: NotificationsAdapter? = null
    private var userID: String? = null
    private var username: String? = null
    private var deviceUsername: String? = null
    private var none: TextView? = null
    private val badgenum: TextView? = null
    private var notiMenu: ImageView? = null
    private var notificationSwipe: SwipeRefreshLayout? = null
    private var notiLayout: RelativeLayout? = null
    private var progressBar: ProgressBar? = null
    private var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val notificationsRootView = inflater.inflate(R.layout.fragment_notifications, null)
        setHasOptionsMenu(true)
        none = notificationsRootView.findViewById(R.id.noNotifications)
        progressBar = notificationsRootView.findViewById(R.id.progressBar)
        notificationSwipe = notificationsRootView.findViewById(R.id.notificationsSwipe)
        recyclerView = notificationsRootView.findViewById(R.id.recyclerNotifications)
        notiLayout = notificationsRootView.findViewById(R.id.notiLayout)
        notiMenu = notificationsRootView.findViewById(R.id.notiMenu)
        mContext = activity
        deviceUsername = SharedPrefManager.getInstance(mContext!!)!!.username
        recyclerView?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = layoutManager
        notifications = ArrayList()
        adapter = NotificationsAdapter(notifications!! as MutableList<NotificationsRecycler>, mContext!!)
        recyclerView?.adapter = adapter
        loadNotifications(currentPage)
        recyclerView?.addOnScrollListener(object : PaginationOnScroll(layoutManager!!) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                loadNotifications(currentPage)
            }
            override fun isLastPage(): Boolean { return isLastPage }
            override fun isLoading(): Boolean { return isLoading }
        })
        notificationSwipe?.setOnRefreshListener {
            val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is NotificationsFragment) {
                val fragTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                fragTransaction.detach(currentFragment)
                fragTransaction.attach(currentFragment)
                fragTransaction.commit()
            }
            notificationSwipe?.isRefreshing = false
        }
        notiMenu?.setOnClickListener { view: View? ->
            val popup = PopupMenu(mContext, view)
            val inflater1 = popup.menuInflater
            inflater1.inflate(R.menu.noti_menu, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                if (item.itemId == R.id.menuSetOpened) {
                    val stringRequest: StringRequest = object : StringRequest(Method.POST, SET_ALL_READ, Response.Listener { response: String? ->
                        try {
                            val obj = JSONObject(response!!)
                            if (!obj.getBoolean("error")) {
                                val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
                                if (currentFragment is NotificationsFragment) {
                                    val fragTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                    fragTransaction.detach(currentFragment)
                                    fragTransaction.attach(currentFragment)
                                    fragTransaction.commit()
                                }
                                notificationSwipe?.isRefreshing = false
                            } else {
                                Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { }) {
                        override fun getParams(): MutableMap<String, String?> {
                            val params: MutableMap<String, String?> = HashMap()
                            params["username"] = deviceUsername
                            return params
                        }
                    }
                    (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
                }
                true
            }
            popup.show()
        }
        return notificationsRootView
    }

    private fun loadNotifications(page: Int) {
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        val items = ArrayList<NotificationsRecycler>()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Notifications_URL,
                Response.Listener { response: String? ->
                    try {
                        val notificationsArray = JSONArray(response)
                        if (notificationsArray.length() == 0) {
                            none!!.visibility = View.VISIBLE
                            progressBar!!.visibility = View.GONE
                        }
                        for (i in 0 until notificationsArray.length()) {
                            val notificationsObject = notificationsArray.getJSONObject(i)
                            val id = notificationsObject.getString("id")
                            val userTo = notificationsObject.getString("user_to")
                            val userFrom = notificationsObject.getString("user_from")
                            if (SharedPrefManager.getInstance(mContext!!)!!.isUserBlocked(userFrom)) continue
                            val message = notificationsObject.getString("message")
                            val type = notificationsObject.getString("type")
                            val link = notificationsObject.getString("link")
                            val datetime = notificationsObject.getString("datetime")
                            val opened = notificationsObject.getString("opened")
                            val viewed = notificationsObject.getString("viewed")
                            val userId = notificationsObject.getString("user_id")
                            val profilePic = notificationsObject.getString("profile_pic")
                            val nickname = notificationsObject.getString("nickname")
                            val verified = notificationsObject.getString("verified")
                            val lastOnline = notificationsObject.getString("last_online")
                            val notificationsResult = NotificationsRecycler(id, userTo, userFrom, message, type, link, datetime, opened, viewed, userId, profilePic, nickname, verified, lastOnline)
                            items.add(notificationsResult)
                        }
                        notiLayout!!.visibility = View.VISIBLE
                        if (currentPage != PaginationOnScroll.PAGE_START) adapter!!.removeLoading()
                        progressBar!!.visibility = View.GONE
                        adapter!!.addItems(items)
                        if (notificationsArray.length() == pageSize) {
                            adapter!!.addLoading()
                        } else {
                            isLastPage = true
                            //adapter.removeLoading();
                        }
                        isLoading = false
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        //TODO FIX THIS
                        notiLayout!!.visibility = View.VISIBLE
                        progressBar!!.visibility = View.GONE
                        none!!.visibility = View.VISIBLE
                    }
                },
                Response.ErrorListener { }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["page"] = page.toString()
                params["items"] = pageSize.toString()
                params["userid"] = userID!!
                params["username"] = username!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val TAG = "NotificationsFragment"
        private const val Notifications_URL = Constants.ROOT_URL + "getNotifications.php"
        private const val SET_ALL_READ = Constants.ROOT_URL + "set_all_noti_read.php"
    }
}