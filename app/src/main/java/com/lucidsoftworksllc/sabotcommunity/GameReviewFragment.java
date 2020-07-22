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
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
public class GameReviewFragment extends Fragment {

    private TextView tvratingsandreviews, tvInteger, gameReviewed, gameTagReviewed, numReviews, tvTotalReviews, noReviews;
    private CircleImageView verifiedIcon;
    private ImageView gameImageReviewed;
    private Context mContext;
    private String username, userProfileID;
    private RelativeLayout player_review_center, player_review_recycler_layout;
    private ProgressBar reviews5, reviews4, reviews3, reviews2, reviews1, player_review_progress;
    private SimpleRatingBar reviewStarRating;
    private RecyclerView gamereviewsView;
    private List<GameReview_Recycler> gamereviewRecyclerList;
    private Button reviewButton, followToReview;
    private GameReviewAdapter reviewAdapter;
    private static final String Game_Reviewed_TOP_URL = Constants.ROOT_URL+"gamereviewsTopGet_api.php";
    private static final String Game_Reviewed_URL = Constants.ROOT_URL+"gamereviewsGet_api.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View playerReviewRootView = inflater.inflate(R.layout.fragment_game_review, null);
        tvratingsandreviews = playerReviewRootView.findViewById(R.id.tvratingsandreviewsGame);
        verifiedIcon = playerReviewRootView.findViewById(R.id.verifiedIcon);
        gameReviewed = playerReviewRootView.findViewById(R.id.gameNameReviewed);
        gameTagReviewed = playerReviewRootView.findViewById(R.id.gameTagReviewed);
        gameImageReviewed = playerReviewRootView.findViewById(R.id.gameImageReviewed);
        player_review_progress = playerReviewRootView.findViewById(R.id.game_review_progress);
        player_review_recycler_layout = playerReviewRootView.findViewById(R.id.game_review_recycler_layout);
        player_review_center = playerReviewRootView.findViewById(R.id.game_review_center);
        tvTotalReviews = playerReviewRootView.findViewById(R.id.tvTotalReviews);
        numReviews = playerReviewRootView.findViewById(R.id.numReviews);
        reviewButton = playerReviewRootView.findViewById(R.id.gameReviewButton);
        tvInteger = playerReviewRootView.findViewById(R.id.tvInteger);
        reviews5 = playerReviewRootView.findViewById(R.id.reviews5);
        reviews4 = playerReviewRootView.findViewById(R.id.reviews4);
        reviews3 = playerReviewRootView.findViewById(R.id.reviews3);
        reviews2 = playerReviewRootView.findViewById(R.id.reviews2);
        reviews1 = playerReviewRootView.findViewById(R.id.reviews1);
        reviewStarRating = playerReviewRootView.findViewById(R.id.reviewStarRating);
        followToReview = playerReviewRootView.findViewById(R.id.followToReview);
        noReviews = playerReviewRootView.findViewById(R.id.noReviews);
        gamereviewRecyclerList = new ArrayList<>();
        gamereviewsView = playerReviewRootView.findViewById(R.id.recyclerGameReviews);
        mContext = getActivity();
        gamereviewsView.setHasFixedSize(true);
        gamereviewsView.setLayoutManager(new LinearLayoutManager(mContext));
        username = SharedPrefManager.getInstance(mContext).getUsername();
        loadReviewsTop();
        loadReviews();
        tvratingsandreviews.requestFocus();
        return playerReviewRootView;
    }

    private void loadReviewsTop(){
        assert getArguments() != null;
        final String id = getArguments().getString("GameId");
        final String game = getArguments().getString("gamename");
        final String tag = getArguments().getString("gametag");
     //   final String verified = getArguments().getString("verified");
        final String game_pic = getArguments().getString("game_pic");
       /* if(verified.equals("yes")){
            verifiedIcon.setVisibility(View.VISIBLE);
        }*/
        reviewButton.setOnClickListener(v -> {
            GameRatingFragment ldf = new GameRatingFragment();
            Bundle args = new Bundle();
            args.putString("GameId", id);
            args.putString("game", game);
            args.putString("tag", tag);
//            args.putString("verified", verified);
            args.putString("game_pic", game_pic);
            ldf.setArguments(args);
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
        });
        gameTagReviewed.setText(String.format("@%s", tag));
        gameReviewed.setText(game);
        Glide.with(mContext)
                .load(Constants.BASE_URL + game_pic)
                .error(R.mipmap.ic_launcher)
                .into(gameImageReviewed);

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, Game_Reviewed_TOP_URL+"?username="+username+"&gameid="+id, response -> {
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
                String followed = profiletopObject.getString("followed");

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
                if(followed.equals("yes")){
                    reviewButton.setVisibility(View.VISIBLE);
                }else{
                    followToReview.setVisibility(View.VISIBLE);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network Error", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadReviews(){
        assert getArguments() != null;
        final String id = getArguments().getString("GameId");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Game_Reviewed_URL+"?username="+username+"&gameid="+id, response -> {
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
                    GameReview_Recycler gamenewsResult = new GameReview_Recycler(ratingnumber, title, comments, reply, time, profile_pic, nickname,userid);
                    gamereviewRecyclerList.add(gamenewsResult);
                }
                if (profilenews.length()==0){
                    noReviews.setVisibility(View.VISIBLE);
                }else{
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
