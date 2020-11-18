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
import com.lucidsoftworksllc.sabotcommunity.models.MessagesHelper
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesDataModel
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesEntity
import com.lucidsoftworksllc.sabotcommunity.fragments.PhotoViewFragment
import com.lucidsoftworksllc.sabotcommunity.others.active_label.SocialTextView
import com.lucidsoftworksllc.sabotcommunity.others.getTimeAgo
import com.lucidsoftworksllc.sabotcommunity.others.setClicks
import com.lucidsoftworksllc.sabotcommunity.others.visible
import java.util.*
import kotlin.collections.ArrayList

class MessagesThreadAdapter(private val context: Context, private val username: String) : RecyclerView.Adapter<MessagesThreadAdapter.ViewHolder>() {
    private val SELF = 786
    private val messages: MutableList<UserMessagesEntity> = ArrayList()
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.user_from == username) {
            SELF
        } else position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = if (viewType == SELF) {
            LayoutInflater.from(context)
                    .inflate(R.layout.row_sent_message, parent, false)
        } else {
            LayoutInflater.from(context)
                    .inflate(R.layout.row_received_message, parent, false)
        }
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.textViewMessage.text = message.body
        holder.textViewMessage.setClicks(context)
        holder.textViewTime.text = getTimeAgo(message.date, context)
        if (message.image != "") {
            holder.imgMsg.visible(true)
            Glide.with((context as ChatActivity))
                    .load(Constants.BASE_URL + message.image)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imgMsg)
            holder.imgMsg.setOnClickListener {
                val asf: Fragment = PhotoViewFragment()
                val args = Bundle()
                args.putString("image", message.image)
                asf.arguments = args
                val fragmentTransaction = (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.chat_fragment_container, asf)
                fragmentTransaction.commit()
            }
        } else {
            holder.imgMsg.visible(false)
            holder.imgMsg.setImageDrawable(null)
        }
    }

    fun add(item: UserMessagesEntity){
        messages.add(0,item)
        notifyDataSetChanged()
    }

    fun addItems(items: List<UserMessagesEntity>) {
        if (messages.isEmpty()){
            messages.addAll(items)
        }else{
            for (item in items.indices){
                var addNew = true
                for (message in messages.indices){
                    if (items[item].message_id == messages[message].message_id){
                        messages[message] = items[item]
                        addNew = false
                        break
                    }
                }
                if (addNew){
                    messages.add(0, items[item])
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewMessage: SocialTextView = itemView.findViewById(R.id.tv_message_content)
        var textViewTime: TextView = itemView.findViewById<View>(R.id.tv_time) as TextView
        var imgMsg: ImageView = itemView.findViewById(R.id.img_msg)
    }
}