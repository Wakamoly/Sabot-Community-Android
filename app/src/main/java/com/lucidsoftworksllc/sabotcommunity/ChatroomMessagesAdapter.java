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

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatroomMessagesAdapter extends RecyclerView.Adapter<ChatroomMessagesAdapter.ViewHolder> {

    private String username;
    private Context context;
    private int SELF = 786;
    private ArrayList<ChatroomMessagesHelper> messages;
    public ChatroomMessagesAdapter(Context context, ArrayList<ChatroomMessagesHelper> messages, String username) {
        this.username = username;
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        ChatroomMessagesHelper message = messages.get(position);
        if (message.getUsername().equals(username)) {
            return SELF;
        }
        return position;
    }

    @NonNull
    @Override
    public ChatroomMessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == SELF) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_chatroom_right, parent, false);
        } else {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_chatroom_left, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatroomMessagesAdapter.ViewHolder holder, int position) {
        final ChatroomMessagesHelper message = messages.get(position);
        if (message.getVerified().equals("yes")){
            holder.verified.setVisibility(View.VISIBLE);
        }else{
            holder.verified.setVisibility(View.GONE);
        }
        if (message.getlast_online().equals("yes")){
            holder.online.setVisibility(View.VISIBLE);
        }else{
            holder.online.setVisibility(View.GONE);
        }
        String profile_pic = message.getProfile_pic().substring(0, message.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(context)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.imageViewProfilenewsPic);
        holder.textViewBody.setText(message.getMessage());
        holder.profileCommentsDateTime_top.setText(message.getTime_message());
        holder.textViewProfileName.setText(message.getNickname());
        holder.textViewProfileName.setOnClickListener(v -> {
            FragmentProfile ldf = new FragmentProfile();
            Bundle args = new Bundle();
            args.putString("UserId", message.getUserid());
            ldf.setArguments(args);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
        });
    }

    @Override public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProfileName, textViewBody;
        TextView profileCommentsDateTime_top;
        CircleImageView imageViewProfilenewsPic, online, verified;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewProfileName = itemView.findViewById(R.id.textViewProfileName);
            imageViewProfilenewsPic = itemView.findViewById(R.id.imageViewProfilenewsPic);
            online = itemView.findViewById(R.id.online);
            verified = itemView.findViewById(R.id.verified);
            profileCommentsDateTime_top = itemView.findViewById(R.id.profileCommentsDateTime_top);
            textViewBody = itemView.findViewById(R.id.textViewBody);
        }
    }

}
