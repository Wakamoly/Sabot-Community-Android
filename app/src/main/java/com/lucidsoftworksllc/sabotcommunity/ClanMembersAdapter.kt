package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClanMembersAdapter extends RecyclerView.Adapter<ClanMembersAdapter.MembersViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String CLAN_MEMBER_ACTION = Constants.ROOT_URL+"clan_member_action.php";
    private Context mCtx;
    private List<Clan_Members_Recycler> membersList;
    private boolean isLoadingAdded = false;

    public ClanMembersAdapter(Context mCtx, List<Clan_Members_Recycler> membersList) {
        this.mCtx = mCtx;
        this.membersList = membersList;
    }

    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MembersViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        switch (viewType) {
            case ITEM:
                holder = getViewHolder(inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                holder = new LoadingVH(v2);
                break;
        }
        assert holder != null;
        return holder;
    }

    @NonNull
    private MembersViewHolder getViewHolder(LayoutInflater inflater) {
        MembersViewHolder holder;
        View v1 = inflater.inflate(R.layout.recycler_member, null);
        holder = new MembersViewHolder(v1);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MembersViewHolder holder, int position) {
        final Clan_Members_Recycler members = membersList.get(position);
        final String userID = SharedPrefManager.getInstance(mCtx).getUserID();
        final String username = SharedPrefManager.getInstance(mCtx).getUsername();
        if (members.getUserPosition().equals("request")){
            holder.memberAccept.setVisibility(View.VISIBLE);
        }else if(members.getUserPosition().equals("member")){
            holder.memberJoined.setVisibility(View.VISIBLE);
        }
        holder.memberAccept.setOnClickListener(view -> {
            holder.memberAccept.setVisibility(View.GONE);
            holder.memberActionProgress.setVisibility(View.VISIBLE);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, CLAN_MEMBER_ACTION, response -> {
                holder.memberJoined.setVisibility(View.VISIBLE);
                holder.memberActionProgress.setVisibility(View.GONE);
            }, error -> Toast.makeText(mCtx,"Could not follow, please try again later...",Toast.LENGTH_LONG).show()){
                @Override
                protected Map<String, String> getParams()  {
                    Map<String,String> parms= new HashMap<>();
                    parms.put("user_id_action",members.getUserid());
                    parms.put("username_action",members.getUsername());
                    parms.put("method","accept");
                    parms.put("request_id",members.getId());
                    parms.put("user_id",userID);
                    parms.put("username",username);
                    parms.put("clan_id",members.getClanid());
                    parms.put("clan_tag",members.getClantag());
                    return parms;
                }
            };
            ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
        });

        holder.memberJoined.setOnClickListener(view -> {
            holder.memberJoined.setVisibility(View.GONE);
            holder.memberActionProgress.setVisibility(View.VISIBLE);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, CLAN_MEMBER_ACTION, response -> holder.clanMemberLayout.setVisibility(View.GONE), error -> Toast.makeText(mCtx,"Could not follow, please try again later...",Toast.LENGTH_LONG).show()){
                @Override
                protected Map<String, String> getParams()  {
                    Map<String,String> parms= new HashMap<>();
                    parms.put("user_id_action",members.getUserid());
                    parms.put("username_action",members.getUsername());
                    parms.put("method","remove");
                    parms.put("request_id",members.getId());
                    parms.put("user_id",userID);
                    parms.put("username",username);
                    parms.put("clan_id",members.getClanid());
                    parms.put("clan_tag",members.getClantag());
                    return parms;
                }
            };
            ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
        });

        holder.textViewUsername.setText(String.format("@%s", members.getUsername()));
        holder.textViewNickname.setText(members.getNickname());
        String profile_pic = members.getProfile_pic().substring(0, members.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.imageView);
        holder.clanMemberLayout.setOnClickListener(v -> {
            FragmentProfile ldf = new FragmentProfile();
            Bundle args = new Bundle();
            args.putString("UserId", members.getUserid());
            ldf.setArguments(args);
            ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
            });
    }


    protected class LoadingVH extends MembersViewHolder {
        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
    @Override public int getItemCount() {
        return membersList.size();
    }
    static class MembersViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView textViewNickname,textViewUsername;
        ProgressBar memberActionProgress;
        Button memberJoined,memberAccept;
        MaterialRippleLayout clanMemberLayout;
        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.memberImageView);
            textViewNickname = itemView.findViewById(R.id.textViewNickname);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            memberActionProgress = itemView.findViewById(R.id.memberActionProgress);
            memberJoined = itemView.findViewById(R.id.memberJoined);
            memberAccept = itemView.findViewById(R.id.memberAccept);
            clanMemberLayout = itemView.findViewById(R.id.clanMemberLayout);
        }
    }

}
