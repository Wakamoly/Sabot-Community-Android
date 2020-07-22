package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListGroupMessageCurrentAdapter extends RecyclerView.Adapter<UserListGroupMessageCurrentAdapter.UserListHolder> {

    private List<UserListRecycler> users;
    private Context context;

    public UserListGroupMessageCurrentAdapter(List<UserListRecycler> UserListRecycler, Context context) {
        this.users = UserListRecycler;
        this.context = context;
    }

    @NonNull
    @Override
    public UserListGroupMessageCurrentAdapter.UserListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_list, parent, false);
        return new UserListGroupMessageCurrentAdapter.UserListHolder(view);
    }

    @Override
    public void onBindViewHolder(UserListGroupMessageCurrentAdapter.UserListHolder holder, int position) {
        final UserListRecycler user = users.get(position);
        holder.nickname.setText(user.getNickname());
        holder.username.setText(String.format("@%s", user.getUsername()));
        holder.user_desc.setText(user.getDesc());
        holder.userListLayout.setOnClickListener(v -> {
            FragmentProfile ldf = new FragmentProfile();
            Bundle args = new Bundle();
            args.putString("UserId", user.getUser_id());
            ldf.setArguments(args);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
        });
        if (user.getOnline().equals("yes")){
            holder.online.setVisibility(View.VISIBLE);
        }else{
            holder.online.setVisibility(View.GONE);
        }
        if (user.getVerified().equals("yes")){
            holder.verified.setVisibility(View.VISIBLE);
        }else{
            holder.verified.setVisibility(View.GONE);
        }
        String profile_pic = user.getProfile_pic().substring(0, user.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(context)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.profile_pic);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public static class UserListHolder extends RecyclerView.ViewHolder{
        TextView username,nickname,user_desc;
        CircleImageView profile_pic, online, verified;
        MaterialRippleLayout userListLayout;
        public UserListHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            nickname = itemView.findViewById(R.id.nickname);
            profile_pic = itemView.findViewById(R.id.profile_image);
            userListLayout = itemView.findViewById(R.id.userListLayout);
            verified = itemView.findViewById(R.id.verified);
            online = itemView.findViewById(R.id.online);
            user_desc = itemView.findViewById(R.id.user_desc);
        }
    }

}
