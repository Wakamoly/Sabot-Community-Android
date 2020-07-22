package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageRequestsAdapter extends RecyclerView.Adapter<MessageRequestsAdapter.ViewHolder> {

    private Context mCtx;
    private List<MessageRequestsHelper> convosList;
    private static final String MESSAGE_SET_REQUEST = Constants.ROOT_URL+"messages.php/request_action";
    private String deviceUsername;
    private String deviceUserID;
    private AdapterCallback mAdapterCallback;

    public MessageRequestsAdapter(Context mCtx, List<MessageRequestsHelper> convosList, AdapterCallback callback) {
        this.mCtx = mCtx;
        this.convosList = convosList;
        this.mAdapterCallback = callback;
    }

    public interface AdapterCallback {
        void onMethodCallback(int position);
    }

    @NonNull
    @Override
    public MessageRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recycler_message_requests, null);
        MessageRequestsAdapter.ViewHolder holder = new ViewHolder(view);
        deviceUsername = SharedPrefManager.getInstance(mCtx).getUsername();
        deviceUserID = SharedPrefManager.getInstance(mCtx).getUserID();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRequestsAdapter.ViewHolder holder, int position) {
        final MessageRequestsHelper convos = convosList.get(position);
        if (convos.getType().equals("user")){
            holder.convoUsername.setText(String.format("@%s", convos.getSent_by()));
            holder.convoBodyPreview.setText(convos.getBody_split());
            holder.convoTimeMessage.setText(convos.getTime_message());
            holder.convoNickname.setText(convos.getNickname());
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
            String profile_pic = convos.getProfile_pic().substring(0, convos.getProfile_pic().length() - 4)+"_r.JPG";
            Glide.with(mCtx)
                    .load(Constants.BASE_URL+profile_pic)
                    .into(holder.userImageView);
            String profile_pic2 = convos.getLatest_profile_pic().substring(0, convos.getLatest_profile_pic().length() - 4)+"_r.JPG";
            Glide.with(mCtx)
                    .load(Constants.BASE_URL+profile_pic2)
                    .into(holder.lastRepliedProfilePic);
            holder.deny.setOnClickListener(v -> new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.white)
                    .setIcon(R.drawable.ic_error)
                    .setTitle(R.string.remove_group_request)
                    .setMessage("Remove group request?")
                    .setPositiveButton(android.R.string.ok, v1 -> {
                        holder.confirm.setVisibility(View.GONE);
                        holder.deny.setVisibility(View.GONE);
                        holder.actionProgress.setVisibility(View.VISIBLE);
                        requestAction("remove", convos.getId(), position,convos.getGroup_id());
                    })
                    .setNegativeButton(android.R.string.no, v12 -> {})
                    .show());
            holder.confirm.setOnClickListener(v -> new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.white)
                    .setIcon(R.drawable.ic_check)
                    .setTitle(R.string.accept_group_request)
                    .setMessage("Accept group request?")
                    .setPositiveButton(android.R.string.ok, v13 -> {
                        holder.confirm.setVisibility(View.GONE);
                        holder.deny.setVisibility(View.GONE);
                        holder.actionProgress.setVisibility(View.VISIBLE);
                        requestAction("accept",convos.getId(),position,convos.getGroup_id());
                    })
                    .setNegativeButton(android.R.string.no, v14 -> {})
                    .show());
        }
    }

    private void requestAction(String method, String request_id, int position, String group_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MESSAGE_SET_REQUEST, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.getString("error").equals("false")) {
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();
                    mAdapterCallback.onMethodCallback(position);
                }else{
                    mAdapterCallback.onMethodCallback(position);
                    Toast.makeText(mCtx, "Could not accept/deny request, you may have already performed this action!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mCtx, "Could not accept/deny request, please try again later...", Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<>();
                parms.put("request_id", request_id);
                parms.put("method", method);
                parms.put("username", deviceUsername);
                parms.put("userid", deviceUserID);
                parms.put("group_id", group_id);
                return parms;
            }
        };
        ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
    }

    @Override
    public int getItemCount() {
        return convosList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImageView,lastRepliedProfilePic;
        TextView convoUsername,convoBodyPreview,convoTimeMessage, convoNickname;
        ImageButton confirm,deny;
        ProgressBar actionProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.userImageView);
            lastRepliedProfilePic = itemView.findViewById(R.id.lastRepliedProfilePic);
            convoUsername = itemView.findViewById(R.id.convoUsername);
            convoNickname = itemView.findViewById(R.id.convoNickname);
            convoBodyPreview = itemView.findViewById(R.id.convoBodyPreview);
            convoTimeMessage = itemView.findViewById(R.id.convoTimeMessage);
            confirm = itemView.findViewById(R.id.confirm);
            deny = itemView.findViewById(R.id.deny);
            actionProgress = itemView.findViewById(R.id.actionProgress);
        }
    }

}
