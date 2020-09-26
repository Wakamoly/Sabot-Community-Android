package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.models.MessageRequestsHelper
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MessageRequestsAdapter(private val mCtx: Context, private val convosList: List<MessageRequestsHelper>, private val mAdapterCallback: AdapterCallback) : RecyclerView.Adapter<MessageRequestsAdapter.ViewHolder>() {
    private var deviceUsername: String? = null
    private var deviceUserID: String? = null

    interface AdapterCallback {
        fun onMethodCallback(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.recycler_message_requests, null)
        val holder = ViewHolder(view)
        deviceUsername = SharedPrefManager.getInstance(mCtx)!!.username
        deviceUserID = SharedPrefManager.getInstance(mCtx)!!.userID
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val convos = convosList[position]
        if (convos.type == "user") {
            holder.convoUsername.text = String.format("@%s", convos.sent_by)
            holder.convoBodyPreview.text = convos.body_split
            holder.convoTimeMessage.text = convos.time_message
            holder.convoNickname.text = convos.nickname
            val profilePic = convos.profile_pic.substring(0, convos.profile_pic.length - 4) + "_r.JPG"
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + profilePic)
                    .into(holder.userImageView)
            val profilePic2 = convos.latest_profile_pic.substring(0, convos.latest_profile_pic.length - 4) + "_r.JPG"
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + profilePic2)
                    .into(holder.lastRepliedProfilePic)
        } else if (convos.type == "group") {
            val usernameText = convos.sent_by + " users in conversation"
            holder.convoUsername.text = usernameText
            holder.convoUsername.setTextColor(mCtx.resources.getColor(R.color.light_blue))
            holder.convoBodyPreview.text = convos.body_split
            holder.convoTimeMessage.text = convos.time_message
            holder.convoNickname.text = convos.nickname
            val profilePic = convos.profile_pic.substring(0, convos.profile_pic.length - 4) + "_r.JPG"
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + profilePic)
                    .into(holder.userImageView)
            val profilePic2 = convos.latest_profile_pic.substring(0, convos.latest_profile_pic.length - 4) + "_r.JPG"
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + profilePic2)
                    .into(holder.lastRepliedProfilePic)
            holder.deny.setOnClickListener {
                LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.white)
                        .setIcon(R.drawable.ic_error)
                        .setTitle(R.string.remove_group_request)
                        .setMessage("Remove group request?")
                        .setPositiveButton(android.R.string.ok) {
                            holder.confirm.visibility = View.GONE
                            holder.deny.visibility = View.GONE
                            holder.actionProgress.visibility = View.VISIBLE
                            requestAction("remove", convos.id, position, convos.group_id)
                        }
                        .setNegativeButton(android.R.string.no) { }
                        .show()
            }
            holder.confirm.setOnClickListener {
                LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.white)
                        .setIcon(R.drawable.ic_check)
                        .setTitle(R.string.accept_group_request)
                        .setMessage("Accept group request?")
                        .setPositiveButton(android.R.string.ok) {
                            holder.confirm.visibility = View.GONE
                            holder.deny.visibility = View.GONE
                            holder.actionProgress.visibility = View.VISIBLE
                            requestAction("accept", convos.id, position, convos.group_id)
                        }
                        .setNegativeButton(android.R.string.no) { }
                        .show()
            }
        }
    }

    private fun requestAction(method: String, request_id: String, position: Int, group_id: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, MESSAGE_SET_REQUEST, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (obj.getString("error") == "false") {
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                    mAdapterCallback.onMethodCallback(position)
                } else {
                    mAdapterCallback.onMethodCallback(position)
                    Toast.makeText(mCtx, "Could not accept/deny request, you may have already performed this action!", Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Toast.makeText(mCtx, "Could not accept/deny request, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["request_id"] = request_id
                params["method"] = method
                params["username"] = deviceUsername!!
                params["userid"] = deviceUserID!!
                params["group_id"] = group_id
                return params
            }
        }
        (mCtx as ChatActivity).addToRequestQueue(stringRequest)
    }

    override fun getItemCount(): Int {
        return convosList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userImageView: CircleImageView = itemView.findViewById(R.id.userImageView)
        var lastRepliedProfilePic: CircleImageView = itemView.findViewById(R.id.lastRepliedProfilePic)
        var convoUsername: TextView = itemView.findViewById(R.id.convoUsername)
        var convoBodyPreview: TextView = itemView.findViewById(R.id.convoBodyPreview)
        var convoTimeMessage: TextView = itemView.findViewById(R.id.convoTimeMessage)
        var convoNickname: TextView = itemView.findViewById(R.id.convoNickname)
        var confirm: ImageButton = itemView.findViewById(R.id.confirm)
        var deny: ImageButton = itemView.findViewById(R.id.deny)
        var actionProgress: ProgressBar = itemView.findViewById(R.id.actionProgress)

    }

    companion object {
        private const val MESSAGE_SET_REQUEST = Constants.ROOT_URL + "messages.php/request_action"
    }
}