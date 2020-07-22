package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListGroupMessageAdapter extends RecyclerView.Adapter<UserListGroupMessageAdapter.UserListHolder> {

    private List<UserListRecycler> users;
    private Context context;
    private AdapterCallback mAdapterCallback;

    public UserListGroupMessageAdapter(List<UserListRecycler> UserListRecycler, Context context, AdapterCallback callback) {
        this.users = UserListRecycler;
        this.context = context;
        this.mAdapterCallback = callback;
    }

    public interface AdapterCallback {
        void onMethodCallbackUserList(int position, String username);
    }

    @NonNull
    @Override
    public UserListGroupMessageAdapter.UserListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_list_new_group_message, parent, false);
        return new UserListGroupMessageAdapter.UserListHolder(view);
    }

    @Override
    public void onBindViewHolder(UserListGroupMessageAdapter.UserListHolder holder, int position) {
        final UserListRecycler user = users.get(position);

        holder.nickname.setText(user.getNickname());
        holder.username.setText(String.format("@%s", user.getUsername()));
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
        if (user.getOnline()!=null&&user.getOnline().equals("yes")){
            holder.online.setVisibility(View.VISIBLE);
        }else{
            holder.online.setVisibility(View.GONE);
        }
        if (user.getVerified()!=null&&user.getVerified().equals("yes")){
            holder.verified.setVisibility(View.VISIBLE);
        }else{
            holder.verified.setVisibility(View.GONE);
        }
        String profile_pic = user.getProfile_pic().substring(0, user.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(context)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.profile_pic);
        holder.removeBtn.setOnClickListener(v -> mAdapterCallback.onMethodCallbackUserList(position, user.getUsername()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public static class UserListHolder extends RecyclerView.ViewHolder{
        TextView username,nickname;
        CircleImageView profile_pic, online, verified;
        MaterialRippleLayout userListLayout;
        ImageView removeBtn;
        public UserListHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            nickname = itemView.findViewById(R.id.nickname);
            profile_pic = itemView.findViewById(R.id.profile_image);
            userListLayout = itemView.findViewById(R.id.userListLayout);
            verified = itemView.findViewById(R.id.verified);
            online = itemView.findViewById(R.id.online);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }

}
