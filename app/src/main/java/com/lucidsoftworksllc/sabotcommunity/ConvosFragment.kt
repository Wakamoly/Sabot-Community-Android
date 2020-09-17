package com.lucidsoftworksllc.sabotcommunity

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
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ConvosFragment : Fragment() {
    private var deviceUsername: String? = null
    private var deviceUserID: String? = null
    private var messagesNew: ImageView? = null
    private var messagesMenu: ImageView? = null
    private var noPosts: TextView? = null
    private var dialog: ProgressDialog? = null
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var messages: ArrayList<ConvosHelper>? = null
    private var mCtx: Context? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val convosRootView = inflater.inflate(R.layout.fragment_convos, null)
        mCtx = activity
        messagesNew = convosRootView.findViewById(R.id.messagesNew)
        messagesMenu = convosRootView.findViewById(R.id.messagesMenu)
        noPosts = convosRootView.findViewById(R.id.noPosts)
        dialog = ProgressDialog(mCtx)
        dialog!!.setMessage("Loading conversations...")
        dialog!!.show()
        recyclerView = convosRootView.findViewById(R.id.chatConvos)
        recyclerView?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(mCtx)
        recyclerView?.layoutManager = layoutManager
        messages = ArrayList()
        deviceUsername = SharedPrefManager.getInstance(mCtx!!)!!.username
        deviceUserID = SharedPrefManager.getInstance(mCtx!!)!!.userID
        messagesMenu?.setOnClickListener {
            requireActivity().finish()
            startActivity(Intent(activity, FragmentContainer::class.java))
        }
        messagesNew?.setOnClickListener {
            val ldf = NewMessageFragment()
            (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
        }
        fetchConvos()
        return convosRootView
    }

    private fun fetchConvos() {
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
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        ) { }
        (mCtx as ChatActivity?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        const val URL_FETCH_CONVOS: String = Constants.ROOT_URL + "messages.php/convos"
    }
}