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

class UserListAdapter(private val users: List<UserListRecycler>, private val context: Context) : RecyclerView.Adapter<UserListAdapter.UserListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_user_list, parent, false)
        return UserListHolder(view)
    }

    override fun onBindViewHolder(holder: UserListHolder, position: Int) {
        val user = users[position]
        holder.nickname.text = user.nickname
        holder.username.text = String.format("@%s", user.username)
        holder.userDesc.text = user.desc
        holder.userListLayout.setOnClickListener {
            val ldf = FragmentProfile()
            val args = Bundle()
            args.putString("UserId", user.user_id)
            ldf.arguments = args
            (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
        if (user.online == "yes") {
            holder.online.visibility = View.VISIBLE
        } else {
            holder.online.visibility = View.GONE
        }
        if (user.verified == "yes") {
            holder.verified.visibility = View.VISIBLE
        } else {
            holder.verified.visibility = View.GONE
        }
        val profilePic = user.profile_pic.substring(0, user.profile_pic.length - 4) + "_r.JPG"
        Glide.with(context)
                .load(Constants.BASE_URL + profilePic)
                .into(holder.profilePic)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UserListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.username)
        var nickname: TextView = itemView.findViewById(R.id.nickname)
        var userDesc: TextView = itemView.findViewById(R.id.user_desc)
        var profilePic: CircleImageView = itemView.findViewById(R.id.profile_image)
        var online: CircleImageView = itemView.findViewById(R.id.online)
        var verified: CircleImageView = itemView.findViewById(R.id.verified)
        var userListLayout: MaterialRippleLayout = itemView.findViewById(R.id.userListLayout)

    }
}