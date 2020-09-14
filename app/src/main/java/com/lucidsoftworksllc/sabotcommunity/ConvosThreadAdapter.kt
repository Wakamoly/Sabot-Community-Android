package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConvosThreadAdapter extends RecyclerView.Adapter<ConvosThreadAdapter.ViewHolder> {

    private Context mCtx;
    private List<ConvosHelper> convosList;
    private static final String SET_READ = Constants.ROOT_URL+"set_message_read.php";
    private String deviceUsername;

    public ConvosThreadAdapter(Context mCtx, List<ConvosHelper> convosList) {
        this.mCtx = mCtx;
        this.convosList = convosList;
    }

    @NonNull
    @Override
    public ConvosThreadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recycler_userslist_messages, null);
        ConvosThreadAdapter.ViewHolder holder = new ViewHolder(view);
        deviceUsername = SharedPrefManager.getInstance(mCtx).getUsername();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConvosThreadAdapter.ViewHolder holder, int position) {
        final ConvosHelper convos = convosList.get(position);
        if (convos.getType().equals("user")){
            holder.convoUsername.setText(String.format("@%s", convos.getSent_by()));
            holder.convoUsername.setTextColor(mCtx.getResources().getColor(android.R.color.secondary_text_dark));
            holder.convoBodyPreview.setText(convos.getBody_split());
            if(convos.getBody_split().contains("Them: ")){
                holder.convoBodyPreview.setTypeface(null, Typeface.BOLD);
            }else{
                holder.convoBodyPreview.setTypeface(null,Typeface.NORMAL);
            }
            holder.convoTimeMessage.setText(convos.getTime_message());
            holder.convoNickname.setText(convos.getNickname());
            if(convos.getViewed().equals("yes")|| convos.getUser_from().equals(deviceUsername)){
                holder.userLayout.setBackgroundColor(Color.parseColor("#111111"));
            } else{
                holder.userLayout.setBackgroundColor(Color.parseColor("#222222"));
            }
            holder.userLayout.setOnClickListener(v -> {
                MessageFragment ldf = new MessageFragment();
                Bundle args = new Bundle();
                args.putString("user_to", convos.getSent_by());
                ldf.setArguments(args);
                ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
                if (!convos.getViewed().equals("yes") && !convos.getSent_by().equals(deviceUsername)){
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SET_READ, response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("error")) {
                                Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Toast.makeText(mCtx, "Could not set message as read, please try again later...", Toast.LENGTH_LONG).show()) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> parms = new HashMap<>();
                            parms.put("username", deviceUsername);
                            parms.put("id", convos.getId());
                            return parms;
                        }
                    };
                    ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
                }
            });
            if (convos.getLast_online().equals("yes")){
                holder.last_online.setVisibility(View.VISIBLE);
            } else {
                holder.last_online.setVisibility(View.GONE);
            }
            if (convos.getVerified().equals("yes")){
                holder.verified.setVisibility(View.VISIBLE);
            }else{
                holder.verified.setVisibility(View.GONE);
            }
            String profile_pic = convos.getProfile_pic().substring(0, convos.getProfile_pic().length() - 4)+"_r.JPG";
            Glide.with(mCtx)
                    .load(Constants.BASE_URL+profile_pic)
                    .into(holder.userImageView);
            String profile_pic2 = convos.getLatest_profile_pic().substring(0, convos.getLatest_profile_pic().length() - 4)+"_r.JPG";
            Glide.with(mCtx)
                    .load(Constants.BASE_URL+profile_pic2)
                    .into(holder.lastRepliedProfilePic);
        }else if (convos.getType().equals("group")){
            String usernameText = convos.getSent_by()+" users in conversation";
            holder.convoUsername.setText(usernameText);
            holder.convoUsername.setTextColor(mCtx.getResources().getColor(R.color.light_blue));
            holder.convoBodyPreview.setText(convos.getBody_split());
            holder.convoTimeMessage.setText(convos.getTime_message());
            holder.convoNickname.setText(convos.getNickname());
            if(convos.getViewed().equals("yes")|| convos.getUser_from().equals(deviceUsername)){
                holder.userLayout.setBackgroundColor(Color.parseColor("#111111"));
            } else{
                holder.userLayout.setBackgroundColor(Color.parseColor("#222222"));
            }
            holder.userLayout.setOnClickListener(v -> {
                MessageGroupFragment ldf = new MessageGroupFragment();
                Bundle args = new Bundle();
                args.putString("group_id", convos.getGroup_id());
                ldf.setArguments(args);
                ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
                //Toast.makeText(context, "You clicked " + user.getId(), Toast.LENGTH_LONG).show();
                /*if (!convos.getViewed().equals("yes") && !convos.getSent_by().equals(deviceUsername)){
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SET_READ, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (obj.getBoolean("error")) {
                                    Toast.makeText(
                                            mCtx,
                                            obj.getString("message"),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mCtx, "Could not set message as read, please try again later...", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> parms = new HashMap<String, String>();
                            parms.put("username", deviceUsername);
                            parms.put("id", convos.getId());
                            return parms;
                        }
                    };
    ((ChatActivity)mContext).addToRequestQueue(stringRequest);
                }*/
            });

            if (convos.getLast_online().equals("yes")){
                holder.last_online.setVisibility(View.VISIBLE);
            } else {
                holder.last_online.setVisibility(View.GONE);
            }
            if (convos.getVerified().equals("yes")){
                holder.verified.setVisibility(View.VISIBLE);
            }else{
                holder.verified.setVisibility(View.GONE);
            }
            Glide.with(mCtx)
                    .load(Constants.BASE_URL+convos.getProfile_pic())
                    .into(holder.userImageView);
            Glide.with(mCtx)
                    .load(Constants.BASE_URL+convos.getLatest_profile_pic())
                    .into(holder.lastRepliedProfilePic);
        }

    }

    @Override
    public int getItemCount() {
        return convosList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialRippleLayout userLayout;
        CircleImageView userImageView,verified,last_online,lastRepliedProfilePic;
        TextView convoUsername,convoBodyPreview,convoTimeMessage, convoNickname;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userLayout = itemView.findViewById(R.id.userLayout);
            userImageView = itemView.findViewById(R.id.userImageView);
            lastRepliedProfilePic = itemView.findViewById(R.id.lastRepliedProfilePic);
            verified = itemView.findViewById(R.id.verified);
            last_online = itemView.findViewById(R.id.online);
            convoUsername = itemView.findViewById(R.id.convoUsername);
            convoNickname = itemView.findViewById(R.id.convoNickname);
            convoBodyPreview = itemView.findViewById(R.id.convoBodyPreview);
            convoTimeMessage = itemView.findViewById(R.id.convoTimeMessage);
        }
    }

}
