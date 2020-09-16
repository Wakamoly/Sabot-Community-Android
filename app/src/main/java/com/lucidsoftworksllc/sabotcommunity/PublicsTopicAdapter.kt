package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.PublicsTopicAdapter.PublicsTopicViewHolder

class PublicsTopicAdapter(private val mCtx: Context, private val publicsTopicList: List<PublicsTopicRecycler>) : RecyclerView.Adapter<PublicsTopicViewHolder>() {
    private val isLoadingAdded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicsTopicViewHolder {
        var holder: PublicsTopicViewHolder? = null
        val inflater = LayoutInflater.from(mCtx)
        when (viewType) {
            ITEM -> holder = getViewHolder(inflater)
            LOADING -> {
                val v2 = inflater.inflate(R.layout.item_progress, parent, false)
                holder = LoadingVH(v2)
            }
        }
        return holder!!
    }

    private fun getViewHolder(inflater: LayoutInflater): PublicsTopicViewHolder {
        val holder: PublicsTopicViewHolder
        val v1 = inflater.inflate(R.layout.recycler_publics_topic, null)
        holder = PublicsTopicViewHolder(v1)
        return holder
    }

    override fun onBindViewHolder(holder: PublicsTopicViewHolder, position: Int) {
        val publics = publicsTopicList[position]
        holder.textViewTitle.text = publics.subject
        holder.textViewDate.text = publics.date
        holder.textViewNumPosts.text = publics.numPosts
        holder.tvProfileName.text = String.format("@%s", publics.username)
        holder.textViewGamename.text = publics.gamename
        val profilePic = publics.profile_pic.substring(0, publics.profile_pic.length - 4) + "_r.JPG"
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profilePic)
                .into(holder.imageView)
        when (publics.type) {
            "Xbox" -> {
                holder.notiType.setImageResource(R.drawable.icons8_xbox_50)
                holder.notiType.visibility = View.VISIBLE
            }
            "PlayStation" -> {
                holder.notiType.setImageResource(R.drawable.icons8_playstation_50)
                holder.notiType.visibility = View.VISIBLE
            }
            "Steam" -> {
                holder.notiType.setImageResource(R.drawable.icons8_steam_48)
                holder.notiType.visibility = View.VISIBLE
            }
            "PC" -> {
                holder.notiType.setImageResource(R.drawable.icons8_workstation_48)
                holder.notiType.visibility = View.VISIBLE
            }
            "Mobile" -> {
                holder.notiType.setImageResource(R.drawable.icons8_mobile_48)
                holder.notiType.visibility = View.VISIBLE
            }
            "Switch" -> {
                holder.notiType.setImageResource(R.drawable.icons8_nintendo_switch_48)
                holder.notiType.visibility = View.VISIBLE
            }
            else -> {
                holder.notiType.setImageResource(R.drawable.icons8_question_mark_64)
                holder.notiType.visibility = View.VISIBLE
            }
        }
        holder.eventDate.text = publics.event_date
        if (publics.event_date == "ended") {
            holder.eventDate.setTextColor(ContextCompat.getColor(mCtx, R.color.pin))
        } else if (publics.event_date == "now") {
            holder.eventDate.setTextColor(ContextCompat.getColor(mCtx, R.color.green))
        }
        holder.publicsTopicLayout.setOnClickListener {
            if (mCtx is FragmentContainer) {
                val ldf = PublicsTopicFragment()
                val args = Bundle()
                args.putString("PublicsId", publics.id)
                ldf.arguments = args
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
        }
        holder.numPlayersAdded.text = publics.num_added
        holder.numPlayersNeeded.text = publics.num_players
        holder.textViewContext.text = publics.context
    }

    private class LoadingVH(itemView: View) : PublicsTopicViewHolder(itemView)

    override fun getItemCount(): Int {
        return publicsTopicList.size
    }

    open class PublicsTopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.publicsImageView)
        var notiType: ImageView = itemView.findViewById(R.id.platformType)
        var textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        var textViewDate: TextView = itemView.findViewById(R.id.textViewProfilenewsDate)
        var textViewNumPosts: TextView = itemView.findViewById(R.id.textViewNumComments)
        var tvProfileName: TextView = itemView.findViewById(R.id.tvProfileName)
        var eventDate: TextView = itemView.findViewById(R.id.eventDate)
        var textViewContext: TextView = itemView.findViewById(R.id.textViewContext)
        var numPlayersAdded: TextView = itemView.findViewById(R.id.numPlayersAdded)
        var numPlayersNeeded: TextView = itemView.findViewById(R.id.numPlayersNeeded)
        var textViewGamename: TextView = itemView.findViewById(R.id.textViewGamename)
        var publicsTopicLayout: RelativeLayout = itemView.findViewById(R.id.publicsTopicLayout)

    }

    companion object {
        private const val ITEM = 0
        private const val LOADING = 1
    }
}