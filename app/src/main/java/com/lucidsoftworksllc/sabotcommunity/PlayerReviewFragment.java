package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Arrays;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerReviewFragment extends Fragment {

    private TextView tvratingsandreviews, tvInteger, usernameReviewed, nicknameReviewed, numReviews, tvTotalReviews, noReviews;
    private ImageView profileOnlineIcon;
    private CircleImageView verifiedIcon, profileImageReviewed;
    private Context mContext;
    private String userID, userProfileID,username,nickname,verified,profile_pic,all_friend_array,last_online;
    private RelativeLayout player_review_center, player_review_recycler_layout;
    private ProgressBar reviews5, reviews4, reviews3, reviews2, reviews1, player_review_progress;
    private SimpleRatingBar reviewStarRating;
    private RecyclerView playerreviewsView;
    private List<PlayerReview_Recycler> playerreviewRecyclerList;
    private Button reviewButton, connectToReview;
    private PlayerReviewAdapter reviewAdapter;
    private static final String Player_Reviewed_TOP_URL = Constants.ROOT_URL+"playerreviewsTopGet_api.php";
    private static final String Player_Reviewed_URL = Constants.ROOT_URL+"playerreviewsGet_api.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View playerReviewRootView = inflater.inflate(R.layout.fragment_player_review, null);

        connectToReview = playerReviewRootView.findViewById(R.id.connectToReview);
        noReviews = playerReviewRootView.findViewById(R.id.noReviews);
        tvratingsandreviews = playerReviewRootView.findViewById(R.id.tvratingsandreviews);
        profileOnlineIcon = playerReviewRootView.findViewById(R.id.profileOnlineIcon);
        verifiedIcon = playerReviewRootView.findViewById(R.id.verifiedIcon);
        usernameReviewed = playerReviewRootView.findViewById(R.id.usernameReviewed);
        nicknameReviewed = playerReviewRootView.findViewById(R.id.nicknameReviewed);
        profileImageReviewed = playerReviewRootView.findViewById(R.id.profileImageReviewed);
        player_review_progress = playerReviewRootView.findViewById(R.id.player_review_progress);
        player_review_recycler_layout = playerReviewRootView.findViewById(R.id.player_review_recycler_layout);
        player_review_center = playerReviewRootView.findViewById(R.id.player_review_center);
        tvTotalReviews = playerReviewRootView.findViewById(R.id.tvTotalReviews);
        numReviews = playerReviewRootView.findViewById(R.id.numReviews);
        reviewButton = playerReviewRootView.findViewById(R.id.reviewButton);
        tvInteger = playerReviewRootView.findViewById(R.id.tvInteger);
        reviews5 = playerReviewRootView.findViewById(R.id.reviews5);
        reviews4 = playerReviewRootView.findViewById(R.id.reviews4);
        reviews3 = playerReviewRootView.findViewById(R.id.reviews3);
        reviews2 = playerReviewRootView.findViewById(R.id.reviews2);
        reviews1 = playerReviewRootView.findViewById(R.id.reviews1);
        reviewStarRating = playerReviewRootView.findViewById(R.id.reviewStarRating);
        playerreviewRecyclerList = new ArrayList<>();

        assert getArguments() != null;
        username = getArguments().getString("username");
        nickname = getArguments().getString("nickname");
        verified = getArguments().getString("verified");
        profile_pic = getArguments().getString("profile_pic");
        all_friend_array = getArguments().getString("all_friend_array");
        last_online = getArguments().getString("last_online");
        playerreviewsView = playerReviewRootView.findViewById(R.id.recyclerPlayerReviews);
        mContext = getActivity();
        playerreviewsView.setHasFixedSize(true);
        playerreviewsView.setLayoutManager(new LinearLayoutManager(mContext));
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        loadReviewsTop();
        loadReviews();
        tvratingsandreviews.requestFocus();
        return playerReviewRootView;
    }

    private void loadReviewsTop(){
        if((getArguments())!=null) {
            userProfileID = getArguments().getString("UserId");
        } else {
            userProfileID = userID;
        }
        if(last_online.equals("yes")){
            profileOnlineIcon.setVisibility(View.VISIBLE);
        }
        if(verified.equals("yes")){
            verifiedIcon.setVisibility(View.VISIBLE);
        }
        reviewButton.setOnClickListener(v -> {
            PlayerRatingFragment ldf = new PlayerRatingFragment();
            Bundle args = new Bundle();
            args.putString("UserId", userProfileID);
            args.putString("username", username);
            args.putString("nickname", nickname);
            args.putString("verified", verified);
            args.putString("profile_pic", profile_pic);
            args.putString("last_online", last_online);
            ldf.setArguments(args);
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
        });

        usernameReviewed.setText(String.format("@%s", username));
        nicknameReviewed.setText(nickname);
        Glide.with(mContext)
                .load(Constants.BASE_URL+ profile_pic)
                .error(R.mipmap.ic_launcher)
                .into(profileImageReviewed);

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, Player_Reviewed_TOP_URL+"?userid="+userID+"&userQuery="+username, response -> {
            try {
                JSONArray profiletop = new JSONArray(response);
                JSONObject profiletopObject = profiletop.getJSONObject(0);

                int count = profiletopObject.getInt("count");
                String average = profiletopObject.getString("average");
                int fivestarratings = profiletopObject.getInt("fivestarratings");
                int fourstarratings = profiletopObject.getInt("fourstarratings");
                int threestarratings = profiletopObject.getInt("threestarratings");
                int twostarratings = profiletopObject.getInt("twostarratings");
                int onestarratings = profiletopObject.getInt("onestarratings");

                reviews5.setMax(count);
                reviews5.setProgress(fivestarratings);
                reviews4.setMax(count);
                reviews4.setProgress(fourstarratings);
                reviews3.setMax(count);
                reviews3.setProgress(threestarratings);
                reviews2.setMax(count);
                reviews2.setProgress(twostarratings);
                reviews1.setMax(count);
                reviews1.setProgress(onestarratings);

                reviewStarRating.setRating(Float.parseFloat(average));
                tvInteger.setText(average);
                numReviews.setText(String.valueOf(count));
                if (count == 1){
                    tvTotalReviews.setText(getString(R.string.review_text));
                }
                player_review_progress.setVisibility(View.GONE);
                player_review_center.setVisibility(View.VISIBLE);
                player_review_recycler_layout.setVisibility(View.VISIBLE);
                String[] array = all_friend_array.split(",");
                if(Arrays.asList(array).contains(SharedPrefManager.getInstance(mContext).getUsername())){
                    reviewButton.setVisibility(View.VISIBLE);
                } else if(!userProfileID.equals(userID)) {
                    connectToReview.setVisibility(View.VISIBLE);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadReviews(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Player_Reviewed_URL+"?userid="+userID+"&userQuery="+username, response -> {
            try {
                JSONArray profilenews = new JSONArray(response);
                for(int i = 0; i<profilenews.length(); i++){
                    JSONObject profilenewsObject = profilenews.getJSONObject(i);

                    String ratingnumber = profilenewsObject.getString("ratingnumber");
                    String title = profilenewsObject.getString("title");
                    String comments = profilenewsObject.getString("comments");
                    String reply = profilenewsObject.getString("reply");
                    String time = profilenewsObject.getString("time");
                    String profile_pic = profilenewsObject.getString("profile_pic");
                    String nickname = profilenewsObject.getString("nickname");
                    String user_id = profilenewsObject.getString("userid");

                    PlayerReview_Recycler profilenewsResult = new PlayerReview_Recycler(ratingnumber, title, comments, reply, time, profile_pic, nickname, user_id);
                    playerreviewRecyclerList.add(profilenewsResult);
                }
                if(profilenews.length() == 0){
                    noReviews.setVisibility(View.VISIBLE);
                }
                reviewAdapter = new PlayerReviewAdapter(mContext, playerreviewRecyclerList);
                playerreviewsView.setAdapter(reviewAdapter);
                ViewCompat.setNestedScrollingEnabled(playerreviewsView, false);
                nicknameReviewed.setFocusable(true);
                nicknameReviewed.setFocusableInTouchMode(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}
