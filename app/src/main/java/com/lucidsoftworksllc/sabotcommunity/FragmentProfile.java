package com.lucidsoftworksllc.sabotcommunity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL;

public class FragmentProfile extends Fragment {
    private static final String TAG = "ProfileFragment";

    private static final String RG = Constants.ROOT_URL+"new_post.php";
    private static final String Profilenews_URL = Constants.ROOT_URL+"profilenews_api.php";
    private static final String ProfilePublicsNews_URL = Constants.ROOT_URL+"profilepublicsnews_api.php";
    private static final String ProfileTop_URL = Constants.ROOT_URL+"profiletop_api.php";
    private static final String GET_REQUESTS = Constants.ROOT_URL+"get_profile_requests.php";
    private static final String ADD_CONNECTION = Constants.ROOT_URL+"add_connection.php";
    private static final String FOLLOW_USER = Constants.ROOT_URL+"user_follow_api.php";
    private static final String ACCEPT_CONNECTION = Constants.ROOT_URL+"accept_connection.php";
    private static final String UPLOAD_IMAGE_URL = Constants.ROOT_URL+"new_post_image_upload.php";
    public static final String URL_JOINED_CLANS = ROOT_URL + "user_joined_clans.php";
    public static final String GET_USER_ID = ROOT_URL + "get_userid.php";
    private RecyclerView profilenewsView;
    private ProfilenewsAdapter newsadapter;
    private PublicsTopicAdapter publicsnewsadapter;
    private JoinedClansAdapter clansAdapter;
    private List<Profilenews_Recycler> profilenewsRecyclerList;
    private List<PublicsTopic_Recycler> profilepublicsnewsRecyclerList;
    private List<Clans_Recycler> clans;
    private TextView userSwitch,userTwitter,userDiscordProfile,profileWebsite,profileItemsLabel,followingTV,connectionsTV,followersTV,postsNoPosts, textViewClanTag, textViewTvPosts, textViewTvLikes, editProfile, textViewUsername, textViewNickname, textViewDescription, reviewCount, userTwitch, userMixer, userPSN, userXbox, userDiscord, userSteam, userInstagram, userYoutube, followersCount, friendsCount, followingCount;
    private ImageView starIcon, imageViewProfilePic, imageViewProfileCover, profileOnlineIcon, imageUploadBtn;
    private CircleImageView verifiedIcon;
    private Context mContext;
    private Button submitStatusButton, profileNewsMoreBtn, profilePostsButton, publicsPostsButton, profileClansButton;
    private RelativeLayout userSwitchDetails,userTwitterDetails,userDiscordProfileDetails,mProgressBar, profileErrorScreen, setProfileCoverButton, setProfilePhotoButton, profileDisabledScreen, userTwitchDetails, userInstagramDetails, userYoutubeDetails, userMixerDetails, userPSNDetails, userXboxDetails, userDiscordDetails, userSteamDetails;
    private LinearLayout profileWebsiteContainer,profileLayout, addPostLayout, playerratingLayout, moreButtonLayout, profileStatusContainer;
    private MaterialRippleLayout addMessageButton, requestSentFriendButton, addFriendProgress, editProfileButton, followProfileButton, followedProfileButton, addFriendButton, addedFriendButton, requestedFriendButton, addItemButton;
    private SimpleRatingBar profileRating;
    private SwipeRefreshLayout profileRefreshLayout;
    private Spinner postTypeSpinner;
    private Bitmap imageToUpload;
    private String profileUsername;
    private RequestQueue rQueue;
    private JSONObject jsonObject;
    private String userID, userProfileID, deviceUsername;
    private EditText statusUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View userProfileRootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext = getActivity();
        mProgressBar = userProfileRootView.findViewById(R.id.profileLoadingScreen);
        addFriendProgress = userProfileRootView.findViewById(R.id.addFriendProgress);
        followingCount = userProfileRootView.findViewById(R.id.followingCount);
        followersCount = userProfileRootView.findViewById(R.id.followersCount);
        friendsCount = userProfileRootView.findViewById(R.id.friendsCount);
        userTwitch = userProfileRootView.findViewById(R.id.userTwitch);
        userMixer = userProfileRootView.findViewById(R.id.userMixer);
        userPSN = userProfileRootView.findViewById(R.id.userPSN);
        userXbox = userProfileRootView.findViewById(R.id.userXbox);
        userDiscord = userProfileRootView.findViewById(R.id.userDiscord);
        userSteam = userProfileRootView.findViewById(R.id.userSteam);
        userTwitchDetails = userProfileRootView.findViewById(R.id.userTwitchDetails);
        userInstagram = userProfileRootView.findViewById(R.id.userInstagram);
        userInstagramDetails = userProfileRootView.findViewById(R.id.userInstagramDetails);
        userYoutube = userProfileRootView.findViewById(R.id.userYoutube);
        userYoutubeDetails = userProfileRootView.findViewById(R.id.userYoutubeDetails);
        userMixerDetails = userProfileRootView.findViewById(R.id.userMixerDetails);
        userPSNDetails = userProfileRootView.findViewById(R.id.userPSNDetails);
        userXboxDetails = userProfileRootView.findViewById(R.id.userXboxDetails);
        userDiscordDetails = userProfileRootView.findViewById(R.id.userDiscordDetails);
        userSteamDetails = userProfileRootView.findViewById(R.id.userSteamDetails);
        profileDisabledScreen = userProfileRootView.findViewById(R.id.profileDisabledScreen);
        textViewNickname = userProfileRootView.findViewById(R.id.textViewNickname);
        textViewUsername = userProfileRootView.findViewById(R.id.textViewUsername);
        textViewTvPosts = userProfileRootView.findViewById(R.id.tvPosts);
        statusUpdate = userProfileRootView.findViewById(R.id.statusUpdate);
        submitStatusButton = userProfileRootView.findViewById(R.id.submitStatusButton);
        postsNoPosts = userProfileRootView.findViewById(R.id.postsNoPosts);
        //textViewTvLikes = userProfileRootView.findViewById(R.id.tvFollowers);
        addPostLayout = userProfileRootView.findViewById(R.id.addPostLayout);
        playerratingLayout = userProfileRootView.findViewById(R.id.profileRatingContainer);
        textViewDescription = (TextView) userProfileRootView.findViewById(R.id.textViewDescription);
        verifiedIcon = userProfileRootView.findViewById(R.id.verifiedIcon);
        addItemButton = userProfileRootView.findViewById(R.id.addItemButton);
        profileOnlineIcon = userProfileRootView.findViewById(R.id.profileOnlineIcon);
        setProfilePhotoButton = userProfileRootView.findViewById(R.id.setProfilePhotoButton);
        setProfileCoverButton = userProfileRootView.findViewById(R.id.setProfileCoverButton);
        imageViewProfilePic = userProfileRootView.findViewById(R.id.imageViewProfilePic);
        imageViewProfileCover = userProfileRootView.findViewById(R.id.profileCover);
        editProfileButton = userProfileRootView.findViewById(R.id.editProfileButton);
        followProfileButton = userProfileRootView.findViewById(R.id.followProfileButton);
        followedProfileButton = userProfileRootView.findViewById(R.id.followedProfileButton);
        requestedFriendButton = userProfileRootView.findViewById(R.id.requestedFriendButton);
        addedFriendButton = userProfileRootView.findViewById(R.id.addedFriendButton);
        addFriendButton = userProfileRootView.findViewById(R.id.addFriendButton);
        addMessageButton = userProfileRootView.findViewById(R.id.addMessageButton);
        starIcon = userProfileRootView.findViewById(R.id.starIcon);
        profileRating = userProfileRootView.findViewById(R.id.profileRating);
        //editProfile = (TextView) userProfileRootView.findViewById(R.id.textEditProfile);
        postTypeSpinner = userProfileRootView.findViewById(R.id.postTypeSpinner);
        profileLayout = userProfileRootView.findViewById(R.id.profileLayout);
        reviewCount = userProfileRootView.findViewById(R.id.reviewCount);
        profileRefreshLayout = userProfileRootView.findViewById(R.id.profileRefreshLayout);
        requestSentFriendButton = userProfileRootView.findViewById(R.id.requestSentFriendButton);
        addMessageButton = userProfileRootView.findViewById(R.id.addMessageButton);
        moreButtonLayout = userProfileRootView.findViewById(R.id.moreButtonLayout);
        imageUploadBtn = userProfileRootView.findViewById(R.id.imageUploadBtn);
        textViewClanTag = userProfileRootView.findViewById(R.id.textViewClanTag);
        profileErrorScreen = userProfileRootView.findViewById(R.id.profileErrorScreen);
        profileStatusContainer = userProfileRootView.findViewById(R.id.profileStatusContainer);
        profileNewsMoreBtn = userProfileRootView.findViewById(R.id.profileNewsMoreBtn);
        followersTV = userProfileRootView.findViewById(R.id.followers);
        followingTV = userProfileRootView.findViewById(R.id.following);
        connectionsTV = userProfileRootView.findViewById(R.id.connections);
        profileClansButton = userProfileRootView.findViewById(R.id.profileClansButtons);
        publicsPostsButton = userProfileRootView.findViewById(R.id.publicsPostsButtons);
        profilePostsButton = userProfileRootView.findViewById(R.id.profilePostsButton);
        profileItemsLabel = userProfileRootView.findViewById(R.id.profileItemsLabel);

        profileWebsite = userProfileRootView.findViewById(R.id.profileWebsite);
        profileWebsiteContainer = userProfileRootView.findViewById(R.id.profileWebsiteContainer);
        userDiscordProfileDetails = userProfileRootView.findViewById(R.id.userDiscordProfileDetails);
        userDiscordProfile = userProfileRootView.findViewById(R.id.userDiscordProfile);
        userTwitterDetails = userProfileRootView.findViewById(R.id.userTwitterDetails);
        userTwitter = userProfileRootView.findViewById(R.id.userTwitter);
        userSwitchDetails = userProfileRootView.findViewById(R.id.userSwitchDetails);
        userSwitch = userProfileRootView.findViewById(R.id.userSwitch);

        imageToUpload = null;
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        deviceUsername = SharedPrefManager.getInstance(mContext).getUsername();

        profilenewsRecyclerList = new ArrayList<>();
        profilepublicsnewsRecyclerList = new ArrayList<>();
        clans = new ArrayList<>();

        profilenewsView = userProfileRootView.findViewById(R.id.recyclerProfilenews);
        profilenewsView.setHasFixedSize(true);
        profilenewsView.setLayoutManager(new LinearLayoutManager(mContext));

        if (getArguments()!=null){
            if(getArguments().getString("UserId")!=null) {
                userProfileID = getArguments().getString("UserId");
                loadProfileTop();
            }else if(getArguments().getString("Username")!=null&&getArguments().getString("UserId")==null) {
                getUserID(getArguments().getString("Username"));
            } else {
                userProfileID = userID;
                loadProfileTop();
            }
        } else {
            userProfileID = userID;
            loadProfileTop();
        }
        profileRefreshLayout.setOnRefreshListener(() -> {
            Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof FragmentProfile) {
                FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
            profileRefreshLayout.setRefreshing(false);
            imageViewProfileCover.requestFocus();
        });

        addItemButton.setOnClickListener(view -> {
            if(addPostLayout.getVisibility()!= GONE){
                addPostLayout.setVisibility(GONE);
            }else{
                addPostLayout.setVisibility(View.VISIBLE);
                statusUpdate.requestFocus();
                if (statusUpdate.hasFocus()) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                imageUploadBtn.setOnClickListener(v -> {
                    requestMultiplePermissions();
                    openCropper();
                });
            }
        });
        editProfileButton.setOnClickListener(view -> {
            Fragment asf = new AccountSettingsFragment();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, asf);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        setProfileCoverButton.setOnClickListener(view -> {
            Fragment asf = new UploadCoverFragment();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, asf);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        setProfilePhotoButton.setOnClickListener(view -> {
            Fragment asf = new UploadProfilePhotoFragment();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, asf);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        return userProfileRootView;
    }

    private void submitStatus(final String body, final String added_by, final String user_to, final String type, final String form) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, RG, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    if(imageToUpload != null){
                        String post_id = jsonObject.getString("postid");
                        postImageUpload(imageToUpload, post_id, deviceUsername);
                    }
                    Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof FragmentProfile) {
                        FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        fragTransaction.detach(currentFragment);
                        fragTransaction.attach(currentFragment);
                        fragTransaction.commit();
                    }
                    statusUpdate.setText("");
                    imageViewProfileCover.requestFocus();
                }else{
                    mProgressBar.setVisibility(GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            mProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Error on Response, please try again later...",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("body",body);
                parms.put("added_by",added_by);
                parms.put("user_to",user_to);
                parms.put("user_id",userID);
                parms.put("type",type);
                parms.put("form",form);
                return parms;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void postImageUpload(Bitmap bitmap, String post_id, String username){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            jsonObject.put("post_id", post_id);
            jsonObject.put("image", encodedImage);
            jsonObject.put("added_by", username);
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_IMAGE_URL, jsonObject,
                jsonObject -> {
                    rQueue.getCache().clear();
                    try{
                        if(jsonObject.getString("error").equals("true")){
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }, volleyError -> {
                    //Log.e("UploadCoverFragment", volleyError.toString());
                });
        rQueue = Volley.newRequestQueue(mContext);
        rQueue.add(jsonObjectRequest);
    }

    private void loadProfileTop(){
        //final String users_friends_array = SharedPrefManager.getInstance(mContext).getUsersFriends();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ProfileTop_URL+"?userid="+userID+"&userid2="+userProfileID+"&deviceusername="+deviceUsername, response -> {
            try {
                JSONArray profiletop = new JSONArray(response);
                JSONObject profiletopObject = profiletop.getJSONObject(0);

                final String id = profiletopObject.getString("id");
                final String nickname = profiletopObject.getString("nickname");
                final String username = profiletopObject.getString("username");
                String description = profiletopObject.getString("description");
                final String verified = profiletopObject.getString("verified");
                //String signup_date = profiletopObject.getString("signup_date");
                final String profile_pic = profiletopObject.getString("profile_pic");
                final String cover_pic = profiletopObject.getString("cover_pic");
                String num_posts = profiletopObject.getString("num_posts");
                //String num_likes = profiletopObject.getString("num_likes");
                String user_closed = profiletopObject.getString("user_closed");
                String user_banned = profiletopObject.getString("user_banned");
                String num_friends = profiletopObject.getString("num_friends");
                String followings = profiletopObject.getString("followings");
                final String followers = profiletopObject.getString("followers");
                final String twitch = profiletopObject.getString("twitch");
                final String mixer = profiletopObject.getString("mixer");
                final String psn = profiletopObject.getString("psn");
                final String xbox = profiletopObject.getString("xbox");
                final String discord = profiletopObject.getString("discord");
                final String steam = profiletopObject.getString("steam");
                final String instagram = profiletopObject.getString("instagram");
                final String youtube = profiletopObject.getString("youtube");
                final String last_online = profiletopObject.getString("last_online");
                String count = profiletopObject.getString("count");
                String average = profiletopObject.getString("average");
                String clantag = profiletopObject.getString("clantag");
                String blocked = profiletopObject.getString("blocked");
                String supporter = profiletopObject.getString("supporter");
                String discord_user = profiletopObject.getString("discord_user");
                String twitter = profiletopObject.getString("twitter");
                String website = profiletopObject.getString("website");
                String nintendo = profiletopObject.getString("nintendo");
                String isFollowing = profiletopObject.getString("isFollowing");
                String isConnected = profiletopObject.getString("isConnected");
                final String connections = profiletopObject.getString("connections");

                profileUsername = username;
                if (userID.equals(userProfileID)){
                    addMessageButton.setVisibility(GONE);
                    addFriendButton.setVisibility(GONE);
                    addFriendProgress.setVisibility(GONE);
                    followProfileButton.setVisibility(GONE);
                    editProfileButton.setVisibility(View.VISIBLE);
                    setProfileCoverButton.setVisibility(View.VISIBLE);
                    setProfilePhotoButton.setVisibility(View.VISIBLE);
                }

                if (blocked.equals("yes")||SharedPrefManager.getInstance(mContext).isUserBlocked(username)){
                    profileErrorScreen.setVisibility(VISIBLE);
                    profileLayout.setVisibility(GONE);
                    mProgressBar.setVisibility(GONE);
                }else if(user_closed.equals("yes") || user_banned.equals("yes")){
                    profileDisabledScreen.setVisibility(View.VISIBLE);
                    profileLayout.setVisibility(View.GONE);
                    mProgressBar.setVisibility(GONE);
                }else{
                    postsQueryButtonClicked(profilePostsButton);
                }

                if (!clantag.equals(""))
                    textViewClanTag.setText(String.format("[%s]", clantag));

                if (supporter.equals("yes")){
                    starIcon.setVisibility(VISIBLE);
                }
                if (!discord_user.isEmpty()){
                    userDiscordProfileDetails.setVisibility(VISIBLE);
                    userDiscordProfile.setText(discord_user);
                }
                if (!twitter.isEmpty()){
                    userTwitterDetails.setVisibility(VISIBLE);
                    userTwitter.setText(twitter);
                    userTwitterDetails.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+twitter)));
                        }});
                }
                if (!website.isEmpty()){
                    profileWebsiteContainer.setVisibility(VISIBLE);
                    profileWebsite.setText(website);
                    profileWebsiteContainer.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website)));
                        }});
                }
                if (!nintendo.isEmpty()){
                    userSwitchDetails.setVisibility(VISIBLE);
                    userSwitch.setText(nintendo);
                }

                imageViewProfileCover.setOnClickListener(view -> {
                    Fragment asf = new PhotoViewFragment();
                    Bundle args = new Bundle();
                    args.putString("image", cover_pic);
                    asf.setArguments(args);
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                });
                imageViewProfilePic.setOnClickListener(view -> {
                    Fragment asf = new PhotoViewFragment();
                    Bundle args = new Bundle();
                    args.putString("image", profile_pic);
                    asf.setArguments(args);
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                });

                addMessageButton.setOnClickListener(view -> startActivity(new Intent(mContext, ChatActivity.class).putExtra("user_to", username)));
                followersCount.setText(followers);
                followingCount.setText(followings);
                friendsCount.setText(num_friends);

                if(!twitch.isEmpty()){
                    userTwitch.setText(twitch);
                    userTwitchDetails.setVisibility(View.VISIBLE);
                    userTwitchDetails.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitch.tv/"+twitch)));
                        }});
                }
                if(!mixer.isEmpty()){
                    userMixer.setText(mixer);
                    userMixerDetails.setVisibility(View.VISIBLE);
                    userMixerDetails.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mixer.com/"+mixer)));
                        }});
                }
                if(!psn.isEmpty()){
                    userPSN.setText(psn);
                    userPSNDetails.setVisibility(View.VISIBLE);
                    userPSNDetails.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://psnprofiles.com/"+psn)));
                        }});
                }
                if(!xbox.isEmpty()){
                    userXbox.setText(xbox);
                    userXboxDetails.setVisibility(View.VISIBLE);
                    userXboxDetails.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://account.xbox.com/en-us/profile?gamertag="+xbox)));
                        }});
                }
                if(!discord.isEmpty()){
                    userDiscord.setText(R.string.discord_server_text);
                    userDiscordDetails.setVisibility(View.VISIBLE);
                    userDiscordDetails.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discord.gg/"+discord)));
                        }});
                }
                if(!steam.isEmpty()){
                    userSteam.setText(steam);
                    userSteamDetails.setVisibility(View.VISIBLE);
                    userSteamDetails.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://steamcommunity.com/id/"+steam)));
                        }});
                }
                if(!youtube.isEmpty()){
                    userYoutubeDetails.setVisibility(View.VISIBLE);
                    userYoutubeDetails.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/"+youtube)));
                        }});
                }
                if(!instagram.isEmpty()){
                    userInstagram.setText(instagram);
                    userInstagramDetails.setVisibility(View.VISIBLE);
                    userInstagramDetails.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/"+instagram)));
                        }});
                }

                float averageFloat = Float.parseFloat(average);
                profileRating.setRating(averageFloat);
                reviewCount.setText(count);

                final String username_to;
                if(!(SharedPrefManager.getInstance(mContext).getUsername().equals(username))){
                    username_to = username;
                    if(isFollowing.equals("yes")){
                        followProfileButton.setVisibility(GONE);
                        followedProfileButton.setVisibility(View.VISIBLE);
                    }
                    if(isConnected.equals("yes")){
                        addedFriendButton.setVisibility(View.VISIBLE);
                        addFriendProgress.setVisibility(GONE);
                        addItemButton.setVisibility(View.VISIBLE);
                    }else{
                        connectionRequest(username);
                    }
                    if(!(isConnected.equals("yes"))&&!(userProfileID.equals(userID))){
                        addItemButton.setVisibility(GONE);
                    }
                } else {
                    // profile is device user's
                    username_to = "none";
                    if(!SharedPrefManager.getInstance(mContext).getProfilePic().equals(profile_pic)){
                        SharedPrefManager.getInstance(mContext).setProfilePic(profile_pic);
                    }
                    /*if(!connections.equals(users_friends_array)){
                        SharedPrefManager.getInstance(mContext).getFriendArray();
                    }*/
                }

                submitStatusButton.setOnClickListener(view -> {
                    String body = statusUpdate.getText().toString();
                    String added_by = SharedPrefManager.getInstance(mContext).getUsername();
                    String spinnerText = String.valueOf(postTypeSpinner.getSelectedItem());
                    String form = "user";
                    if(!(statusUpdate.getText().toString()).isEmpty() && !(spinnerText.isEmpty())){
                        profileLayout.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        submitStatus(body, added_by, username_to, spinnerText, form);
                        hideKeyboardFrom(mContext,view);
                    } else {
                        Toast.makeText(mContext,"You must enter text before submitting!",Toast.LENGTH_LONG).show();
                    }
                });

                playerratingLayout.setOnClickListener(view -> {
                    PlayerReviewFragment ldf = new PlayerReviewFragment();
                    Bundle args = new Bundle();
                    args.putString("UserId", id);
                    args.putString("username", username);
                    args.putString("nickname", nickname);
                    args.putString("verified", verified);
                    args.putString("profile_pic", profile_pic);
                    args.putString("all_friend_array", connections);
                    args.putString("last_online", last_online);
                    ldf.setArguments(args);
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                });

                moreButtonLayout.setOnClickListener(view -> {
                    PopupMenu popup = new PopupMenu(mContext, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.profile_more_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.menuPlayerReview) {
                            PlayerReviewFragment ldf = new PlayerReviewFragment();
                            Bundle args = new Bundle();
                            args.putString("UserId", id);
                            args.putString("username", username);
                            args.putString("nickname", nickname);
                            args.putString("verified", verified);
                            args.putString("profile_pic", profile_pic);
                            args.putString("all_friend_array", connections);
                            args.putString("last_online", last_online);
                            ldf.setArguments(args);
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                        }
                        if (item.getItemId() == R.id.menuPlayerReport) {
                            ReportFragment ldf = new ReportFragment();
                            Bundle args = new Bundle();
                            args.putString("context", "player");
                            args.putString("type", "user");
                            args.putString("id", id);
                            ldf.setArguments(args);
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                        }if (item.getItemId() == R.id.menuPlayerBlock) {
                            if (!deviceUsername.equals(profileUsername)) {
                                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            SharedPrefManager.getInstance(mContext).block_user(username);
                                            Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                            if (currentFragment instanceof FragmentProfile) {
                                                FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                                fragTransaction.detach(currentFragment);
                                                fragTransaction.attach(currentFragment);
                                                fragTransaction.commit();
                                            }
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:break;
                                    }
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
                                builder.setMessage("Block user?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();
                            }else{
                                Toast.makeText(mContext, "Are you really trying to block yourself?", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return true;
                    });
                    popup.show();
                });
                followProfileButton.setOnClickListener(view -> followUser(username,followers,"follow"));
                followedProfileButton.setOnClickListener(view -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.green)
                        .setIcon(R.drawable.ic_friend_add)
                        .setTitle("Unfollow "+"@"+username+"?")
                        .setPositiveButton(R.string.yes, v -> followUser(username,followers,"unfollow"))
                        .setNegativeButton(R.string.no, null)
                        .show());
                addFriendButton.setOnClickListener(view -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.green)
                        .setIcon(R.drawable.ic_friend_add)
                        .setTitle("Connection")
                        .setMessage("Connecting is PERMANENT as this user will be able to review you at any time if accepted.\n\nConnect with "+"@"+username+"?")
                        .setPositiveButton(R.string.yes, v -> {
                            addFriendButton.setVisibility(View.GONE);
                            requestSentFriendButton.setVisibility(View.VISIBLE);
                            addConnection(username);
                            followUser(username,followers,"follow");
                        })
                        .setNegativeButton(R.string.no, null)
                        .show());
                requestedFriendButton.setOnClickListener(view -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.green)
                        .setIcon(R.drawable.ic_friend_add)
                        .setTitle("Connection")
                        .setMessage("Connecting is PERMANENT as this user will be able to review you at any time if accepted.\n\nConnect with "+"@"+username+"?")
                        .setPositiveButton(R.string.yes, v -> {
                            addFriendButton.setVisibility(View.GONE);
                            requestedFriendButton.setVisibility(View.VISIBLE);
                            acceptConnection(username);
                            followUser(username,followers,"follow");
                        })
                        .setNegativeButton(R.string.no, null)
                        .show());
                requestSentFriendButton.setOnClickListener(view -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.green)
                        .setIcon(R.drawable.ic_friend_add)
                        .setTitle("Connection")
                        .setMessage("You are in the process of being permanently connected! If you're having trouble with this user, consider blocking or reporting.")
                        .show());
                addedFriendButton.setOnClickListener(view -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.green)
                        .setIcon(R.drawable.ic_friend_add)
                        .setTitle("Connection")
                        .setMessage("You are permanently connected! If you're having trouble with this user, consider blocking or reporting.")
                        .show());
                if (!description.equals("")){
                    textViewDescription.setText(description);
                    profileStatusContainer.setVisibility(VISIBLE);
                }
                if(verified.equals("yes")){
                    verifiedIcon.setVisibility(View.VISIBLE);
                }
                if(last_online.equals("yes")){
                    profileOnlineIcon.setVisibility(View.VISIBLE);
                }
                textViewUsername.setText(String.format("@%s", username));
                textViewTvPosts.setText(num_posts);
                textViewNickname.setText(nickname);

                Glide.with(mContext)
                        .load(Constants.BASE_URL + profile_pic)
                        .error(R.mipmap.ic_launcher)
                        .into(imageViewProfilePic);
                if (!cover_pic.isEmpty()) {
                    Glide.with(mContext)
                            .load(Constants.BASE_URL + cover_pic)
                            .error(R.mipmap.ic_launcher)
                            .into(imageViewProfileCover);
                }

                followersTV.setOnClickListener(v -> {
                    Fragment asf = new UserListFragment();
                    Bundle args = new Bundle();
                    args.putString("query", "followers");
                    args.putString("queryID", profileUsername);
                    asf.setArguments(args);
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                });
                followingTV.setOnClickListener(v -> {
                    Fragment asf = new UserListFragment();
                    Bundle args = new Bundle();
                    args.putString("query", "following");
                    args.putString("queryID", profileUsername);
                    asf.setArguments(args);
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                });
                connectionsTV.setOnClickListener(v -> {
                    Fragment asf = new UserListFragment();
                    Bundle args = new Bundle();
                    args.putString("query", "connections");
                    args.putString("queryID", profileUsername);
                    asf.setArguments(args);
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                });
                imageViewProfileCover.requestFocus();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void followUser(String username,String followers,String method){
        if (method.equals("follow")){
            followProfileButton.setVisibility(View.GONE);
            followedProfileButton.setVisibility(View.VISIBLE);
            int numFollowers = Integer.parseInt(followers);
            numFollowers++;
            followersCount.setText(String.valueOf(numFollowers));
        }else if(method.equals("unfollow")){
            followedProfileButton.setVisibility(View.GONE);
            followProfileButton.setVisibility(View.VISIBLE);
            int numFollowers = Integer.parseInt(followers);
            numFollowers--;
            followersCount.setText(String.valueOf(numFollowers));
        }
        StringRequest stringRequest=new StringRequest(Request.Method.POST, FOLLOW_USER, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if(obj.getString("error").equals("false")){
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext,"Could not follow/unfollow user, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("username",deviceUsername);
                parms.put("user_id",userID);
                parms.put("user_followed",username);
                parms.put("method",method);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadProfilenews(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Profilenews_URL+"?userid="+userID+"&userprofileid="+userProfileID+"&thisusername="+deviceUsername, response -> {
            try {
                JSONArray profilenews = new JSONArray(response);
                for(int i = 0; i<profilenews.length(); i++){
                    JSONObject profilenewsObject = profilenews.getJSONObject(i);

                    int id = profilenewsObject.getInt("id");
                    String type = profilenewsObject.getString("type");
                    String likes = profilenewsObject.getString("likes");
                    String body = profilenewsObject.getString("body");
                    String added_by = profilenewsObject.getString("added_by");
                    String user_to = profilenewsObject.getString("user_to");
                    String date_added = profilenewsObject.getString("date_added");
                    String user_closed = profilenewsObject.getString("user_closed");
                    String deleted = profilenewsObject.getString("deleted");
                    String image = profilenewsObject.getString("image");
                    String user_id = profilenewsObject.getString("user_id");
                    String profile_pic = profilenewsObject.getString("profile_pic");
                    String verified = profilenewsObject.getString("verified");
                    String online = profilenewsObject.getString("online");
                    String nickname = profilenewsObject.getString("nickname");
                    String username = profilenewsObject.getString("username");
                    String commentcount = profilenewsObject.getString("commentcount");
                    String likedbyuserYes = profilenewsObject.getString("likedbyuseryes");
                    String form = profilenewsObject.getString("form");
                    String edited = profilenewsObject.getString("edited");

                    Profilenews_Recycler profilenewsResult = new Profilenews_Recycler(id, type, likes, body, added_by, user_to, date_added, user_closed, deleted, image, user_id, profile_pic, verified, online, nickname, username, commentcount, likedbyuserYes, form, edited);
                    profilenewsRecyclerList.add(profilenewsResult);
                }
                try {
                    if (profilenews.length()==0){
                        postsNoPosts.setVisibility(View.VISIBLE);
                        postsNoPosts.setText(mContext.getString(R.string.no_posts_to_show));
                    }else{
                        postsNoPosts.setVisibility(GONE);
                    }
                }
                catch (Exception ignored) {
                }

                newsadapter = new ProfilenewsAdapter(mContext, profilenewsRecyclerList);
                profilenewsView.setAdapter(newsadapter);
                profilenewsView.setNestedScrollingEnabled(false);
                profileLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(GONE);
                profileNewsMoreBtn.setOnClickListener(v -> {
                    SeeAllFragment ldf = new SeeAllFragment();
                    Bundle args = new Bundle();
                    args.putString("queryid", profileUsername);
                    args.putString("queryidextra", userProfileID);
                    args.putString("method", "posts");
                    ldf.setArguments(args);
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                });
                profileClansButton.setOnClickListener(v -> postsQueryButtonClicked(profileClansButton));
                publicsPostsButton.setOnClickListener(v -> postsQueryButtonClicked(publicsPostsButton));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
//                Toast.makeText(getActivity(), "Error on Response ProfileNEWS", Toast.LENGTH_SHORT).show();
        });
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadProfilePublicsnews(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ProfilePublicsNews_URL+"?username="+profileUsername, response -> {
            try {
                JSONArray profilepublicsnews = new JSONArray(response);
                for(int i = 0; i<profilepublicsnews.length(); i++){
                    JSONObject profilenewsObject = profilepublicsnews.getJSONObject(i);

                    String username = profilenewsObject.getString("username");
                    if (SharedPrefManager.getInstance(mContext).isUserBlocked(username))continue;
                    String id = profilenewsObject.getString("id");
                    String numposts = profilenewsObject.getString("numposts");
                    String subject = profilenewsObject.getString("subject");
                    String date = profilenewsObject.getString("date");
                    String cat = profilenewsObject.getString("cat");
                    String topic_by = profilenewsObject.getString("topic_by");
                    String type = profilenewsObject.getString("type");
                    String user_id = profilenewsObject.getString("user_id");
                    String profile_pic = profilenewsObject.getString("profile_pic");
                    String nickname = profilenewsObject.getString("nickname");
                    String event_date = profilenewsObject.getString("event_date");
                    String zone = profilenewsObject.getString("zone");
                    String context = profilenewsObject.getString("context");
                    String num_players = profilenewsObject.getString("num_players");
                    String num_added = profilenewsObject.getString("num_added");
                    String gamename = profilenewsObject.getString("gamename");

                    PublicsTopic_Recycler publicsTopicResult = new PublicsTopic_Recycler(id, numposts, subject, date, cat, topic_by, type, user_id, profile_pic, nickname, username, event_date, zone, context, num_players, num_added, gamename);
                    profilepublicsnewsRecyclerList.add(publicsTopicResult);
                }
                profileNewsMoreBtn.setOnClickListener(v -> {
                    SeeAllFragment ldf = new SeeAllFragment();
                    Bundle args = new Bundle();
                    args.putString("queryid", profileUsername);
                    args.putString("method", "publics");
                    ldf.setArguments(args);
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                });

                if (profilepublicsnews.length()==0){
                    postsNoPosts.setVisibility(View.VISIBLE);
                    postsNoPosts.setText(getString(R.string.no_posts_to_show));
                }else{
                    postsNoPosts.setVisibility(GONE);
                }
                publicsnewsadapter = new PublicsTopicAdapter(mContext, profilepublicsnewsRecyclerList);
                profilenewsView.setAdapter(publicsnewsadapter);
                profilenewsView.setNestedScrollingEnabled(false);
                profileClansButton.setOnClickListener(v -> postsQueryButtonClicked(profileClansButton));
                profilePostsButton.setOnClickListener(v -> postsQueryButtonClicked(profilePostsButton));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadJoinedClans(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_JOINED_CLANS+"?userid="+userProfileID+"&username="+profileUsername,
                response -> {
                    try {
                        JSONObject res = new JSONObject(response);
                        JSONArray thread = res.getJSONArray("clans");
                        for (int i = 0; i < thread.length(); i++) {
                            JSONObject obj = thread.getJSONObject(i);
                            String position = obj.getString("position");
                            String tag = obj.getString("tag");
                            String name = obj.getString("name");
                            String num_members = obj.getString("num_members");
                            String insignia = obj.getString("insignia");
                            String games = obj.getString("games");
                            String id = obj.getString("id");
                            String avg = obj.getString("avg");
                            Clans_Recycler clansObject = new Clans_Recycler(position,tag,name,num_members,insignia,games,id,avg);
                            clans.add(clansObject);
                        }

                        if (thread.length()==0){
                            postsNoPosts.setVisibility(View.VISIBLE);
                            postsNoPosts.setText(getString(R.string.no_clans_text));
                        }

                        mProgressBar.setVisibility(View.GONE);
                        clansAdapter = new JoinedClansAdapter(mContext, clans);
                        profilenewsView.setAdapter(clansAdapter);
                        profilenewsView.setNestedScrollingEnabled(false);
                        publicsPostsButton.setOnClickListener(v -> postsQueryButtonClicked(publicsPostsButton));
                        profilePostsButton.setOnClickListener(v -> postsQueryButtonClicked(profilePostsButton));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                });
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void connectionRequest(final String thisUsername){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, GET_REQUESTS, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (!obj.getBoolean("error")) {
                    if(obj.has("request_sent")&& obj.getString("request_sent").equals("yes")){
                        requestSentFriendButton.setVisibility(View.VISIBLE);
                        addFriendButton.setVisibility(GONE);
                        addFriendProgress.setVisibility(GONE);
                    } else if (obj.has("request_received") && obj.getString("request_received").equals("yes")) {
                        requestedFriendButton.setVisibility(View.VISIBLE);
                        addFriendButton.setVisibility(GONE);
                        addFriendProgress.setVisibility(GONE);
                    } else if(!(addedFriendButton.getVisibility()==VISIBLE || userID.equals(userProfileID))){
                        addFriendButton.setVisibility(View.VISIBLE);
                        addFriendProgress.setVisibility(GONE);
                    }
                } else {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext,"Could not get requests, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("username",deviceUsername);
                parms.put("thisusername",thisUsername);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void addConnection(final String thisusername){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, ADD_CONNECTION, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (!obj.getBoolean("error")) {
                    if(obj.getString("request_sent").equals("yes")){
                        Toast.makeText(mContext, "Request Sent!", Toast.LENGTH_LONG).show();
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
                parms.put("username",deviceUsername);
                parms.put("thisusername",thisusername);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void acceptConnection(final String thisusername){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, ACCEPT_CONNECTION, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (!obj.getBoolean("error")) {
                    if(obj.getString("request_accepted").equals("yes")){
                        addedFriendButton.setVisibility(VISIBLE);
                        requestedFriendButton.setVisibility(GONE);
                        //SharedPrefManager.getInstance(mContext).getFriendArray();
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
                parms.put("username",deviceUsername);
                parms.put("thisusername",thisusername);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    final Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), resultUri);
                    imageUploadBtn.setImageBitmap(bitmap1);
                    imageToUpload = bitmap1;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), "Failed! Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestMultiplePermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(mContext.getApplicationContext(), "No permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(mContext.getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    private void postsQueryButtonClicked(Button clicked){
        if (clicked == profilePostsButton){
            profilePostsButton.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            profileClansButton.setBackgroundColor(mContext.getResources().getColor(R.color.grey_80));
            publicsPostsButton.setBackgroundColor(mContext.getResources().getColor(R.color.grey_80));
            //TODO add progressbar
            profilenewsRecyclerList.clear();
            loadProfilenews();
            textViewTvPosts.setVisibility(VISIBLE);
            profileItemsLabel.setText(mContext.getString(R.string.profile_posts_text));
            //TODO: Fix this with see all fragment --> clans
            profileNewsMoreBtn.setVisibility(VISIBLE);
        }
        if (clicked == profileClansButton){
            profilePostsButton.setBackgroundColor(mContext.getResources().getColor(R.color.grey_80));
            publicsPostsButton.setBackgroundColor(mContext.getResources().getColor(R.color.grey_80));
            profileClansButton.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            clans.clear();
            loadJoinedClans();
            textViewTvPosts.setVisibility(GONE);
            profileItemsLabel.setText(mContext.getString(R.string.clans_joined_text));
            //TODO: Fix this with see all fragment --> clans
            profileNewsMoreBtn.setVisibility(GONE);
        }
        if (clicked == publicsPostsButton){
            profileClansButton.setBackgroundColor(mContext.getResources().getColor(R.color.grey_80));
            profilePostsButton.setBackgroundColor(mContext.getResources().getColor(R.color.grey_80));
            publicsPostsButton.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            profilepublicsnewsRecyclerList.clear();
            loadProfilePublicsnews();
            textViewTvPosts.setVisibility(GONE);
            profileItemsLabel.setText(mContext.getString(R.string.publics_posts_text));
            //TODO: Fix this with see all fragment --> clans
            profileNewsMoreBtn.setVisibility(VISIBLE);
        }
    }

    private void getUserID(String username){
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_USER_ID, response -> {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("error").equals("false")) {
                        userProfileID = obj.getString("userid");
                        loadProfileTop();
                    }else{
                        profileErrorScreen.setVisibility(VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                //Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parms = new HashMap<>();
                    parms.put("username", username);
                    return parms;
                }
            };
            ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
        }
        catch (Exception e) {
            // stringrequest error
        }
    }

    private void openCropper(){
        CropImage.activity()
                .start(requireContext(), this);
    }

}