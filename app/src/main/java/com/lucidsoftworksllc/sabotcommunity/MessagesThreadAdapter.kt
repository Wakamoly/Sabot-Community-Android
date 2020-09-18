package com.lucidsoftworksllc.sabotcommunity

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
import java.util.*

class MessagesThreadAdapter(private val context: Context, private val messages: ArrayList<MessagesHelper>, private val username: String) : RecyclerView.Adapter<MessagesThreadAdapter.ViewHolder>() {
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
        holder.textViewTime.text = message.date
        if (message.image != "") {
            holder.imgMsg.visibility = View.VISIBLE
            Glide.with((context as ChatActivity))
                    .load(Constants.BASE_URL + message.image)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imgMsg)
            holder.imgMsg.setOnClickListener {
                val asf: Fragment = PhotoViewFragment()
                val args = Bundle()
                args.putString("image", message.image)
                asf.arguments = args
                val fragmentTransaction = (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out)
                fragmentTransaction.replace(R.id.chat_fragment_container, asf)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        } else {
            holder.imgMsg.visibility = View.GONE
            holder.imgMsg.setImageDrawable(null)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewMessage: TextView = itemView.findViewById<View>(R.id.tv_message_content) as TextView
        var textViewTime: TextView = itemView.findViewById<View>(R.id.tv_time) as TextView
        var imgMsg: ImageView = itemView.findViewById(R.id.img_msg)

    }
}