package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class SearchAdapter(private val users: MutableList<User>, context: Context) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>(), Filterable {
    private var usersFull: Collection<User>? = null
    private val context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_user_listitem, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = users[position]
        holder.nickname.text = user.subname
        holder.numPosts.text = user.extra
        holder.numRatings.text = user.numratings
        if (user.image!!.contains("/profile_pics/")) {
            val profilePic = user.image.substring(0, user.image.length - 4) + "_r.JPG"
            Glide.with(context)
                    .load(Constants.BASE_URL + profilePic)
                    .into(holder.profilePic)
        } else {
            Glide.with(context)
                    .load(Constants.BASE_URL + user.image)
                    .into(holder.profilePic)
        }
        if (user.type == "users") {
            holder.username.text = String.format("@%s", user.name)
            holder.username.setTextColor(ContextCompat.getColor(context, R.color.light_blue))
            if (user.verified == "yes") {
                holder.verified.visibility = View.VISIBLE
            } else {
                holder.verified.setImageDrawable(null)
            }
            if (user.last_Online == "yes") {
                holder.online.visibility = View.VISIBLE
            } else {
                holder.online.setImageDrawable(null)
            }
        }
        if (user.type == "publics_cat") {
            holder.tvTotalPosts.visibility = View.GONE
            holder.username.text = String.format("@%s", user.name)
            holder.username.setTextColor(ContextCompat.getColor(context, R.color.sponsored))
        }
        if (user.type == "clans") {
            holder.tvTotalPosts.visibility = View.GONE
            holder.username.text = String.format("[%s]", user.name)
            holder.username.isAllCaps = true
            holder.username.setTextColor(ContextCompat.getColor(context, R.color.pin))
        }
        holder.userListLayout.setOnClickListener {
            if (context is FragmentContainer) {
                if (user.type == "users") {
                    val ldf = FragmentProfile()
                    val args = Bundle()
                    args.putString("UserId", user.id)
                    ldf.arguments = args
                    (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                if (user.type == "publics_cat") {
                    val ldf = FragmentPublicsCat()
                    val args = Bundle()
                    args.putString("PublicsId", user.id)
                    ldf.arguments = args
                    (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                if (user.type == "clans") {
                    val ldf = ClanFragment()
                    val args = Bundle()
                    args.putString("ClanId", user.id)
                    ldf.arguments = args
                    (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                if (user.type != "users") newSearchCount(user.type!!, user.id!!)
            }
        }
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
//        var publicsTopicLayout: RelativeLayout = itemView.findViewById(R.id.publicsTopicList)
        var tvTotalPosts: RelativeLayout = itemView.findViewById(R.id.tvTotalPosts)

    }

    private fun newSearchCount(searchType: String, typeID: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, NEW_SEARCH_COUNT, Response.Listener { }, Response.ErrorListener { }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["type"] = searchType
                params["id"] = typeID
                params["user_id"] = SharedPrefManager.getInstance(context)!!.userID!!
                params["username"] = SharedPrefManager.getInstance(context)!!.username!!
                return params
            }
        }
        (context as FragmentContainer).addToRequestQueue(stringRequest)
    }

    companion object {
        private const val NEW_SEARCH_COUNT = Constants.ROOT_URL + "new_search_count.php"
    }

    init {
        usersFull = ArrayList(users)
        this.context = context
    }
}