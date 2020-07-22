package com.lucidsoftworksllc.sabotcommunity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ClanFragment extends Fragment {
    private static final String TAG = "ClanFrag";
    private static final String CLAN_TOP = Constants.ROOT_URL+"clan_top.php";
    private static final String CLAN_BOTTOM_POSTS = Constants.ROOT_URL+"clan_bottom_posts.php";
    private static final String CLAN_BOTTOM_EVENTS = Constants.ROOT_URL+"clan_bottom_events.php";
    private static final String JOIN_CLAN = Constants.ROOT_URL+"clan_join.php";
    private static final String GET_REQUESTS = Constants.ROOT_URL+"get_clan_requests.php";
    private static final String LEAVE_CLAN = Constants.ROOT_URL+"leave_clan.php";
    private static final String RG = Constants.ROOT_URL+"new_post.php";
    private static final String UPLOAD_IMAGE_URL = Constants.ROOT_URL+"new_post_image_upload.php";
    private RecyclerView clanEventsRecycler, clanNewsRecycler;
    private ProfilenewsAdapter clanNewsAdapter;
    private List<Profilenews_Recycler> clanNewsRecyclerList;
    private TextView textViewTvClicks, textViewTvReviews, eventsCount, textViewClanLow, textViewClanName, textViewClanDescription, clanProfileTags, textViewMembersNum, clanCreatedOn, tvPosts, tvClanEvents;
    private ProgressBar mProgressBar, followProgressClan, newProgress;
    private ImageView imageButtonWebsite,backArrow, clanClickMenu, imageViewClanPic, imageViewClanBackPic, imageViewFacebook, imageViewTwitter, imageViewYoutube, imageViewInstagram, imageViewDiscord, imageUploadBtn2;
    private Toolbar toolbar;
    private Context mContext;
    private RelativeLayout clanLayout,errorLayout;
    private SimpleRatingBar clanRating;
    private LinearLayout clanRatingContainer, clanReviewsContainer, memberLayout, addPostLayout, clanEventsSpotlight, clanNewsSpotlight;
    private Button clanActionBtn, clanActionBtnJoined, clanActionBtnRequested, clanActionAdmin, clanNewPost, clanNewEvent, submitStatusButton, clanNewsMoreBtn;
    private String userID, username, clanID;
    private EditText statusUpdate;
    private Spinner postTypeSpinner;
    private Bitmap imageToUpload;
    RequestQueue rQueue;
    JSONObject jsonObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View clanRootView = inflater.inflate(R.layout.fragment_clan, null);

        clanReviewsContainer = clanRootView.findViewById(R.id.clanReviewsContainer);
        eventsCount = clanRootView.findViewById(R.id.eventsCount);
        mProgressBar = clanRootView.findViewById(R.id.clanCatProgressBar);
        textViewClanName = clanRootView.findViewById(R.id.textViewClanName);
        textViewClanLow = clanRootView.findViewById(R.id.textViewClanLow);
        imageViewFacebook = clanRootView.findViewById(R.id.clanFacebookPage);
        imageViewTwitter = clanRootView.findViewById(R.id.clanTwitterPage);
        imageViewYoutube = clanRootView.findViewById(R.id.clanYoutubePage);
        imageViewInstagram = clanRootView.findViewById(R.id.clanInstagramPage);
        imageButtonWebsite = clanRootView.findViewById(R.id.clanWebsiteBtn);
        imageViewDiscord = clanRootView.findViewById(R.id.clanDiscordChannel);
        textViewTvClicks = clanRootView.findViewById(R.id.tvClicks);
        textViewMembersNum = clanRootView.findViewById(R.id.membersNum);
        textViewTvReviews = clanRootView.findViewById(R.id.numReviews);
        textViewClanDescription = clanRootView.findViewById(R.id.textViewClanDescription);
        clanProfileTags = clanRootView.findViewById(R.id.clanProfileDetails);
        imageViewClanPic = clanRootView.findViewById(R.id.imageViewClanPic);
        imageViewClanBackPic = clanRootView.findViewById(R.id.imageViewClanBackPic);
        clanClickMenu = clanRootView.findViewById(R.id.clanClickMenu);
        toolbar = clanRootView.findViewById(R.id.clanToolBar);
        clanLayout = clanRootView.findViewById(R.id.clanLayout);
        clanRating = clanRootView.findViewById(R.id.ratingBar);
        clanRatingContainer = clanRootView.findViewById(R.id.clanRatingContainer);
        clanActionBtn = clanRootView.findViewById(R.id.clanActionBtn);
        followProgressClan = clanRootView.findViewById(R.id.followProgressClan);
        clanActionBtnJoined = clanRootView.findViewById(R.id.clanActionBtnJoined);
        clanActionBtnRequested = clanRootView.findViewById(R.id.clanActionBtnRequested);
        clanActionAdmin = clanRootView.findViewById(R.id.clanActionAdmin);
        clanCreatedOn = clanRootView.findViewById(R.id.clanCreatedOn);
        memberLayout = clanRootView.findViewById(R.id.memberLayout);
        addPostLayout = clanRootView.findViewById(R.id.addPostLayout);
        clanNewPost = clanRootView.findViewById(R.id.clanNewPost);
        statusUpdate = clanRootView.findViewById(R.id.statusUpdate);
        clanNewEvent = clanRootView.findViewById(R.id.clanNewEvent);
        tvPosts = clanRootView.findViewById(R.id.tvPosts);
        tvClanEvents = clanRootView.findViewById(R.id.tvClanEvents);
        clanEventsSpotlight = clanRootView.findViewById(R.id.clanEventsSpotlight);
        clanNewsSpotlight = clanRootView.findViewById(R.id.clanNewsSpotlight);
        postTypeSpinner = clanRootView.findViewById(R.id.postTypeSpinner);
        imageUploadBtn2 = clanRootView.findViewById(R.id.imageUploadBtn2);
        submitStatusButton = clanRootView.findViewById(R.id.submitStatusButton);
        newProgress = clanRootView.findViewById(R.id.newProgress);
        clanClickMenu = clanRootView.findViewById(R.id.clanClickMenu);
        errorLayout = clanRootView.findViewById(R.id.errorLayout);
        backArrow = clanRootView.findViewById(R.id.backArrow);
        clanNewsMoreBtn = clanRootView.findViewById(R.id.profileNewsMoreBtn);
        mContext = getActivity();
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        assert getArguments() != null;
        clanID = getArguments().getString("ClanId");

        clanNewsRecyclerList = new ArrayList<>();
        clanNewsRecycler = clanRootView.findViewById(R.id.recyclerProfilenews);
        clanNewsRecycler.setHasFixedSize(true);
        clanNewsRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        clanNewsRecycler.setNestedScrollingEnabled(false);

        //clanEventsRecyclerList = new ArrayList<>();
        clanEventsRecycler = clanRootView.findViewById(R.id.recyclerClanEvents);
        clanEventsRecycler.setHasFixedSize(true);
        clanEventsRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        clanEventsRecycler.setNestedScrollingEnabled(false);

        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        /*refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof ClanFragment) {
                    FragmentTransaction fragTransaction =   (getActivity()).getSupportFragmentManager().beginTransaction();
                    fragTransaction.detach(currentFragment);
                    fragTransaction.attach(currentFragment);
                    fragTransaction.commit();
                }
                profileRefreshLayout.setRefreshing(false);
                textViewUsername.requestFocus();
            }
        });*/
        loadClanTop();
        return clanRootView;
    }

    private void openCropper(){
        requestMultiplePermissions();
        CropImage.activity()
                .start(requireContext(), this);
    }

    private void loadClanTop(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CLAN_TOP+"?userid="+userID+"&username="+username+"&clanid="+clanID, response -> {
            try {
                JSONArray profiletop = new JSONArray(response);
                JSONObject profiletopObject = profiletop.getJSONObject(0);

                String banned = profiletopObject.getString("banned");
                String deleted = profiletopObject.getString("deleted");
                if (banned.equals("no")&&deleted.equals("no")) {
                    final String id = profiletopObject.getString("id");
                    final String name = profiletopObject.getString("name");
                    final String tag = profiletopObject.getString("tag");
                    final String members = profiletopObject.getString("members");
                    String num_members = profiletopObject.getString("num_members");
                    final String insignia = profiletopObject.getString("insignia");
                    String background = profiletopObject.getString("background");
                    String num_searches = profiletopObject.getString("num_searches");
                    String avgrating = profiletopObject.getString("avgrating");
                    String facebook = profiletopObject.getString("facebook");
                    String twitter = profiletopObject.getString("twitter");
                    String youtube = profiletopObject.getString("youtube");
                    String instagram = profiletopObject.getString("instagram");
                    String owner = profiletopObject.getString("owner");
                    String created_on = profiletopObject.getString("created_on");
                    String description = profiletopObject.getString("description");
                    String games = profiletopObject.getString("games");
                    String num_events = profiletopObject.getString("num_events");
                    String num_reviews = profiletopObject.getString("num_reviews");
                    String discord = profiletopObject.getString("discord");
                    String website = profiletopObject.getString("website");
                    String num_posts = profiletopObject.getString("num_posts");

                    tvPosts.setText(num_posts);
                    tvClanEvents.setText(num_events);
                    String[] array = members.split(",");

                    //if player is owner of clan
                    if (owner.equals(username)) {
                        followProgressClan.setVisibility(GONE);
                        clanActionAdmin.setVisibility(VISIBLE);
                        clanActionAdmin.setOnClickListener(v -> {
                            Fragment asf = new PhotoViewFragment();
                            Bundle args = new Bundle();
                            args.putString("ClanId", clanID);
                            asf.setArguments(args);
                            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, asf);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        });
                        memberLayout.setVisibility(VISIBLE);
                        clanNewEvent.setOnClickListener(v -> Toast.makeText(mContext, "Coming Soon!", Toast.LENGTH_SHORT).show());
                        clanNewPost.setOnClickListener(v -> {
                            addPostLayout.setVisibility(VISIBLE);
                            statusUpdate.setFocusable(true);
                            imageUploadBtn2.setOnClickListener(v1 -> {
                                requestMultiplePermissions();
                                openCropper();
                            });
                            submitStatusButton.setOnClickListener(view -> {
                                String body = statusUpdate.getText().toString();
                                String added_by = SharedPrefManager.getInstance(mContext).getUsername();
                                String spinnerText = String.valueOf(postTypeSpinner.getSelectedItem());
                                String form = "clan";
                                if (!(statusUpdate.getText().toString()).isEmpty() && !(spinnerText.isEmpty())) {
                                    clanLayout.setVisibility(View.GONE);
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    submitClanPost(body, added_by, tag, spinnerText, form);
                                    hideKeyboardFrom(mContext, view);
                                } else {
                                    Toast.makeText(mContext, "You must enter text before submitting!", Toast.LENGTH_LONG).show();
                                }
                            });
                        });
                        clanActionAdmin.setOnClickListener(v -> {
                            ClanAdminPanel ldf = new ClanAdminPanel();
                            Bundle args = new Bundle();
                            args.putString("ClanId", id);
                            ldf.setArguments(args);
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                        });
                    }
                    //if player is a member of clan
                    else if (Arrays.asList(array).contains(username)) {
                        followProgressClan.setVisibility(GONE);
                        clanActionBtnJoined.setVisibility(View.VISIBLE);
                        memberLayout.setVisibility(VISIBLE);
                        clanNewEvent.setOnClickListener(v -> Toast.makeText(mContext, "Coming Soon!", Toast.LENGTH_SHORT).show());
                        clanNewPost.setOnClickListener(v -> {
                            addPostLayout.setVisibility(VISIBLE);
                            statusUpdate.setFocusable(true);
                            imageUploadBtn2.setOnClickListener(v12 -> {
                                requestMultiplePermissions();
                                openCropper();
                            });
                            submitStatusButton.setOnClickListener(view -> {
                                String body = statusUpdate.getText().toString();
                                String added_by = SharedPrefManager.getInstance(mContext).getUsername();
                                String spinnerText = String.valueOf(postTypeSpinner.getSelectedItem());
                                String form = "clan";
                                if (!(statusUpdate.getText().toString()).isEmpty() && !(spinnerText.isEmpty())) {
                                    clanLayout.setVisibility(View.GONE);
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    submitClanPost(body, added_by, tag, spinnerText, form);
                                    hideKeyboardFrom(mContext, view);
                                } else {
                                    Toast.makeText(mContext, "You must enter text before submitting!", Toast.LENGTH_LONG).show();
                                }
                            });
                        });
                    }
                    //if player is NOT a member of clan
                    else {
                        clanUserRequest(clanID);
                    }
                    clanCreatedOn.setText(String.format("Started on: %s", created_on));
                    eventsCount.setText(num_events);
                    textViewTvReviews.setText(num_reviews);
                    textViewMembersNum.setText(num_members);
                    if (!avgrating.equals("null")) {
                        clanRating.setRating(Float.parseFloat(avgrating));
                    }
                    textViewTvReviews.setText(num_reviews);
                    if (!games.equals(",")) {
                        int len = games.length();
                        String[] genreString2 = games.substring(1, len - 1).split(",");
                        for (String s : genreString2) {
                            clanProfileTags.append(s);
                            clanProfileTags.append("\n");
                        }
                    } else {
                        clanProfileTags.setVisibility(GONE);
                    }

                    if (facebook.equals("")) {
                        imageViewFacebook.setVisibility(View.GONE);
                    } else {
                        final String ffacebook;
                        if (!facebook.contains("https://")) {
                            ffacebook = "https://www.facebook.com/" + facebook;
                        } else {
                            ffacebook = "www.facebook.com/" + facebook;
                        }
                        imageViewFacebook.setOnClickListener(v -> {
                            if (mContext instanceof FragmentContainer) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ffacebook)));
                            }
                        });
                    }
                    if (instagram.equals("")) {
                        imageViewInstagram.setVisibility(View.GONE);
                    } else {
                        final String finstagram;
                        if (!instagram.contains("https://")) {
                            finstagram = "https://www.instagram.com/" + instagram;
                        } else {
                            finstagram = "www.instagram.com/" + instagram;
                        }
                        imageViewInstagram.setOnClickListener(v -> {
                            if (mContext instanceof FragmentContainer) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finstagram)));
                            }
                        });
                    }
                    if (youtube.equals("")) {
                        imageViewYoutube.setVisibility(View.GONE);
                    } else {
                        final String fyoutube;
                        if (!youtube.contains("https://")) {
                            fyoutube = "https://youtube.com/user/" + youtube;
                        } else {
                            fyoutube = "youtube.com/user/" + youtube;
                        }
                        imageViewYoutube.setOnClickListener(v -> {
                            if (mContext instanceof FragmentContainer) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fyoutube)));
                            }
                        });
                    }
                    if (twitter.equals("")) {
                        imageViewTwitter.setVisibility(View.GONE);
                    } else {
                        final String ftwitter;
                        if (!twitter.contains("https://")) {
                            ftwitter = "https://www.twitter.com/" + twitter;
                        } else {
                            ftwitter = "www.twitter.com/" + twitter;
                        }
                        imageViewTwitter.setOnClickListener(v -> {
                            if (mContext instanceof FragmentContainer) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ftwitter)));
                            }
                        });
                    }
                    if (discord.equals("")) {
                        imageViewDiscord.setVisibility(View.GONE);
                    } else {
                        final String fdiscord;
                        if (!discord.contains("https://")) {
                            fdiscord = "https://www.discord.gg/" + discord;
                        } else {
                            fdiscord = "www.discord.gg/" + discord;
                        }
                        imageViewDiscord.setOnClickListener(v -> {
                            if (mContext instanceof FragmentContainer) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fdiscord)));
                            }
                        });
                    }

                    if (website.equals("")) {
                        imageButtonWebsite.setVisibility(View.GONE);
                    } else {
                        final String fwebsite;
                        if (!website.contains("https://")) {
                            fwebsite = "https://" + website;
                        } else {
                            fwebsite = website;
                        }
                        imageButtonWebsite.setOnClickListener(v -> {
                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        if (mContext instanceof FragmentContainer) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fwebsite)));
                                        }
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:break;
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
                            builder.setMessage("WARNING: You're leaving Sabot Community and opening this clan's website. Malicious intent should be reported.\nYou're being directed to:\n"+fwebsite+"\n\nContinue?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        });
                    }

                    textViewClanDescription.setText(description);
                    //publicsProfileTags.setText(genre2);
                    textViewClanLow.setText(String.format("[%s]", tag));
                    textViewClanLow.setTextColor(ContextCompat.getColor(mContext, R.color.pin));
                    textViewTvClicks.setText(num_searches);
                    textViewClanName.setText(name);
                    Glide.with(mContext)
                            .load(Constants.BASE_URL + insignia)
                            .error(R.mipmap.ic_launcher)
                            .into(imageViewClanPic);
                    Glide.with(mContext)
                            .load(Constants.BASE_URL + background)
                            .error(R.drawable.profile_default_cover)
                            .into(imageViewClanBackPic);
                    clanReviewsContainer.setOnClickListener(view -> {
                        ClanReviewFragment ldf = new ClanReviewFragment();
                        Bundle args = new Bundle();
                        args.putString("ClanId", id);
                        args.putString("Clanname", name);
                        args.putString("Clantag", tag);
                        args.putString("Clan_pic", insignia);
                        args.putString("Clan_members", members);
                        ldf.setArguments(args);
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                    });
                    clanRatingContainer.setOnClickListener(view -> {
                        ClanReviewFragment ldf = new ClanReviewFragment();
                        Bundle args = new Bundle();
                        args.putString("ClanId", id);
                        args.putString("Clanname", name);
                        args.putString("Clantag", tag);
                        args.putString("Clan_pic", insignia);
                        args.putString("Clan_members", members);
                        ldf.setArguments(args);
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                    });

                    clanActionBtn.setOnClickListener(view -> {
                        clanActionBtn.setVisibility(View.GONE);
                        followProgressClan.setVisibility(View.VISIBLE);
                        requestToJoinClan(clanID, name);
                    });

                    clanActionBtnJoined.setOnClickListener(view -> {
                        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    clanActionBtnJoined.setVisibility(View.GONE);
                                    followProgressClan.setVisibility(View.VISIBLE);
                                    leaveClan(clanID);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:break;
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
                        builder.setMessage("Leave clan?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    });

                    clanClickMenu.setOnClickListener(v -> {
                        PopupMenu popup = new PopupMenu(mContext, v);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.clan_top_menu, popup.getMenu());

                        popup.setOnMenuItemClickListener(item -> {
                            if (item.getItemId() == R.id.menuClanReview) {
                                ClanReviewFragment ldf = new ClanReviewFragment();
                                Bundle args = new Bundle();
                                args.putString("ClanId", id);
                                args.putString("Clanname", name);
                                args.putString("Clantag", tag);
                                args.putString("Clan_pic", insignia);
                                args.putString("Clan_members", members);
                                ldf.setArguments(args);
                                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            }
                            if (item.getItemId() == R.id.menuClanReport) {
                                ReportFragment ldf = new ReportFragment();
                                Bundle args = new Bundle();
                                args.putString("context", "clan");
                                args.putString("type", "clan");
                                args.putString("id", id);
                                ldf.setArguments(args);
                                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            }
                            return true;
                        });
                        popup.show();
                    });
                    clanLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    loadClanPosts();
                    //loadClanEvents();
                }else{
                    mProgressBar.setVisibility(GONE);
                    errorLayout.setVisibility(VISIBLE);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void submitClanPost(final String body, final String added_by, final String user_to, final String type, final String form) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, RG, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    if(imageToUpload != null){
                        String post_id = jsonObject.getString("postid");
                        postImageUpload(imageToUpload, post_id, username);
                    }else{
                        resetFragment();
                    }
                    Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof DashboardFragment) {
                        FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        fragTransaction.detach(currentFragment);
                        fragTransaction.attach(currentFragment);
                        fragTransaction.commit();
                    }
                    statusUpdate.setText("");
                }else{
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                parms.put("clanid",clanID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
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
                        if(!jsonObject.getString("error").equals("true")){
                            resetFragment();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }, volleyError -> Log.e("UploadCoverFragment", volleyError.toString()));
        rQueue = Volley.newRequestQueue(mContext);
        rQueue.add(jsonObjectRequest);
    }

    private void loadClanPosts(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CLAN_BOTTOM_POSTS+"?userid="+userID+"&clanid="+clanID+"&thisusername="+username, response -> {
            try {
                JSONArray clannews = new JSONArray(response);
                for(int i = 0; i<clannews.length(); i++) {
                    JSONObject clannewsObject = clannews.getJSONObject(i);

                    int id = clannewsObject.getInt("id");
                    String type = clannewsObject.getString("type");
                    String likes = clannewsObject.getString("likes");
                    String body = clannewsObject.getString("body");
                    String added_by = clannewsObject.getString("added_by");
                    String user_to = clannewsObject.getString("user_to");
                    String date_added = clannewsObject.getString("date_added");
                    String user_closed = clannewsObject.getString("user_closed");
                    String deleted = clannewsObject.getString("deleted");
                    String image = clannewsObject.getString("image");
                    String user_id = clannewsObject.getString("user_id");
                    String profile_pic = clannewsObject.getString("profile_pic");
                    String verified = clannewsObject.getString("verified");
                    String online = clannewsObject.getString("online");
                    String nickname = clannewsObject.getString("nickname");
                    String username = clannewsObject.getString("username");
                    String commentcount = clannewsObject.getString("commentcount");
                    String likedbyuserYes = clannewsObject.getString("likedbyuseryes");
                    String form = clannewsObject.getString("form");
                    String edited = clannewsObject.getString("edited");

                    Profilenews_Recycler clanPosts_Recycler = new Profilenews_Recycler(id, type, likes, body, added_by, user_to, date_added, user_closed, deleted, image, user_id, profile_pic, verified, online, nickname, username, commentcount, likedbyuserYes, form, edited);
                    clanNewsRecyclerList.add(clanPosts_Recycler);
                }
                clanNewsAdapter = new ProfilenewsAdapter(mContext, clanNewsRecyclerList);
                clanNewsRecycler.setAdapter(clanNewsAdapter);
                if (clannews.length()==0) {
                    clanNewsSpotlight.setVisibility(GONE);
                }else{
                    newProgress.setVisibility(GONE);
                }
                clanNewsMoreBtn.setOnClickListener(v -> {
                    SeeAllFragment ldf = new SeeAllFragment();
                    Bundle args = new Bundle();
                    args.putString("queryid", clanID);
                    args.putString("method", "clan_posts");
                    ldf.setArguments(args);
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    /*private void loadClanEvents(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CLAN_BOTTOM_EVENTS+"?userid="+userID+"&username="+username+"&clanid="+clanID, response -> {
            try {
                JSONArray events = new JSONArray(response);
                for(int i = 0; i<events.length(); i++){
                    JSONObject clanEventsObject = events.getJSONObject(i);

                    String id = clanEventsObject.getString("id");
                    String numposts = clanEventsObject.getString("numposts");
                    String subject = clanEventsObject.getString("subject");
                    String date = clanEventsObject.getString("date");
                    String cat = clanEventsObject.getString("cat");
                    String topic_by = clanEventsObject.getString("topic_by");
                    String type = clanEventsObject.getString("type");
                    String user_id = clanEventsObject.getString("user_id");
                    String profile_pic = clanEventsObject.getString("profile_pic");
                    String nickname = clanEventsObject.getString("nickname");
                    String username = clanEventsObject.getString("username");

                    //ClanEvent_Recycler clanEvent_Recycler = new ClanEvent_Recycler(id, numposts, subject, date, cat, topic_by, type, user_id, profile_pic, nickname, username);
                    //clanEventsRecyclerList.add(clanEvent_Recycler);
                }
                //clanEventsAdapter = new EventAdapter(mContext, clanEventsRecyclerList);
                //clanEventsRecycler.setAdapter(clanEventsAdapter);
                clanLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }*/

    private void clanUserRequest(final String clan_id){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, GET_REQUESTS, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.getString("error").equals("false")) {
                    if(obj.has("request_sent")&& obj.getString("request_sent").equals("yes")){
                        clanActionBtnRequested.setVisibility(View.VISIBLE);
                        clanActionBtn.setVisibility(GONE);
                    }else{
                        clanActionBtn.setVisibility(View.VISIBLE);
                    }
                    followProgressClan.setVisibility(GONE);
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
                parms.put("clan_id",clan_id);
                parms.put("thisusername",username);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    public void requestToJoinClan(final String clan_id, final String clan_name){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, JOIN_CLAN, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (!obj.getBoolean("error")) {
                    if(obj.getString("result").equals("yes")){
                        clanActionBtnRequested.setVisibility(View.VISIBLE);
                        followProgressClan.setVisibility(View.GONE);
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
                parms.put("clanid",clan_id);
                parms.put("clanname",clan_name);
                parms.put("thisusername",username);
                parms.put("thisuserid",userID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    public void leaveClan(final String clan_id){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, LEAVE_CLAN, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (!obj.getBoolean("error")) {
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
                parms.put("clanid",clan_id);
                parms.put("thisusername",username);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    final Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), resultUri);
                    imageUploadBtn2.setImageBitmap(bitmap1);
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

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_IMAGE) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    final Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
                    imageUploadBtn2.setImageBitmap(bitmap1);
                    imageToUpload = bitmap1;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/

    private void  requestMultiplePermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(mContext, "No permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    private void resetFragment(){
        Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof ClanFragment) {
            FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
        }
        //refresh.setRefreshing(false);
    }

}