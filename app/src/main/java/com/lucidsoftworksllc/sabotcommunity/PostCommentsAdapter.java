package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.ProfileCommentsViewHolder> {

    private static final String LIKE_URL = Constants.ROOT_URL+"comment_like.php";
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private Context mCtx;
    private List<PostComment_Recycler> postCommentsList;
    private boolean isLoadingAdded = false;

    public PostCommentsAdapter(Context mCtx, List<PostComment_Recycler> postCommentsList) {
        this.mCtx = mCtx;
        this.postCommentsList = postCommentsList;
    }

    @NonNull
    @Override
    public ProfileCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProfileCommentsViewHolder holder = null;
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
    private ProfileCommentsViewHolder getViewHolder(LayoutInflater inflater) {
        ProfileCommentsViewHolder holder;
        View v1 = inflater.inflate(R.layout.recycler_profilenews, null);
        holder = new ProfileCommentsViewHolder(v1);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileCommentsViewHolder holder, int position) {
        final PostComment_Recycler comment = postCommentsList.get(position);
        holder.textViewNickname.setText(comment.getNickname());
        holder.textViewPostBody.setText(comment.getBody());
        String [] bodybits = comment.getBody().split("\\s+");
        for( final String item : bodybits ) {
            if(android.util.Patterns.WEB_URL.matcher(item).matches()) {
                final String finalItem;
                if(!item.contains("http://")&&!item.contains("https://")){
                    finalItem = "https://"+item;
                }else {
                    finalItem = item;
                }
                final String[] imageUrl = {null};
                final String[] title = new String[1];
                final String[] desc = {null};
                holder.urlPreview.setVisibility(View.VISIBLE);
                holder.urlImage.setOnClickListener(v -> {
                    Uri uri = Uri.parse(finalItem);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mCtx.startActivity(intent);
                });
                new Thread(() -> {
                    try {
                        Document doc = Jsoup.connect(finalItem).get();
                        Elements ogTags = doc.select("meta[property^=og:]");
                        if (ogTags.size() <= 0) {
                            return;
                        }
                        Elements metaOgTitle = doc.select("meta[property=og:title]");
                        if (metaOgTitle!=null) {
                            title[0] = metaOgTitle.attr("content");
                        }
                        else {
                            title[0] = doc.title();
                        }
                        Elements metaOgDesc = doc.select("meta[property=og:description]");
                        if (metaOgDesc!=null) {
                            desc[0] = metaOgDesc.attr("content");
                        }
                        Elements metaOgImage = doc.select("meta[property=og:image]");
                        if (metaOgImage!=null) {
                            imageUrl[0] = metaOgImage.attr("content");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                //Fucking code wouldn't work any other way than I'm currently capable. Fuck it, have a delay
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    Glide.with(mCtx)
                            .load(imageUrl[0])
                            .error(R.drawable.ic_error)
                            .into(holder.urlImage);
                    if (title[0] !=null) {
                        holder.urlTitle.setText(title[0]);
                    }else{
                        holder.urlTitle.setText(mCtx.getString(R.string.no_content));
                    }
                    if (desc[0] !=null) {
                        holder.urlDesc.setText(desc[0]);
                    }
                    holder.urlProgress.setVisibility(View.GONE);
                    holder.urlBits.setVisibility(View.VISIBLE);
                }, 5000);
                break;
            }
        }

        holder.textViewPostDateTime.setText(comment.getTime());
        holder.textViewUsername.setText(String.format("@%s", comment.getUsername()));
        holder.textViewNumLikes.setText(comment.getLikes());
        holder.textViewUsernameTo.setVisibility(View.GONE);
        holder.visIfPost.setVisibility(View.GONE);
        holder.textViewUsername.setTextColor(ContextCompat.getColor(mCtx, R.color.light_blue));
        String profile_pic = comment.getProfile_pic().substring(0, comment.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.imageView);
        holder.textViewNickname.setOnClickListener(v -> {
            if (mCtx instanceof FragmentContainer) {
                FragmentProfile ldf = new FragmentProfile ();
                Bundle args = new Bundle();
                args.putString("UserId", comment.getUser_id());
                args.putString("Username", comment.getUsername());
                ldf.setArguments(args);
                ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }});
        if(comment.getLikedbyuseryes().equals("yes")){
            holder.likeView.setVisibility(View.GONE);
            holder.likedView.setVisibility(View.VISIBLE);
        }
        holder.likeView.setOnClickListener(v -> {
            holder.likeView.setVisibility(View.GONE);
            holder.likeProgress.setVisibility(View.VISIBLE);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, LIKE_URL, response -> {
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        String newValue = Integer.toString(Integer.parseInt(holder.textViewNumLikes.getText().toString())+1);
                        holder.textViewNumLikes.setText(newValue);
                        holder.likeProgress.setVisibility(View.GONE);
                        holder.likedView.setVisibility(View.VISIBLE);
                        holder.likedView.setEnabled(false);
                        new Handler().postDelayed(() -> holder.likedView.setEnabled(true),3500);
                    } else {
                        Toast.makeText(mCtx,obj.getString("message"),Toast.LENGTH_LONG).show();
                        holder.likeProgress.setVisibility(View.GONE);
                        holder.likeView.setVisibility(View.VISIBLE);
                        holder.likeView.setEnabled(false);
                        new Handler().postDelayed(() -> holder.likeView.setEnabled(true),3000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(mCtx,"Could not like, please try again later...",Toast.LENGTH_LONG).show();
                holder.likeProgress.setVisibility(View.GONE);
                holder.likeView.setVisibility(View.VISIBLE);
                holder.likeView.setEnabled(false);
                new Handler().postDelayed(() -> holder.likeView.setEnabled(true),3000);
            }){
                @Override
                protected Map<String, String> getParams()  {
                    Map<String,String> parms= new HashMap<>();
                    parms.put("comment_id", String.valueOf(comment.getId()));
                    parms.put("method","like");
                    parms.put("user_to",comment.getUsername());
                    parms.put("user_id",holder.userID);
                    parms.put("username",holder.username);
                    parms.put("body",comment.getBody());
                    parms.put("post_id",comment.getPost_id());
                    return parms;
                }
            };
            ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
        });
        holder.likedView.setOnClickListener(v -> {
            holder.likedView.setVisibility(View.GONE);
            holder.likeProgress.setVisibility(View.VISIBLE);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, LIKE_URL, response -> {
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        String newValue = Integer.toString(Integer.parseInt(holder.textViewNumLikes.getText().toString())-1);
                        holder.textViewNumLikes.setText(newValue);
                        holder.likeProgress.setVisibility(View.GONE);
                        holder.likeView.setVisibility(View.VISIBLE);
                        holder.likeView.setEnabled(false);
                        new Handler().postDelayed(() -> holder.likeView.setEnabled(true),3500);
                    } else {
                        Toast.makeText(mCtx,obj.getString("message"),Toast.LENGTH_LONG).show();
                        holder.likeProgress.setVisibility(View.GONE);
                        holder.likedView.setVisibility(View.VISIBLE);
                        holder.likedView.setEnabled(false);
                        new Handler().postDelayed(() -> holder.likedView.setEnabled(true),3000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(mCtx,"Could not remove like, please try again later...",Toast.LENGTH_LONG).show();
                holder.likeProgress.setVisibility(View.GONE);
                holder.likedView.setVisibility(View.VISIBLE);
                holder.likedView.setEnabled(false);
                new Handler().postDelayed(() -> holder.likeView.setEnabled(true),3000);
            }){
                @Override
                protected Map<String, String> getParams()  {
                    Map<String,String> parms= new HashMap<>();
                    parms.put("comment_id", String.valueOf(comment.getId()));
                    parms.put("method","unlike");
                    parms.put("user_to",comment.getUsername());
                    parms.put("user_id",holder.userID);
                    parms.put("username",holder.username);
                    parms.put("body",comment.getBody());
                    parms.put("post_id",comment.getPost_id());
                    return parms;
                }
            };
            ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
        });

        if (comment.getOnline().equals("yes")) {
            holder.online.setVisibility(View.VISIBLE);
        }else{
            holder.online.setVisibility(View.GONE);
        }
        if (comment.getVerified().equals("yes")) {
            holder.verified.setVisibility(View.VISIBLE);
        }else{
            holder.verified.setVisibility(View.GONE);
        }

        holder.textViewLikes.setOnClickListener(v -> {
            Fragment asf = new UserListFragment();
            Bundle args = new Bundle();
            args.putString("query", "comment");
            args.putString("queryID", comment.getId());
            asf.setArguments(args);
            FragmentTransaction fragmentTransaction = ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, asf);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        if (comment.getIsEdited().equals("yes")){
            holder.tvEdited.setVisibility(View.VISIBLE);
        }else{
            holder.tvEdited.setVisibility(View.GONE);
        }
        holder.contentDivider.setVisibility(View.GONE);
    }

    protected class LoadingVH extends ProfileCommentsViewHolder {
        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return postCommentsList.size();
    }

    class ProfileCommentsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView verified, online;
        MaterialRippleLayout contentLayout;
        LinearLayout urlBits,urlPreview,likesLayout;
        ImageView imageView, urlImage;
        RelativeLayout visIfPost;
        ProgressBar likeProgress, urlProgress;
        ImageView likeView, likedView, contentDivider;
        TextView tvEdited,textViewNickname, textViewUsername, textViewUsernameTo, textViewPostBody, textViewPostDateTime,textViewNumLikes,urlTitle,urlDesc,textViewLikes;
        String userID, username;
        SharedPrefManager Sharedprefmanager = SharedPrefManager.getInstance(mCtx);

        public ProfileCommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            userID = Sharedprefmanager.getUserID();
            verified = itemView.findViewById(R.id.verified);
            online = itemView.findViewById(R.id.online);
            username = Sharedprefmanager.getUsername();
            visIfPost = itemView.findViewById(R.id.visIfPost);
            imageView = itemView.findViewById(R.id.imageViewProfilenewsPic);
            likeProgress = itemView.findViewById(R.id.likeProgress);
            likeView = itemView.findViewById(R.id.like);
            likedView = itemView.findViewById(R.id.liked);
            textViewNickname = itemView.findViewById(R.id.textViewProfileName);
            textViewUsername = itemView.findViewById(R.id.postUsername_top);
            textViewUsernameTo = itemView.findViewById(R.id.textViewToUserName);
            textViewPostBody = itemView.findViewById(R.id.textViewBody);
            textViewPostDateTime = itemView.findViewById(R.id.profileCommentsDateTime_top);
            textViewNumLikes = itemView.findViewById(R.id.textViewNumLikes);
            urlPreview = itemView.findViewById(R.id.urlPreview);
            urlProgress = itemView.findViewById(R.id.urlProgress);
            urlImage = itemView.findViewById(R.id.urlImage);
            urlTitle = itemView.findViewById(R.id.urlTitle);
            urlDesc = itemView.findViewById(R.id.urlDesc);
            urlBits = itemView.findViewById(R.id.urlBits);
            likesLayout = itemView.findViewById(R.id.likesLayout);
            contentLayout = itemView.findViewById(R.id.contentLayout);
            textViewLikes = itemView.findViewById(R.id.textViewLikes);
            contentDivider = itemView.findViewById(R.id.contentDivider);
            tvEdited = itemView.findViewById(R.id.tvEdited);
        }
    }

}
