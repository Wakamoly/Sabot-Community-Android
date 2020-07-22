package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PublicsTopicAdapter extends RecyclerView.Adapter<PublicsTopicAdapter.PublicsTopicViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private Context mCtx;
    private List<PublicsTopic_Recycler> publicsTopicList;
    private boolean isLoadingAdded = false;

    public PublicsTopicAdapter(Context mCtx, List<PublicsTopic_Recycler> publicsTopicList) {
        this.mCtx = mCtx;
        this.publicsTopicList = publicsTopicList;
    }

    @NonNull
    @Override
    public PublicsTopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PublicsTopicViewHolder holder = null;
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
    private PublicsTopicViewHolder getViewHolder(LayoutInflater inflater) {
        PublicsTopicViewHolder holder;
        View v1 = inflater.inflate(R.layout.recycler_publics_topic, null);
        holder = new PublicsTopicViewHolder(v1);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PublicsTopicViewHolder holder, int position) {
        final PublicsTopic_Recycler publics = publicsTopicList.get(position);
        holder.textViewTitle.setText(publics.getSubject());
        holder.textViewDate.setText(publics.getDate());
        holder.textViewNumPosts.setText(publics.getNumPosts());
        holder.tvProfileName.setText(String.format("@%s", publics.getUsername()));
        holder.textViewGamename.setText(publics.getGamename());
        String profile_pic = publics.getProfile_pic().substring(0, publics.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.imageView);
        switch (publics.getType()) {
            case "Xbox":
                holder.notiType.setImageResource(R.drawable.icons8_xbox_50);
                holder.notiType.setVisibility(View.VISIBLE);
                break;
            case "PlayStation":
                holder.notiType.setImageResource(R.drawable.icons8_playstation_50);
                holder.notiType.setVisibility(View.VISIBLE);
                break;
            case "Steam":
                holder.notiType.setImageResource(R.drawable.icons8_steam_48);
                holder.notiType.setVisibility(View.VISIBLE);
                break;
            case "PC":
                holder.notiType.setImageResource(R.drawable.icons8_workstation_48);
                holder.notiType.setVisibility(View.VISIBLE);
                break;
            case "Mobile":
                holder.notiType.setImageResource(R.drawable.icons8_mobile_48);
                holder.notiType.setVisibility(View.VISIBLE);
                break;
            case "Switch":
                holder.notiType.setImageResource(R.drawable.icons8_nintendo_switch_48);
                holder.notiType.setVisibility(View.VISIBLE);
                break;
            default:
                holder.notiType.setImageResource(R.drawable.icons8_question_mark_64);
                holder.notiType.setVisibility(View.VISIBLE);
                break;
        }
        holder.eventDate.setText(publics.getEvent_date());
        if (publics.getEvent_date().equals("ended")){
            holder.eventDate.setTextColor(ContextCompat.getColor(mCtx, R.color.pin));
        }else if(publics.getEvent_date().equals("now")){
            holder.eventDate.setTextColor(ContextCompat.getColor(mCtx, R.color.green));
        }
        holder.publicsTopicLayout.setOnClickListener(v -> {
            if (mCtx instanceof FragmentContainer) {
                PublicsTopicFragment ldf = new PublicsTopicFragment ();
                Bundle args = new Bundle();
                args.putString("PublicsId", publics.getId());
                ldf.setArguments(args);
                ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }});
        holder.numPlayersAdded.setText(publics.getNum_added());
        holder.numPlayersNeeded.setText(publics.getNum_players());
        holder.textViewContext.setText(publics.getContext());
    }

    protected static class LoadingVH extends PublicsTopicViewHolder {
        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return publicsTopicList.size();
    }
    static class PublicsTopicViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, notiType;
        TextView textViewTitle, textViewDate, textViewNumPosts, tvProfileName, eventDate, textViewContext, numPlayersAdded, numPlayersNeeded, textViewGamename;
        RelativeLayout publicsTopicLayout;
        public PublicsTopicViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.publicsImageView);
            notiType = itemView.findViewById(R.id.platformType);
            tvProfileName = itemView.findViewById(R.id.tvProfileName);
            textViewNumPosts = itemView.findViewById(R.id.textViewNumComments);
            textViewDate = itemView.findViewById(R.id.textViewProfilenewsDate);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            publicsTopicLayout = itemView.findViewById(R.id.publicsTopicLayout);
            eventDate = itemView.findViewById(R.id.eventDate);
            numPlayersNeeded = itemView.findViewById(R.id.numPlayersNeeded);
            textViewContext = itemView.findViewById(R.id.textViewContext);
            numPlayersAdded = itemView.findViewById(R.id.numPlayersAdded);
            textViewGamename = itemView.findViewById(R.id.textViewGamename);
        }
    }

}
