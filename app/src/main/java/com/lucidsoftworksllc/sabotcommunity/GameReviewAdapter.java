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
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GameReviewAdapter extends RecyclerView.Adapter<GameReviewAdapter.GameReviewViewHolder> {

    private Context mCtx;
    private List<GameReviewRecycler> gamereviewList;

    public GameReviewAdapter(Context mCtx, List<GameReviewRecycler> gamereviewList) {
        this.mCtx = mCtx;
        this.gamereviewList = gamereviewList;
    }

    @NonNull
    @Override
    public GameReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recycler_gamereviews, null);
        return new GameReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GameReviewViewHolder holder, int position) {
        final GameReviewRecycler gamereviews = gamereviewList.get(position);
        holder.textViewBody.setText(gamereviews.getComments());
        holder.textViewReviewTitle.setText(gamereviews.getTitle());
        holder.textViewAdded_by.setText(gamereviews.getNickname());
        holder.textViewAdded_by.setOnClickListener(v -> {
            FragmentProfile ldf = new FragmentProfile();
            Bundle args = new Bundle();
            args.putString("UserId", gamereviews.getUser_id());
            ldf.setArguments(args);
            ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
        });
        holder.textViewDate_added.setText(gamereviews.getTime());
        holder.gamerated.setRating(Float.parseFloat(gamereviews.getRatingnumber()));
        String profile_pic = gamereviews.getProfile_pic().substring(0, gamereviews.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(mCtx)
                .load(Constants.BASE_URL+ profile_pic)
                .into(holder.imageViewGamenewsPic);
        holder.gamereview_layout.setOnClickListener(v -> {
            holder.textViewReviewTitle.setMaxLines(3);
            holder.textViewBody.setMaxLines(20);
        });
    }

    @Override
    public int getItemCount() {
        return gamereviewList.size();
    }
    static class GameReviewViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageViewGamenewsPic;
        SimpleRatingBar gamerated;
        TextView textViewBody, textViewAdded_by, textViewDate_added, textViewReviewTitle;
        MaterialRippleLayout gamereview_layout;

        public GameReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAdded_by = itemView.findViewById(R.id.textViewProfileName);
            imageViewGamenewsPic = itemView.findViewById(R.id.imageViewProfilenewsPic);
            textViewBody = itemView.findViewById(R.id.textViewBody);
            gamerated = itemView.findViewById(R.id.gamerated);
            textViewReviewTitle = itemView.findViewById(R.id.textViewReviewTitle);
            textViewDate_added = itemView.findViewById(R.id.textViewReviewPostedDate);
            gamereview_layout = itemView.findViewById(R.id.gamereview_layout);
        }
    }
}
