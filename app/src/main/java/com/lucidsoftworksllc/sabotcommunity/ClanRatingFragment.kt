package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class ClanRatingFragment extends Fragment {

    private static final String New_Review_URL = Constants.ROOT_URL+"new_clan_review.php";

    SimpleRatingBar mRatingBar;
    TextView mRatingScale, nicknameReview, usernameReview;
    EditText mFeedback, mTitle;
    Button mSendFeedback;
    LinearLayout newReviewProgressBar, newReviewDetails;
    ImageView profileOnlineIcon, player_rating_profile_photo;
    CircleImageView verifiedIcon;
    Context mContext;
    String userID, nickname, username, verified, profile_pic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View playerRatingRootView = inflater.inflate(R.layout.fragment_gamerating, container, false);

        mRatingBar = playerRatingRootView.findViewById(R.id.newReviewStarRating);
        profileOnlineIcon = playerRatingRootView.findViewById(R.id.profileOnlineIcon);
        verifiedIcon = playerRatingRootView.findViewById(R.id.verifiedIcon);
        player_rating_profile_photo = playerRatingRootView.findViewById(R.id.player_rating_profile_photo);
        newReviewDetails = playerRatingRootView.findViewById(R.id.newReviewDetails);
        nicknameReview = playerRatingRootView.findViewById(R.id.nicknameReview);
        usernameReview = playerRatingRootView.findViewById(R.id.usernameReview);
        mRatingScale = (TextView) playerRatingRootView.findViewById(R.id.tvRatingScale);
        mFeedback = (EditText) playerRatingRootView.findViewById(R.id.etFeedback);
        mSendFeedback = (Button) playerRatingRootView.findViewById(R.id.btnSubmit);
        mTitle = playerRatingRootView.findViewById(R.id.mTitle);
        newReviewProgressBar = playerRatingRootView.findViewById(R.id.newReviewProgressBar);
        mContext = getActivity();

        loadNewReview();

        newReviewProgressBar.setVisibility(View.GONE);
        return playerRatingRootView;
    }

    private void submitReview(final String body, final String added_by, final String clan_id, final String rating, final String title) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, New_Review_URL, response -> {
            newReviewProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Review Posted!",Toast.LENGTH_LONG).show();
            requireActivity().getSupportFragmentManager().popBackStackImmediate();
        }, error -> {
            newReviewProgressBar.setVisibility(GONE);
            newReviewDetails.setVisibility(View.VISIBLE);
            Toast.makeText(mContext,"Error on Response, please try again later...",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("body",body);
                parms.put("added_by",added_by);
                parms.put("clan_id",clan_id);
                parms.put("rating",rating);
                parms.put("title",title);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadNewReview(){
        assert getArguments() != null;
        final String ClanId = getArguments().getString("ClanId");
        final String Clanname = getArguments().getString("Clanname");
        final String Clantag = getArguments().getString("Clantag");
        final String Clan_pic = getArguments().getString("Clan_pic");

        mRatingBar.setOnRatingBarChangeListener((simpleRatingBar, rating, fromUser) -> {
            mRatingScale.setText(String.valueOf(rating));
            switch ((int) simpleRatingBar.getRating()) {
                case 1:
                    mRatingScale.setText(getString(R.string.bad));
                    break;
                case 2:
                    mRatingScale.setText(getString(R.string.cusi));
                    break;
                case 3:
                    mRatingScale.setText(getString(R.string.average));
                    break;
                case 4:
                    mRatingScale.setText(getString(R.string.good));
                    break;
                case 5:
                    mRatingScale.setText(getString(R.string.great));
                    break;
                default:
                    mRatingScale.setText(R.string.choose_a_rating_text);
            }
        });
        mSendFeedback.setOnClickListener(view -> {
            if(!(mFeedback.getText().toString()).isEmpty() && !(mTitle.getText().toString()).isEmpty() && !(String.valueOf(mRatingBar.getRating())).equals("0.0")) {
                new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.green)
                        .setIcon(R.drawable.ic_error)
                        .setTitle("New Review")
                        .setMessage("WARNING: You risk immediate account termination if your submitted review goes against our code of conduct.\nExamples of inappropriate content include (but are not limited to):\n" +
                                "\n" +
                                "•Content that could be considered violent or threatening.\n" +
                                "•References to illegal use of alcohol, illegal drugs/illicit substances.\n" +
                                "•Content that is sexually suggestive or revealing, or could be considered objectionable.\n" +
                                "•Content that may be considered insulting, non-constructive, defamatory to individuals/organizations.\n" +
                                "•Staff/users' confidential or private information.\n" +
                                "•Any other content that is inconsistent with Sabot Community policies, code of conduct, or mission statement.\n\nSubmit Review?")
                        .setPositiveButton(R.string.yes, v -> {
                            newReviewProgressBar.setVisibility(View.VISIBLE);
                            newReviewDetails.setVisibility(GONE);
                            String body = mFeedback.getText().toString();
                            String added_by = SharedPrefManager.getInstance(mContext).getUsername();
                            String rating = String.valueOf(mRatingBar.getRating());
                            String title = mTitle.getText().toString();
                            submitReview(body, added_by, ClanId, rating, title);
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            } else {
                Toast.makeText(mContext,"You must enter the required fields!",Toast.LENGTH_LONG).show();
                if (mRatingScale.getText().equals("Please select a rating!")){
                    mRatingScale.setTextColor(Color.RED);
                }
            }
        });

       /* if(verified.equals("yes")){
            verifiedIcon.setVisibility(View.VISIBLE);
        }*/

        usernameReview.setText(String.format("[%s]", Clantag));
        nicknameReview.setText(Clanname);
        Glide.with(mContext)
                .load(Constants.BASE_URL+ Clan_pic)
                .error(R.mipmap.ic_launcher)
                .into(player_rating_profile_photo);
    }
}
