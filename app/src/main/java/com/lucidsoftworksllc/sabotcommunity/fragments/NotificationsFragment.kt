package com.lucidsoftworksllc.sabotcommunity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.adapters.NotificationsAdapter
import com.lucidsoftworksllc.sabotcommunity.databinding.FragmentNotificationsBinding
import com.lucidsoftworksllc.sabotcommunity.db.SabotDatabase
import com.lucidsoftworksllc.sabotcommunity.db.notifications.*
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.NotificationsRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.NotificationsVM
import com.lucidsoftworksllc.sabotcommunity.network.NotificationsApi
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseFragment
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.snippet_top_notificationsbar.*
import java.util.*

class NotificationsFragment : BaseFragment<NotificationsVM, FragmentNotificationsBinding, NotificationsRepo>() {
    private var currentPage = PaginationOnScroll.PAGE_START
    private var isLastPage = false
    private val pageSize = PaginationOnScroll.PAGE_SIZE
    private var isLoading = false
    private var last_id: Int = 0
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: NotificationsAdapter
    private lateinit var notificationDao: NotificationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCtx = requireContext()
        notificationDao = SabotDatabase(mCtx).getNotificationsDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initRecycler()
        initNotiMenu()
        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.currentNotifications.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Loading -> {
                    displayProgressbar(true)
                }
                is DataState.Success -> {
                    displayProgressbar(false)
                    appendNotifications(it.data, true)
                }
                is DataState.Error -> {
                    displayError(it.exception.toString())
                }
            }
        })
        viewModel.newNotifications.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Loading -> {
                    view?.snackbarShort(getString(R.string.updating_notifications), "")
                }
                is DataState.Success -> {
                    appendNotifications(it.data, false)
                }
            }
        })

        viewModel.getCurrentNotifications(deviceUsername, deviceUserID, currentPage, pageSize)
    }

    private fun displayError(message: String?){
        noNotifications?.visible(true)
        if(message != null){
            println("Error message: $message")
            mCtx.toastShort("Error!")
        }
        mCtx.toastShort("Error!")
    }

    private fun displayProgressbar(isDisplayed: Boolean){
        progressBar?.visible(isDisplayed)
    }

    private fun scrollToTop() {
        if (adapter.itemCount > 1) Objects.requireNonNull(recyclerNotifications?.layoutManager!!).smoothScrollToPosition(recyclerNotifications, null, 0)
    }

    private fun appendNotifications(notis: List<NotificationCacheEntity>, init: Boolean){
        println("Appending notifications")
        displayProgressbar(false)
        if (notis.isEmpty() && adapter.size() == 0) {
            noNotifications?.visible(true)
        }
        if (init){
            notiLayout?.visible(true)
            if (currentPage != PaginationOnScroll.PAGE_START) adapter.removeLoading()
            adapter.addItems(notis)
            if (notis.size == pageSize) {
                adapter.addLoading()
            } else {
                isLastPage = true
                //adapter.removeLoading();
            }
            isLoading = false
            last_id = notis.first().id
            viewModel.getNewNotifications(deviceUsername, deviceUserID, currentPage, pageSize, last_id)
        }else{
            var atTop = false
            val currentView = layoutManager.findFirstVisibleItemPosition()
            if (currentView == 0){
                atTop = true
            }
            if (currentPage != PaginationOnScroll.PAGE_START) adapter.removeLoading()
            adapter.addItemsToTop(notis)
            if (notis.size == pageSize) {
                adapter.addLoading()
            } else {
                isLastPage = true
            }
            isLoading = false
            if (atTop){
                scrollToTop()
            }
        }
    }

    private fun initNotiMenu(){
        //TODO: Add opened to Room db
        notiMenu?.setOnClickListener { view: View? ->
            val popup = PopupMenu(mCtx, view)
            val inflater1 = popup.menuInflater
            inflater1.inflate(R.menu.noti_menu, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                if (item.itemId == R.id.menuSetOpened) {
                    viewModel.setAllOpened(deviceUsername)
                    refreshFragment()
                }
                true
            }
            popup.show()
        }
    }

    private fun initRecycler(){
        recyclerNotifications?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mCtx, LinearLayoutManager.VERTICAL, false)
        recyclerNotifications?.layoutManager = layoutManager
        adapter = NotificationsAdapter(mCtx)
        recyclerNotifications?.adapter = adapter


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


        notificationsSwipe?.setOnRefreshListener {
            refreshFragment()
        }
    }

    private fun refreshFragment(){
        noNotifications?.visible(false)
        adapter.clear()
        viewModel.getCurrentNotifications(deviceUsername, deviceUserID, currentPage, pageSize)
        notificationsSwipe?.isRefreshing = false
    }

    companion object {
        private const val TAG = "NotificationsFragment"
    }

    override fun getViewModel(): Class<NotificationsVM> = NotificationsVM::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentNotificationsBinding
                    .inflate(inflater, container, false)

    override fun getFragmentRepository(): NotificationsRepo {
        val api = remoteDataSource.buildApi(NotificationsApi::class.java, mCtx.fcmToken)
        return NotificationsRepo(api, notificationDao)
    }
}