package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class PublicsPrevious extends Fragment {

    private static final String PREVIOUS_PUBLICS = Constants.ROOT_URL+"previous_publics.php";
    private PublicsTopicAdapter publicsnewsadapter;
    private List<PublicsTopic_Recycler> profilepublicsnewsRecyclerList;
    private RelativeLayout profileErrorScreen;
    private ProgressBar progressBar;
    private RecyclerView recyclerSeeAll;
    private ImageView backButton;
    private String gameID,thisUserID,thisUsername,gamename;
    private Context mCtx;
    private TextView queryName, nothingToShow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View seeAllRootView = inflater.inflate(R.layout.fragment_see_all, null);
        backButton = seeAllRootView.findViewById(R.id.backButton);
        recyclerSeeAll = seeAllRootView.findViewById(R.id.recyclerSeeAll);
        progressBar = seeAllRootView.findViewById(R.id.progressBar);
        queryName = seeAllRootView.findViewById(R.id.queryName);
        profileErrorScreen = seeAllRootView.findViewById(R.id.profileErrorScreen);
        nothingToShow = seeAllRootView.findViewById(R.id.nothingToShow);
        mCtx = getActivity();
        assert getArguments() != null;
        gameID = getArguments().getString("GameId");
        gamename = getArguments().getString("gamename");
        thisUserID = SharedPrefManager.getInstance(mCtx).getUserID();
        thisUsername = SharedPrefManager.getInstance(mCtx).getUsername();
        profilepublicsnewsRecyclerList = new ArrayList<>();
        recyclerSeeAll.setHasFixedSize(true);
        recyclerSeeAll.setLayoutManager(new LinearLayoutManager(mCtx));
        queryName.setText(gamename);
        loadPublics();
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());

        return seeAllRootView;
    }

    private void loadPublics(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, PREVIOUS_PUBLICS+"?publicsid="+gameID+"&userid="+thisUserID+"&username="+thisUsername, response -> {
            try {
                JSONArray profilepublicsnews = new JSONArray(response);
                for(int i = 0; i<profilepublicsnews.length(); i++){
                    JSONObject profilenewsObject = profilepublicsnews.getJSONObject(i);

                    String username = profilenewsObject.getString("username");
                    if (SharedPrefManager.getInstance(mCtx).isUserBlocked(username))continue;
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

                    PublicsTopic_Recycler publicsTopicResult = new PublicsTopic_Recycler(id, numposts, subject, date, cat, topic_by, type, user_id, profile_pic, nickname, username, event_date, zone, context, num_players, num_added, gamename);
                    profilepublicsnewsRecyclerList.add(publicsTopicResult);
                }
                if (profilepublicsnews.length()==0){
                    nothingToShow.setVisibility(View.VISIBLE);
                }
                publicsnewsadapter = new PublicsTopicAdapter(mCtx, profilepublicsnewsRecyclerList);
                recyclerSeeAll.setAdapter(publicsnewsadapter);
                progressBar.setVisibility(GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(mCtx, "Network error!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(GONE);
            profileErrorScreen.setVisibility(View.VISIBLE);
        });
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

}