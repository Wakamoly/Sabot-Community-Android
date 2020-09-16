package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.List;

public class PlayerReviewAdapter extends RecyclerView.Adapter<PlayerReviewAdapter.PlayerReviewViewHolder> {

    private Context mCtx;
    private List<PlayerReview_Recycler> playerreviewList;

    public PlayerReviewAdapter(Context mCtx, List<PlayerReview_Recycler> playerreviewList) {
        this.mCtx = mCtx;
        this.playerreviewList = playerreviewList;
    }

    @NonNull
    @Override
    public PlayerReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recycler_playerreviews, null);
        return new PlayerReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayerReviewViewHolder holder, int position) {
        final PlayerReview_Recycler playerreviews = playerreviewList.get(position);

        holder.textViewBody.setText(playerreviews.getComments());
        holder.textViewReviewTitle.setText(playerreviews.getTitle());
        holder.textViewAdded_by.setText(playerreviews.getNickname());
        holder.textViewAdded_by.setOnClickListener(v -> {
            FragmentProfile ldf = new FragmentProfile();
            Bundle args = new Bundle();
            args.putString("UserId", playerreviews.getUser_id());
            ldf.setArguments(args);
            ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
        });
        holder.textViewDate_added.setText(playerreviews.getTime());
        holder.playerrated.setRating(Float.parseFloat(playerreviews.getRatingnumber()));
        String profile_pic = playerreviews.getProfile_pic().substring(0, playerreviews.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.imageViewProfilenewsPic);
        holder.playerreview_layout.setOnClickListener(v -> {
            holder.textViewReviewTitle.setMaxLines(3);
            holder.textViewBody.setMaxLines(20);
        });
    }

    @Override
    public int getItemCount() {
        return playerreviewList.size();
    }
    static class PlayerReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfilenewsPic;
        SimpleRatingBar playerrated;
        TextView textViewBody, textViewAdded_by, textViewDate_added, textViewReviewTitle;
        MaterialRippleLayout playerreview_layout;

        public PlayerReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAdded_by = itemView.findViewById(R.id.textViewProfileName);
            imageViewProfilenewsPic = itemView.findViewById(R.id.imageViewProfilenewsPic);
            textViewBody = itemView.findViewById(R.id.textViewBody);
            playerrated = itemView.findViewById(R.id.playerrated);
            textViewReviewTitle = itemView.findViewById(R.id.textViewReviewTitle);
            textViewDate_added = itemView.findViewById(R.id.textViewReviewPostedDate);
            playerreview_layout = itemView.findViewById(R.id.playerreview_layout);
        }
    }

}
