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
import com.lucidsoftworksllc.sabotcommunity.PublicsSubCommentAdapter.PublicsSubCommentViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class PublicsSubCommentAdapter(private val mCtx: Context, private val subCommentList: List<PublicsSubCommentsRecycler>) : RecyclerView.Adapter<PublicsSubCommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicsSubCommentViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.recycler_publics_sub_comment, null)
        return PublicsSubCommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicsSubCommentViewHolder, position: Int) {
        val subComment = subCommentList[position]
        if (subComment.online == "yes") {
            holder.online.visibility = View.VISIBLE
        } else {
            holder.online.visibility = View.GONE
        }
        if (subComment.verified == "yes") {
            holder.verified.visibility = View.VISIBLE
        } else {
            holder.verified.visibility = View.GONE
        }
        holder.clantag.text = subComment.clantag
        holder.content.text = subComment.reply
        holder.username.text = String.format("@%s", subComment.username)
        holder.nickname.text = subComment.nickname
        holder.nickname.setOnClickListener {
            val ldf = FragmentProfile()
            val args = Bundle()
            args.putString("UserId", subComment.userid)
            ldf.arguments = args
            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
        holder.commentDate.text = subComment.post_date
        val profilePic = subComment.profile_pic.substring(0, subComment.profile_pic.length - 4) + "_r.JPG"
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profilePic)
                .into(holder.profilepic)
    }

    override fun getItemCount(): Int {
        return subCommentList.size
    }

    class PublicsSubCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userListLayout: MaterialRippleLayout = itemView.findViewById(R.id.userListLayout)
        var profilepic: CircleImageView = itemView.findViewById(R.id.profile_image)
        var online: CircleImageView = itemView.findViewById(R.id.online)
        var verified: CircleImageView = itemView.findViewById(R.id.verified)
        var nickname: TextView = itemView.findViewById(R.id.nickname)
        var username: TextView = itemView.findViewById(R.id.username)
        var content: TextView = itemView.findViewById(R.id.content)
        var commentDate: TextView = itemView.findViewById(R.id.comment_date)
        var clantag: TextView = itemView.findViewById(R.id.clantag)

    }
}