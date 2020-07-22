package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private static final String FOLLOW_GAME_URL = Constants.ROOT_URL+"publicscat_follow_api.php";

    private Context mCtx;
    private List<Publics_Recycler> publicsList;

    public PublicsAdapter(Context mCtx,List<Publics_Recycler> publicsList) {
        this.mCtx = mCtx;
        this.publicsList = publicsList;
    }

    @NonNull @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_publics, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == publicsList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return publicsList == null ? 0 : publicsList.size();
    }

    public void addItems(List<Publics_Recycler> publicsitems) {
        publicsList.addAll(publicsitems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        publicsList.add(new Publics_Recycler());
        notifyItemInserted(publicsList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        if (publicsList.size()!=0){
            int position = publicsList.size() - 1;
            Publics_Recycler item = getItem(position);
            if (item != null) {
                publicsList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }


    public void clear() {
        publicsList.clear();
        notifyDataSetChanged();
    }

    Publics_Recycler getItem(int position) {
        return publicsList.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        ImageView imageView;
        TextView textViewTitle, textViewNumRatings, postCount;
        LinearLayout publicsTopicLayout;
        Button publicsActionBtn, publicsActionBtnFollowed;
        SimpleRatingBar profileRating;
        ProgressBar followProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            followProgress = itemView.findViewById(R.id.followProgress);
            profileRating = itemView.findViewById(R.id.profileRating);
            publicsActionBtn = itemView.findViewById(R.id.publicsActionBtn);
            publicsActionBtnFollowed = itemView.findViewById(R.id.publicsActionBtnFollowed);
            imageView = itemView.findViewById(R.id.publicsImageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewNumRatings = itemView.findViewById(R.id.reviewCount);
            publicsTopicLayout = itemView.findViewById(R.id.recyclerPublicsLayout);
            postCount = itemView.findViewById(R.id.postCount);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            final Publics_Recycler publics = publicsList.get(position);

            if(publics.getFollowed().equals("yes")){
                publicsActionBtn.setVisibility(View.GONE);
                publicsActionBtnFollowed.setVisibility(View.VISIBLE);
            } else {
                publicsActionBtnFollowed.setVisibility(View.GONE);
                publicsActionBtn.setVisibility(View.VISIBLE);
            }

            if (publics.getAvgrating() != null && !publics.getAvgrating().isEmpty() && !publics.getAvgrating().equals("null")) {
                profileRating.setVisibility(View.VISIBLE);
                profileRating.setRating(Float.parseFloat(publics.getAvgrating()));
            }else{
                profileRating.setVisibility(View.INVISIBLE);
            }

            publicsActionBtn.setOnClickListener(view -> {
                publicsActionBtn.setVisibility(View.GONE);
                followProgress.setVisibility(View.VISIBLE);
                final String userID = SharedPrefManager.getInstance(mCtx).getUserID();
                final String username = SharedPrefManager.getInstance(mCtx).getUsername();

                StringRequest stringRequest=new StringRequest(Request.Method.POST, FOLLOW_GAME_URL, response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("error").equals("false")) {
                            publicsActionBtnFollowed.setVisibility(View.VISIBLE);
                            followProgress.setVisibility(View.GONE);
                            publics.setFollowed("yes");
                        }else{
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(mCtx,"Could not follow, please try again later...",Toast.LENGTH_LONG).show()){
                    @Override
                    protected Map<String, String> getParams()  {
                        Map<String,String> parms= new HashMap<>();
                        parms.put("game_id",publics.getId());
                        parms.put("game_name",publics.getTitle());
                        parms.put("method","follow");
                        parms.put("user_id",userID);
                        parms.put("username",username);
                        return parms;
                    }
                };
                ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
            });

            publicsActionBtnFollowed.setOnClickListener(view -> {
                publicsActionBtnFollowed.setVisibility(View.GONE);
                followProgress.setVisibility(View.VISIBLE);
                new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.white)
                        .setIcon(R.drawable.ic_check)
                        .setTitle(R.string.game_unfollow)
                        .setMessage(mCtx.getResources().getString(R.string.unfollow)+" "+publics.getTitle()+"?")
                        .setPositiveButton(android.R.string.ok, v -> {
                            final String userID = SharedPrefManager.getInstance(mCtx).getUserID();
                            final String username = SharedPrefManager.getInstance(mCtx).getUsername();
                            StringRequest stringRequest=new StringRequest(Request.Method.POST, FOLLOW_GAME_URL, response -> {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("error").equals("false")) {
                                        publicsActionBtn.setVisibility(View.VISIBLE);
                                        followProgress.setVisibility(View.GONE);
                                        publics.setFollowed("no");
                                    }else{
                                        Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }, error -> Toast.makeText(mCtx,"Could not unfollow, please try again later...",Toast.LENGTH_LONG).show()){
                                @Override
                                protected Map<String, String> getParams()  {
                                    Map<String,String> parms= new HashMap<>();
                                    parms.put("game_id",publics.getId());
                                    parms.put("game_name",publics.getTitle());
                                    parms.put("method","unfollow");
                                    parms.put("user_id",userID);
                                    parms.put("username",username);
                                    return parms;
                                }
                            };
                            ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
                        })
                        .setNegativeButton(android.R.string.no, v -> {
                            followProgress.setVisibility(View.GONE);
                            publicsActionBtnFollowed.setVisibility(View.VISIBLE);
                        })
                        .show();
            });

            postCount.setText(publics.getPostcount());
            textViewTitle.setText(publics.getTitle());
            textViewNumRatings.setText(publics.getNumratings());
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + publics.getImage())
                    .into(imageView);
            publicsTopicLayout.setOnClickListener(v -> {
                if (mCtx instanceof FragmentContainer) {
                    FragmentPublicsCat ldf = new FragmentPublicsCat ();
                    Bundle args = new Bundle();
                    args.putString("PublicsId", publics.getId());
                    ldf.setArguments(args);
                    ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }});
        }
    }

    public static class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }
        @Override
        protected void clear() {
        }
    }

}