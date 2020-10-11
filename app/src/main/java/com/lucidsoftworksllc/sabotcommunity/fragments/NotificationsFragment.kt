package com.lucidsoftworksllc.sabotcommunity.fragments

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.NotificationsAdapter
import com.lucidsoftworksllc.sabotcommunity.db.notifications.MainStateEvent
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationDataModel
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationViewModel
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private var currentPage = PaginationOnScroll.PAGE_START
    private var isLastPage = false
    private val pageSize = PaginationOnScroll.PAGE_SIZE
    private var isLoading = false
    private var recyclerView: RecyclerView? = null
    private var layoutManager: LinearLayoutManager? = null
    private var notifications: List<NotificationDataModel>? = null
    private var adapter: NotificationsAdapter? = null
    private var deviceUserID: String? = null
    private var deviceUsername: String? = null
    private var none: TextView? = null
    private val badgenum: TextView? = null
    private var notiMenu: ImageView? = null
    private var notificationSwipe: SwipeRefreshLayout? = null
    private var notiLayout: RelativeLayout? = null
    private var progressBar: ProgressBar? = null
    private var mContext: Context? = null
    private val viewModel: NotificationViewModel by viewModels()

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
        deviceUsername = mContext?.deviceUsername
        deviceUserID = mContext?.deviceUserID

        initRecycler()
        initNotiMenu()
        subscribeObservers()
        viewModel.setStateEvent(MainStateEvent.GetNotiEvents, mContext!!)
        return notificationsRootView
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState){
                is DataState.Success<List<NotificationDataModel>> -> {
                    println("Success!")
                    displayProgressbar(false)
                    appendNotifications(dataState.data)
                }
                is DataState.Error -> {
                    println("Error!")
                    displayProgressbar(false)
                    displayError(dataState.exception.message)
                }
                is DataState.Loading -> {
                    println("Loading!")
                    displayProgressbar(true)
                }
                is DataState.UpdateSuccess<List<NotificationDataModel>> -> {
                    println("Update Success!")
                    displayProgressbar(false)
                    appendNotifications(dataState.data)
                }
            }
        })
    }

    private fun displayError(message: String?){
        none?.visibility = View.VISIBLE
        if(message != null){
            println("Error message: $message")
            mContext?.toastLong("Error!: $message")
        }else{
            mContext?.toastShort("Error!")
        }

    }

    private fun displayProgressbar(isDisplayed: Boolean){
        progressBar?.visibility = if(isDisplayed) View.VISIBLE else View.GONE
    }

    private fun appendNotifications(notis: List<NotificationDataModel>){
        println("Appending notifications")
        (recyclerView?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        if (notis.isEmpty()) {
            none?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
        }
        notiLayout!!.visibility = View.VISIBLE
        if (currentPage != PaginationOnScroll.PAGE_START) adapter?.removeLoading()
        progressBar!!.visibility = View.GONE
        adapter?.addItems(notis)
        //recyclerView?.scheduleLayoutAnimation()
        if (notis.size == pageSize) {
            adapter!!.addLoading()
        } else {
            isLastPage = true
            //adapter.removeLoading();
        }
        isLoading = false
    }

    /*private fun loadNotifications(page: Int) {
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
                params["userid"] = deviceUserID!!
                params["username"] = deviceUsername!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }*/

    private fun initNotiMenu(){
        //TODO: Add opened to Room db
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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commitNowAllowingStateLoss()
                                    (mContext as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commitAllowingStateLoss()
                                } else {
                                    (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
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
    }

    private fun initRecycler(){
        recyclerView?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = layoutManager


        notifications = ArrayList()
        adapter = NotificationsAdapter(notifications!! as MutableList<NotificationDataModel>, mContext!!)
        recyclerView?.adapter = adapter


        /*loadNotifications(currentPage)
        recyclerView?.addOnScrollListener(object : PaginationOnScroll(layoutManager!!) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                loadNotifications(currentPage)
            }
            override fun isLastPage(): Boolean { return isLastPage }
            override fun isLoading(): Boolean { return isLoading }
        })*/


        notificationSwipe?.setOnRefreshListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commitNowAllowingStateLoss()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commitAllowingStateLoss()
            } else {
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
            }
            notificationSwipe?.isRefreshing = false
        }
    }

    companion object {
        private const val TAG = "NotificationsFragment"
        private const val Notifications_URL = Constants.ROOT_URL + "getNotifications.php"
        private const val SET_ALL_READ = Constants.ROOT_URL + "set_all_noti_read.php"
    }
}