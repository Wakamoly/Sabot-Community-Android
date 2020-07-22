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

//Class extending RecyclerviewAdapter
public class JoinedClansAdapter extends RecyclerView.Adapter<JoinedClansAdapter.ViewHolder> {

    private Context mCtx;
    private List<Clans_Recycler> clansList;

    public JoinedClansAdapter(Context mCtx, List<Clans_Recycler> clansList) {
        this.mCtx = mCtx;
        this.clansList = clansList;
    }

    @NonNull
    @Override
    public JoinedClansAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recycler_clans, null);
        JoinedClansAdapter.ViewHolder holder = new JoinedClansAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull JoinedClansAdapter.ViewHolder holder, int position) {
        final Clans_Recycler clans = clansList.get(position);

        holder.textViewTag.setText("["+clans.getTag()+"]");
        holder.textViewTag.setAllCaps(true);
        holder.textViewTitle.setText(clans.getName());
        holder.memberCount.setText(clans.getNum_members());
        holder.tvPosition.setText(clans.getPosition());

        if (clans.getAvg() != null && !clans.getAvg().isEmpty() && !clans.getAvg().equals("null")) {
            holder.clanRating.setRating(Float.parseFloat(clans.getAvg()));
        }

        Glide.with(mCtx)
                .load(Constants.BASE_URL+clans.getInsignia())
                .into(holder.clanImageView);

        holder.recyclerclanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClanFragment ldf = new ClanFragment();
                Bundle args = new Bundle();
                args.putString("ClanId", clans.getId());
                ldf.setArguments(args);
                ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return clansList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        MaterialRippleLayout recyclerclanLayout;
        ImageView clanImageView;
        TextView textViewTag,textViewTitle,memberCount, tvPosition;
        SimpleRatingBar clanRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerclanLayout = itemView.findViewById(R.id.recyclerclanLayout);
            clanImageView = itemView.findViewById(R.id.clanImageView);
            textViewTag = itemView.findViewById(R.id.textViewTag);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            memberCount = itemView.findViewById(R.id.memberCount);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            clanRating = itemView.findViewById(R.id.clanRating);
        }
    }
}
