package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageUserListAdapter extends RecyclerView.Adapter<MessageUserListAdapter.UserListHolder> {

    private List<MessageUserListRecycler> users;
    private Context context;
    private String deviceusername,deviceuserid;
    private static final String MANAGE_USER = Constants.ROOT_URL+"messages.php/manage_user";
    private static final String REMOVE_USER = Constants.ROOT_URL+"messages.php/remove_user";
    private AdapterCallback mAdapterCallback;

    public MessageUserListAdapter(List<MessageUserListRecycler> UserListRecycler, Context context, AdapterCallback callback) {
        this.users = UserListRecycler;
        this.context = context;
        this.mAdapterCallback = callback;
    }

    public interface AdapterCallback {
        void onMethodCallback();
    }

    @NonNull
    @Override
    public UserListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_message_user_list, parent, false);
        deviceusername = SharedPrefManager.getInstance(context).getUsername();
        deviceuserid = SharedPrefManager.getInstance(context).getUserID();
        return new UserListHolder(view);
    }

    @Override
    public void onBindViewHolder(UserListHolder holder, int position) {
        final MessageUserListRecycler user = users.get(position);

        holder.nickname.setText(user.getNickname());
        holder.username.setText(String.format("@%s", user.getUsername()));
        holder.user_desc.setText(user.getDesc());
        holder.userListLayout.setOnClickListener(v -> context.startActivity(new Intent(context, FragmentContainer.class).putExtra("user_to_id", user.getUser_id())));
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
        if (user.getOwner().equals(deviceusername)){
            holder.remove_user_btn.setVisibility(View.VISIBLE);
            holder.manage_user.setVisibility(View.VISIBLE);
            holder.manage_user.setOnClickListener(v -> manageUser(user.getUsername(),user.getPosition(),user.getGroupID()));
            holder.remove_user_btn.setOnClickListener(v -> removeUser(user.getUsername(),user.getPosition(),user.getGroupID()));
        }else{
            holder.manage_user.setVisibility(View.GONE);
            if (user.getCanRemove().equals("yes")){
                if (!user.getPosition().equals("owner")&&!user.getPosition().equals("admin")){
                    holder.remove_user_btn.setVisibility(View.VISIBLE);
                    holder.remove_user_btn.setOnClickListener(v -> removeUser(user.getUsername(),user.getPosition(),user.getGroupID()));
                }else {
                    holder.remove_user_btn.setVisibility(View.GONE);
                }
            }else{
                holder.remove_user_btn.setVisibility(View.GONE);
            }
        }
        if (user.getPosition().equals("owner")){
            holder.remove_user_btn.setVisibility(View.GONE);
            holder.manage_user.setVisibility(View.GONE);
        }
        String profile_pic = user.getProfile_pic().substring(0, user.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(context)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.profile_pic);
    }

    private void removeUser(String username, String position, String group_id){
        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.green)
                .setIcon(R.drawable.ic_action_remove)
                .setTitle("Remove "+"@"+username+"?")
                .setPositiveButton(R.string.yes, v -> {
                    StringRequest stringRequest=new StringRequest(Request.Method.POST, REMOVE_USER, response -> {
                        try {
                            JSONObject res = new JSONObject(response);
                            String error = res.getString("error");
                            if (error.equals("false")){
                                String newPositionResult = res.getString("position");
                                Toast.makeText(context,newPositionResult,Toast.LENGTH_SHORT).show();
                                mAdapterCallback.onMethodCallback();
                            }else{
                                Toast.makeText(context,"Error, please try again later... #1",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Toast.makeText(context,"Error, please try again later... #2",Toast.LENGTH_LONG).show()){
                        @Override
                        protected Map<String, String> getParams()  {
                            Map<String,String> parms= new HashMap<>();
                            parms.put("deviceuser",deviceusername);
                            parms.put("deviceuserid",deviceuserid);
                            parms.put("group_id",group_id);
                            parms.put("current_position",position);
                            parms.put("username",username);
                            return parms;
                        }
                    };
                    ((ChatActivity)context).addToRequestQueue(stringRequest);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void manageUser(String username, String position, String group_id){
        if (!username.equals("")&&!position.equals("")){
            LayoutInflater li = LayoutInflater.from(context);
            View dialog_view = li.inflate(R.layout.dialog_message_manage_user, null);
            Button manageSaveBtn = dialog_view.findViewById(R.id.manageSaveBtn);
            ProgressBar manageSaveProgress = dialog_view.findViewById(R.id.manageSaveProgress);
            CheckBox adminBox = dialog_view.findViewById(R.id.adminBox);
            CheckBox userBox = dialog_view.findViewById(R.id.userBox);
            if (position.equals("admin")){
                adminBox.setChecked(true);
            }else if (position.equals("user")){
                userBox.setChecked(true);
            }
            adminBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (adminBox.isChecked()){
                    userBox.setChecked(false);
                }
            });
            userBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(userBox.isChecked()){
                    adminBox.setChecked(false);
                }
            });
            final LovelyCustomDialog dialog = new LovelyCustomDialog(context);
            dialog.setView(dialog_view)
                    .setTopColorRes(R.color.green)
                    .setTitle(R.string.messages_manage_user)
                    .setIcon(R.drawable.ic_person_black_24dp)
                    .setListener(R.id.manageSaveBtn, v -> {
                        manageSaveBtn.setVisibility(View.GONE);
                        manageSaveProgress.setVisibility(View.VISIBLE);
                        String newPermission;
                        if (userBox.isChecked()&&!adminBox.isChecked()){
                            newPermission = "user";
                        }else if (!userBox.isChecked()&&adminBox.isChecked()){
                            newPermission = "admin";
                        }else{
                            manageSaveBtn.setVisibility(View.VISIBLE);
                            manageSaveProgress.setVisibility(View.GONE);
                            Toast.makeText(context,"Please check 1 box!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        StringRequest stringRequest=new StringRequest(Request.Method.POST, MANAGE_USER, response -> {
                            try {
                                JSONObject res = new JSONObject(response);
                                String error = res.getString("error");
                                if (error.equals("false")){
                                    String newPositionResult = res.getString("position");
                                    Toast.makeText(context,newPositionResult,Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    mAdapterCallback.onMethodCallback();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            manageSaveBtn.setVisibility(View.VISIBLE);
                            manageSaveProgress.setVisibility(View.GONE);
                            Toast.makeText(context,"Error, please try again later...",Toast.LENGTH_LONG).show();
                        }){
                            @Override
                            protected Map<String, String> getParams()  {
                                Map<String,String> parms= new HashMap<>();
                                parms.put("deviceuser",deviceusername);
                                parms.put("deviceuserid",deviceuserid);
                                parms.put("group_id",group_id);
                                parms.put("current_position",position);
                                parms.put("new_position",newPermission);
                                parms.put("username",username);
                                return parms;
                            }
                        };
                        ((ChatActivity)context).addToRequestQueue(stringRequest);
                        dialog.dismiss();

                    })
                    .show();
        }else{
            Toast.makeText(context, "An error occured! (#3)", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserListHolder extends RecyclerView.ViewHolder{
        TextView username,nickname,user_desc;
        CircleImageView profile_pic, online, verified;
        MaterialRippleLayout userListLayout;
        Button remove_user_btn,manage_user;

        public UserListHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            nickname = itemView.findViewById(R.id.nickname);
            profile_pic = itemView.findViewById(R.id.profile_image);
            userListLayout = itemView.findViewById(R.id.userListLayout);
            verified = itemView.findViewById(R.id.verified);
            online = itemView.findViewById(R.id.online);
            user_desc = itemView.findViewById(R.id.user_desc);
            remove_user_btn = itemView.findViewById(R.id.remove_user_btn);
            manage_user = itemView.findViewById(R.id.manage_user);
        }
    }

}
