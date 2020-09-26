package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.User
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.fragments.MessageFragment
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class NewMessageSearchAdapter(private val users: MutableList<User>, context: Context) : RecyclerView.Adapter<NewMessageSearchAdapter.SearchViewHolder>(), Filterable {
    private var usersFull: MutableList<User>? = null
    private val context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_user_listitem, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = users[position]
        holder.username.text = String.format("@%s", user.name)
        holder.nickname.text = user.subname
        holder.numPosts.text = user.extra
        holder.numRatings.text = user.numratings
        val profilePic = user.image!!.substring(0, user.image.length - 4) + "_r.JPG"
        Glide.with(context)
                .load(Constants.BASE_URL + profilePic)
                .into(holder.profilePic)
        if (user.type == "users") {
            if (user.verified == "yes") {
                holder.verified.visibility = View.VISIBLE
            }
            if (user.last_Online == "yes") {
                holder.online.visibility = View.VISIBLE
            }
        }
        if (user.type == "publics_cat") {
            holder.userListLayout.visibility = View.GONE
        } else {
            holder.tvTotalPosts.visibility = View.VISIBLE
        }
        holder.userListLayout.setOnClickListener {
            if (context is ChatActivity) {
                if (user.type == "users") {
                    val ldf = MessageFragment()
                    val args = Bundle()
                    args.putString("user_to", user.name)
                    args.putString("profile_pic", user.image)
                    args.putString("nickname", user.subname)
                    ldf.arguments = args
                    (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.chat_fragment_container, ldf).addToBackStack(null).commit()
                } /*

                if(user.getType().equals("publics_cat")) {
                    //Put the value
                    FragmentPublicsCat ldf = new FragmentPublicsCat();
                    Bundle args = new Bundle();
                    args.putString("PublicsId", user.getId());
                    ldf.setArguments(args);

                    //Inflate the fragment
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.chat_fragment_container, ldf).addToBackStack(null).commit();
                    //Toast.makeText(context, "You clicked " + user.getId(), Toast.LENGTH_LONG).show();
                }*/
            }
        }
    }

    override fun getItemCount(): Int {
        //     Log.d("getItemCount", String.format("getItemCount: %d", users.size()));
        return users.size
    }

    override fun getFilter(): Filter {
        return userFilter
    }

    private val userFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<User> = ArrayList()
            if (constraint.isEmpty()) {
                filteredList.addAll(usersFull!!)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }
                for (user in usersFull!!) {
                    if (user.name!!.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(user)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            users.clear()
            users.addAll(results.values as Collection<User>)
            notifyDataSetChanged()
        }
    }

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.Searchusername)
        var nickname: TextView = itemView.findViewById(R.id.Searchnickname)
        var numPosts: TextView = itemView.findViewById(R.id.textViewNumPublicsPosts)
        var numRatings: TextView = itemView.findViewById(R.id.reviewCount)
        var profilePic: CircleImageView = itemView.findViewById(R.id.profile_image)
        var online: CircleImageView = itemView.findViewById(R.id.online)
        var verified: CircleImageView = itemView.findViewById(R.id.verified)
        var userListLayout: RelativeLayout = itemView.findViewById(R.id.userListLayout)
        var publicsTopicLayout: RelativeLayout = itemView.findViewById(R.id.publicsTopicList)
        var tvTotalPosts: RelativeLayout = itemView.findViewById(R.id.tvTotalPosts)
    }

    init {
        usersFull = ArrayList(users)
        this.context = context
    }
}