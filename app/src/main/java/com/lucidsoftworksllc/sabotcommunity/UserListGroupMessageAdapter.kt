package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class UserListGroupMessageAdapter(private val users: List<UserListRecycler>, private val context: Context, private val mAdapterCallback: AdapterCallback) : RecyclerView.Adapter<UserListGroupMessageAdapter.UserListHolder>() {
    interface AdapterCallback {
        fun onMethodCallbackUserList(position: Int, username: String?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_user_list_new_group_message, parent, false)
        return UserListHolder(view)
    }

    override fun onBindViewHolder(holder: UserListHolder, position: Int) {
        val user = users[position]
        holder.nickname.text = user.nickname
        holder.username.text = String.format("@%s", user.username)
        /*holder.userListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Put the value
                FragmentProfile ldf = new FragmentProfile();
                Bundle args = new Bundle();
                args.putString("UserId", user.getUser_id());
                ldf.setArguments(args);
                //Inflate the fragment
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                //Toast.makeText(context, "You clicked " + user.getId(), Toast.LENGTH_LONG).show();
            }
        });*/
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
        holder.removeBtn.setOnClickListener { mAdapterCallback.onMethodCallbackUserList(position, user.username) }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UserListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.username)
        var nickname: TextView = itemView.findViewById(R.id.nickname)
        var profilePic: CircleImageView = itemView.findViewById(R.id.profile_image)
        var online: CircleImageView = itemView.findViewById(R.id.online)
        var verified: CircleImageView = itemView.findViewById(R.id.verified)
        var userListLayout: MaterialRippleLayout = itemView.findViewById(R.id.userListLayout)
        var removeBtn: ImageView = itemView.findViewById(R.id.removeBtn)

    }
}