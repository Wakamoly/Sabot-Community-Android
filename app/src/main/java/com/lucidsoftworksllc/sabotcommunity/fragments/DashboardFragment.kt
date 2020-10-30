package com.lucidsoftworksllc.sabotcommunity.fragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.INVISIBLE
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.R.drawable
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.DashCurrentPublicsAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.DashViewPagerAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.ProfilenewsAdapter
import com.lucidsoftworksllc.sabotcommunity.databinding.FragmentDashboardBinding
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.DashboardRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.DashboardVM
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.CurrentPublicsModel
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.DashboardAdModelItem
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UsersOnlineModel2
import com.lucidsoftworksllc.sabotcommunity.network.DashApi
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseFragment
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.snippet_top_dashboardbar.*
import kotlin.collections.ArrayList

class DashboardFragment : BaseFragment<DashboardVM, FragmentDashboardBinding, DashboardRepo>(), ProfilenewsAdapter.Interaction {

    private lateinit var filter: String
    private var adNotified: ArrayList<String>? = null
    private var sliderImgAdsModel: ArrayList<DashboardAdModelItem?>? = null
    private var currentPublicsList: ArrayList<CurrentPublicsModel?>? = null
    private lateinit var mContext: Context
    private var currentPage = PaginationOnScroll.PAGE_START
    private var isLastPage = false
    private val pageSize = PaginationOnScroll.PAGE_SIZE
    private var isLoading = true
    private var clicked: String? = ""
    private lateinit var dashboardfeedadapter: ProfilenewsAdapter
    private var viewPagerAdapter: DashViewPagerAdapter? = null
    private var currentPublicsAdapter: DashCurrentPublicsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPublicsVPDots.setupWithViewPager(viewPager, true)

        sliderImgAdsModel = ArrayList()
        currentPublicsList = ArrayList()
        adNotified = ArrayList()
        currentPublicsVPDots?.setupWithViewPager(currentPublicsVP, true)
        dashboardMenu?.setOnClickListener { (mContext as FragmentContainer?)!!.openDrawer() }
        dashboardToMessages?.setOnClickListener { startActivity(Intent(mContext, ChatActivity::class.java)) }
        followingPostsButton?.setOnClickListener { postsQueryButtonClicked(followingPostsButton) }
        allPostsButton?.setOnClickListener { postsQueryButtonClicked(allPostsButton) }
        filter = SharedPrefManager.getInstance(mContext)!!.currentPublics.toString()


        dashSwipe.setOnRefreshListener { refreshDash() }

        initDashFeedRecycler()
        initAdVP()
        initPublicsVP()

        subscribeObservers()

        hideKeyboardFrom(mContext, view)

    }

    private fun refreshDash(){
        adNotified!!.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commitNowAllowingStateLoss()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commitAllowingStateLoss()
        } else {
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }
        dashSwipe?.isRefreshing = false
        sliderboi?.requestFocus()
    }

    private fun subscribeObservers(){
        viewModel.getAds(deviceUsername)
        viewModel.ads.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success -> {
                    dashProgressBar.visible(false)
                    relLayoutDash2!!.visible(true)
                    updateAdUI(it.data)
                }
                is DataState.Loading -> {
                    dashProgressBar.visible(true)
                }
                is DataState.Failure -> handleApiError(it) { refreshDash() }
            }
        })

        viewModel.getNumOnline(deviceUsername)
        viewModel.numOnline.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success -> {
                    updateNumOnlineUI(it.data)
                }
            }
        })


        viewModel.getCurrentPublics(deviceUsername, filter)
        viewModel.publics.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success -> {
                    currentPublicsProgress.visible(false)
                    updateCurrentPublicsUI(it.data)
                }
                is DataState.Loading -> {
                    currentPublicsProgress.visible(true)
                }
            }
        })

        postsQueryButtonClicked(followingPostsButton)
        viewModel.feed.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success -> {
                    isLoading = false
                    postsProgress.visible(false)
                    updateDashFeedUI(it.data)
                }
                is DataState.Loading -> {
                    isLoading = true
                    postsProgress.visible(true)
                }
            }
        })


    }

    private fun initDashFeedRecycler(){
        dashboardfeedView?.layoutManager = LinearLayoutManager(this@DashboardFragment.context)
        ViewCompat.setNestedScrollingEnabled(dashboardfeedView!!, false)
        dashboardfeedadapter = ProfilenewsAdapter(mContext, this@DashboardFragment)
        dashboardfeedView?.adapter = dashboardfeedadapter
        // TODO: 10/21/20 FIX THIS

        /*dashScroll?.viewTreeObserver?.addOnScrollChangedListener {
            if (dashScroll?.viewTreeObserver?.isAlive!!){
                if (dashScroll?.getChildAt(0)?.bottom!! <= dashScroll?.height!! + dashScroll?.scrollY!!) {
                    if (!isLoading && !isLastPage) {
                        isLoading = true
                        currentPage++
                        viewModel.getDashboardFeed(currentPage, pageSize, deviceUsername.toString(), deviceUserID!!, clicked.toString())
                    }
                }
            }
        }*/

        /*dashboardfeedView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == dashboardfeedadapter.itemCount.minus(2)){
                    println("DashFeed getting next page")
                    if (!isLoading && !isLastPage) {
                        isLoading = true
                        currentPage++
                        viewModel.getDashboardFeed(currentPage, pageSize, deviceUsername.toString(), deviceUserID!!, clicked.toString())
                    }
                }
            }
        })*/

    }

    private fun initAdVP(){
        viewPagerAdapter = DashViewPagerAdapter(sliderImgAdsModel!!, mContext)
        viewPager?.adapter = viewPagerAdapter
        viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val adID = sliderImgAdsModel!![position]!!.ad_id
                adViewed(adID)
            }
            override fun onPageScrollStateChanged(state: Int) {
                dashSwipe?.isEnabled = state != ViewPager.SCROLL_STATE_DRAGGING
            }
        })
    }

    private fun initPublicsVP(){
        currentPublicsAdapter = DashCurrentPublicsAdapter(currentPublicsList!!, mContext)
        currentPublicsVP.adapter = currentPublicsAdapter
        currentPublicsVP?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {
                dashSwipe?.isEnabled = state != ViewPager.SCROLL_STATE_DRAGGING
            }
        })

        setPlatformImage(filter)

        currentPublicsOptions?.setOnClickListener {
            val items = resources.getStringArray(R.array.platform_array_w_all)
            LovelyChoiceDialog(mContext)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle(R.string.platform_filter)
                    .setIcon(drawable.icons8_workstation_48)
                    .setMessage(resources.getString(R.string.selected_platform) + " " + SharedPrefManager.getInstance(mContext)!!.currentPublics)
                    .setItems(items) { _: Int, item: String? ->
                        SharedPrefManager.getInstance(mContext)!!.currentPublics = item
                        setPlatformImage(item)
                        filter = item.toString()
                        currentPublicsAdapter?.clear()
                        viewModel.getCurrentPublics(deviceUsername.toString(), filter)
                    }
                    .show()
        }
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
            viewModel.setAdViewed(adID.toInt(), "view", deviceUserID!!, deviceUsername!!)
        }
    }

    private fun updateNumOnlineUI(data: UsersOnlineModel2) {
        val dashNews = data.dashnews
        val usersOnline = data.numonline

        with(binding){
            numUsersOnline.text = usersOnline.num.toString()
            numCurrentPublics.text = usersOnline.numpublics.toString()
            if (usersOnline.unreadmessages > 0) {
                badge_text_view.visible(true)
                if (usersOnline.unreadmessages > 9) {
                    badge_text_view.text = "9+"
                } else {
                    badge_text_view.text = usersOnline.unreadmessages.toString()
                }
            }

            newsLayout.visible(true)
            newsTextView.text = dashNews.toptext
            newsTextView2.text = dashNews.bottomtext
            newsLayout.setOnClickListener { mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(dashNews.link))) }
        }
    }

    private fun updateCurrentPublicsUI(data: List<CurrentPublicsModel>){
        currentPublicsAdapter?.clear()
        if (data.isEmpty()){
            with(binding){
                currentPublicsVP.visibility = INVISIBLE
                noCurrentPublics.visible(true)
                currentPublicsTV.setOnClickListener {
                    currentPublicsAdapter?.clear()
                    viewModel.getCurrentPublics(deviceUsername.toString(), filter)
                }
                noCurrentPublics.setOnClickListener {
                    val asf = PublicsFragment()
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
        }else{
            with(binding){
                noCurrentPublics.visible(false)
                currentPublicsVP.visible(true)
                currentPublicsAdapter?.addItems(data)
                currentPublicsTV.setOnClickListener {
                    currentPublicsProgress.visible(true)
                    viewModel.getCurrentPublics(deviceUsername.toString(), filter)
                }
            }
        }
    }

    private fun updateAdUI(data: List<DashboardAdModelItem>){
        adViewed(data[0].ad_id)
        viewPagerAdapter?.addItems(data)
    }

    private fun updateDashFeedUI(data: List<ProfilenewsRecycler>){
        if (data.isEmpty()){
            noPosts.visible(true)
        }else{
            if (currentPage != PaginationOnScroll.PAGE_START) dashboardfeedadapter.removeLoading()
            postsProgress.visible(false)
            dashboardfeedadapter.addItems(data)
            if (currentPage == 1) {
                dashboardfeedView.scheduleLayoutAnimation()
            }
            // check whether is last page or not
            if (data.size == pageSize) {
                dashboardfeedadapter.addLoading()
            } else {
                isLastPage = true
                //adapter.removeLoading()
            }
            isLoading = false
            noPosts.visible(false)
        }
    }

    private fun setPlatformImage(item: String?) {
        when (item) {
            "Xbox" -> {
                currentPublicsOptions.setImageResource(drawable.icons8_xbox_50)
                filterText.visible(true)
            }
            "PlayStation" -> {
                currentPublicsOptions.setImageResource(drawable.icons8_playstation_50)
                filterText.visible(true)
            }
            "Steam" -> {
                currentPublicsOptions.setImageResource(drawable.icons8_steam_48)
                filterText.visible(true)
            }
            "PC" -> {
                currentPublicsOptions.setImageResource(drawable.icons8_workstation_48)
                filterText.visible(true)
            }
            "Mobile" -> {
                currentPublicsOptions.setImageResource(drawable.icons8_mobile_48)
                filterText.visible(true)
            }
            "Switch" -> {
                currentPublicsOptions.setImageResource(drawable.icons8_nintendo_switch_48)
                filterText.visible(true)
            }
            "Cross-Platform" -> {
                currentPublicsOptions.setImageResource(drawable.icons8_collect_40)
                filterText.visible(true)
            }
            "Other" -> {
                currentPublicsOptions.setImageResource(drawable.icons8_question_mark_64)
                filterText.visible(true)
            }
            else -> {
                currentPublicsOptions.setImageResource(drawable.ic_ellipses)
                filterText.visible(true)
            }
        }
    }

    private fun postsQueryButtonClicked(click: Button?) {
        dashboardfeedadapter.clear()
        postsProgress.visible(true)
        if (click === allPostsButton) {
            if(clicked == "following"){
                val colorFrom = ContextCompat.getColor(mContext, R.color.grey_80)
                val colorTo = ContextCompat.getColor(mContext, R.color.green)
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                colorAnimation.duration = 750
                colorAnimation.addUpdateListener { animator: ValueAnimator -> allPostsButton.setBackgroundColor(animator.animatedValue as Int) }
                colorAnimation.start()
                val colorAnimation2 = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
                colorAnimation2.duration = 750
                colorAnimation2.addUpdateListener { animator: ValueAnimator -> followingPostsButton.setBackgroundColor(animator.animatedValue as Int) }
                colorAnimation2.start()
            }
            clicked = "all"
            currentPage = 1
            viewModel.getDashboardFeed(currentPage, pageSize, deviceUsername.toString(), deviceUserID!!, clicked.toString())

        }
        if (click === followingPostsButton) {
            if (clicked == "all" || clicked == ""){
                val colorTo = ContextCompat.getColor(mContext, R.color.grey_80)
                val colorFrom = ContextCompat.getColor(mContext, R.color.green)
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                colorAnimation.duration = 250
                colorAnimation.addUpdateListener { animator: ValueAnimator -> allPostsButton.setBackgroundColor(animator.animatedValue as Int) }
                colorAnimation.start()
                val colorAnimation2 = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
                colorAnimation2.duration = 250
                colorAnimation2.addUpdateListener { animator: ValueAnimator -> followingPostsButton.setBackgroundColor(animator.animatedValue as Int) }
                colorAnimation2.start()
            }
            clicked = "following"
            currentPage = 1
            viewModel.getDashboardFeed(currentPage, pageSize, deviceUsername.toString(), deviceUserID!!, clicked.toString())

        }
    }

    companion object {
        fun hideKeyboardFrom(context: Context?, view: View) {
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun getViewModel() = DashboardVM::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): DashboardRepo {
        val api = remoteDataSource.buildApi(DashApi::class.java, mContext.fcmToken)
        return DashboardRepo(api)
    }

    override fun onItemSelected(position: Int, item: ProfilenewsRecycler) {
        TODO("Not yet implemented")
    }

    override fun restoreListPosition() {
        TODO("Not yet implemented")
    }
}