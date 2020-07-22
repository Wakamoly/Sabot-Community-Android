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

public class PublicsPlayersNonAdminAdapter extends RecyclerView.Adapter<PublicsPlayersNonAdminAdapter.MembersViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private Context mCtx;
    private List<Publics_Players_Recycler> membersList;
    private boolean isLoadingAdded = false;

    public PublicsPlayersNonAdminAdapter(Context mCtx, List<Publics_Players_Recycler> membersList) {
        this.mCtx = mCtx;
        this.membersList = membersList;
    }

    @NonNull
    @Override
    public PublicsPlayersNonAdminAdapter.MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PublicsPlayersNonAdminAdapter.MembersViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        switch (viewType) {
            case ITEM:
                holder = getViewHolder(parent, inflater);
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
    private PublicsPlayersNonAdminAdapter.MembersViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        PublicsPlayersNonAdminAdapter.MembersViewHolder holder;
        View v1 = inflater.inflate(R.layout.recycler_member, null);
        holder = new MembersViewHolder(v1);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PublicsPlayersNonAdminAdapter.MembersViewHolder holder, int position) {
        final Publics_Players_Recycler members = membersList.get(position);
        holder.textViewUsername.setText(String.format("@%s", members.getUsername()));
        holder.textViewNickname.setText(members.getNickname());
        String profile_pic = members.getProfile_pic().substring(0, members.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.imageView);
        holder.clanMemberLayout.setOnClickListener(v -> {
            FragmentProfile ldf = new FragmentProfile();
            Bundle args = new Bundle();
            args.putString("UserId", members.getUserid());
            ldf.setArguments(args);
            ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
        });
    }

    protected static class LoadingVH extends PublicsPlayersNonAdminAdapter.MembersViewHolder {
        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }
    static class MembersViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView textViewNickname,textViewUsername;
        MaterialRippleLayout clanMemberLayout;
        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.memberImageView);
            textViewNickname = itemView.findViewById(R.id.textViewNickname);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            clanMemberLayout = itemView.findViewById(R.id.clanMemberLayout);
        }
    }

}
