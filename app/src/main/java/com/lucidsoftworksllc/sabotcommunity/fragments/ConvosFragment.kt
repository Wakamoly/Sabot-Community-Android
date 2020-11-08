package com.lucidsoftworksllc.sabotcommunity.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.ConvosThreadAdapter
import com.lucidsoftworksllc.sabotcommunity.databinding.FragmentConvosBinding
import com.lucidsoftworksllc.sabotcommunity.db.SabotDatabase
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.*
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.ConvosRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.ConvosVM
import com.lucidsoftworksllc.sabotcommunity.models.ConvosHelper
import com.lucidsoftworksllc.sabotcommunity.network.UserMessageApi
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseFragment
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_convos.*
import java.util.*

class ConvosFragment : BaseFragment<ConvosVM, FragmentConvosBinding, ConvosRepo>() {
    private var currentPage = PaginationOnScroll.PAGE_START
    private var isLastPage = false
    private val pageSize = PaginationOnScroll.PAGE_SIZE
    private var isLoading = false
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: ConvosThreadAdapter? = null
    private var messages: ArrayList<ConvosHelper>? = null
    private var canUpdate: Boolean = false
    private lateinit var generalMessagesDao: MessagesDao

    override fun onPause() {
        super.onPause()
        canUpdate = false
    }

    override fun onResume() {
        super.onResume()
        canUpdate = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCtx = requireContext()
        generalMessagesDao = SabotDatabase(mCtx).getMessagesDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        canUpdate = true
        initRecycler()
        initMenus()
        subscribeObservers()
    }

    private fun initRecycler(){
        chatConvos?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mCtx, LinearLayoutManager.VERTICAL, false)
        chatConvos?.layoutManager = layoutManager

        messages = ArrayList()
        adapter = ConvosThreadAdapter(mCtx)
        chatConvos?.adapter = adapter
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
        viewModel.getMessageUnits(deviceUsername, deviceUserID)
        viewModel.messageUnits.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success -> {
                    displayProgressbar(false)
                    appendMessages(it.data)
                }
                is DataState.Error -> {
                    displayProgressbar(false)
                    displayError(it.exception.message)
                }
                is DataState.Loading -> {
                    displayProgressbar(true)
                }
                is DataState.UpdateSuccess -> {
                    displayProgressbar(false)
                    appendMessages(it.data)
                }
            }
        })
    }

    private fun displayError(message: String?){
        noPosts?.visible(true)
        if(message != null){
            println("Error message: $message")
            mCtx.toastLong("Error!: $message")
        }else{
            mCtx.toastShort("Error!")
        }

    }

    private fun displayProgressbar(isDisplayed: Boolean){
        progressBar?.visible(isDisplayed)
    }

    private fun appendMessages(messages: List<MessagesCacheEntity>){
        if (messages.isEmpty()) {
            noPosts?.visible(true)
            progressBar?.visible(false)
        }
        //notiLayout!!.visibility = View.VISIBLE
        //if (currentPage != PaginationOnScroll.PAGE_START) adapter?.removeLoading()
        progressBar?.visible(false)
        adapter?.addItems(messages)
        //recyclerView?.scheduleLayoutAnimation()
        /*if (messages.size == pageSize) {
            adapter!!.addLoading()
        } else {
            isLastPage = true
            //adapter?.removeLoading()
        }
        isLoading = false*/
    }

    override fun getViewModel() = ConvosVM::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentConvosBinding.inflate(
                    inflater,
                    container,
                    false)

    override fun getFragmentRepository(): ConvosRepo {
        val api = remoteDataSource.buildApi(MessagesRetrofit::class.java, mCtx.fcmToken)
        return ConvosRepo(api, generalMessagesDao)
    }

}