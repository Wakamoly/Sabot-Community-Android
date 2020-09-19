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
import com.lucidsoftworksllc.sabotcommunity.models.ClansRecycler
import com.lucidsoftworksllc.sabotcommunity.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.fragments.ClanFragment

//Class extending RecyclerviewAdapter
class JoinedClansAdapter(private val mCtx: Context, private val clansList: List<ClansRecycler>) : RecyclerView.Adapter<JoinedClansAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.recycler_clans, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clans = clansList[position]
        val clanTag = clans.tag.capitalize()
        val clanTagFull = "[$clanTag]"
        holder.textViewTag.text = clanTagFull
        holder.textViewTag.isAllCaps = true
        holder.textViewTitle.text = clans.name
        holder.memberCount.text = clans.num_members
        holder.tvPosition.text = clans.position
        if (clans.avg.isNotEmpty() && clans.avg != "null") {
            holder.clanRating.rating = clans.avg.toFloat()
        }
        Glide.with(mCtx)
                .load(Constants.BASE_URL + clans.insignia)
                .into(holder.clanImageView)
        holder.recyclerclanLayout.setOnClickListener {
            val ldf = ClanFragment()
            val args = Bundle()
            args.putString("ClanId", clans.id)
            ldf.arguments = args
            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
    }

    override fun getItemCount(): Int {
        return clansList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyclerclanLayout: MaterialRippleLayout = itemView.findViewById(R.id.recyclerclanLayout)
        var clanImageView: ImageView = itemView.findViewById(R.id.clanImageView)
        var textViewTag: TextView = itemView.findViewById(R.id.textViewTag)
        var textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        var memberCount: TextView = itemView.findViewById(R.id.memberCount)
        var tvPosition: TextView = itemView.findViewById(R.id.tvPosition)
        var clanRating: SimpleRatingBar = itemView.findViewById(R.id.clanRating)

    }
}