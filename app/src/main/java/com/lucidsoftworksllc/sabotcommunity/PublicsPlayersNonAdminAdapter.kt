package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class PublicsPlayersNonAdminAdapter(private val mCtx: Context, private val membersList: List<PublicsPlayersRecycler>) : RecyclerView.Adapter<PublicsPlayersNonAdminAdapter.MembersViewHolder>() {
    private val isLoadingAdded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        var holder: MembersViewHolder? = null
        val inflater = LayoutInflater.from(mCtx)
        when (viewType) {
            ITEM -> holder = getViewHolder(parent, inflater)
            LOADING -> {
                val v2 = inflater.inflate(R.layout.item_progress, parent, false)
                holder = LoadingVH(v2)
            }
        }
        return holder!!
    }

    private fun getViewHolder(parent: ViewGroup, inflater: LayoutInflater): MembersViewHolder {
        val holder: MembersViewHolder
        val v1 = inflater.inflate(R.layout.recycler_member, null)
        holder = MembersViewHolder(v1)
        return holder
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        val members = membersList[position]
        holder.textViewUsername.text = String.format("@%s", members.username)
        holder.textViewNickname.text = members.nickname
        val profilePic = members.profile_pic.substring(0, members.profile_pic.length - 4) + "_r.JPG"
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profilePic)
                .into(holder.imageView)
        holder.clanMemberLayout.setOnClickListener {
            val ldf = FragmentProfile()
            val args = Bundle()
            args.putString("UserId", members.userid)
            ldf.arguments = args
            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
    }

    private class LoadingVH(itemView: View) : MembersViewHolder(itemView)

    override fun getItemCount(): Int {
        return membersList.size
    }

    open class MembersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: CircleImageView = itemView.findViewById(R.id.memberImageView)
        var textViewNickname: TextView = itemView.findViewById(R.id.textViewNickname)
        var textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        var clanMemberLayout: MaterialRippleLayout = itemView.findViewById(R.id.clanMemberLayout)

    }

    companion object {
        private const val ITEM = 0
        private const val LOADING = 1
    }
}