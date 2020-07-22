package com.lucidsoftworksllc.sabotcommunity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class PublicsPostAdapter extends RecyclerView.Adapter<PublicsPostAdapter.PublicsPostViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String VOTE_URL = Constants.ROOT_URL+"publics_topic_vote.php";
    private static final String SUB_COMMENTS_URL = Constants.ROOT_URL+"publics_post_subcomments.php";
    private static final String SUB_COMMENTS_SUBMIT_URL = Constants.ROOT_URL+"publics_subcomment_submit.php";
    PublicsSubCommentAdapter subCommentAdapter;
    List<Publics_SubComments_Recycler> subCommentList;
    private LinearLayoutManager layoutManager;
    private Context mCtx;
    private List<PublicsPost_Recycler> publicsPostList;
    private boolean isLoadingAdded = false;

    public PublicsPostAdapter(Context mCtx, List<PublicsPost_Recycler> publicsPostList) {
        this.mCtx = mCtx;
        this.publicsPostList = publicsPostList;
    }

    @NonNull
    @Override
    public PublicsPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PublicsPostViewHolder holder = null;
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
    private PublicsPostViewHolder getViewHolder(LayoutInflater inflater) {
        PublicsPostViewHolder holder;
        View v1 = inflater.inflate(R.layout.recycler_publics_posts, null);
        holder = new PublicsPostViewHolder(v1);
        return holder;
    }

    private void topicVote(@NonNull final PublicsPostViewHolder holder, final String method, final String layout_id, final String user_to){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, VOTE_URL, response -> {
            JSONObject obj;
            try {
                obj = new JSONObject(response);
                if (obj.getString("error").equals("false")) {
                    if (obj.getString("layout").equals("post")) {
                        if (obj.getString("method").equals("up")) {
                            String newValue = Integer.toString(Integer.parseInt(holder.textViewNumPublicsPoints.getText().toString()) + 1);
                            holder.textViewNumPublicsPoints.setText(newValue);
                            holder.voteProgress.setVisibility(View.GONE);
                            holder.publicsPostsUpvoteGreen.setVisibility(View.VISIBLE);
                            holder.publicsPostsDownvoteWhite.setVisibility(View.VISIBLE);
                            holder.publicsPostsUpvoteGreen.setEnabled(false);
                            holder.publicsPostsDownvoteWhite.setEnabled(false);
                            new Handler().postDelayed(() -> {
                                holder.publicsPostsUpvoteGreen.setEnabled(true);
                                holder.publicsPostsDownvoteWhite.setEnabled(true);
                            }, 3000);
                        } else if (obj.getString("method").equals("down")) {
                            String newValue = Integer.toString(Integer.parseInt(holder.textViewNumPublicsPoints.getText().toString()));
                            holder.textViewNumPublicsPoints.setText(newValue);
                            holder.voteProgress.setVisibility(View.GONE);
                            holder.publicsPostsUpvoteWhite.setVisibility(View.VISIBLE);
                            holder.publicsPostsDownvoteRed.setVisibility(View.VISIBLE);
                            holder.publicsPostsUpvoteWhite.setEnabled(false);
                            holder.publicsPostsDownvoteRed.setEnabled(false);
                            new Handler().postDelayed(() -> {
                                holder.publicsPostsUpvoteWhite.setEnabled(true);
                                holder.publicsPostsDownvoteRed.setEnabled(true);
                            }, 3000);
                        } else {
                            String finalValue = "";
                            if (obj.getString("result").equals("-1")){
                                finalValue = Integer.toString(Integer.parseInt(holder.textViewNumPublicsPoints.getText().toString())-1);
                            }else if(obj.getString("result").equals("+1")){
                                finalValue = Integer.toString(Integer.parseInt(holder.textViewNumPublicsPoints.getText().toString())+1);
                            }
                            holder.textViewNumPublicsPoints.setText(finalValue);
                            holder.publicsPostsUpvoteWhite.setVisibility(View.VISIBLE);
                            holder.publicsPostsDownvoteWhite.setVisibility(View.VISIBLE);
                            holder.voteProgress.setVisibility(View.GONE);
                            holder.publicsPostsUpvoteWhite.setEnabled(false);
                            holder.publicsPostsDownvoteWhite.setEnabled(false);
                            new Handler().postDelayed(() -> {
                                holder.publicsPostsUpvoteWhite.setEnabled(true);
                                holder.publicsPostsDownvoteWhite.setEnabled(true);
                            }, 3000);
                        }
                    }
                } else {
                    Toast.makeText(mCtx,obj.getString("message"),Toast.LENGTH_LONG).show();
                    holder.voteProgress.setVisibility(View.GONE);
                    holder.publicsPostsUpvoteWhite.setVisibility(View.VISIBLE);
                    holder.publicsPostsDownvoteWhite.setVisibility(View.VISIBLE);
                    holder.publicsPostsUpvoteWhite.setEnabled(false);
                    holder.publicsPostsDownvoteWhite.setEnabled(false);
                    new Handler().postDelayed(() -> {
                        holder.publicsPostsUpvoteWhite.setEnabled(true);
                        holder.publicsPostsDownvoteWhite.setEnabled(true);
                    },3000);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(mCtx,"Could not vote, please try again later...",Toast.LENGTH_LONG).show();
            holder.voteProgress.setVisibility(View.GONE);
            holder.publicsPostsUpvoteWhite.setVisibility(View.VISIBLE);
            holder.publicsPostsDownvoteWhite.setVisibility(View.VISIBLE);
            holder.publicsPostsUpvoteWhite.setEnabled(false);
            holder.publicsPostsDownvoteWhite.setEnabled(false);
            new Handler().postDelayed(() -> {
                holder.publicsPostsUpvoteWhite.setEnabled(true);
                holder.publicsPostsDownvoteWhite.setEnabled(true);
            },3000);
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("post_id",layout_id);
                parms.put("method",method);
                parms.put("layout", "post");
                parms.put("user_to",user_to);
                parms.put("user_id",holder.userID);
                parms.put("username",holder.deviceusername);
                return parms;
            }
        };
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBindViewHolder(@NonNull final PublicsPostViewHolder holder, int position) {
        final PublicsPost_Recycler publics = publicsPostList.get(position);
        holder.publicsPostsClantag.setText(publics.getClantag());
        holder.textViewNickname.setText(publics.getNickname());
        holder.textViewPostBody.setText(publics.getPost_content());
        holder.publicsPostsDateTime.setText(publics.getPost_date());
        holder.textViewUsername.setText(String.format("@%s", publics.getUsername()));
        holder.textViewNumPublicsPoints.setText(publics.getVotes());
        holder.num_replies.setText(publics.getReplies());
        if (publics.getOnline().equals("yes")){
            holder.online.setVisibility(View.VISIBLE);
        }else{
            holder.online.setVisibility(View.GONE);
        }
        if (publics.getVerified().equals("yes")){
            holder.verified.setVisibility(View.VISIBLE);
        }else{
            holder.verified.setVisibility(View.GONE);
        }
        String profile_pic = publics.getProfile_pic().substring(0, publics.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profile_pic)
                .into(holder.imageView);
        holder.textViewNickname.setOnClickListener(v -> {
            if (mCtx instanceof FragmentContainer) {
                FragmentProfile ldf = new FragmentProfile ();
                Bundle args = new Bundle();
                args.putString("UserId", publics.getUser_id());
                ldf.setArguments(args);
                ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }});
        if (publics.getVoted().equals("up")){
            holder.publicsPostsUpvoteWhite.setVisibility(GONE);
            holder.publicsPostsUpvoteGreen.setVisibility(View.VISIBLE);
        }else if (publics.getVoted().equals("down")){
            holder.publicsPostsDownvoteWhite.setVisibility(GONE);
            holder.publicsPostsDownvoteRed.setVisibility(View.VISIBLE);
        }
        holder.publicsPostsUpvoteWhite.setOnClickListener(v -> {
            holder.publicsPostsDownvoteRed.setVisibility(View.GONE);
            holder.publicsPostsDownvoteWhite.setVisibility(GONE);
            holder.publicsPostsUpvoteGreen.setVisibility(GONE);
            holder.publicsPostsUpvoteWhite.setVisibility(GONE);
            holder.voteProgress.setVisibility(View.VISIBLE);
            topicVote(holder,"up", publics.getPost_id(), publics.getPost_by());
        });
        holder.publicsPostsUpvoteGreen.setOnClickListener(v -> {
            holder.publicsPostsDownvoteRed.setVisibility(View.GONE);
            holder.publicsPostsDownvoteWhite.setVisibility(GONE);
            holder.publicsPostsUpvoteGreen.setVisibility(GONE);
            holder.publicsPostsUpvoteWhite.setVisibility(GONE);
            holder.voteProgress.setVisibility(View.VISIBLE);
            topicVote(holder,"remove", publics.getPost_id(), publics.getPost_by());
        });
        holder.publicsPostsDownvoteWhite.setOnClickListener(v -> {
            holder.publicsPostsDownvoteRed.setVisibility(View.GONE);
            holder.publicsPostsDownvoteWhite.setVisibility(GONE);
            holder.publicsPostsUpvoteGreen.setVisibility(GONE);
            holder.publicsPostsUpvoteWhite.setVisibility(GONE);
            holder.voteProgress.setVisibility(View.VISIBLE);
            topicVote(holder,"down", publics.getPost_id(), publics.getPost_by());
        });
        holder.publicsPostsDownvoteRed.setOnClickListener(v -> {
            holder.publicsPostsDownvoteRed.setVisibility(View.GONE);
            holder.publicsPostsDownvoteWhite.setVisibility(GONE);
            holder.publicsPostsUpvoteGreen.setVisibility(GONE);
            holder.publicsPostsUpvoteWhite.setVisibility(GONE);
            holder.voteProgress.setVisibility(View.VISIBLE);
            topicVote(holder,"remove", publics.getPost_id(), publics.getPost_by());
        });
        holder.userPublicsPostsListLayout.setOnClickListener(v -> replyLayoutClick(holder, publics.getPost_id(), publics.getId()));
        holder.reply_text.setOnClickListener(v -> replyLayoutClick(holder, publics.getPost_id(), publics.getId()));
    }

    private void replyLayoutClick(@NonNull final PublicsPostViewHolder holder, final String postid, final String topicid){
        if (holder.reply_edittext.getVisibility()==View.VISIBLE){
            holder.reply_text.setVisibility(View.VISIBLE);
            holder.reply_edittext.setVisibility(GONE);
            holder.reply_button.setEnabled(false);
            holder.replies_recycler.setVisibility(GONE);
            holder.replies_progress.setVisibility(GONE);
            holder.reply_button.setBackground(mCtx.getResources().getDrawable(R.drawable.grey_transparent_wide_blob));
        }else if (holder.reply_edittext.getVisibility()== GONE){
            holder.reply_text.setVisibility(GONE);
            holder.reply_edittext.setVisibility(View.VISIBLE);
            holder.reply_button.setEnabled(true);
            holder.replies_recycler.setVisibility(View.VISIBLE);
            holder.replies_progress.setVisibility(View.VISIBLE);
            holder.reply_button.setBackground(mCtx.getResources().getDrawable(R.drawable.details_button));
            holder.reply_button.setOnClickListener(v -> {
                String body = holder.reply_edittext.getText().toString();
                String added_by = holder.deviceusername;
                String added_by_user_id = holder.userID;
                if (!body.isEmpty()){
                    holder.reply_button.setVisibility(GONE);
                    holder.reply_button_progress.setVisibility(View.VISIBLE);
                    submitComment(body, added_by, added_by_user_id, postid, topicid, holder);
                    holder.reply_edittext.getText().clear();
                    hideKeyboardFrom(mCtx, v);
                } else {
                    Toast.makeText(mCtx, "You must enter text before submitting!", Toast.LENGTH_LONG).show();
                }
            });
            layoutManager = new LinearLayoutManager(mCtx);
            layoutManager.setStackFromEnd(true);
            holder.replies_recycler.setLayoutManager(layoutManager);
            subCommentList = new ArrayList<>();
            subCommentAdapter = new PublicsSubCommentAdapter(mCtx,subCommentList);
            holder.replies_recycler.setAdapter(subCommentAdapter);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, SUB_COMMENTS_URL+"?userid="+holder.userID+"&username="+holder.deviceusername+"&postid="+postid, response -> {
                try {
                    JSONArray profilenews = new JSONArray(response);
                    for(int i = 0; i<profilenews.length(); i++){
                        JSONObject profilenewsObject = profilenews.getJSONObject(i);

                        String online = profilenewsObject.getString("online");
                        String post_id = profilenewsObject.getString("post_id");
                        String reply = profilenewsObject.getString("post_content");
                        String post_date = profilenewsObject.getString("post_date");
                        String profile_pic = profilenewsObject.getString("profile_pic");
                        String nickname = profilenewsObject.getString("nickname");
                        String username = profilenewsObject.getString("username");
                        String userid = profilenewsObject.getString("userid");
                        String verified = profilenewsObject.getString("verified");
                        String clantag = profilenewsObject.getString("clantag");

                        Publics_SubComments_Recycler subCommentResult = new Publics_SubComments_Recycler(online,post_id,reply,post_date,profile_pic,nickname,userid,username,verified,clantag);
                        subCommentList.add(subCommentResult);
                    }
                    subCommentAdapter.notifyDataSetChanged();
                    holder.replies_progress.setVisibility(GONE);
                } catch (JSONException e) {
                    holder.replies_progress.setVisibility(GONE);
                    e.printStackTrace();
                }
            }, error -> holder.replies_progress.setVisibility(GONE));
            ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
        }

    }

    private void submitComment(final String body, final String added_by, final String userID, final String post_id, final String topic_id, @NonNull final PublicsPostViewHolder holder) {
        final String profile_pic = SharedPrefManager.getInstance(mCtx).getProfilePic();
        final String nickname = SharedPrefManager.getInstance(mCtx).getNickname();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SUB_COMMENTS_SUBMIT_URL,
                response -> {
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        if(!jsonObject.getString("error").equals("true")){
                            subCommentList.add(new Publics_SubComments_Recycler("yes",null,body,"Just Now",profile_pic,nickname,userID,added_by,"no", null));
                            holder.reply_button.setVisibility(View.VISIBLE);
                            holder.reply_button_progress.setVisibility(GONE);
                            subCommentAdapter.notifyDataSetChanged();
                        }else{
                            holder.reply_button_progress.setVisibility(GONE);
                            Toast.makeText(mCtx,"Something went wrong, please try again later...",Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mCtx, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    holder.reply_button_progress.setVisibility(GONE);
                    Toast.makeText(mCtx,"Network error, please try again later...",Toast.LENGTH_LONG).show();
                }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("body",body);
                parms.put("added_by",added_by);
                parms.put("post_id",post_id);
                parms.put("topic_id",topic_id);
                parms.put("user_id",userID);
                return parms;
            }
        };
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected class LoadingVH extends PublicsPostViewHolder {
        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return publicsPostList.size();
    }
    class PublicsPostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,publicsPostsUpvoteWhite, publicsPostsDownvoteWhite, publicsPostsUpvoteGreen, publicsPostsDownvoteRed, reply_button, online, verified;
        TextView textViewNickname, textViewUsername, textViewPostBody, textViewNumPublicsPoints, publicsPostsDateTime, num_replies,publicsPostsClantag;
        ProgressBar voteProgress;
        String userID, deviceusername;
        LinearLayout reply_layout, reply_text;
        EditText reply_edittext;
        RecyclerView replies_recycler;
        MaterialRippleLayout userPublicsPostsListLayout;
        ProgressBar replies_progress, reply_button_progress;
        public PublicsPostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.publicsPostsProfile_image);
            textViewNickname = itemView.findViewById(R.id.publicsPostsNickname);
            textViewUsername = itemView.findViewById(R.id.publicsPostsUsername);
            textViewPostBody = itemView.findViewById(R.id.publicsPostBody);
            textViewNumPublicsPoints = itemView.findViewById(R.id.textViewNumPublicsPoints);
            publicsPostsDateTime = itemView.findViewById(R.id.publicsPostsDateTime);
            publicsPostsUpvoteWhite = itemView.findViewById(R.id.publicsPostsUpvoteWhiteBottom);
            publicsPostsDownvoteWhite = itemView.findViewById(R.id.publicsPostsDownvoteWhiteBottom);
            publicsPostsUpvoteGreen = itemView.findViewById(R.id.publicsPostsUpvoteGreen);
            publicsPostsDownvoteRed = itemView.findViewById(R.id.publicsPostsDownvoteRed);
            voteProgress = itemView.findViewById(R.id.voteProgress);
            userID = SharedPrefManager.getInstance(mCtx).getUserID();
            deviceusername = SharedPrefManager.getInstance(mCtx).getUsername();
            reply_layout = itemView.findViewById(R.id.reply_layout);
            reply_edittext = itemView.findViewById(R.id.reply_edittext);
            reply_text = itemView.findViewById(R.id.reply_text);
            reply_button = itemView.findViewById(R.id.reply_button);
            replies_recycler = itemView.findViewById(R.id.replies_recycler);
            userPublicsPostsListLayout = itemView.findViewById(R.id.userPublicsPostsListLayout);
            replies_progress = itemView.findViewById(R.id.replies_progress);
            reply_button_progress = itemView.findViewById(R.id.reply_button_progress);
            num_replies = itemView.findViewById(R.id.num_replies);
            online = itemView.findViewById(R.id.online);
            verified = itemView.findViewById(R.id.verified);
            publicsPostsClantag = itemView.findViewById(R.id.publicsPostsClantag);
        }
    }

}
