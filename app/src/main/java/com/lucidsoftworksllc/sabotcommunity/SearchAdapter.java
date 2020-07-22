package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements Filterable {

    private static final String NEW_SEARCH_COUNT = Constants.ROOT_URL+"new_search_count.php";
    private List<User> users;
    private List<User> usersFull;
    private Context context;
    public SearchAdapter(List<User> Search_Recycler, Context context) {
        this.users = Search_Recycler;
        this.usersFull = new ArrayList<>(users);
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
        holder.nickname.setText(user.getSubname());
        holder.num_posts.setText(user.getExtra());
        holder.num_ratings.setText(user.getNumratings());
        if(user.getImage().contains("/profile_pics/")){
            String profile_pic = user.getImage().substring(0, user.getImage().length() - 4)+"_r.JPG";
            Glide.with(context)
                    .load(Constants.BASE_URL + profile_pic)
                    .into(holder.profile_pic);
        }else{
            Glide.with(context)
                    .load(Constants.BASE_URL + user.getImage())
                    .into(holder.profile_pic);
        }
        if(user.getType().equals("users")){
            holder.username.setText(String.format("@%s", user.getName()));
            holder.username.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
            if(user.getVerified().equals("yes")) {
                holder.verified.setVisibility(View.VISIBLE);
            }else{
                holder.verified.setImageDrawable(null);
            }
            if(user.getLast_Online().equals("yes")){
                holder.online.setVisibility(View.VISIBLE);
            }else{
                holder.online.setImageDrawable(null);
            }
        }
        if(user.getType().equals("publics_cat")) {
            holder.tvTotalPosts.setVisibility(View.GONE);
            holder.username.setText(String.format("@%s", user.getName()));
            holder.username.setTextColor(ContextCompat.getColor(context, R.color.sponsored));
        }
        if(user.getType().equals("clans")){
            holder.tvTotalPosts.setVisibility(View.GONE);
            holder.username.setText(String.format("[%s]", user.getName()));
            holder.username.setAllCaps(true);
            holder.username.setTextColor(ContextCompat.getColor(context, R.color.pin));
        }
        holder.userListLayout.setOnClickListener(v -> {
            if (context instanceof FragmentContainer) {
                if(user.getType().equals("users")) {
                    FragmentProfile ldf = new FragmentProfile();
                    Bundle args = new Bundle();
                    args.putString("UserId", user.getId());
                    ldf.setArguments(args);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }
                if(user.getType().equals("publics_cat")) {
                    FragmentPublicsCat ldf = new FragmentPublicsCat();
                    Bundle args = new Bundle();
                    args.putString("PublicsId", user.getId());
                    ldf.setArguments(args);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }
                if(user.getType().equals("clans")) {
                    ClanFragment ldf = new ClanFragment();
                    Bundle args = new Bundle();
                    args.putString("ClanId", user.getId());
                    ldf.setArguments(args);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }
                if(!user.getType().equals("users"))newSearchCount(user.getType(), user.getId());
        }});
    }

    @Override public int getItemCount() { return users.size(); }
    @Override public Filter getFilter() {
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

    public void newSearchCount(final String searchType, final String typeID){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, NEW_SEARCH_COUNT, response -> {}, error -> {}){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("type",searchType);
                parms.put("id", typeID);
                parms.put("user_id",SharedPrefManager.getInstance(context).getUserID());
                parms.put("username",SharedPrefManager.getInstance(context).getUsername());
                return parms;
            }
        };
        ((FragmentContainer)context).addToRequestQueue(stringRequest);
    }

}
