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

public class PublicsSubCommentAdapter extends RecyclerView.Adapter<PublicsSubCommentAdapter.PublicsSubCommentViewHolder> {

    private Context mCtx;
    private List<Publics_SubComments_Recycler> subCommentList;

    public PublicsSubCommentAdapter(Context mCtx, List<Publics_SubComments_Recycler> subCommentList) {
        this.mCtx = mCtx;
        this.subCommentList = subCommentList;
    }

    @NonNull
    @Override
    public PublicsSubCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recycler_publics_sub_comment, null);
        return new PublicsSubCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicsSubCommentViewHolder holder, int position) {
        final Publics_SubComments_Recycler subComment = subCommentList.get(position);
        if (subComment.getOnline().equals("yes")){
            holder.online.setVisibility(View.VISIBLE);
        }else{
            holder.online.setVisibility(View.GONE);
        }
        if (subComment.getVerified().equals("yes")){
            holder.verified.setVisibility(View.VISIBLE);
        }else{
            holder.verified.setVisibility(View.GONE);
        }
        holder.clantag.setText(subComment.getClantag());
        holder.content.setText(subComment.getReply());
        holder.username.setText(String.format("@%s", subComment.getUsername()));
        holder.nickname.setText(subComment.getNickname());
        holder.nickname.setOnClickListener(v -> {
            FragmentProfile ldf = new FragmentProfile();
            Bundle args = new Bundle();
            args.putString("UserId", subComment.getUserid());
            ldf.setArguments(args);
            ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
        });
        holder.comment_date.setText(subComment.getPost_date());
        String profile_pic = subComment.getProfile_pic().substring(0, subComment.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(mCtx)
                .load(Constants.BASE_URL+ profile_pic)
                .into(holder.profilepic);
    }

    @Override
    public int getItemCount() {
        return subCommentList.size();
    }
    static class PublicsSubCommentViewHolder extends RecyclerView.ViewHolder {
        MaterialRippleLayout userListLayout;
        CircleImageView profilepic, online, verified;
        TextView nickname, username, content, comment_date, clantag;
        public PublicsSubCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            online = itemView.findViewById(R.id.online);
            profilepic = itemView.findViewById(R.id.profile_image);
            verified = itemView.findViewById(R.id.verified);
            nickname = itemView.findViewById(R.id.nickname);
            username = itemView.findViewById(R.id.username);
            content = itemView.findViewById(R.id.content);
            comment_date = itemView.findViewById(R.id.comment_date);
            userListLayout = itemView.findViewById(R.id.userListLayout);
            clantag = itemView.findViewById(R.id.clantag);
        }
    }

}
