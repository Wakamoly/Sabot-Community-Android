package com.lucidsoftworksllc.sabotcommunity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentPublicsCat extends Fragment {
    private static final String TAG = "PublicsCatFragment";

    private static final String Publics_Cat_URL = Constants.ROOT_URL+"publics_cat.php";
    private static final String Publics_Cat_Bottom_URL = Constants.ROOT_URL+"publics_cat_bottom.php";
    private static final String FOLLOW_GAME_URL = Constants.ROOT_URL+"publicscat_follow_api.php";

    private RecyclerView recyclerPublicsCatBottom;
    private PublicsTopicAdapter newsadapter;
    private List<PublicsTopic_Recycler> publicsRecyclerList;
    private TextView textViewTvClicks, textViewTvReviews, followersCount, textViewPublicsLow, textViewPublicsName, textViewDescription, publicsProfileTags, publicsPostsCount;
    private ProgressBar mProgressBar, followProgressCat;
    private ImageButton imageButtonPurchase;
    private ImageView publicsClickMenu, imageViewPublicsPic, imageViewPublicsBackPic, imageViewFacebook, imageViewTwitter, imageViewYoutube, imageViewInstagram;
    private Toolbar toolbar;
    private Context mContext;
    private RelativeLayout relLayout2;
    private SimpleRatingBar gameRating;
    private LinearLayout publicsRatingContainer, publicsReviewsContainer, buttonLayout, publicsPostsContainer;
    private Button gameActionBtn, gameActionBtnFollowed, newPublicsButton, followToPostButton, previousPublics, chatRoomButton;
    private String userID, username, publicsCatID;
    private EditText statusUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View publicsCatRootView = inflater.inflate(R.layout.fragment_publics_cat, container, false);

        publicsReviewsContainer = publicsCatRootView.findViewById(R.id.publicsReviewsContainer);
        followProgressCat = publicsCatRootView.findViewById(R.id.followProgressCat);
        followersCount = publicsCatRootView.findViewById(R.id.followersCount);
        mProgressBar = publicsCatRootView.findViewById(R.id.publicsCatProgressBar);
        textViewPublicsName = publicsCatRootView.findViewById(R.id.textViewPublicsName);
        textViewPublicsLow = publicsCatRootView.findViewById(R.id.textViewPublicsLow);
        imageViewFacebook = publicsCatRootView.findViewById(R.id.profileFacebookPage);
        imageViewTwitter = publicsCatRootView.findViewById(R.id.profileTwitterPage);
        imageViewYoutube = publicsCatRootView.findViewById(R.id.profileYoutubePage);
        imageViewInstagram = publicsCatRootView.findViewById(R.id.profileInstagramPage);
        imageButtonPurchase = publicsCatRootView.findViewById(R.id.profileBuyBtn);
        textViewTvClicks = publicsCatRootView.findViewById(R.id.tvClicks);
        textViewTvReviews = publicsCatRootView.findViewById(R.id.tvReviews);
        textViewDescription = publicsCatRootView.findViewById(R.id.textViewPublicsDescription);
        publicsProfileTags = publicsCatRootView.findViewById(R.id.publicsProfileDetails);
        imageViewPublicsPic = publicsCatRootView.findViewById(R.id.imageViewPublicsPic);
        imageViewPublicsBackPic = publicsCatRootView.findViewById(R.id.imageViewPublicsBackPic);
        publicsClickMenu = publicsCatRootView.findViewById(R.id.publicsClickMenu);
        toolbar = publicsCatRootView.findViewById(R.id.publicsToolBar);
        relLayout2 = publicsCatRootView.findViewById(R.id.publicsLayout);
        gameRating = publicsCatRootView.findViewById(R.id.ratingBar);
        publicsRatingContainer = publicsCatRootView.findViewById(R.id.publicsRatingContainer);
        gameActionBtn = publicsCatRootView.findViewById(R.id.profileActionBtn);
        gameActionBtnFollowed = publicsCatRootView.findViewById(R.id.profileActionBtnFollowed);
        newPublicsButton = publicsCatRootView.findViewById(R.id.newPublicsButton);
        publicsPostsCount = publicsCatRootView.findViewById(R.id.publicsPostsCount);
        followToPostButton = publicsCatRootView.findViewById(R.id.followToPostButton);
        buttonLayout = publicsCatRootView.findViewById(R.id.buttonLayout);
        previousPublics = publicsCatRootView.findViewById(R.id.previousPublics);
        chatRoomButton = publicsCatRootView.findViewById(R.id.chatRoomButton);
        publicsPostsContainer = publicsCatRootView.findViewById(R.id.publicsPostsContainer);
        mContext = getActivity();
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        publicsRecyclerList = new ArrayList<>();
        assert getArguments() != null;
        publicsCatID = getArguments().getString("PublicsId");
        recyclerPublicsCatBottom = publicsCatRootView.findViewById(R.id.recyclerPublicsTopics);
        recyclerPublicsCatBottom.setHasFixedSize(true);
        recyclerPublicsCatBottom.setLayoutManager(new LinearLayoutManager(mContext));
        loadPublicsCatTop();
        textViewPublicsLow.requestFocus();
        return publicsCatRootView;
    }

    private void loadPublicsCatTop(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Publics_Cat_URL+"?userid="+userID+"&username="+username+"&publicsid="+publicsCatID, response -> {
            try {
                JSONArray profiletop = new JSONArray(response);
                JSONObject profiletopObject = profiletop.getJSONObject(0);

                final String id = profiletopObject.getString("id");
                final String name = profiletopObject.getString("name");
                String genre = profiletopObject.getString("genre");
                final String image = profiletopObject.getString("image");
                final String back_image = profiletopObject.getString("back_image");
                final String cat_tag = profiletopObject.getString("cat_tag");
                String cat_description = profiletopObject.getString("cat_description");
                String search_hits = profiletopObject.getString("search_hits");
                String ratings = profiletopObject.getString("ratings");
                String facebook = profiletopObject.getString("facebook");
                String twitter = profiletopObject.getString("twitter");
                String youtube = profiletopObject.getString("youtube");
                String instagram = profiletopObject.getString("instagram");
                String purchase = profiletopObject.getString("purchase");
                String steampurchase = profiletopObject.getString("steampurchase");
                String followers = profiletopObject.getString("followers");
                //String count = profiletopObject.getString("count");
                String average = profiletopObject.getString("average");
                String publicsposts = profiletopObject.getString("publicsposts");
                String followed = profiletopObject.getString("followed");

                publicsPostsCount.setText(publicsposts);
                followersCount.setText(followers);
                textViewTvReviews.setText(ratings);
                gameRating.setRating(Float.parseFloat(average));
                imageViewPublicsPic.setOnClickListener(v -> {
                    Fragment asf = new PhotoViewFragment();
                    Bundle args = new Bundle();
                    args.putString("image", image);
                    asf.setArguments(args);
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                });
                imageViewPublicsBackPic.setOnClickListener(v -> {
                    Fragment asf = new PhotoViewFragment();
                    Bundle args = new Bundle();
                    args.putString("image", back_image);
                    asf.setArguments(args);
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                });

                if(followed.equals("yes")){
                    gameActionBtnFollowed.setVisibility(View.VISIBLE);
                    buttonLayout.setVisibility(View.VISIBLE);
                    loadPublicsCatBottom();
                } else{
                    gameActionBtn.setVisibility(View.VISIBLE);
                    followToPostButton.setVisibility(View.VISIBLE);
                }
                int len = genre.length();
                String[] genreString2 = genre.substring(1,len-1).split(",");

                for (String s : genreString2) {
                    publicsProfileTags.append(s);
                    publicsProfileTags.append("\n");
                }

                if (facebook.equals("")){
                    imageViewFacebook.setVisibility(View.GONE);
                }else{
                    final String ffacebook = facebook;
                    imageViewFacebook.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ffacebook)));
                        }});
                }
                if (instagram.equals("")){
                    imageViewInstagram.setVisibility(View.GONE);
                }else{
                    final String finstagram = instagram;
                    imageViewInstagram.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finstagram)));
                        }});
                }
                if (youtube.equals("")){
                    imageViewYoutube.setVisibility(View.GONE);
                }else{
                    final String fyoutube = youtube;
                    imageViewYoutube.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fyoutube)));
                        }});
                }
                if (twitter.equals("")){
                    imageViewTwitter.setVisibility(View.GONE);
                }else{
                    final String ftwitter = twitter;
                    imageViewTwitter.setOnClickListener(v -> {
                        if (mContext instanceof FragmentContainer) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ftwitter)));
                        }});
                }

                if (purchase.equals("")&&steampurchase.equals("")){
                    imageButtonPurchase.setVisibility(View.GONE);
                }else{

                    if (purchase.equals("")){
                        final String fsteampurchase = steampurchase;
                        imageButtonPurchase.setOnClickListener(v -> {
                            if (mContext instanceof FragmentContainer) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fsteampurchase)));
                            }});
                    }else if (steampurchase.equals("")){
                        final String fpurchase = purchase;
                        imageButtonPurchase.setOnClickListener(v -> {
                            if (mContext instanceof FragmentContainer) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fpurchase)));
                            }});
                    }else {
                        final String fpurchase = purchase;
                        final String fsteampurchase = steampurchase;
                        imageButtonPurchase.setOnClickListener(v -> {
                            if (mContext instanceof FragmentContainer) {
                                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            if (mContext instanceof FragmentContainer) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fsteampurchase)));
                                            }
                                            break;
                                        case DialogInterface.BUTTON_NEUTRAL:
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            if (mContext instanceof FragmentContainer) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fpurchase)));
                                            }
                                            break;
                                    }
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
                                builder.setMessage(R.string.purchasealterdialog).setPositiveButton(R.string.steam, dialogClickListener)
                                        .setNegativeButton(R.string.other2, dialogClickListener).setNeutralButton(R.string.back, dialogClickListener).show();
                            }
                        });
                    }
                }

                textViewDescription.setText(cat_description);
//                  publicsProfileTags.setText(genre2);
                textViewPublicsLow.setText(String.format("@%s", cat_tag));
                textViewTvClicks.setText(search_hits);
                textViewPublicsName.setText(name);

                Glide.with(mContext)
                        .load(Constants.BASE_URL+ image)
                        .error(R.mipmap.ic_launcher)
                        .into(imageViewPublicsPic);
                Glide.with(mContext)
                        .load(Constants.BASE_URL+ back_image)
                        .error(R.drawable.profile_default_cover)
                        .into(imageViewPublicsBackPic);

                publicsPostsContainer.setOnClickListener(v -> {
                    PublicsPrevious ldf = new PublicsPrevious();
                    Bundle args = new Bundle();
                    args.putString("GameId", id);
                    args.putString("gamename", name);
                    ldf.setArguments(args);
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();

                });
                chatRoomButton.setOnClickListener(v -> {
                    PublicsChatRoom ldf = new PublicsChatRoom();
                    Bundle args = new Bundle();
                    args.putString("game_pic", image);
                    args.putString("GameId", id);
                    args.putString("gamename", name);
                    ldf.setArguments(args);
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                });
                previousPublics.setOnClickListener(v -> {
                    PublicsPrevious ldf = new PublicsPrevious();
                    Bundle args = new Bundle();
                    args.putString("GameId", id);
                    args.putString("gamename", name);
                    ldf.setArguments(args);
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                });
                publicsReviewsContainer.setOnClickListener(view -> {
                    GameReviewFragment ldf = new GameReviewFragment();
                    Bundle args = new Bundle();
                    args.putString("GameId", id);
                    args.putString("gamename", name);
                    args.putString("gametag", cat_tag);
                    args.putString("game_pic", image);
                    ldf.setArguments(args);
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                });
                publicsRatingContainer.setOnClickListener(view -> {
                    GameReviewFragment ldf = new GameReviewFragment();
                    Bundle args = new Bundle();
                    args.putString("GameId", id);
                    args.putString("gamename", name);
                    args.putString("gametag", cat_tag);
                    args.putString("game_pic", image);
                    ldf.setArguments(args);
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                });

                followToPostButton.setOnClickListener(v -> {
                    gameActionBtn.setVisibility(View.GONE);
                    followProgressCat.setVisibility(View.VISIBLE);
                    StringRequest stringRequest1 =new StringRequest(Request.Method.POST, FOLLOW_GAME_URL, response1 -> {
                        //TODO: get verified response
                        gameActionBtnFollowed.setVisibility(View.VISIBLE);
                        followProgressCat.setVisibility(View.GONE);
                        buttonLayout.setVisibility(View.VISIBLE);
                        recyclerPublicsCatBottom.setVisibility(View.VISIBLE);
                        followToPostButton.setVisibility(View.VISIBLE);
                        loadPublicsCatBottom();
                        followToPostButton.setVisibility(View.GONE);
                    }, error -> Toast.makeText(mContext,"Could not follow, please try again later...",Toast.LENGTH_LONG).show()){
                        @Override
                        protected Map<String, String> getParams()  {
                            Map<String,String> parms= new HashMap<>();
                            parms.put("game_id",id);
                            parms.put("game_name",name);
                            parms.put("method","follow");
                            parms.put("user_id",userID);
                            parms.put("username",username);
                            return parms;
                        }
                    };
                    ((FragmentContainer)mContext).addToRequestQueue(stringRequest1);
                });

                gameActionBtn.setOnClickListener(view -> {
                    gameActionBtn.setVisibility(View.GONE);
                    followProgressCat.setVisibility(View.VISIBLE);
                    StringRequest stringRequest1 =new StringRequest(Request.Method.POST, FOLLOW_GAME_URL, response12 -> {
                        //TODO: get verified response
                        gameActionBtnFollowed.setVisibility(View.VISIBLE);
                        followProgressCat.setVisibility(View.GONE);
                        buttonLayout.setVisibility(View.VISIBLE);
                        recyclerPublicsCatBottom.setVisibility(View.VISIBLE);
                        followToPostButton.setVisibility(View.VISIBLE);
                        loadPublicsCatBottom();
                        followToPostButton.setVisibility(View.GONE);
                    }, error -> Toast.makeText(mContext,"Could not follow, please try again later...",Toast.LENGTH_LONG).show()){
                        @Override
                        protected Map<String, String> getParams()  {
                            Map<String,String> parms= new HashMap<>();
                            parms.put("game_id",id);
                            parms.put("game_name",name);
                            parms.put("method","follow");
                            parms.put("user_id",userID);
                            parms.put("username",username);
                            return parms;
                        }
                    };
                    ((FragmentContainer)mContext).addToRequestQueue(stringRequest1);
                });

                gameActionBtnFollowed.setOnClickListener(view -> {
                    gameActionBtnFollowed.setVisibility(View.GONE);
                    followProgressCat.setVisibility(View.VISIBLE);
                    StringRequest stringRequest1 =new StringRequest(Request.Method.POST, FOLLOW_GAME_URL, response13 -> {
                        //TODO: get verified response
                        gameActionBtn.setVisibility(View.VISIBLE);
                        followToPostButton.setVisibility(View.VISIBLE);
                        followProgressCat.setVisibility(View.GONE);
                        buttonLayout.setVisibility(View.GONE);
                        recyclerPublicsCatBottom.setVisibility(View.GONE);
                    }, error -> Toast.makeText(mContext,"Could not unfollow, please try again later...",Toast.LENGTH_LONG).show()){
                        @Override
                        protected Map<String, String> getParams()  {
                            Map<String,String> parms= new HashMap<>();
                            parms.put("game_id",id);
                            parms.put("game_name",name);
                            parms.put("method","unfollow");
                            parms.put("user_id",userID);
                            parms.put("username",username);
                            return parms;
                        }
                    };
                    ((FragmentContainer)mContext).addToRequestQueue(stringRequest1);
                });
                publicsClickMenu.setOnClickListener(view -> {
                    PopupMenu popup = new PopupMenu(mContext, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.publics_cat_top_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.menuCatReport) {
                            ReportFragment ldf = new ReportFragment();
                            Bundle args = new Bundle();
                            args.putString("context", "game");
                            args.putString("type", "publics_cat");
                            args.putString("id", id);
                            ldf.setArguments(args);
                            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                        }if (item.getItemId() == R.id.menuCatReview) {
                            GameReviewFragment ldf = new GameReviewFragment();
                            Bundle args = new Bundle();
                            args.putString("GameId", id);
                            args.putString("gamename", name);
                            args.putString("gametag", cat_tag);
                            args.putString("game_pic", image);
                            ldf.setArguments(args);
                            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();

                        }if (item.getItemId() == R.id.menuCatNewPublicsTopic) {
                            NewPublicsTopic ldf = new NewPublicsTopic();
                            Bundle args = new Bundle();
                            args.putString("gameid", id);
                            args.putString("gamename", name);
                            args.putString("gameimage", image);
                            ldf.setArguments(args);
                            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                        }
                        return true;
                    });
                    popup.show();
                });

                newPublicsButton.setOnClickListener(v -> {
                    NewPublicsTopic ldf = new NewPublicsTopic();
                    Bundle args = new Bundle();
                    args.putString("gameid", id);
                    args.putString("gamename", name);
                    args.putString("gameimage", image);
                    ldf.setArguments(args);
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                });
                relLayout2.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadPublicsCatBottom(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Publics_Cat_Bottom_URL+"?userid="+userID+"&publicsid="+ publicsCatID, response -> {
            try {
                JSONArray profilenews = new JSONArray(response);
                for(int i = 0; i<profilenews.length(); i++){
                    JSONObject profilenewsObject = profilenews.getJSONObject(i);

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
                    publicsRecyclerList.add(publicsTopicResult);
                }
                newsadapter = new PublicsTopicAdapter(mContext, publicsRecyclerList);
                recyclerPublicsCatBottom.setAdapter(newsadapter);
                recyclerPublicsCatBottom.setNestedScrollingEnabled(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}
