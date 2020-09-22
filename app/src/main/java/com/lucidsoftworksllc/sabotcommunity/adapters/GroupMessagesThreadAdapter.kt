package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.models.GroupMessagesHelper
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.fragments.PhotoViewFragment
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class GroupMessagesThreadAdapter(private val context: Context, private val messages: ArrayList<GroupMessagesHelper>, private val username: String) : RecyclerView.Adapter<GroupMessagesThreadAdapter.ViewHolder>() {
    private val SELF = 786
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.user_from == username) {
            SELF
        } else position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = if (viewType == SELF) {
            LayoutInflater.from(context)
                    .inflate(R.layout.row_group_sent_message, parent, false)
        } else {
            LayoutInflater.from(context)
                    .inflate(R.layout.row_group_received_message, parent, false)
        }
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.textViewMessage.text = message.body
        holder.textViewTime.text = message.date
        if (message.image != "") {
            holder.img_msg.visibility = View.VISIBLE
            Glide.with((context as ChatActivity))
                    .load(Constants.BASE_URL + message.image)
                    .thumbnail(0.5f)
                    .into(holder.img_msg)
            holder.img_msg.setOnClickListener {
                val asf: Fragment = PhotoViewFragment()
                val args = Bundle()
                args.putString("image", message.image)
                asf.arguments = args
                val fragmentTransaction = (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                fragmentTransaction.replace(R.id.chat_fragment_container, asf)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        } else {
            holder.img_msg.setImageDrawable(null)
            holder.img_msg.visibility = View.GONE
        }
        val profilePic = message.profile_pic.substring(0, message.profile_pic.length - 4) + "_r.JPG"
        Glide.with((context as ChatActivity))
                .load(Constants.BASE_URL + profilePic)
                .error(R.mipmap.ic_launcher)
                .into(holder.profilePicGroup)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewMessage: TextView = itemView.findViewById<View>(R.id.tv_message_content) as TextView
        var textViewTime: TextView = itemView.findViewById<View>(R.id.tv_time) as TextView
        var img_msg: ImageView = itemView.findViewById(R.id.img_msg)
        var profilePicGroup: CircleImageView = itemView.findViewById(R.id.profile_pic_group)

    }
}