package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.lucidsoftworksllc.sabotcommunity.Constants
import com.lucidsoftworksllc.sabotcommunity.models.PlayerReviewRecycler
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.adapters.PlayerReviewAdapter.PlayerReviewViewHolder
import com.lucidsoftworksllc.sabotcommunity.fragments.FragmentProfile

class PlayerReviewAdapter(private val mCtx: Context, private val playerreviewList: List<PlayerReviewRecycler>) : RecyclerView.Adapter<PlayerReviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerReviewViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.recycler_playerreviews, null)
        return PlayerReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerReviewViewHolder, position: Int) {
        val playerreviews = playerreviewList[position]
        holder.textViewBody.text = playerreviews.comments
        holder.textViewReviewTitle.text = playerreviews.title
        holder.textViewAdded_by.text = playerreviews.nickname
        holder.textViewAdded_by.setOnClickListener { v: View? ->
            val ldf = FragmentProfile()
            val args = Bundle()
            args.putString("UserId", playerreviews.user_id)
            ldf.arguments = args
            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
        holder.textviewdateAdded.text = playerreviews.time
        holder.playerrated.rating = playerreviews.ratingnumber.toFloat()
        val profilePic = playerreviews.profile_pic.substring(0, playerreviews.profile_pic.length - 4) + "_r.JPG"
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profilePic)
                .into(holder.imageViewProfilenewsPic)
        holder.playerreviewLayout.setOnClickListener { v: View? ->
            holder.textViewReviewTitle.maxLines = 3
            holder.textViewBody.maxLines = 20
        }
    }

    override fun getItemCount(): Int {
        return playerreviewList.size
    }

    class PlayerReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageViewProfilenewsPic: ImageView = itemView.findViewById(R.id.imageViewProfilenewsPic)
        var playerrated: SimpleRatingBar = itemView.findViewById(R.id.playerrated)
        var textViewBody: TextView = itemView.findViewById(R.id.textViewBody)
        var textViewAdded_by: TextView = itemView.findViewById(R.id.textViewProfileName)
        var textviewdateAdded: TextView = itemView.findViewById(R.id.textViewReviewPostedDate)
        var textViewReviewTitle: TextView = itemView.findViewById(R.id.textViewReviewTitle)
        var playerreviewLayout: MaterialRippleLayout = itemView.findViewById(R.id.playerreview_layout)
    }

}