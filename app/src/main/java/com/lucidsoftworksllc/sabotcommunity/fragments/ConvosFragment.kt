package com.lucidsoftworksllc.sabotcommunity.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.ConvosThreadAdapter
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MainStateEvent
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesDataModel
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesViewModel
import com.lucidsoftworksllc.sabotcommunity.models.ConvosHelper
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ConvosFragment : Fragment() {
    private var currentPage = PaginationOnScroll.PAGE_START
    private var isLastPage = false
    private val pageSize = PaginationOnScroll.PAGE_SIZE
    private var isLoading = false
    private var deviceUsername: String? = null
    private var deviceUserID: String? = null
    private var messagesNew: ImageView? = null
    private var messagesMenu: ImageView? = null
    private var noPosts: TextView? = null
    private var dialog: ProgressDialog? = null
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: ConvosThreadAdapter? = null
    private var messages: ArrayList<ConvosHelper>? = null
    private var mCtx: Context? = null
    private var progressBar: ProgressBar? = null
    private val viewModel: MessagesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val convosRootView = inflater.inflate(R.layout.fragment_convos, null)
        mCtx = activity
        deviceUsername = mCtx?.deviceUsername
        deviceUserID = mCtx?.deviceUserID
        messagesNew = convosRootView.findViewById(R.id.messagesNew)
        messagesMenu = convosRootView.findViewById(R.id.messagesMenu)
        noPosts = convosRootView.findViewById(R.id.noPosts)
        recyclerView = convosRootView.findViewById(R.id.chatConvos)
        progressBar = convosRootView.findViewById(R.id.progressBar)

        /*dialog = ProgressDialog(mCtx)
        dialog!!.setMessage("Loading conversations...")
        dialog!!.show()*/

        initRecycler()
        initMenus()
        subscribeObservers()
        viewModel.setStateEvent(MainStateEvent.GetMessagesEvents, mCtx!!)
        return convosRootView
    }

    private fun initRecycler(){
        recyclerView?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mCtx, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = layoutManager

        messages = ArrayList()
        adapter = ConvosThreadAdapter(mCtx!!, messages!! as MutableList<MessagesDataModel>)
        recyclerView?.adapter = adapter
    }

    private fun initMenus(){
        messagesMenu?.setOnClickListener {
            requireActivity().finish()
            startActivity(Intent(activity, FragmentContainer::class.java))
        }
        messagesNew?.setOnClickListener {
            val ldf = NewMessageFragment()
            (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
        }
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState){
                is DataState.Success<List<MessagesDataModel>> -> {
                    println("Success!")
                    displayProgressbar(false)
                    appendMessages(dataState.data)
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
                is DataState.UpdateSuccess<List<MessagesDataModel>> -> {
                    println("Update Success!")
                    displayProgressbar(false)
                    appendMessages(dataState.data)
                }
            }
        })
    }

    private fun displayError(message: String?){
        noPosts?.visibility = View.VISIBLE
        if(message != null){
            println("Error message: $message")
            mCtx?.toastLong("Error!: $message")
        }else{
            mCtx?.toastShort("Error!")
        }

    }

    private fun displayProgressbar(isDisplayed: Boolean){
        progressBar?.visibility = if(isDisplayed) View.VISIBLE else View.GONE
    }

    private fun appendMessages(messages: List<MessagesDataModel>){
        println("Appending notifications")
        if (messages.isEmpty()) {
            noPosts?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
        }
        //notiLayout!!.visibility = View.VISIBLE
        //if (currentPage != PaginationOnScroll.PAGE_START) adapter?.removeLoading()
        progressBar!!.visibility = View.GONE
        adapter?.addItemsToTop(messages)
        //recyclerView?.scheduleLayoutAnimation()
        /*if (messages.size == pageSize) {
            adapter!!.addLoading()
        } else {
            isLastPage = true
            //adapter?.removeLoading()
        }
        isLoading = false*/
    }

    /*private fun fetchConvos() {
        val stringRequest = StringRequest(Request.Method.GET, "$URL_FETCH_CONVOS?username=$deviceUsername&userid=$deviceUserID",
                { response: String? ->
                    try {
                        dialog!!.dismiss()
                        val res = JSONObject(response!!)
                        val thread = res.getJSONArray("messages")
                        for (i in 0 until thread.length()) {
                            val obj = thread.getJSONObject(i)
                            val sentBy = obj.getString("sent_by")
                            val profilePic = obj.getString("profile_pic")
                            val bodySplit = obj.getString("body_split")
                            val timeMessage = obj.getString("time_message")
                            val latestProfilePic = obj.getString("latest_profile_pic")
                            val nickname = obj.getString("nickname")
                            val verified = obj.getString("verified")
                            val lastOnline = obj.getString("last_online")
                            val viewed = obj.getString("viewed")
                            val id = obj.getString("id")
                            val userFrom = obj.getString("user_from")
                            val type = obj.getString("type")
                            val groupId = obj.getString("group_id")
                            val messageObject = ConvosHelper(sentBy, bodySplit, timeMessage, latestProfilePic, profilePic, nickname, verified, lastOnline, viewed, id, userFrom, type, groupId)
                            messages!!.add(messageObject)
                        }
                        if (thread.length() == 0) {
                            noPosts!!.visibility = View.VISIBLE
                        }
                        adapter = ConvosThreadAdapter(mCtx!!, messages!!)
                        recyclerView!!.adapter = adapter
                        recyclerView?.scheduleLayoutAnimation()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        ) { }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }*/

    companion object {
        const val URL_FETCH_CONVOS: String = Constants.ROOT_URL + "messages.php/convos"
    }
}