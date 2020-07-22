package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewMessageSearchAdapter extends RecyclerView.Adapter<NewMessageSearchAdapter.SearchViewHolder> implements Filterable {

    private List<User> users;
    private List<User> usersFull;
    private Context context;

    public NewMessageSearchAdapter(List<User> Search_Recycler, Context context) {
        this.users = Search_Recycler;
        usersFull = new ArrayList<>(users);
        this.context = context;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_listitem, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        final User user = users.get(position);

        holder.username.setText(String.format("@%s", user.getName()));
        holder.nickname.setText(user.getSubname());
        holder.num_posts.setText(user.getExtra());
        holder.num_ratings.setText(user.getNumratings());
        String profile_pic = user.getImage().substring(0, user.getImage().length() - 4)+"_r.JPG";
        Glide.with(context)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.profile_pic);
        if(user.getType().equals("users")){
            if(user.getVerified().equals("yes")) {
                holder.verified.setVisibility(View.VISIBLE);
            }
            if(user.getLast_Online().equals("yes")){
                holder.online.setVisibility(View.VISIBLE);
            }
        }
        if(user.getType().equals("publics_cat")) {
            holder.userListLayout.setVisibility(View.GONE);
        } else {
            holder.tvTotalPosts.setVisibility(View.VISIBLE);
        }
        holder.userListLayout.setOnClickListener(v -> {
            if (context instanceof ChatActivity) {
                if(user.getType().equals("users")) {
                    MessageFragment ldf = new MessageFragment();
                    Bundle args = new Bundle();
                    args.putString("user_to", user.getName());
                    args.putString("profile_pic", user.getImage());
                    args.putString("nickname", user.getSubname());
                    ldf.setArguments(args);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.chat_fragment_container, ldf).addToBackStack(null).commit();
                }/*

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
            }});
    }

    @Override
    public int getItemCount() {
        //     Log.d("getItemCount", String.format("getItemCount: %d", users.size()));
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(usersFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (User user : usersFull) {
                    if (user.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users.clear();
            users.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView username,nickname, num_posts, num_ratings;
        CircleImageView profile_pic, online, verified;
        RelativeLayout userListLayout, publicsTopicLayout, tvTotalPosts;

        public SearchViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.Searchusername);
            nickname = itemView.findViewById(R.id.Searchnickname);
            num_posts = itemView.findViewById(R.id.textViewNumPublicsPosts);
            num_ratings = itemView.findViewById(R.id.reviewCount);
            profile_pic = itemView.findViewById(R.id.profile_image);
            userListLayout = itemView.findViewById(R.id.userListLayout);
            publicsTopicLayout = itemView.findViewById(R.id.publicsTopicList);
            tvTotalPosts = itemView.findViewById(R.id.tvTotalPosts);
            verified = itemView.findViewById(R.id.verified);
            online = itemView.findViewById(R.id.online);
        }
    }
}
