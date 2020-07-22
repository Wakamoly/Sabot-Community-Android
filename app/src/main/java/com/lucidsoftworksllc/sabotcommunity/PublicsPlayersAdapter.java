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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicsPlayersAdapter extends RecyclerView.Adapter<PublicsPlayersAdapter.MembersViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String CLAN_MEMBER_ACTION = Constants.ROOT_URL+"publics_player_action.php";
    private static final String IS_CONNECTED = Constants.ROOT_URL+"publics_member_accept_is_connected.php";
    private Context mCtx;
    private List<Publics_Players_Recycler> membersList;

    public PublicsPlayersAdapter(Context mCtx, List<Publics_Players_Recycler> membersList) {
        this.mCtx = mCtx;
        this.membersList = membersList;
    }

    @NonNull
    @Override
    public PublicsPlayersAdapter.MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PublicsPlayersAdapter.MembersViewHolder holder = null;
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
    private PublicsPlayersAdapter.MembersViewHolder getViewHolder(LayoutInflater inflater) {
        PublicsPlayersAdapter.MembersViewHolder holder;
        View v1 = inflater.inflate(R.layout.recycler_member, null);
        holder = new MembersViewHolder(v1);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PublicsPlayersAdapter.MembersViewHolder holder, int position) {
        final Publics_Players_Recycler members = membersList.get(position);
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
            StringRequest stringRequest=new StringRequest(Request.Method.POST, IS_CONNECTED, response -> {
                try {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getString("result").equals("success")){
                        if(!obj.get("isFriend").equals("yes")){
                            new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTopColorRes(R.color.green)
                                    .setButtonsColorRes(R.color.green)
                                    .setIcon(R.drawable.ic_friend_add)
                                    .setTitle(R.string.accept_connection_request_publics)
                                    .setMessage(R.string.accept_connection_request_publics_message_owner)
                                    .setPositiveButton(R.string.yes, v -> acceptMember(members.getUserid(),members.getUsername(),"accept",members.getId(),userID,username,members.getTopicID(),members.getUsername(),holder))
                                    .setNegativeButton(R.string.no, null)
                                    .show();
                        }else{
                            acceptMember(members.getUserid(),members.getUsername(),"accept",members.getId(),userID,username,members.getTopicID(),"",holder);
                        }
                    } else {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(mCtx,"Could not send request, please try again later...",Toast.LENGTH_LONG).show()){
                @Override
                protected Map<String, String> getParams()  {
                    Map<String,String> parms= new HashMap<>();
                    parms.put("playerusername",members.getUsername());
                    parms.put("thisusername",username);
                    parms.put("thisuserid",userID);
                    return parms;
                }
            };
            ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
            holder.memberAccept.setVisibility(View.GONE);
        });
        holder.memberJoined.setOnClickListener(view -> {
            holder.memberJoined.setVisibility(View.GONE);
            holder.memberActionProgress.setVisibility(View.VISIBLE);
            acceptMember(members.getUserid(),members.getUsername(),"remove",members.getId(),userID,username,members.getTopicID(),"",holder);
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

    private void acceptMember(String user_id_action,String username_action,String method,String request_id,String user_id,String username,String topic_id,String add_connection,MembersViewHolder holder){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, CLAN_MEMBER_ACTION, response -> {
            JSONObject obj;
            try {
                obj = new JSONObject(response);
                if(obj.getString("error").equals("false")) {
                    holder.resultImage.setVisibility(View.VISIBLE);
                    holder.memberActionProgress.setVisibility(View.GONE);
                }else if(obj.getString("error").equals("true")){
                    Toast.makeText(mCtx,obj.getString("message"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mCtx,"Error on accepting!",Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(mCtx,"Error, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("user_id_action",user_id_action);
                parms.put("username_action",username_action);
                parms.put("method",method);
                parms.put("request_id",request_id);
                parms.put("user_id",user_id);
                parms.put("username",username);
                parms.put("topic_id",topic_id);
                parms.put("user_to_connect",add_connection);
                return parms;
            }
        };
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    protected static class LoadingVH extends PublicsPlayersAdapter.MembersViewHolder {
        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    @Override public int getItemCount() {
        return membersList.size();
    }

    static class MembersViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        ImageView resultImage;
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
            resultImage = itemView.findViewById(R.id.resultImage);
        }
    }

}
