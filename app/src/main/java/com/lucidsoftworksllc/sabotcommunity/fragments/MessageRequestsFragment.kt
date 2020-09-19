package com.lucidsoftworksllc.sabotcommunity.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL
import com.lucidsoftworksllc.sabotcommunity.adapters.MessageRequestsAdapter
import com.lucidsoftworksllc.sabotcommunity.models.MessageRequestsHelper
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MessageRequestsFragment : Fragment(), MessageRequestsAdapter.AdapterCallback {
    private var deviceUsername: String? = null
    private var deviceUserID: String? = null
    private var messagesNew: ImageView? = null
    private var messagesMenu: ImageView? = null
    private var noPosts: TextView? = null
    private var messagesText: TextView? = null
    private var dialog: ProgressDialog? = null
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var requests: ArrayList<MessageRequestsHelper>? = null
    private var mCtx: Context? = null
    override fun onMethodCallback(position: Int) {
        requests!!.removeAt(position)
        adapter!!.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val convosRootView = inflater.inflate(R.layout.fragment_convos, null)
        mCtx = activity
        messagesNew = convosRootView.findViewById(R.id.messagesNew)
        messagesMenu = convosRootView.findViewById(R.id.messagesMenu)
        noPosts = convosRootView.findViewById(R.id.noPosts)
        messagesText = convosRootView.findViewById(R.id.messagesText)
        messagesText?.setText(R.string.message_requests_text)
        noPosts?.setText(R.string.no_message_requests_text)
        dialog = ProgressDialog(mCtx)
        dialog!!.setMessage("Loading message requests...")
        dialog!!.show()
        recyclerView = convosRootView.findViewById(R.id.chatConvos)
        recyclerView?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mCtx)
        recyclerView?.layoutManager = layoutManager
        requests = ArrayList()
        deviceUsername = SharedPrefManager.getInstance(mCtx!!)!!.username
        deviceUserID = SharedPrefManager.getInstance(mCtx!!)!!.userID
        messagesMenu?.setOnClickListener {
            requireActivity().finish()
            startActivity(Intent(mCtx, FragmentContainer::class.java))
        }
        messagesNew?.setOnClickListener {
            val ldf = NewMessageFragment()
            (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
        }
        fetchConvos()
        return convosRootView
    }

    private fun fetchConvos() {
        val stringRequest = StringRequest(Request.Method.GET, "$URL_FETCH_REQUESTS?username=$deviceUsername&userid=$deviceUserID",
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
                            val id = obj.getString("id")
                            val userFrom = obj.getString("user_from")
                            val type = obj.getString("type")
                            val groupId = obj.getString("group_id")
                            val messageObject = MessageRequestsHelper(sentBy, bodySplit, timeMessage, latestProfilePic, profilePic, nickname, id, userFrom, type, groupId)
                            requests!!.add(messageObject)
                        }
                        if (thread.length() == 0) {
                            noPosts!!.visibility = View.VISIBLE
                        }
                        adapter = MessageRequestsAdapter(mCtx!!, requests!!, this@MessageRequestsFragment)
                        recyclerView!!.adapter = adapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        ) { }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        const val URL_FETCH_REQUESTS: String = ROOT_URL + "messages.php/message_requests"
    }
}