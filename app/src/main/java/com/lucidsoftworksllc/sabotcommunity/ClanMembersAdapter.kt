package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

open class ClanMembersAdapter(private val mCtx: Context, private val membersList: List<ClanMembersRecycler>) : RecyclerView.Adapter<ClanMembersAdapter.MembersViewHolder>() {
    private val isLoadingAdded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        var holder: MembersViewHolder? = null
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

    private fun getViewHolder(inflater: LayoutInflater): MembersViewHolder {
        val holder: MembersViewHolder
        val v1 = inflater.inflate(R.layout.recycler_member, null)
        holder = MembersViewHolder(v1)
        return holder
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        val members = membersList[position]
        val userID = SharedPrefManager.getInstance(mCtx)!!.userID
        val username = SharedPrefManager.getInstance(mCtx)!!.username
        if (members.userPosition == "request") {
            holder.memberAccept.visibility = View.VISIBLE
        } else if (members.userPosition == "member") {
            holder.memberJoined.visibility = View.VISIBLE
        }
        holder.memberAccept.setOnClickListener { view: View? ->
            holder.memberAccept.visibility = View.GONE
            holder.memberActionProgress.visibility = View.VISIBLE
            val stringRequest: StringRequest = object : StringRequest(Method.POST, CLAN_MEMBER_ACTION, Response.Listener {
                holder.memberJoined.visibility = View.VISIBLE
                holder.memberActionProgress.visibility = View.GONE
            }, Response.ErrorListener { Toast.makeText(mCtx, "Could not follow, please try again later...", Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["user_id_action"] = members.userid
                    params["username_action"] = members.username
                    params["method"] = "accept"
                    params["request_id"] = members.id
                    params["user_id"] = userID!!
                    params["username"] = username!!
                    params["clan_id"] = members.clanid
                    params["clan_tag"] = members.clantag
                    return params
                }
            }
            (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
        }
        holder.memberJoined.setOnClickListener {
            holder.memberJoined.visibility = View.GONE
            holder.memberActionProgress.visibility = View.VISIBLE
            val stringRequest: StringRequest = object : StringRequest(Method.POST, CLAN_MEMBER_ACTION, Response.Listener { holder.clanMemberLayout.visibility = View.GONE }, Response.ErrorListener { error: VolleyError? -> Toast.makeText(mCtx, "Could not follow, please try again later...", Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["user_id_action"] = members.userid
                    params["username_action"] = members.username
                    params["method"] = "remove"
                    params["request_id"] = members.id
                    params["user_id"] = userID!!
                    params["username"] = username!!
                    params["clan_id"] = members.clanid
                    params["clan_tag"] = members.clantag
                    return params
                }
            }
            (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
        }
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
            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
    }

    protected inner class LoadingVH(itemView: View) : MembersViewHolder(itemView)

    override fun getItemCount(): Int {
        return membersList.size
    }

    open class MembersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: CircleImageView = itemView.findViewById(R.id.memberImageView)
        var textViewNickname: TextView = itemView.findViewById(R.id.textViewNickname)
        var textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        var memberActionProgress: ProgressBar = itemView.findViewById(R.id.memberActionProgress)
        var memberJoined: Button = itemView.findViewById(R.id.memberJoined)
        var memberAccept: Button = itemView.findViewById(R.id.memberAccept)
        var clanMemberLayout: MaterialRippleLayout = itemView.findViewById(R.id.clanMemberLayout)

    }

    companion object {
        private const val ITEM = 0
        private const val LOADING = 1
        private const val CLAN_MEMBER_ACTION = Constants.ROOT_URL + "clan_member_action.php"
    }
}