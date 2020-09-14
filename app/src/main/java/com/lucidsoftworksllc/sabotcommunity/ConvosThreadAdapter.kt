package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ConvosThreadAdapter(private val mCtx: Context, private val convosList: List<ConvosHelper>) : RecyclerView.Adapter<ConvosThreadAdapter.ViewHolder>() {
    private var deviceUsername: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.recycler_userslist_messages, null)
        val holder = ViewHolder(view)
        deviceUsername = SharedPrefManager.getInstance(mCtx).username
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val convos = convosList[position]
        if (convos.type == "user") {
            holder.convoUsername.text = String.format("@%s", convos.sent_by)
            holder.convoUsername.setTextColor(mCtx.resources.getColor(android.R.color.secondary_text_dark))
            holder.convoBodyPreview.text = convos.body_split
            if (convos.body_split.contains("Them: ")) {
                holder.convoBodyPreview.setTypeface(null, Typeface.BOLD)
            } else {
                holder.convoBodyPreview.setTypeface(null, Typeface.NORMAL)
            }
            holder.convoTimeMessage.text = convos.time_message
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
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
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
                            params["id"] = convos.id
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
            holder.convoTimeMessage.text = convos.time_message
            holder.convoNickname.text = convos.nickname
            if (convos.viewed == "yes" || convos.user_from == deviceUsername) {
                holder.userLayout.setBackgroundColor(Color.parseColor("#111111"))
            } else {
                holder.userLayout.setBackgroundColor(Color.parseColor("#222222"))
            }
            holder.userLayout.setOnClickListener {
                val ldf = MessageGroupFragment()
                val args = Bundle()
                args.putString("group_id", convos.group_id)
                ldf.arguments = args
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
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