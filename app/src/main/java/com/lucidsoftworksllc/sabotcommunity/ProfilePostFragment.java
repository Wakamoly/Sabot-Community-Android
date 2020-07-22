package com.lucidsoftworksllc.sabotcommunity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import static android.view.View.GONE;

public class ProfilePostFragment extends Fragment {
    private static final String ProfileComment_URL = Constants.ROOT_URL+"profilePostComments_api.php";
    private static final String CommentPost_URL = Constants.ROOT_URL+"profilePostCommentSubmit_api.php";
    private static final String LIKE_URL = Constants.ROOT_URL+"post_like.php";
    private static final String ProfilePost_URL = Constants.ROOT_URL+"profilePost.php";
    private static final String POST_DELETE = Constants.ROOT_URL+"profile_post_action.php/post_delete";

    private ProgressBar mProgressBar, likeProgress, urlProgress;
    private String thisUserID, thisUsername;
    private TextView tvEdited,urlTitle, urlDesc, postToUser, profilePostPostsNickname_top, profilePostPostsUsername_top, profilePostPostBody_top, profilePostPostsDateTime_top, profileCommentsLikes_top, textViewNumComments;
    private ImageView profilePostImage, likeView, likedView, platformType2, urlImage,profileTopicMenu;
    private CircleImageView profilePostPostsProfile_image_top, onlineView, verifiedView;
    private LinearLayout commentLayout, urlPreview, urlBits, likesLayout;
    private MaterialRippleLayout submitComment;
    private EditText commentEditText;
    private Context mCtx;
    private RecyclerView profilePostView;
    private PostCommentsAdapter adapter;
    private SwipeRefreshLayout profilePostsSwipe;
    private List<PostComment_Recycler> profilePostPostList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View profilePostRootView = inflater.inflate(R.layout.fragment_profile_post, null);

        urlBits = profilePostRootView.findViewById(R.id.urlBits);
        urlDesc = profilePostRootView.findViewById(R.id.urlDesc);
        urlTitle = profilePostRootView.findViewById(R.id.urlTitle);
        urlPreview = profilePostRootView.findViewById(R.id.urlPreview);
        urlImage = profilePostRootView.findViewById(R.id.urlImage);
        urlProgress = profilePostRootView.findViewById(R.id.urlProgress);
        onlineView = profilePostRootView.findViewById(R.id.online);
        verifiedView = profilePostRootView.findViewById(R.id.verified);
        likeView = profilePostRootView.findViewById(R.id.like);
        likedView = profilePostRootView.findViewById(R.id.liked);
        likeProgress = profilePostRootView.findViewById(R.id.likeProgress);
        mProgressBar = profilePostRootView.findViewById(R.id.postProgressBar);
        profilePostImage = profilePostRootView.findViewById(R.id.profilePostImage);
        commentLayout = profilePostRootView.findViewById(R.id.commentLayout);
        submitComment = profilePostRootView.findViewById(R.id.submitComment);
        commentEditText = profilePostRootView.findViewById(R.id.commentEditText);
        textViewNumComments = profilePostRootView.findViewById(R.id.textViewNumComments);
        postToUser = profilePostRootView.findViewById(R.id.postToUser);
        profilePostPostsNickname_top = profilePostRootView.findViewById(R.id.profileCommentsNickname_top);
        profilePostPostsUsername_top = profilePostRootView.findViewById(R.id.profileCommentsUsername_top);
        profilePostPostBody_top = profilePostRootView.findViewById(R.id.profileCommentsBody_top);
        profilePostPostsDateTime_top = profilePostRootView.findViewById(R.id.profileCommentsDateTime_top);
        profilePostPostsProfile_image_top = profilePostRootView.findViewById(R.id.profileCommentsProfile_image_top);
        profileCommentsLikes_top = profilePostRootView.findViewById(R.id.profileCommentsLikes_top);
        platformType2 = profilePostRootView.findViewById(R.id.platformType2);
        likesLayout = profilePostRootView.findViewById(R.id.likesLayout);
        profileTopicMenu = profilePostRootView.findViewById(R.id.profileTopicMenu);
        tvEdited = profilePostRootView.findViewById(R.id.tvEdited);
        mCtx = getActivity();

        thisUserID = SharedPrefManager.getInstance(mCtx).getUserID();
        thisUsername = SharedPrefManager.getInstance(mCtx).getUsername();
        profilePostPostList = new ArrayList<>();
        profilePostView = profilePostRootView.findViewById(R.id.profileCommentsPostRecyclerView);
        profilePostView.setHasFixedSize(true);
        profilePostView.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    submitComment.setEnabled(false);
                    submitComment.setVisibility(View.GONE);
                } else {
                    submitComment.setEnabled(true);
                    submitComment.setVisibility(View.VISIBLE);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
        });

        if(!TextUtils.isEmpty(commentEditText.getText())){
            submitComment.setVisibility(View.VISIBLE);
        }

        loadprofilePostTopicTop();
        loadprofilePostTopic();
        profilePostsSwipe = profilePostRootView.findViewById(R.id.profilePostsSwipe);
        profilePostsSwipe.setOnRefreshListener(() -> {
            Fragment currentFragment = ((FragmentActivity)mCtx).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof ProfilePostFragment) {
                FragmentTransaction fragTransaction =   ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
            profilePostsSwipe.setRefreshing(false);
        });
        profilePostPostsNickname_top.requestFocus();
        return profilePostRootView;
    }

    private void submitComment(final String body, final String added_by, final String user_to, final String image, final String post_id) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, CommentPost_URL, response -> {
            Fragment currentFragment = ((FragmentActivity)mCtx).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof ProfilePostFragment) {
                FragmentTransaction fragTransaction =   ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
            profilePostsSwipe.setRefreshing(false);
            profilePostPostsNickname_top.requestFocus();
        }, error -> {
            mProgressBar.setVisibility(GONE);
            Toast.makeText(mCtx,"Error on Response, please try again later...",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("body",body);
                parms.put("added_by",added_by);
                parms.put("user_to",user_to);
                parms.put("image",image);
                parms.put("post_id",post_id);
                parms.put("user_id",thisUserID);
                return parms;
            }
        };
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    private void loadprofilePostTopicTop() {
        assert getArguments() != null;
        final String post_id = getArguments().getString("id");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ProfilePost_URL+"?postID="+post_id+"&username="+thisUsername, response -> {
            try {
                JSONArray profilepost = new JSONArray(response);
                JSONObject profilepostObject = profilepost.getJSONObject(0);

                int id = profilepostObject.getInt("id");
                String type = profilepostObject.getString("type");
                final String likes = profilepostObject.getString("likes");
                String body = profilepostObject.getString("body");
                //String added_by = profilepostObject.getString("added_by");
                String user_to = profilepostObject.getString("user_to");
                String date_added = profilepostObject.getString("date_added");
                //String user_closed = profilepostObject.getString("user_closed");
                //String deleted = profilepostObject.getString("deleted");
                final String image = profilepostObject.getString("image");
                final String user_id = profilepostObject.getString("user_id");
                String profile_pic = profilepostObject.getString("profile_pic");
                String nickname = profilepostObject.getString("nickname");
                final String username = profilepostObject.getString("username");
                String commentcount = profilepostObject.getString("commentcount");
                String likedbyuserYes = profilepostObject.getString("likedbyuseryes");
                String online = profilepostObject.getString("online");
                String verified = profilepostObject.getString("verified");
                final String userto_id = profilepostObject.getString("userto_id");
                String form = profilepostObject.getString("form");
                String isEdited = profilepostObject.getString("edited");

                if (isEdited.equals("yes")){
                    tvEdited.setVisibility(View.VISIBLE);
                }else{
                    tvEdited.setVisibility(GONE);
                }
                profileTopicMenu.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(mCtx, v);
                    MenuInflater inflater = popup.getMenuInflater();
                    if (username.equals(thisUsername)){
                        inflater.inflate(R.menu.profile_post_owner, popup.getMenu());
                        popup.setOnMenuItemClickListener(item -> {
                            if (item.getItemId() == R.id.menuDelete) {
                                deletePost(post_id);
                            }
                            if (item.getItemId() == R.id.menuEdit) {
                                ProfilePostEditFragment ldf = new ProfilePostEditFragment();
                                Bundle args = new Bundle();
                                args.putString("id", Integer.toString(id));
                                ldf.setArguments(args);
                                ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            }
                            if (item.getItemId() == R.id.menuReport) {
                                ReportFragment ldf = new ReportFragment();
                                Bundle args = new Bundle();
                                args.putString("context", body);
                                args.putString("type", "post");
                                args.putString("id", Integer.toString(id));
                                ldf.setArguments(args);
                                ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            }
                            return true;
                        });
                    }else{
                        inflater.inflate(R.menu.profile_post_nonowner, popup.getMenu());
                        popup.setOnMenuItemClickListener(item -> {
                            if (item.getItemId() == R.id.menuReport) {
                                ReportFragment ldf = new ReportFragment();
                                Bundle args = new Bundle();
                                args.putString("context", body);
                                args.putString("type", "post");
                                args.putString("id", Integer.toString(id));
                                ldf.setArguments(args);
                                ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            }
                            return true;
                        });
                    }
                    popup.show();
                });
                if (!user_to.equals("none")){
                    postToUser.setVisibility(View.VISIBLE);
                    switch (form) {
                        case "user":
                            postToUser.setText(String.format("to @%s", user_to));
                            postToUser.setOnClickListener(v -> {
                                FragmentProfile ldf = new FragmentProfile();
                                Bundle args = new Bundle();
                                args.putString("UserId", userto_id);
                                ldf.setArguments(args);
                                ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            });
                            break;
                        case "clan":
                            postToUser.setText(String.format("to [%s]", user_to));
                            postToUser.setTextColor(ContextCompat.getColor(mCtx,R.color.pin));
                            postToUser.setOnClickListener(v -> {
                                ClanFragment ldf = new ClanFragment();
                                Bundle args = new Bundle();
                                args.putString("ClanId", userto_id);
                                ldf.setArguments(args);
                                ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            });
                            break;
                        case "event":
                            break;
                    }
                }

                switch (type) {
                    case "Xbox":
                        platformType2.setImageResource(R.drawable.icons8_xbox_50);
                        platformType2.setVisibility(View.VISIBLE);
                        break;
                    case "PlayStation":
                        platformType2.setImageResource(R.drawable.icons8_playstation_50);
                        platformType2.setVisibility(View.VISIBLE);
                        break;
                    case "Steam":
                        platformType2.setImageResource(R.drawable.icons8_steam_48);
                        platformType2.setVisibility(View.VISIBLE);
                        break;
                    case "PC":
                        platformType2.setImageResource(R.drawable.icons8_workstation_48);
                        platformType2.setVisibility(View.VISIBLE);
                        break;
                }

                String [] bodybits = body.split("\\s+");
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
                        urlPreview.setVisibility(View.VISIBLE);
                        urlImage.setOnClickListener(v -> {
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
                                    .into(urlImage);
                            if (title[0] !=null) {
                                urlTitle.setText(title[0]);
                            }else{
                                urlTitle.setText(mCtx.getString(R.string.no_content));
                            }
                            if (desc[0] !=null) {
                                urlDesc.setText(desc[0]);
                            }
                            urlProgress.setVisibility(View.GONE);
                            urlBits.setVisibility(View.VISIBLE);
                        }, 5000);
                        break;
                    }
                }

                profilePostPostsNickname_top.setText(nickname);
                profilePostPostsUsername_top.setText(String.format("@%s", username));
                profilePostPostBody_top.setText(body);
                profilePostPostsDateTime_top.setText(date_added);
                profileCommentsLikes_top.setText(likes);
                textViewNumComments.setText(commentcount);
                likesLayout.setOnClickListener(v -> {
                    Fragment asf = new UserListFragment();
                    Bundle args = new Bundle();
                    args.putString("query", "post");
                    args.putString("queryID", String.valueOf(id));
                    asf.setArguments(args);
                    FragmentTransaction fragmentTransaction = ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                });
                if (online.equals("yes")) {
                    onlineView.setVisibility(View.VISIBLE);
                }
                if (verified.equals("yes")) {
                    verifiedView.setVisibility(View.VISIBLE);
                }
                if(likedbyuserYes.equals("yes")){
                    likeView.setVisibility(View.GONE);
                    likedView.setVisibility(View.VISIBLE);
                }
                profilePostPostsNickname_top.setOnClickListener(v -> {
                    FragmentProfile ldf = new FragmentProfile();
                    Bundle args = new Bundle();
                    args.putString("UserId", user_id);
                    ldf.setArguments(args);
                    ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                });

                likeView.setOnClickListener(v -> {
                    likeView.setVisibility(View.GONE);
                    likeProgress.setVisibility(View.VISIBLE);
                    StringRequest stringRequest1 =new StringRequest(Request.Method.POST, LIKE_URL, response1 -> {
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response1);
                            if (!obj.getBoolean("error")) {
                                String newValue = Integer.toString(Integer.parseInt(profileCommentsLikes_top.getText().toString())+1);
                                profileCommentsLikes_top.setText(newValue);
                                likeProgress.setVisibility(View.GONE);
                                likedView.setVisibility(View.VISIBLE);
                                likedView.setEnabled(false);
                                new Handler().postDelayed(() -> likedView.setEnabled(true),3500);
                            } else {
                                Toast.makeText(mCtx,obj.getString("message"),Toast.LENGTH_LONG).show();
                                likeProgress.setVisibility(View.GONE);
                                likeView.setVisibility(View.VISIBLE);
                                likeView.setEnabled(false);
                                new Handler().postDelayed(() -> likeView.setEnabled(true),3000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Toast.makeText(mCtx,"Could not like, please try again later...",Toast.LENGTH_LONG).show();
                        likeProgress.setVisibility(View.GONE);
                        likeView.setVisibility(View.VISIBLE);
                        likeView.setEnabled(false);
                        new Handler().postDelayed(() -> likeView.setEnabled(true),3000);
                    }){
                        @Override
                        protected Map<String, String> getParams()  {
                            Map<String,String> parms= new HashMap<>();
                            parms.put("post_id", Objects.requireNonNull(post_id));
                            parms.put("method","like");
                            parms.put("user_to",username);
                            parms.put("user_id",thisUserID);
                            parms.put("username",thisUsername);
                            return parms;
                        }
                    };
                    ((FragmentContainer)mCtx).addToRequestQueue(stringRequest1);
                });
                likedView.setOnClickListener(v -> {
                    likedView.setVisibility(View.GONE);
                    likeProgress.setVisibility(View.VISIBLE);
                    StringRequest stringRequest1 =new StringRequest(Request.Method.POST, LIKE_URL, response12 -> {
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response12);
                            if (!obj.getBoolean("error")) {
                                String newValue = Integer.toString(Integer.parseInt(profileCommentsLikes_top.getText().toString())-1);
                                profileCommentsLikes_top.setText(newValue);
                                likeProgress.setVisibility(View.GONE);
                                likeView.setVisibility(View.VISIBLE);
                                likeView.setEnabled(false);
                                new Handler().postDelayed(() -> likeView.setEnabled(true),3500);
                            } else {
                                Toast.makeText(mCtx,obj.getString("message"),Toast.LENGTH_LONG).show();
                                likeProgress.setVisibility(View.GONE);
                                likedView.setVisibility(View.VISIBLE);
                                likedView.setEnabled(false);
                                new Handler().postDelayed(() -> likedView.setEnabled(true),3000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Toast.makeText(mCtx,"Could not remove like, please try again later...",Toast.LENGTH_LONG).show();
                        likeProgress.setVisibility(View.GONE);
                        likedView.setVisibility(View.VISIBLE);
                        likedView.setEnabled(false);
                        new Handler().postDelayed(() -> likeView.setEnabled(true),3000);
                    }){
                        @Override
                        protected Map<String, String> getParams()  {
                            Map<String,String> parms= new HashMap<>();
                            assert post_id != null;
                            parms.put("post_id", post_id);
                            parms.put("method","unlike");
                            parms.put("user_to",username);
                            parms.put("user_id",thisUserID);
                            parms.put("username",thisUsername);
                            return parms;
                        }
                    };
                    ((FragmentContainer)mCtx).addToRequestQueue(stringRequest1);
                });
                submitComment.setOnClickListener(view -> {
                    String body1 = commentEditText.getText().toString();
                    String added_by1 = SharedPrefManager.getInstance(mCtx).getUsername();
                    String image1 = "";
                    if(!body1.isEmpty()) {
                        commentLayout.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        submitComment(body1, added_by1, username, image1, post_id);
                        commentEditText.getText().clear();
                        hideKeyboardFrom(mCtx, view);
                    } else {
                        Toast.makeText(mCtx,"You must enter text before submitting!",Toast.LENGTH_LONG).show();
                    }
                });
                String profile_pic2 = profile_pic.substring(0, profile_pic.length() - 4)+"_r.JPG";
                Glide.with(mCtx)
                        .load(Constants.BASE_URL + profile_pic2)
                        .into(profilePostPostsProfile_image_top);
                if (!image.isEmpty()) {
                    Glide.with(mCtx)
                            .load(Constants.BASE_URL + image).override(1000)
                            .into(profilePostImage);
                    profilePostImage.setOnClickListener(view -> {
                        Fragment asf = new PhotoViewFragment();
                        Bundle args = new Bundle();
                        args.putString("image", image);
                        asf.setArguments(args);
                        FragmentTransaction fragmentTransaction = ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, asf);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    });
                }
                mProgressBar.setVisibility(View.GONE);
                commentLayout.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mCtx, "Error on Response: Dashboard Feed", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    private void loadprofilePostTopic(){
        assert getArguments() != null;
        String profilePostID = getArguments().getString("id");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ProfileComment_URL+"?userid="+thisUserID+"&username="+thisUsername+"&postid="+profilePostID, response -> {
            try {
                JSONArray profilePost = new JSONArray(response);
                for(int i = 0; i<profilePost.length(); i++){
                    JSONObject profilePostObject = profilePost.getJSONObject(i);
                    final String id = profilePostObject.getString("id");
                    String body = profilePostObject.getString("body");
                    String posted_by = profilePostObject.getString("posted_by");
                    String posted_to = profilePostObject.getString("posted_to");
                    String time = profilePostObject.getString("time");
                    String post_id = profilePostObject.getString("post_id");
                    String likes = profilePostObject.getString("likes");
                    String liked_by = profilePostObject.getString("liked_by");
                    String user_id = profilePostObject.getString("userid");
                    String profile_pic = profilePostObject.getString("profile_pic");
                    String nickname = profilePostObject.getString("nickname");
                    String username = profilePostObject.getString("username");
                    String likedbyuser = profilePostObject.getString("likedbyuseryes");
                    String online = profilePostObject.getString("online");
                    String verified = profilePostObject.getString("verified");
                    String edited = profilePostObject.getString("edited");

                    PostComment_Recycler profilePostPostResult = new PostComment_Recycler(id, body, posted_by, posted_to, time, post_id, likes, liked_by, user_id, profile_pic, nickname, username, likedbyuser, online, verified, edited);
                    profilePostPostList.add(profilePostPostResult);
                }
                adapter = new PostCommentsAdapter(mCtx, profilePostPostList);
                profilePostView.setAdapter(adapter);
                profilePostsSwipe.setRefreshing(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    private void deletePost(String postID){
        new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.green)
                .setIcon(R.drawable.ic_error)
                .setTitle(R.string.delete_post_string)
                .setMessage(R.string.confirm)
                .setPositiveButton(R.string.yes, v -> {
                    StringRequest stringRequest=new StringRequest(Request.Method.POST, POST_DELETE, response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("error").equals("false")){
                                Toast.makeText(mCtx,"Post deleted!", Toast.LENGTH_LONG).show();
                                requireActivity().getSupportFragmentManager().popBackStackImmediate();
                            }else{
                                Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Toast.makeText(mCtx,"Network error, please try again later...",Toast.LENGTH_LONG).show()){
                        @Override
                        protected Map<String, String> getParams()  {
                            Map<String,String> parms= new HashMap<>();
                            parms.put("postid",postID);
                            parms.put("username",thisUsername);
                            parms.put("userid",thisUserID);
                            return parms;
                        }
                    };
                    ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
