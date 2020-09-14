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

public class ClanReviewFragment extends Fragment {

    private TextView tvratingsandreviews, tvInteger, gameReviewed, gameTagReviewed, numReviews, tvTotalReviews, noReviews;
    private ImageView gameImageReviewed;
    private Context mContext;
    private String userID, username;
    private RelativeLayout player_review_center, player_review_recycler_layout;
    private ProgressBar reviews5, reviews4, reviews3, reviews2, reviews1, player_review_progress;
    private SimpleRatingBar reviewStarRating;
    private RecyclerView gamereviewsView;
    private List<GameReview_Recycler> gamereviewRecyclerList;
    private Button reviewButton,joinToReview;
    private GameReviewAdapter reviewAdapter;
    private static final String Clan_Reviewed_TOP_URL = Constants.ROOT_URL+"clanreviewsTopGet_api.php";
    private static final String Clan_Reviewed_URL = Constants.ROOT_URL+"clanreviewsGet_api.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View clanReviewRootView = inflater.inflate(R.layout.fragment_clan_review, null);

        tvratingsandreviews = clanReviewRootView.findViewById(R.id.tvratingsandreviews);
        gameReviewed = clanReviewRootView.findViewById(R.id.nameReviewed);
        gameTagReviewed = clanReviewRootView.findViewById(R.id.tagReviewed);
        gameImageReviewed = clanReviewRootView.findViewById(R.id.imageReviewed);
        player_review_progress = clanReviewRootView.findViewById(R.id.review_progress);
        player_review_recycler_layout = clanReviewRootView.findViewById(R.id.review_recycler_layout);
        player_review_center = clanReviewRootView.findViewById(R.id.review_center);
        tvTotalReviews = clanReviewRootView.findViewById(R.id.tvTotalReviews);
        numReviews = clanReviewRootView.findViewById(R.id.numReviews);
        reviewButton = clanReviewRootView.findViewById(R.id.reviewButton);
        joinToReview = clanReviewRootView.findViewById(R.id.joinToReview);
        tvInteger = clanReviewRootView.findViewById(R.id.tvInteger);
        reviews5 = clanReviewRootView.findViewById(R.id.reviews5);
        reviews4 = clanReviewRootView.findViewById(R.id.reviews4);
        reviews3 = clanReviewRootView.findViewById(R.id.reviews3);
        reviews2 = clanReviewRootView.findViewById(R.id.reviews2);
        reviews1 = clanReviewRootView.findViewById(R.id.reviews1);
        reviewStarRating = clanReviewRootView.findViewById(R.id.reviewStarRating);
        noReviews = clanReviewRootView.findViewById(R.id.noReviews);
        mContext = getActivity();
        gamereviewRecyclerList = new ArrayList<>();
        gamereviewsView = clanReviewRootView.findViewById(R.id.recyclerReviews);
        gamereviewsView.setHasFixedSize(true);
        gamereviewsView.setLayoutManager(new LinearLayoutManager(mContext));
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        loadReviewsTop();
        loadReviews();
        tvratingsandreviews.requestFocus();
        return clanReviewRootView;
    }


    private void loadReviewsTop(){
        assert getArguments() != null;
        final String ClanId = getArguments().getString("ClanId");
        final String Clanname = getArguments().getString("Clanname");
        final String Clantag = getArguments().getString("Clantag");
        final String Clan_pic = getArguments().getString("Clan_pic");
        final String Clan_members = getArguments().getString("Clan_members");
        reviewButton.setOnClickListener(v -> {
            ClanRatingFragment ldf = new ClanRatingFragment();
            Bundle args = new Bundle();
            args.putString("ClanId", ClanId);
            args.putString("Clanname", Clanname);
            args.putString("Clantag", Clantag);
            args.putString("Clan_pic", Clan_pic);
            ldf.setArguments(args);
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
        });

        gameTagReviewed.setText(String.format("[%s]", Clantag));
        gameReviewed.setText(Clanname);
        Glide.with(mContext)
                .load(Constants.BASE_URL + Clan_pic)
                .error(R.mipmap.ic_launcher)
                .into(gameImageReviewed);
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, Clan_Reviewed_TOP_URL+"?userid="+userID+"&username="+username+"&clanid="+ClanId, response -> {
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
                    tvTotalReviews.setText(R.string.review_text);
                }

                player_review_progress.setVisibility(View.GONE);
                player_review_center.setVisibility(View.VISIBLE);
                player_review_recycler_layout.setVisibility(View.VISIBLE);

                String[] array = Clan_members != null ? Clan_members.split(",") : new String[0];
                if(Arrays.asList(array).contains(username)){
                    reviewButton.setVisibility(View.VISIBLE);
                }else{
                    joinToReview.setVisibility(View.VISIBLE);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadReviews(){
        assert getArguments() != null;
        final String id = getArguments().getString("ClanId");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Clan_Reviewed_URL+"?userid="+userID+"&clanid="+id+"&username="+username, response -> {
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
                    String userid = profilenewsObject.getString("userid");

                    GameReview_Recycler gamenewsResult = new GameReview_Recycler(ratingnumber, title, comments, reply, time, profile_pic, nickname, userid);
                    gamereviewRecyclerList.add(gamenewsResult);
                }
                if (profilenews.length()==0){
                    noReviews.setVisibility(View.VISIBLE);
                }else {
                    reviewAdapter = new GameReviewAdapter(mContext, gamereviewRecyclerList);
                    gamereviewsView.setAdapter(reviewAdapter);
                    ViewCompat.setNestedScrollingEnabled(gamereviewsView, false);
                    gameReviewed.setFocusable(true);
                    gameReviewed.setFocusableInTouchMode(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network Error", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }
}

