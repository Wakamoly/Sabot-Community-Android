package com.lucidsoftworksllc.sabotcommunity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static com.lucidsoftworksllc.sabotcommunity.R.drawable.details_button;
import static com.lucidsoftworksllc.sabotcommunity.R.drawable.grey_button;
import static com.lucidsoftworksllc.sabotcommunity.R.drawable.red_button;

public class PublicsTopicFragment extends Fragment {

    private static final String PublicsTopicTop_URL = Constants.ROOT_URL+"publicsTopicTop_api.php";
    private static final String PublicsTopic_URL = Constants.ROOT_URL+"publicsTopic_api.php";
    private static final String LEAVE_TOPIC = Constants.ROOT_URL+"publics_leave_topic.php";
    private static final String JOIN_TOPIC = Constants.ROOT_URL+"publics_join_topic.php";
    private static final String JOIN_TOPIC_NON_CONNECTION = Constants.ROOT_URL+"publics_join_topic_non_connection.php";
    private static final String JOIN_TOPIC_NEW_CONNECTION = Constants.ROOT_URL+"publics_join_topic_new_connection.php";
    private static final String IS_CONNECTED = Constants.ROOT_URL+"publics_join_topic_is_connected.php";
    private static final String GET_REQUESTS = Constants.ROOT_URL+"publics_get_requests.php";
    private static final String PUBLICS_POST_SUBMIT = Constants.ROOT_URL+"publics_comment_submit.php";
    private static final String PUBLICS_DELETE = Constants.ROOT_URL+"publics_topic_delete.php";
    private static final String VOTE_URL = Constants.ROOT_URL+"publics_topic_vote.php";
    private ProgressBar mProgressBar, requestProgressBar, voteProgress;
    private String userID, publicsID, deviceusername;
    private TextView topicTitle,clantag,publicsPostTitle, publicsPostsNickname_top, publicsPostsUsername_top, publicsPostBody_top, publicsPostsDateTime_top, tvWhen, numPlayersNeeded, numPlayersAdded, textViewNumPublicsPoints_top;
    private CircleImageView publicsPostsProfile_image_top, online, verified;
    private LinearLayout whenLayout, topicLayout, postDeletedScreenContent, nicknameLayout,playingNowLayout;
    private Context mContext;
    private MaterialRippleLayout submitComment;
    private ImageView publicsImageView, platformType, publicsPostsUpvoteWhite_top, publicsPostsDownvoteWhite, publicsPostsUpvoteGreen_top, publicsPostsDownvoteRed_top, publicsTopicMenu;
    private Button noRequests,joinRequests,requestToJoin,requestedToJoin,topicJoined,deleteTopic,topicMembers,topicEdit,newComment;
    private EditText commentEditText;
    private RelativeLayout newCommentLayout;
    private RecyclerView publicsView;
    private PublicsPostAdapter adapter;
    private SwipeRefreshLayout publicsSwipe;
    private List<PublicsPost_Recycler> publicsPostList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View publicsRootView = inflater.inflate(R.layout.fragment_publics_topic, null);
        mProgressBar = publicsRootView.findViewById(R.id.topicProgressBar);
        publicsPostTitle = publicsRootView.findViewById(R.id.publicsPostTitle);
        publicsPostsNickname_top = publicsRootView.findViewById(R.id.publicsPostsNickname_top);
        publicsPostsUsername_top = publicsRootView.findViewById(R.id.publicsPostsUsername_top);
        publicsPostBody_top = publicsRootView.findViewById(R.id.publicsPostBody_top);
        publicsPostsDateTime_top = publicsRootView.findViewById(R.id.publicsPostsDateTime_top);
        publicsPostsProfile_image_top = publicsRootView.findViewById(R.id.publicsPostsProfile_image_top);
        numPlayersNeeded = publicsRootView.findViewById(R.id.numPlayersNeeded);
        numPlayersAdded = publicsRootView.findViewById(R.id.numPlayersAdded);
        topicLayout = publicsRootView.findViewById(R.id.topicLayout);
        tvWhen = publicsRootView.findViewById(R.id.tvWhen);
        whenLayout = publicsRootView.findViewById(R.id.whenLayout);
        platformType = publicsRootView.findViewById(R.id.platformType);
        noRequests = publicsRootView.findViewById(R.id.noRequests);
        joinRequests = publicsRootView.findViewById(R.id.joinRequests);
        requestToJoin = publicsRootView.findViewById(R.id.requestToJoin);
        requestedToJoin = publicsRootView.findViewById(R.id.requestedToJoin);
        topicJoined = publicsRootView.findViewById(R.id.topicJoined);
        textViewNumPublicsPoints_top = publicsRootView.findViewById(R.id.textViewNumPublicsPoints_top);
        requestProgressBar = publicsRootView.findViewById(R.id.requestProgressBar);
        submitComment = publicsRootView.findViewById(R.id.submitComment);
        commentEditText = publicsRootView.findViewById(R.id.commentEditText);
        publicsPostsDownvoteRed_top = publicsRootView.findViewById(R.id.publicsPostsDownvoteRed_top);
        publicsPostsUpvoteGreen_top = publicsRootView.findViewById(R.id.publicsPostsUpvoteGreen_top);
        publicsPostsDownvoteWhite = publicsRootView.findViewById(R.id.publicsPostsDownvoteWhite);
        publicsPostsUpvoteWhite_top = publicsRootView.findViewById(R.id.publicsPostsUpvoteWhite_top);
        voteProgress = publicsRootView.findViewById(R.id.voteProgress);
        deleteTopic = publicsRootView.findViewById(R.id.deleteTopic);
        postDeletedScreenContent = publicsRootView.findViewById(R.id.postDeletedScreenContent);
        newCommentLayout = publicsRootView.findViewById(R.id.newCommentLayout);
        publicsTopicMenu = publicsRootView.findViewById(R.id.publicsTopicMenu);
        publicsImageView = publicsRootView.findViewById(R.id.publicsImageView);
        nicknameLayout = publicsRootView.findViewById(R.id.nicknameLayout);
        clantag = publicsRootView.findViewById(R.id.clantag);
        topicMembers = publicsRootView.findViewById(R.id.topicMembers);
        online = publicsRootView.findViewById(R.id.online);
        verified = publicsRootView.findViewById(R.id.verified);
        topicTitle = publicsRootView.findViewById(R.id.topicTitle);
        topicEdit = publicsRootView.findViewById(R.id.topicEdit);
        newComment = publicsRootView.findViewById(R.id.newComment);
        playingNowLayout = publicsRootView.findViewById(R.id.playingNowLayout);
        mContext = getActivity();
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        deviceusername = SharedPrefManager.getInstance(mContext).getUsername();
        assert getArguments() != null;
        publicsID = getArguments().getString("PublicsId");
        publicsPostList = new ArrayList<>();
        publicsView = publicsRootView.findViewById(R.id.recyclerPublicsTopic);
        publicsView.setHasFixedSize(true);
        publicsView.setLayoutManager(new LinearLayoutManager(mContext));
        loadPublicsTopicTop();
        loadPublicsTopic();
        publicsSwipe = publicsRootView.findViewById(R.id.publicsPostsSwipe);
        publicsSwipe.setOnRefreshListener(() -> {
            Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof PublicsTopicFragment) {
                FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
            publicsSwipe.setRefreshing(false);
        });
        newComment.setOnClickListener(v -> {
            newCommentLayout.setVisibility(View.VISIBLE);
            submitComment.setVisibility(View.VISIBLE);
            commentEditText.requestFocus();
            if (commentEditText.hasFocus()) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
        if(!TextUtils.isEmpty(commentEditText.getText())){
            submitComment.setVisibility(View.VISIBLE);
        }
        publicsPostTitle.requestFocus();
        return publicsRootView;
    }

    private void loadPublicsTopicTop(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, PublicsTopicTop_URL+"?userid="+userID+"&username="+deviceusername+"&topicid="+publicsID, response -> {
            try {
                JSONArray publicstop = new JSONArray(response);
                JSONObject publicsObject = publicstop.getJSONObject(0);
                String deleted = publicsObject.getString("deleted");
                if (deleted.equals("yes")) {
                    postDeletedScreenContent.setVisibility(View.VISIBLE);
                } else {
                    final String post_id = publicsObject.getString("post_id");
                    String post_topic = publicsObject.getString("post_topic");
                    String post_content = publicsObject.getString("post_content");
                    String post_date = publicsObject.getString("post_date");
                    final String user_id = publicsObject.getString("user_id");
                    String profile_pic = publicsObject.getString("profile_pic");
                    String nickname = publicsObject.getString("nickname");
                    final String username = publicsObject.getString("username");
                    String num_players = publicsObject.getString("num_players");
                    int num_added = publicsObject.getInt("num_added");
                    String event_date = publicsObject.getString("event_date");
                    String type = publicsObject.getString("type");
                    String votes = publicsObject.getString("votes");
                    String accepted_array = publicsObject.getString("accepted_array");
                    int requests = publicsObject.getInt("requests");
                    String vote = publicsObject.getString("vote");
                    String backimage = publicsObject.getString("back_image");
                    String clan_tag = publicsObject.getString("clantag");
                    String isOnline = publicsObject.getString("online");
                    String isVerified = publicsObject.getString("verified");
                    String gameName = publicsObject.getString("game_name");
                    String cat_id = publicsObject.getString("cat_id");
                    String playing_now = publicsObject.getString("playing_now");

                    clantag.setText(clan_tag);
                    topicTitle.setText(post_topic);

                    if (playing_now.equals("yes")){
                        playingNowLayout.setVisibility(View.VISIBLE);
                    }else{
                        playingNowLayout.setVisibility(GONE);
                    }

                    if (isOnline.equals("yes")) {
                        online.setVisibility(View.VISIBLE);
                    }else{
                        online.setVisibility(View.GONE);
                    }
                    if (isVerified.equals("yes")) {
                        verified.setVisibility(View.VISIBLE);
                    }else{
                        verified.setVisibility(View.GONE);
                    }

                    if (vote.equals("up")){
                        publicsPostsUpvoteWhite_top.setVisibility(GONE);
                        publicsPostsUpvoteGreen_top.setVisibility(View.VISIBLE);
                    }else if (vote.equals("down")){
                        publicsPostsDownvoteWhite.setVisibility(GONE);
                        publicsPostsDownvoteRed_top.setVisibility(View.VISIBLE);
                    }

                    String[] accepted = accepted_array.split(",");
                    //if user is topic owner
                    if (username.equals(deviceusername)) {
                        topicEdit.setVisibility(View.VISIBLE);
                        topicEdit.setOnClickListener(v -> {
                            //Put the value
                            EditPublicsTopic ldf = new EditPublicsTopic();
                            Bundle args = new Bundle();
                            args.putString("topic_id", post_id);
                            args.putString("gamename", gameName);
                            args.putString("gameimage", backimage);
                            args.putString("content", post_content);
                            args.putString("title", post_topic);
                            args.putString("num_players", num_players);
                            args.putString("gameid", cat_id);
                            ldf.setArguments(args);
                            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                        });
                        deleteTopic.setVisibility(View.VISIBLE);
                        deleteTopic.setOnClickListener(v -> {
                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        if (mContext instanceof FragmentContainer) {
                                            deleteTopic(post_id);
                                        }
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
                            builder.setMessage("Delete Publics?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        });
                        requestProgressBar.setVisibility(GONE);
                        if (requests > 0) {
                            joinRequests.setVisibility(View.VISIBLE);
                            joinRequests.setOnClickListener(v -> {
                                //Put the value
                                TopicManagePlayers ldf = new TopicManagePlayers();
                                Bundle args = new Bundle();
                                args.putString("topic_id", post_id);
                                args.putString("permission", "admin");
                                ldf.setArguments(args);
                                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            });
                        } else {
                            noRequests.setVisibility(View.VISIBLE);
                        }
                    }
                    //if user join request accepted
                    else if (Arrays.asList(accepted).contains(deviceusername)) {
                        requestProgressBar.setVisibility(GONE);
                        if (num_added > 0){
                            topicMembers.setVisibility(View.VISIBLE);
                            topicMembers.setOnClickListener(v -> {
                                TopicManagePlayers ldf = new TopicManagePlayers();
                                Bundle args = new Bundle();
                                args.putString("topic_id", post_id);
                                args.putString("permission", "user");
                                ldf.setArguments(args);
                                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            });
                        }
                        topicJoined.setVisibility(View.VISIBLE);
                        topicJoined.setOnClickListener(v -> {
                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        if (mContext instanceof FragmentContainer) {
                                            topicJoined.setVisibility(GONE);
                                            requestProgressBar.setVisibility(View.VISIBLE);
                                            leaveTopic(post_id);
                                        }
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
                            builder.setMessage(R.string.leave_publics_topic).setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        });
                    } else {
                        isJoinRequested(post_id, event_date);
                        if (num_added > 0){
                            topicMembers.setVisibility(View.VISIBLE);
                            topicMembers.setOnClickListener(v -> {
                                TopicManagePlayers ldf = new TopicManagePlayers();
                                Bundle args = new Bundle();
                                args.putString("topic_id", post_id);
                                args.putString("permission", "user");
                                ldf.setArguments(args);
                                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            });
                        }
                    }

                    if (event_date.equals("now")) {
                        whenLayout.setBackgroundResource(details_button);
                    } else if (event_date.equals("ended")) {
                        whenLayout.setBackgroundResource(red_button);
                    }

                    textViewNumPublicsPoints_top.setText(votes);

                    nicknameLayout.setOnClickListener(v -> {
                        //Put the value
                        FragmentProfile ldf = new FragmentProfile();
                        Bundle args = new Bundle();
                        args.putString("UserId", user_id);
                        ldf.setArguments(args);
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                    });

                    submitComment.setOnClickListener(view -> {
                        String body = commentEditText.getText().toString();
                        String added_by = SharedPrefManager.getInstance(mContext).getUsername();
                        String image = "";
                        if (!body.isEmpty()) {
                            topicLayout.setVisibility(View.GONE);
                            newCommentLayout.setVisibility(GONE);
                            submitComment.setVisibility(GONE);
                            mProgressBar.setVisibility(View.VISIBLE);
                            submitComment(body, added_by, username, image, post_id);
                            commentEditText.getText().clear();
                            hideKeyboardFrom(mContext, view);
                        } else {
                            Toast.makeText(mContext, "You must enter text before submitting!", Toast.LENGTH_LONG).show();
                        }
                    });

                    switch (type) {
                        case "Xbox":
                            platformType.setImageResource(R.drawable.icons8_xbox_50);
                            platformType.setVisibility(View.VISIBLE);
                            break;
                        case "PlayStation":
                            platformType.setImageResource(R.drawable.icons8_playstation_50);
                            platformType.setVisibility(View.VISIBLE);
                            break;
                        case "Steam":
                            platformType.setImageResource(R.drawable.icons8_steam_48);
                            platformType.setVisibility(View.VISIBLE);
                            break;
                        case "PC":
                            platformType.setImageResource(R.drawable.icons8_workstation_48);
                            platformType.setVisibility(View.VISIBLE);
                            break;
                        case "Mobile":
                            platformType.setImageResource(R.drawable.icons8_mobile_48);
                            platformType.setVisibility(View.VISIBLE);
                            break;
                        case "Switch":
                            platformType.setImageResource(R.drawable.icons8_nintendo_switch_48);
                            platformType.setVisibility(View.VISIBLE);
                            break;
                        case "Cross-Platform":
                            platformType.setImageResource(R.drawable.icons8_collect_40);
                            platformType.setVisibility(View.VISIBLE);
                            break;
                        default:
                            platformType.setImageResource(R.drawable.icons8_question_mark_64);
                            platformType.setVisibility(View.VISIBLE);
                            break;
                    }

                    publicsPostsUpvoteWhite_top.setOnClickListener(v -> {
                        publicsPostsDownvoteRed_top.setVisibility(View.GONE);
                        publicsPostsDownvoteWhite.setVisibility(GONE);
                        publicsPostsUpvoteGreen_top.setVisibility(GONE);
                        publicsPostsUpvoteWhite_top.setVisibility(GONE);
                        voteProgress.setVisibility(View.VISIBLE);
                        topicVote("up", post_id, username);
                    });
                    publicsPostsUpvoteGreen_top.setOnClickListener(v -> {
                        publicsPostsDownvoteRed_top.setVisibility(View.GONE);
                        publicsPostsDownvoteWhite.setVisibility(GONE);
                        publicsPostsUpvoteGreen_top.setVisibility(GONE);
                        publicsPostsUpvoteWhite_top.setVisibility(GONE);
                        voteProgress.setVisibility(View.VISIBLE);
                        topicVote("remove", post_id, username);
                    });
                    publicsPostsDownvoteWhite.setOnClickListener(v -> {
                        publicsPostsDownvoteRed_top.setVisibility(View.GONE);
                        publicsPostsDownvoteWhite.setVisibility(GONE);
                        publicsPostsUpvoteGreen_top.setVisibility(GONE);
                        publicsPostsUpvoteWhite_top.setVisibility(GONE);
                        voteProgress.setVisibility(View.VISIBLE);
                        topicVote("down", post_id, username);
                    });
                    publicsPostsDownvoteRed_top.setOnClickListener(v -> {
                        publicsPostsDownvoteRed_top.setVisibility(View.GONE);
                        publicsPostsDownvoteWhite.setVisibility(GONE);
                        publicsPostsUpvoteGreen_top.setVisibility(GONE);
                        publicsPostsUpvoteWhite_top.setVisibility(GONE);
                        voteProgress.setVisibility(View.VISIBLE);
                        topicVote("remove", post_id, username);
                    });

                    numPlayersAdded.setText(String.valueOf(num_added));
                    numPlayersNeeded.setText(num_players);

                    publicsPostTitle.setText(gameName);
                    publicsPostsNickname_top.setText(nickname);
                    String usernameText = "@"+username;
                    publicsPostsUsername_top.setText(usernameText);
                    publicsPostBody_top.setText(post_content);
                    publicsPostsDateTime_top.setText(post_date);
                    tvWhen.setText(event_date);

                    String profile_pic2 = profile_pic.substring(0, profile_pic.length() - 4)+"_r.JPG";
                    Glide.with(mContext)
                            .load(Constants.BASE_URL + profile_pic2)
                            .into(publicsPostsProfile_image_top);
                    Glide.with(mContext)
                            .load(Constants.BASE_URL + backimage)
                            .into(publicsImageView);

                    topicLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);

                    publicsTopicMenu.setOnClickListener(view -> {
                        PopupMenu popup = new PopupMenu(mContext, view);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.publics_topic_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(item -> {
                            if (item.getItemId() == R.id.menuTopicReport) {
                                ReportFragment ldf = new ReportFragment();
                                Bundle args = new Bundle();
                                args.putString("context", "publics_topic");
                                args.putString("type", "topic");
                                args.putString("id", post_id);
                                ldf.setArguments(args);
                                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            }if (item.getItemId() == R.id.menuTopicPlayer) {
                                if (mContext instanceof FragmentContainer) {
                                    FragmentProfile ldf = new FragmentProfile ();
                                    Bundle args = new Bundle();
                                    args.putString("UserId", user_id);
                                    ldf.setArguments(args);
                                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                                }
                            }
                            return true;
                        });
                        popup.show();
                    });
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadPublicsTopic(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, PublicsTopic_URL+"?userid="+userID+"&username="+deviceusername+"&publicsid="+publicsID, response -> {
            try {
                JSONArray publics = new JSONArray(response);
                for(int i = 0; i<publics.length(); i++){
                    JSONObject publicsObject = publics.getJSONObject(i);

                    String id = publicsObject.getString("id");
                    String subject = publicsObject.getString("subject");
                    String date = publicsObject.getString("date");
                    String cat = publicsObject.getString("cat");
                    String topic_by = publicsObject.getString("topic_by");
                    String post_id = publicsObject.getString("post_id");
                    String post_topic = publicsObject.getString("post_topic");
                    String post_content = publicsObject.getString("post_content");
                    String post_date = publicsObject.getString("post_date");
                    String post_by = publicsObject.getString("post_by");
                    String user_id = publicsObject.getString("user_id");
                    String profile_pic = publicsObject.getString("profile_pic");
                    String nickname = publicsObject.getString("nickname");
                    String username = publicsObject.getString("username");
                    String voted = publicsObject.getString("voted");
                    String votes = publicsObject.getString("votes");
                    String replies = publicsObject.getString("replies");
                    String online = publicsObject.getString("online");
                    String verified = publicsObject.getString("verified");
                    String clantag = publicsObject.getString("clantag");

                    PublicsPost_Recycler publicsPostResult = new PublicsPost_Recycler(id, subject, date, cat, topic_by, post_id,post_topic,post_content,post_date,post_by,user_id,profile_pic,nickname,username,voted,votes,replies,online,verified,clantag);
                    publicsPostList.add(publicsPostResult);
                }
                adapter = new PublicsPostAdapter(mContext, publicsPostList);
                publicsView.setAdapter(adapter);

                mProgressBar.setVisibility(View.GONE);
                publicsSwipe.setVisibility(View.VISIBLE);
                publicsSwipe.setRefreshing(false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void isJoinRequested(final String post_id, final String time){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, GET_REQUESTS, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.getString("error").equals("false")) {
                    if(obj.has("request_sent")&& obj.getString("request_sent").equals("yes")){
                        requestedToJoin.setVisibility(View.VISIBLE);
                        requestProgressBar.setVisibility(GONE);
                        requestedToJoin.setOnClickListener(v -> {
                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        requestedToJoin.setVisibility(GONE);
                                        requestProgressBar.setVisibility(View.VISIBLE);
                                        if (mContext instanceof FragmentContainer) {
                                            leaveTopic(post_id);
                                        }
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
                            builder.setMessage(R.string.leave_publics_topic_requested).setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        });
                    }else if(obj.has("request_sent")&& obj.getString("request_sent").equals("no")){
                        requestToJoin.setVisibility(View.VISIBLE);
                        requestProgressBar.setVisibility(GONE);
                        if (time.equals("ended")) {
                            requestToJoin.setBackgroundResource(grey_button);
                            requestToJoin.setOnClickListener(v -> {

                            });
                        }else{
                            requestToJoin.setOnClickListener(v -> {
                                requestToJoin.setVisibility(GONE);
                                requestProgressBar.setVisibility(View.VISIBLE);
                                isConnectOrReceivedConnect(post_id);
                            });
                        }
                    }
                } else {
                    Toast.makeText(
                            mContext,
                            obj.getString("message"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext,"Could not get requests, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("topic_id",post_id);
                parms.put("thisusername",deviceusername);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    public void requestToJoin(final String topic_id){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, JOIN_TOPIC, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if(obj.getString("result").equals("success")){
                    requestedToJoin.setVisibility(View.VISIBLE);
                    requestProgressBar.setVisibility(View.GONE);
                    Toast.makeText(mContext, "Request Sent!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext,"Could not send request, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("topic_id",topic_id);
                parms.put("thisusername",deviceusername);
                parms.put("thisuserid",userID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    public void requestToJoinNonConnection(final String topic_id){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, JOIN_TOPIC_NON_CONNECTION, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if(obj.getString("result").equals("success")){
                    requestedToJoin.setVisibility(View.VISIBLE);
                    requestProgressBar.setVisibility(View.GONE);
                    Toast.makeText(mContext, "Request Sent!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext,"Could not send request, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("topic_id",topic_id);
                parms.put("thisusername",deviceusername);
                parms.put("thisuserid",userID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    public void requestToJoinNewConnection(final String topic_id){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, JOIN_TOPIC_NEW_CONNECTION, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if(obj.getString("result").equals("success")){
                    requestedToJoin.setVisibility(View.VISIBLE);
                    requestProgressBar.setVisibility(View.GONE);
                    Toast.makeText(mContext, "Request Sent!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext,"Could not send request, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("topic_id",topic_id);
                parms.put("thisusername",deviceusername);
                parms.put("thisuserid",userID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    public void isConnectOrReceivedConnect(final String topic_id){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, IS_CONNECTED, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if(obj.getString("result").equals("success")){
                    if(obj.get("isFriend").equals("yes")){
                        requestToJoin(topic_id);
                    }else if(obj.get("isFriend").equals("received")){
                        new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTopColorRes(R.color.green)
                                .setButtonsColorRes(R.color.green)
                                .setIcon(R.drawable.ic_friend_add)
                                .setTitle(R.string.accept_connection_request_publics)
                                .setMessage(R.string.accept_connection_request_publics_message)
                                .setPositiveButton(R.string.yes, v -> requestToJoinNewConnection(topic_id))
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }else{
                        new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTopColorRes(R.color.green)
                                .setButtonsColorRes(R.color.green)
                                .setIcon(R.drawable.ic_friend_add)
                                .setTitle(R.string.send_connection_request)
                                .setMessage(R.string.send_connection_request_message)
                                .setPositiveButton(R.string.yes, v -> requestToJoinNonConnection(topic_id))
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                } else {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext,"Could not send request, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("topic_id",topic_id);
                parms.put("thisusername",deviceusername);
                parms.put("thisuserid",userID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    public void leaveTopic(final String topic_id){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, LEAVE_TOPIC, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.getString("error").equals("false")) {
                    if(obj.getString("result").equals("yes")){
                        resetFragment();
                    }
                } else {
                    Toast.makeText(
                            mContext,
                            obj.getString("message"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext,"Could not send request, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("topic_id",topic_id);
                parms.put("thisusername",deviceusername);
                parms.put("thisuserid",userID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void resetFragment(){
        Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof PublicsTopicFragment) {
            FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
            publicsSwipe.setRefreshing(false);
            publicsPostsNickname_top.requestFocus();
        }
        //refresh.setRefreshing(false);
    }

    private void submitComment(final String body, final String added_by, final String user_to, final String image, final String post_id) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, PUBLICS_POST_SUBMIT, response -> resetFragment(), error -> {
            mProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Network error, please try again later...",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("body",body);
                parms.put("added_by",added_by);
                parms.put("user_to",user_to);
                parms.put("image",image);
                parms.put("post_id",post_id);
                parms.put("user_id",userID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void topicVote(final String method, final String layout_id, final String user_to){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, VOTE_URL, response -> {
            JSONObject obj;
            try {
                obj = new JSONObject(response);
                if (obj.getString("error").equals("false")) {
                    if (obj.getString("layout").equals("topic")) {
                        if (obj.getString("method").equals("up")) {
                            String newValue = Integer.toString(Integer.parseInt(textViewNumPublicsPoints_top.getText().toString()) + 1);
                            textViewNumPublicsPoints_top.setText(newValue);
                            voteProgress.setVisibility(View.GONE);
                            publicsPostsUpvoteGreen_top.setVisibility(View.VISIBLE);
                            publicsPostsDownvoteWhite.setVisibility(View.VISIBLE);
                            publicsPostsUpvoteGreen_top.setEnabled(false);
                            publicsPostsDownvoteWhite.setEnabled(false);
                            new Handler().postDelayed(() -> {
                                publicsPostsUpvoteGreen_top.setEnabled(true);
                                publicsPostsDownvoteWhite.setEnabled(true);
                            }, 3000);
                        } else if (obj.getString("method").equals("down")) {
                            String newValue = Integer.toString(Integer.parseInt(textViewNumPublicsPoints_top.getText().toString()));
                            textViewNumPublicsPoints_top.setText(newValue);
                            voteProgress.setVisibility(View.GONE);
                            publicsPostsUpvoteWhite_top.setVisibility(View.VISIBLE);
                            publicsPostsDownvoteRed_top.setVisibility(View.VISIBLE);
                            publicsPostsUpvoteWhite_top.setEnabled(false);
                            publicsPostsDownvoteRed_top.setEnabled(false);
                            new Handler().postDelayed(() -> {
                                publicsPostsUpvoteWhite_top.setEnabled(true);
                                publicsPostsDownvoteRed_top.setEnabled(true);
                            }, 3000);
                        } else {
                            String finalValue = "";
                            if (obj.getString("result").equals("-1")){
                                finalValue = Integer.toString(Integer.parseInt(textViewNumPublicsPoints_top.getText().toString())-1);
                            }else if(obj.getString("result").equals("+1")){
                                finalValue = Integer.toString(Integer.parseInt(textViewNumPublicsPoints_top.getText().toString())+1);
                            }
                            textViewNumPublicsPoints_top.setText(finalValue);
                            publicsPostsUpvoteWhite_top.setVisibility(View.VISIBLE);
                            publicsPostsDownvoteWhite.setVisibility(View.VISIBLE);
                            voteProgress.setVisibility(View.GONE);
                            publicsPostsUpvoteWhite_top.setEnabled(false);
                            publicsPostsDownvoteWhite.setEnabled(false);
                            new Handler().postDelayed(() -> {
                                publicsPostsUpvoteWhite_top.setEnabled(true);
                                publicsPostsDownvoteWhite.setEnabled(true);
                            }, 3000);
                        }
                    }
                } else {
                    Toast.makeText(mContext,obj.getString("message"),Toast.LENGTH_LONG).show();
                    voteProgress.setVisibility(View.GONE);
                    publicsPostsUpvoteWhite_top.setVisibility(View.VISIBLE);
                    publicsPostsDownvoteWhite.setVisibility(View.VISIBLE);
                    publicsPostsUpvoteWhite_top.setEnabled(false);
                    publicsPostsDownvoteWhite.setEnabled(false);
                    new Handler().postDelayed(() -> {
                        publicsPostsUpvoteWhite_top.setEnabled(true);
                        publicsPostsDownvoteWhite.setEnabled(true);
                    },3000);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(mContext,"Could not vote, please try again later...",Toast.LENGTH_LONG).show();
            voteProgress.setVisibility(View.GONE);
            publicsPostsUpvoteWhite_top.setVisibility(View.VISIBLE);
            publicsPostsDownvoteWhite.setVisibility(View.VISIBLE);
            publicsPostsUpvoteWhite_top.setEnabled(false);
            publicsPostsDownvoteWhite.setEnabled(false);
            new Handler().postDelayed(() -> {
                publicsPostsUpvoteWhite_top.setEnabled(true);
                publicsPostsDownvoteWhite.setEnabled(true);
            },3000);
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("post_id",layout_id);
                parms.put("method",method);
                parms.put("layout", "topic");
                parms.put("user_to",user_to);
                parms.put("user_id",userID);
                parms.put("username",deviceusername);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void deleteTopic(final String post_id){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, PUBLICS_DELETE, response -> resetFragment(), error -> {
            mProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Network error, please try again later...",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("username",deviceusername);
                parms.put("topic_id",post_id);
                parms.put("user_id",userID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}
