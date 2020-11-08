package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesCacheEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesDataModel
import kotlin.collections.indices
import com.lucidsoftworksllc.sabotcommunity.fragments.MessageFragment
import com.lucidsoftworksllc.sabotcommunity.fragments.MessageGroupFragment
import com.lucidsoftworksllc.sabotcommunity.others.getTimeAgo
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ConvosThreadAdapter(private val mCtx: Context) : RecyclerView.Adapter<ConvosThreadAdapter.ViewHolder>() {
    private var deviceUsername: String? = null
    private var isLoaderVisible = false
    private val convosList: MutableList<MessagesCacheEntity> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.recycler_userslist_messages, null)
        val holder = ViewHolder(view)
        deviceUsername = SharedPrefManager.getInstance(mCtx)!!.username
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val convos = convosList[position]
        if (convos.type == "user") {
            holder.convoUsername.text = String.format("@%s", convos.sent_by)
            holder.convoUsername.setTextColor(ContextCompat.getColor(mCtx, android.R.color.secondary_text_dark))
            holder.convoBodyPreview.text = convos.body_split
            if (convos.body_split.contains("Them: ")) {
                holder.convoBodyPreview.setTypeface(null, Typeface.BOLD)
            } else {
                holder.convoBodyPreview.setTypeface(null, Typeface.NORMAL)
            }
            holder.convoTimeMessage.text = getTimeAgo(convos.time_message, mCtx)
            holder.convoNickname.text = convos.nickname
            if (convos.viewed == "yes" || convos.user_from == deviceUsername) {
                holder.userLayout.setBackgroundColor(Color.parseColor("#111111"))
            } else {
                holder.userLayout.setBackgroundColor(Color.parseColor("#222222"))
            }
            holder.userLayout.setOnClickListener {
                val ldf = MessageFragment()
                val args = Bundle()
                args.putString("user_to", convos.sent_by)
                ldf.arguments = args
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_up).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
                if (convos.viewed != "yes" && convos.sent_by != deviceUsername) {
                    val stringRequest: StringRequest = object : StringRequest(Method.POST, SET_READ, Response.Listener { response: String? ->
                        try {
                            val obj = JSONObject(response!!)
                            if (obj.getBoolean("error")) {
                                Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { Toast.makeText(mCtx, "Could not set message as read, please try again later...", Toast.LENGTH_LONG).show() }) {
                        override fun getParams(): MutableMap<String, String?> {
                            val params: MutableMap<String, String?> = HashMap()
                            params["username"] = deviceUsername
                            params["id"] = convos.message_id.toString()
                            return params
                        }
                    }
                    (mCtx as ChatActivity).addToRequestQueue(stringRequest)
                }
            }
            if (convos.last_online == "yes") {
                holder.lastOnline.visibility = View.VISIBLE
            } else {
                holder.lastOnline.visibility = View.GONE
            }
            if (convos.verified == "yes") {
                holder.verified.visibility = View.VISIBLE
            } else {
                holder.verified.visibility = View.GONE
            }
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

            //color deprecation
            holder.convoUsername.setTextColor(ContextCompat.getColor(mCtx, R.color.light_blue))

            holder.convoBodyPreview.text = convos.body_split
            holder.convoTimeMessage.text = getTimeAgo(convos.time_message, mCtx)
            holder.convoNickname.text = convos.nickname
            if (convos.viewed == "yes" || convos.user_from == deviceUsername) {
                holder.userLayout.setBackgroundColor(Color.parseColor("#111111"))
            } else {
                holder.userLayout.setBackgroundColor(Color.parseColor("#222222"))
            }
            holder.userLayout.setOnClickListener {
                val ldf = MessageGroupFragment()
                val args = Bundle()
                args.putString("group_id", convos.group_id.toString())
                ldf.arguments = args
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_up).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
            }
            if (convos.last_online == "yes") {
                holder.lastOnline.visibility = View.VISIBLE
            } else {
                holder.lastOnline.visibility = View.GONE
            }
            if (convos.verified == "yes") {
                holder.verified.visibility = View.VISIBLE
            } else {
                holder.verified.visibility = View.GONE
            }
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + convos.profile_pic)
                    .into(holder.userImageView)
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + convos.latest_profile_pic)
                    .into(holder.lastRepliedProfilePic)
        }
    }

    override fun getItemCount(): Int {
        return convosList.size
    }

    fun addItems(items: List<MessagesCacheEntity>) {
        if (convosList.isEmpty()){
            convosList.addAll(items)
        }else{
            for (item in items.indices){
                var addNew = true
                for (convo in convosList.indices){
                    if (convosList[convo].type == "user"){
                        if (items[item].sent_by == convosList[convo].sent_by){
                            convosList[convo] = items[item]
                            addNew = false
                            break
                        }
                    }else if (convosList[convo].type == "group"){
                        if (items[item].group_id == convosList[convo].group_id){
                            convosList[convo] = items[item]
                            addNew = false
                            break
                        }
                    }
                }
                if (addNew){
                    convosList.add(0, items[item])
                }
            }
        }
        notifyDataSetChanged()
    }

    fun addItemsToTop(items: List<MessagesCacheEntity>) {
        convosList.addAll(0, items)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        convosList.add(MessagesCacheEntity(0, false, null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), "yes", 0, null.toString(), null.toString(), 0))
        notifyItemInserted(convosList.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        if (convosList.isNotEmpty()) {
            val position = convosList.size - 1
            //val item = getItem(position)
            convosList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position >= 0) {
            val animation: Animation = AnimationUtils.loadAnimation(mCtx, R.anim.slide_in)
            viewToAnimate.startAnimation(animation)
        }
    }

    fun clear() {
        convosList.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): MessagesCacheEntity {
        return convosList[position]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userLayout: MaterialRippleLayout = itemView.findViewById(R.id.userLayout)
        var userImageView: CircleImageView = itemView.findViewById(R.id.userImageView)
        var verified: CircleImageView = itemView.findViewById(R.id.verified)
        var lastOnline: CircleImageView = itemView.findViewById(R.id.online)
        var lastRepliedProfilePic: CircleImageView = itemView.findViewById(R.id.lastRepliedProfilePic)
        var convoUsername: TextView = itemView.findViewById(R.id.convoUsername)
        var convoBodyPreview: TextView = itemView.findViewById(R.id.convoBodyPreview)
        var convoTimeMessage: TextView = itemView.findViewById(R.id.convoTimeMessage)
        var convoNickname: TextView = itemView.findViewById(R.id.convoNickname)

    }

    companion object {
        private const val SET_READ = Constants.ROOT_URL + "set_message_read.php"
    }
}