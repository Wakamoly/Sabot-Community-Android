package com.lucidsoftworksllc.sabotcommunity.fragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.material.tabs.TabLayout
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.R.drawable
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.DashCurrentPublicsAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.DashViewPagerAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.ProfilenewsAdapter
import com.lucidsoftworksllc.sabotcommunity.models.CurrentPublicsPOJO
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.PaginationOnScroll
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DashboardFragment : Fragment() {
    private var dashboardRefreshLayout: SwipeRefreshLayout? = null
    private var sliderImg: ArrayList<SliderUtilsDash?>? = null
    private var currentPublicsList: ArrayList<CurrentPublicsPOJO?>? = null
    private var viewPager: ViewPager? = null
    private var currentPublicsVP: ViewPager? = null
    private var sliderDotspanel: TabLayout? = null
    private var currentPublicsVPDots: TabLayout? = null
    private var currentPublicsTV: LinearLayout? = null
    private var noCurrentPublics: LinearLayout? = null
    private var newsLayout: LinearLayout? = null
    private var noPosts: TextView? = null
    private var numUsersOnline: TextView? = null
    private var numCurrentPublics: TextView? = null
    private var filterText: TextView? = null
    private var badgeTextView: TextView? = null
    private var newsTextView: TextView? = null
    private var newsTextView2: TextView? = null
    private var sliderboi: RelativeLayout? = null
    private var relLayoutDash2: RelativeLayout? = null
    private var dashContainer: RelativeLayout? = null
    private var followingPostsButton: Button? = null
    private var allPostsButton: Button? = null
    private var dashProgressBar: ProgressBar? = null
    private var currentPublicsProgress: ProgressBar? = null
    private var postsProgress: ProgressBar? = null
    private var mContext: Context? = null
    private var viewPagerAdapter: DashViewPagerAdapter? = null
    private var userID: String? = null
    private var username: String? = null
    private var dashboardMenu: ImageView? = null
    private var dashboardToMessages: ImageView? = null
    private var currentPublicsOptions: ImageView? = null
    private var filter: String? = null
    private var adNotified: ArrayList<String>? = null
    private var dashScroll: ScrollView? = null
    private var currentPage = PaginationOnScroll.PAGE_START
    private var isLastPage = false
    private val pageSize = PaginationOnScroll.PAGE_SIZE
    private var isLoading = false

    //private AdView adView;
    private var clicked: String? = null
    private var dashboardfeedRecyclerList: MutableList<ProfilenewsRecycler>? = null
    private var dashboardfeedadapter: ProfilenewsAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dashboardRootView = inflater.inflate(R.layout.fragment_dashboard, null)
        mContext = activity
        dashProgressBar = dashboardRootView.findViewById(R.id.dashProgressBar)
        sliderboi = dashboardRootView.findViewById(R.id.sliderboi)
        relLayoutDash2 = dashboardRootView.findViewById(R.id.relLayoutDash2)
        followingPostsButton = dashboardRootView.findViewById(R.id.followingPostsButton)
        allPostsButton = dashboardRootView.findViewById(R.id.allPostsButton)
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        val dashboardfeedView: RecyclerView = dashboardRootView.findViewById(R.id.dashboardfeedView)
        dashboardRefreshLayout = dashboardRootView.findViewById(R.id.dashSwipe)
        dashboardMenu = dashboardRootView.findViewById(R.id.dashboardMenu)
        dashboardToMessages = dashboardRootView.findViewById(R.id.dashboardToMessages)
        noPosts = dashboardRootView.findViewById(R.id.noPosts)
        numUsersOnline = dashboardRootView.findViewById(R.id.numUsersOnline)
        currentPublicsTV = dashboardRootView.findViewById(R.id.currentPublicsTV)
        currentPublicsProgress = dashboardRootView.findViewById(R.id.currentPublicsProgress)
        numCurrentPublics = dashboardRootView.findViewById(R.id.numCurrentPublics)
        currentPublicsOptions = dashboardRootView.findViewById(R.id.currentPublicsOptions)
        noCurrentPublics = dashboardRootView.findViewById(R.id.noCurrentPublics)
        filterText = dashboardRootView.findViewById(R.id.filterText)
        dashContainer = dashboardRootView.findViewById(R.id.dashContainer)
        badgeTextView = dashboardRootView.findViewById(R.id.badge_text_view)
        postsProgress = dashboardRootView.findViewById(R.id.postsProgress)
        newsLayout = dashboardRootView.findViewById(R.id.newsLayout)
        newsTextView = dashboardRootView.findViewById(R.id.newsTextView)
        newsTextView2 = dashboardRootView.findViewById(R.id.newsTextView2)
        dashScroll = dashboardRootView.findViewById(R.id.dashScroll)
        //adView = dashboardRootView.findViewById(R.id.adView);
        dashboardfeedRecyclerList = ArrayList()
        //dashboardfeedView.setHasFixedSize(true);
        dashboardfeedView.layoutManager = LinearLayoutManager(mContext)
        ViewCompat.setNestedScrollingEnabled(dashboardfeedView, false)
        dashboardfeedadapter = ProfilenewsAdapter(mContext!!, dashboardfeedRecyclerList)
        dashboardfeedView.adapter = dashboardfeedadapter
        dashScroll?.viewTreeObserver?.addOnScrollChangedListener {
            if (dashScroll?.getChildAt(0)?.bottom!! <= dashScroll?.height!! + dashScroll?.scrollY!!) {
                if (!isLoading && !isLastPage) {
                    isLoading = true
                    currentPage++
                    loadDashboardFeed(currentPage, clicked)
                }
            }
        }
        sliderImg = ArrayList()
        currentPublicsList = ArrayList()
        adNotified = ArrayList()
        viewPager = dashboardRootView.findViewById(R.id.viewPager)
        sliderDotspanel = dashboardRootView.findViewById(R.id.SliderDots)
        sliderDotspanel?.setupWithViewPager(viewPager, true)
        currentPublicsVP = dashboardRootView.findViewById(R.id.currentPublicsVP)
        currentPublicsVPDots = dashboardRootView.findViewById(R.id.currentPublicsVPDots)
        currentPublicsVPDots?.setupWithViewPager(currentPublicsVP, true)
        dashboardMenu?.setOnClickListener { (mContext as FragmentContainer?)!!.openDrawer() }
        dashboardToMessages?.setOnClickListener { startActivity(Intent(mContext, ChatActivity::class.java)) }
        followingPostsButton?.setOnClickListener { postsQueryButtonClicked(followingPostsButton) }
        allPostsButton?.setOnClickListener { postsQueryButtonClicked(allPostsButton) }
        filter = SharedPrefManager.getInstance(mContext!!)!!.currentPublics
        usersOnline()
        sendRequest()
        postsQueryButtonClicked(followingPostsButton)
        dashboardRefreshLayout?.setOnRefreshListener {
            adNotified!!.clear()
            val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is DashboardFragment) {
                val fragTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                fragTransaction.detach(currentFragment)
                fragTransaction.attach(currentFragment)
                fragTransaction.commit()
            }
            dashboardRefreshLayout?.isRefreshing = false
            sliderboi?.requestFocus()
        }
        viewPagerAdapter = DashViewPagerAdapter(sliderImg!!, mContext!!)
        viewPager?.adapter = viewPagerAdapter
        viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val adID = sliderImg!![position]!!.sliderAdID
                adViewed(adID!!)
            }

            override fun onPageScrollStateChanged(state: Int) {
                dashboardRefreshLayout?.isEnabled = state != ViewPager.SCROLL_STATE_DRAGGING
            }
        })
        currentPublicsVP?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {
                dashboardRefreshLayout?.isEnabled = state != ViewPager.SCROLL_STATE_DRAGGING
            }
        })
        setPlatformImage(filter)
        currentPublicsOptions?.setOnClickListener {
            val items = resources.getStringArray(R.array.platform_array_w_all)
            LovelyChoiceDialog(mContext)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle(R.string.platform_filter)
                    .setIcon(drawable.icons8_workstation_48)
                    .setMessage(resources.getString(R.string.selected_platform) + " " + SharedPrefManager.getInstance(mContext!!)!!.currentPublics)
                    .setItems(items) { _: Int, item: String? ->
                        SharedPrefManager.getInstance(mContext!!)!!.currentPublics = item
                        setPlatformImage(item)
                        filter = item
                        currentPublicsProgress?.visibility = View.VISIBLE
                        currentPublicsList!!.clear()
                        currentPublics
                    }
                    .show()
        }

        /*MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);*/hideKeyboardFrom(mContext, dashboardRootView)
        return dashboardRootView
    }

    private fun adViewed(adID: String) {
        var isAdViewed = "no"
        for (adViewed in adNotified!!) {
            if (adViewed.contains(adID)) {
                isAdViewed = "yes"
                break
            }
        }
        if (isAdViewed == "no") {
            adNotified!!.add(adID)
            val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_VIEWED, Response.Listener { }, Response.ErrorListener { }) {
                override fun getParams(): Map<String, String> {
                    val parms: MutableMap<String, String> = HashMap()
                    parms["id"] = adID
                    parms["method"] = "view"
                    parms["user_id"] = SharedPrefManager.getInstance(mContext!!)!!.userID!!
                    parms["username"] = SharedPrefManager.getInstance(mContext!!)!!.username!!
                    return parms
                }
            }
            (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
        }
    }

    private fun usersOnline() {
        val usersOnlineThread: Thread = object : Thread() {
            //create thread
            override fun run() {
                val stringRequest = StringRequest(Request.Method.GET, "$USERS_ONLINE?username=$username", { response: String? ->
                    try {
                        val obj = JSONObject(response!!)
                        val usersonline = obj.getJSONArray("numonline")
                        for (i in 0 until usersonline.length()) {
                            val usersonlineObj = usersonline.getJSONObject(i)
                            val num = usersonlineObj.getString("num")
                            val numpublics = usersonlineObj.getString("numpublics")
                            numUsersOnline!!.text = num
                            numCurrentPublics!!.text = numpublics
                            val unreadmessages = usersonlineObj.getString("unreadmessages").toInt()
                            if (unreadmessages > 0) {
                                badgeTextView!!.visibility = View.VISIBLE
                                if (unreadmessages > 9) {
                                    badgeTextView!!.text = "9+"
                                } else {
                                    badgeTextView!!.text = usersonlineObj.getString("unreadmessages")
                                }
                            }
                        }
                        val dashnews = obj.getJSONArray("dashnews")
                        if (dashnews.length() != 0) {
                            newsLayout!!.visibility = View.VISIBLE
                            for (i in 0 until dashnews.length()) {
                                val usersonlineObj = dashnews.getJSONObject(i)
                                val toptext = usersonlineObj.getString("toptext")
                                val bottomtext = usersonlineObj.getString("bottomtext")
                                val link = usersonlineObj.getString("link")
                                newsTextView!!.text = toptext
                                newsTextView2!!.text = bottomtext
                                newsLayout!!.setOnClickListener { mContext!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link))) }
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }) { }
                (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
            }
        }
        usersOnlineThread.start()
    }

    // start thread
/*if (currentPublicsVP!=null){
                    currentPublicsVP.notifyAll();
                }*/
    //create thread
    val currentPublics: Unit
        get() {
            val getCurrentPublicsThread: Thread = object : Thread() {
                //create thread
                override fun run() {
                    currentPublicsList!!.clear()
                    /*if (currentPublicsVP!=null){
           currentPublicsVP.notifyAll();
       }*/
                    val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, "$CURRENT_PUBLICS?filter=$filter&username=$username", null, { response: JSONArray ->
                        if (response.length() == 0) {
                            noCurrentPublics!!.visibility = View.VISIBLE
                            currentPublicsProgress!!.visibility = View.GONE
                            currentPublicsTV!!.setOnClickListener {
                                currentPublicsProgress!!.visibility = View.VISIBLE
                                currentPublicsList!!.clear()
                                currentPublics
                            }
                            noCurrentPublics!!.setOnClickListener {
                                val asf: Fragment = PublicsFragment()
                                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                fragmentTransaction.replace(R.id.fragment_container, asf)
                                fragmentTransaction.addToBackStack(null)
                                fragmentTransaction.commit()
                            }
                        } else {
                            for (i in 0 until response.length()) {
                                val currentPublics = CurrentPublicsPOJO()
                                try {
                                    val jsonObject = response.getJSONObject(i)
                                    currentPublics.id = jsonObject.getString("id")
                                    currentPublics.subject = jsonObject.getString("subject")
                                    currentPublics.catname = jsonObject.getString("catname")
                                    currentPublics.type = jsonObject.getString("type")
                                    currentPublics.profilePic = jsonObject.getString("profile_pic")
                                    currentPublics.nickname = jsonObject.getString("nickname")
                                    currentPublics.eventDate = jsonObject.getString("event_date")
                                    currentPublics.context = jsonObject.getString("context")
                                    currentPublics.numPlayers = jsonObject.getString("num_players")
                                    currentPublics.numAdded = jsonObject.getString("num_added")
                                    currentPublics.image = jsonObject.getString("image")
                                    currentPublics.playingNow = jsonObject.getString("playing_now")
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                currentPublicsList!!.add(currentPublics)
                            }
                            val currentPublicsAdapter = DashCurrentPublicsAdapter(currentPublicsList!!, mContext!!)
                            currentPublicsVP!!.adapter = currentPublicsAdapter
                            currentPublicsProgress!!.visibility = View.GONE
                            noCurrentPublics!!.visibility = View.GONE
                            currentPublicsTV!!.setOnClickListener {
                                currentPublicsProgress!!.visibility = View.VISIBLE
                                currentPublics
                            }
                        }
                    }) { }
                    DashSliderRequest.getInstance(mContext!!)?.addToRequestQueue(jsonArrayRequest)
                }
            }
            getCurrentPublicsThread.start() // start thread
        }

    private fun sendRequest() {
        val sendRequestThread: Thread = object : Thread() {
            //create thread
            override fun run() {
                val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, "$DashboardAds_URL?username=$username", null, { response: JSONArray ->
                    for (i in 0 until response.length()) {
                        val sliderUtils = SliderUtilsDash()
                        try {
                            val jsonObject = response.getJSONObject(i)
                            sliderUtils.sliderImageUrl = jsonObject.getString("cat_image")
                            sliderUtils.sliderDescription = jsonObject.getString("cat_description")
                            sliderUtils.sliderTitle = jsonObject.getString("cat_name")
                            sliderUtils.sliderID = jsonObject.getString("cat_id")
                            sliderUtils.sliderType = jsonObject.getString("type")
                            sliderUtils.sliderTag = jsonObject.getString("tag")
                            sliderUtils.sliderAdID = jsonObject.getString("ad_id")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        sliderImg!!.add(sliderUtils)
                        adViewed(sliderImg!![0]!!.sliderAdID!!)
                        viewPagerAdapter!!.notifyDataSetChanged()
                    }
                    currentPublics
                }) { }
                DashSliderRequest.getInstance(mContext!!)!!.addToRequestQueue(jsonArrayRequest)
            }
        }
        sendRequestThread.start()
    }

    private fun loadDashboardFeed(page: Int, method: String?) {
        val loadDashboardFeedThread: Thread = object : Thread() {
            //create thread
            override fun run() {
                val items = ArrayList<ProfilenewsRecycler>()
                val stringRequest: StringRequest = object : StringRequest(Method.POST, DashboardFeed_URL, Response.Listener { response: String? ->
                    try {
                        val dashboardfeed = JSONArray(response)
                        for (i in 0 until dashboardfeed.length()) {
                            val dashboardfeedObject = dashboardfeed.getJSONObject(i)
                            val addedBy = dashboardfeedObject.getString("added_by")
                            if (SharedPrefManager.getInstance(mContext!!)!!.isUserBlocked(addedBy)) continue
                            val id = dashboardfeedObject.getInt("id")
                            val type = dashboardfeedObject.getString("type")
                            val likes = dashboardfeedObject.getString("likes")
                            val body = dashboardfeedObject.getString("body")
                            val userTo = dashboardfeedObject.getString("user_to")
                            val dateAdded = dashboardfeedObject.getString("date_added")
                            val userClosed = dashboardfeedObject.getString("user_closed")
                            val deleted = dashboardfeedObject.getString("deleted")
                            val image = dashboardfeedObject.getString("image")
                            val userId = dashboardfeedObject.getString("user_id")
                            val profilePic = dashboardfeedObject.getString("profile_pic")
                            val verified = dashboardfeedObject.getString("verified")
                            val online = dashboardfeedObject.getString("online")
                            val nickname = dashboardfeedObject.getString("nickname")
                            val username = dashboardfeedObject.getString("username")
                            val commentcount = dashboardfeedObject.getString("commentcount")
                            val likedbyuserYes = dashboardfeedObject.getString("likedbyuseryes")
                            val form = dashboardfeedObject.getString("form")
                            val edited = dashboardfeedObject.getString("edited")
                            val dashboardfeedResult = ProfilenewsRecycler(id, type, likes, body, addedBy, userTo, dateAdded, userClosed, deleted, image, userId, profilePic, verified, online, nickname, username, commentcount, likedbyuserYes, form, edited)
                            items.add(dashboardfeedResult)
                        }
                        if (dashboardfeed.length() == 0) {
                            dashProgressBar!!.visibility = View.GONE
                            relLayoutDash2!!.visibility = View.VISIBLE
                            noPosts!!.visibility = View.VISIBLE
                        } else {
                            if (currentPage != PaginationOnScroll.PAGE_START) dashboardfeedadapter!!.removeLoading()
                            postsProgress!!.visibility = View.GONE
                            dashboardfeedadapter!!.addItems(items)
                            // check whether is last page or not
                            if (dashboardfeed.length() == pageSize) {
                                dashboardfeedadapter!!.addLoading()
                            } else {
                                isLastPage = true
                                //adapter.removeLoading();
                            }
                            isLoading = false
                            noPosts!!.visibility = View.GONE
                        }
                        dashProgressBar!!.visibility = View.GONE
                        relLayoutDash2!!.visibility = View.VISIBLE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        //TODO FIX THIS
                        dashProgressBar!!.visibility = View.GONE
                        relLayoutDash2!!.visibility = View.VISIBLE
                        noPosts!!.visibility = View.VISIBLE
                        postsProgress!!.visibility = View.GONE
                    }
                },
                        Response.ErrorListener { Toast.makeText(mContext, "Couldn't get dashboard feed!", Toast.LENGTH_SHORT).show() }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["page"] = page.toString()
                        params["items"] = pageSize.toString()
                        params["userid"] = userID!!
                        params["username"] = username!!
                        params["method"] = method!!
                        return params
                    }
                }
                (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
            }
        }
        loadDashboardFeedThread.start()
    }

    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }*/
    private fun setPlatformImage(item: String?) {
        when (item) {
            "Xbox" -> {
                currentPublicsOptions!!.setImageResource(drawable.icons8_xbox_50)
                filterText!!.visibility = View.VISIBLE
            }
            "PlayStation" -> {
                currentPublicsOptions!!.setImageResource(drawable.icons8_playstation_50)
                filterText!!.visibility = View.VISIBLE
            }
            "Steam" -> {
                currentPublicsOptions!!.setImageResource(drawable.icons8_steam_48)
                filterText!!.visibility = View.VISIBLE
            }
            "PC" -> {
                currentPublicsOptions!!.setImageResource(drawable.icons8_workstation_48)
                filterText!!.visibility = View.VISIBLE
            }
            "Mobile" -> {
                currentPublicsOptions!!.setImageResource(drawable.icons8_mobile_48)
                filterText!!.visibility = View.VISIBLE
            }
            "Switch" -> {
                currentPublicsOptions!!.setImageResource(drawable.icons8_nintendo_switch_48)
                filterText!!.visibility = View.VISIBLE
            }
            "Cross-Platform" -> {
                currentPublicsOptions!!.setImageResource(drawable.icons8_collect_40)
                filterText!!.visibility = View.VISIBLE
            }
            "Other" -> {
                currentPublicsOptions!!.setImageResource(drawable.icons8_question_mark_64)
                filterText!!.visibility = View.VISIBLE
            }
            else -> {
                currentPublicsOptions!!.setImageResource(drawable.ic_ellipses)
                filterText!!.visibility = View.GONE
            }
        }
    }

    private fun postsQueryButtonClicked(click: Button?) {
        postsProgress!!.visibility = View.VISIBLE
        if (click === allPostsButton) {
            dashboardfeedRecyclerList!!.clear()
            if (dashboardfeedadapter != null) {
                dashboardfeedadapter!!.notifyDataSetChanged()
            }
            val colorFrom = ContextCompat.getColor(mContext!!, R.color.grey_80)
            val colorTo = ContextCompat.getColor(mContext!!, R.color.green)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 750
            colorAnimation.addUpdateListener { animator: ValueAnimator -> allPostsButton!!.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.start()
            val colorAnimation2 = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
            colorAnimation2.duration = 750
            colorAnimation2.addUpdateListener { animator: ValueAnimator -> followingPostsButton!!.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation2.start()
            clicked = "all"
            currentPage = 1
            loadDashboardFeed(currentPage, clicked)
        }
        if (click === followingPostsButton) {
            dashboardfeedRecyclerList!!.clear()
            if (dashboardfeedadapter != null) {
                dashboardfeedadapter!!.notifyDataSetChanged()
            }
            val colorTo = ContextCompat.getColor(mContext!!, R.color.grey_80)
            val colorFrom = ContextCompat.getColor(mContext!!, R.color.green)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 250
            colorAnimation.addUpdateListener { animator: ValueAnimator -> allPostsButton!!.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.start()
            val colorAnimation2 = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
            colorAnimation2.duration = 250
            colorAnimation2.addUpdateListener { animator: ValueAnimator -> followingPostsButton!!.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation2.start()
            clicked = "following"
            currentPage = 1
            loadDashboardFeed(currentPage, clicked)
        }
    } /*private void clearBackStack() {
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStackImmediate(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }*/

    companion object {
        private const val DashboardAds_URL = Constants.ROOT_URL + "dashboardads_api.php"
        private const val CURRENT_PUBLICS = Constants.ROOT_URL + "current_publics.php"
        private const val DashboardFeed_URL = Constants.ROOT_URL + "dashboardfeed_api.php"
        private const val URL_VIEWED = Constants.ROOT_URL + "dashboard_ad_interaction.php"
        private const val USERS_ONLINE = Constants.ROOT_URL + "num_users_online.php"
        fun hideKeyboardFrom(context: Context?, view: View) {
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}