package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class MessagesThreadAdapter extends RecyclerView.Adapter<MessagesThreadAdapter.ViewHolder> {
    private String username;
    private Context context;
    private int SELF = 786;
    private ArrayList<MessagesHelper> messages;

    public MessagesThreadAdapter(Context context, ArrayList<MessagesHelper> messages, String username) {
        this.username = username;
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        MessagesHelper message = messages.get(position);
        if (message.getUser_from().equals(username)) {
            return SELF;
        }
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == SELF) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.row_sent_message, parent, false);
        } else {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.row_received_message, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MessagesHelper message = messages.get(position);
        holder.textViewMessage.setText(message.getBody());
        holder.textViewTime.setText(message.getDate());
        if(!message.getImage().equals("")){
            holder.img_msg.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(Constants.BASE_URL+ message.getImage())
                    .error(R.mipmap.ic_launcher)
                    .into(holder.img_msg);
            holder.img_msg.setOnClickListener(view -> {
                Fragment asf = new PhotoViewFragment();
                Bundle args = new Bundle();
                args.putString("image", message.getImage());
                asf.setArguments(args);
                FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.chat_fragment_container, asf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });
        }else{
            holder.img_msg.setVisibility(View.GONE);
            holder.img_msg.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewTime;
        ImageView img_msg;
        public ViewHolder(View itemView) {
            super(itemView);
            img_msg = itemView.findViewById(R.id.img_msg);
            textViewMessage = (TextView) itemView.findViewById(R.id.tv_message_content);
            textViewTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
