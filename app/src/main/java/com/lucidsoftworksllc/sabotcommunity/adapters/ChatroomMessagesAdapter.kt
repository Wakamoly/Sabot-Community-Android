package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.models.ChatroomMessagesHelper
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.fragments.FragmentProfile
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ChatroomMessagesAdapter(private val context: Context, private val messages: ArrayList<ChatroomMessagesHelper>, private val username: String) : RecyclerView.Adapter<ChatroomMessagesAdapter.ViewHolder>() {
    private val SELF = 786
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.username == username) {
            SELF
        } else position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = if (viewType == SELF) {
            LayoutInflater.from(context)
                    .inflate(R.layout.recycler_chatroom_right, parent, false)
        } else {
            LayoutInflater.from(context)
                    .inflate(R.layout.recycler_chatroom_left, parent, false)
        }
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        if (message.verified == "yes") {
            holder.verified.visibility = View.VISIBLE
        } else {
            holder.verified.visibility = View.GONE
        }
        if (message.lastonline == "yes") {
            holder.online.visibility = View.VISIBLE
        } else {
            holder.online.visibility = View.GONE
        }
        val profilePic = message.profile_pic.substring(0, message.profile_pic.length - 4) + "_r.JPG"
        Glide.with(context)
                .load(Constants.BASE_URL + profilePic)
                .into(holder.imageViewProfilenewsPic)
        holder.textViewBody.text = message.message
        holder.profileCommentsDateTimeTop.text = message.time_message
        holder.textViewProfileName.text = message.nickname
        holder.textViewProfileName.setOnClickListener {
            val ldf = FragmentProfile()
            val args = Bundle()
            args.putString("UserId", message.userid)
            ldf.arguments = args
            (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewProfileName: TextView = itemView.findViewById(R.id.textViewProfileName)
        var textViewBody: TextView = itemView.findViewById(R.id.textViewBody)
        var profileCommentsDateTimeTop: TextView = itemView.findViewById(R.id.profileCommentsDateTime_top)
        var imageViewProfilenewsPic: CircleImageView = itemView.findViewById(R.id.imageViewProfilenewsPic)
        var online: CircleImageView = itemView.findViewById(R.id.online)
        var verified: CircleImageView = itemView.findViewById(R.id.verified)

    }
}