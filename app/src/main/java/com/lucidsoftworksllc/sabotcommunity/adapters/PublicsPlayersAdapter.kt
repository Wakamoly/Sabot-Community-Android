package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.Constants
import com.lucidsoftworksllc.sabotcommunity.models.PublicsPlayersRecycler
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.fragments.FragmentProfile
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PublicsPlayersAdapter(private val mCtx: Context, private val membersList: List<PublicsPlayersRecycler>) : RecyclerView.Adapter<PublicsPlayersAdapter.MembersViewHolder>() {
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
        holder.memberAccept.setOnClickListener {
            holder.memberAccept.visibility = View.GONE
            holder.memberActionProgress.visibility = View.VISIBLE
            val stringRequest: StringRequest = object : StringRequest(Method.POST, IS_CONNECTED, Response.Listener { response: String? ->
                try {
                    val obj = JSONObject(response!!)
                    if (obj.getString("result") == "success") {
                        if (obj["isFriend"] != "yes") {
                            LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTopColorRes(R.color.green)
                                    .setButtonsColorRes(R.color.green)
                                    .setIcon(R.drawable.ic_friend_add)
                                    .setTitle(R.string.accept_connection_request_publics)
                                    .setMessage(R.string.accept_connection_request_publics_message_owner)
                                    .setPositiveButton(R.string.yes) { acceptMember(members.userid, members.username, "accept", members.id, userID!!, username!!, members.topicID, members.username, holder) }
                                    .setNegativeButton(R.string.no, null)
                                    .show()
                        } else {
                            acceptMember(members.userid, members.username, "accept", members.id, userID!!, username!!, members.topicID, "", holder)
                        }
                    } else {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { Toast.makeText(mCtx, "Could not send request, please try again later...", Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["playerusername"] = members.username
                    params["thisusername"] = username!!
                    params["thisuserid"] = userID!!
                    return params
                }
            }
            (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
            holder.memberAccept.visibility = View.GONE
        }
        holder.memberJoined.setOnClickListener {
            holder.memberJoined.visibility = View.GONE
            holder.memberActionProgress.visibility = View.VISIBLE
            acceptMember(members.userid, members.username, "remove", members.id, userID!!, username!!, members.topicID, "", holder)
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
            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
    }

    private fun acceptMember(user_id_action: String, username_action: String, method: String, request_id: String, user_id: String, username: String, topic_id: String, add_connection: String, holder: MembersViewHolder) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, CLAN_MEMBER_ACTION, Response.Listener { response: String? ->
            val obj: JSONObject
            try {
                obj = JSONObject(response!!)
                if (obj.getString("error") == "false") {
                    holder.resultImage.visibility = View.VISIBLE
                    holder.memberActionProgress.visibility = View.GONE
                } else if (obj.getString("error") == "true") {
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(mCtx, "Error on accepting!", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { Toast.makeText(mCtx, "Error, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user_id_action"] = user_id_action
                params["username_action"] = username_action
                params["method"] = method
                params["request_id"] = request_id
                params["user_id"] = user_id
                params["username"] = username
                params["topic_id"] = topic_id
                params["user_to_connect"] = add_connection
                return params
            }
        }
        (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
    }

    private class LoadingVH(itemView: View) : MembersViewHolder(itemView)

    override fun getItemCount(): Int {
        return membersList.size
    }

    open class MembersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: CircleImageView = itemView.findViewById(R.id.memberImageView)
        var resultImage: ImageView = itemView.findViewById(R.id.resultImage)
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
        private const val CLAN_MEMBER_ACTION = Constants.ROOT_URL + "publics_player_action.php"
        private const val IS_CONNECTED = Constants.ROOT_URL + "publics_member_accept_is_connected.php"
    }
}