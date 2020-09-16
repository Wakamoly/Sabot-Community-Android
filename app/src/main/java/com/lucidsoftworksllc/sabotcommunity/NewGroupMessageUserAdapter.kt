package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class NewGroupMessageUserAdapter(private val users: MutableList<User>, context: Context, callback: AdapterCallback) : RecyclerView.Adapter<NewGroupMessageUserAdapter.SearchViewHolder>(), Filterable {
    private var usersFull: MutableList<User>? = null
    private val context: Context
    private val mAdapterCallback: AdapterCallback

    interface AdapterCallback {
        fun onNewGroupMethodCallback(user_id: String?, profile_pic: String?, nickname: String?, username: String?, verified: String?, online: String?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_user_listitem_group, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = users[position]
        val userAt = "@" + user.name
        holder.username.text = userAt
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
        holder.acceptBtn.setOnClickListener {
            holder.acceptBtn.visibility = View.GONE
            mAdapterCallback.onNewGroupMethodCallback(user.id, user.image, user.subname, user.name, user.verified, user.last_Online)
        }

        /*holder.userListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof ChatActivity) {
                    */
        /*if(user.getType().equals("users")) {
                        //Put the value
                        MessageFragment ldf = new MessageFragment();
                        Bundle args = new Bundle();
                        args.putString("user_to", user.getName());
                        args.putString("profile_pic", user.getImage());
                        args.putString("nickname", user.getSubname());
                        ldf.setArguments(args);

                        //Inflate the fragment
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.chat_fragment_container, ldf).addToBackStack(null).commit();
                        //Toast.makeText(context, "You clicked " + user.getId(), Toast.LENGTH_LONG).show();
                    }*/
        /*

                }}
        });*/
    }

    override fun getItemCount(): Int {
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
        var acceptBtn: Button = itemView.findViewById(R.id.acceptBtn)
    }
    init {
        usersFull = ArrayList(users)
        this.context = context
        mAdapterCallback = callback
    }
}