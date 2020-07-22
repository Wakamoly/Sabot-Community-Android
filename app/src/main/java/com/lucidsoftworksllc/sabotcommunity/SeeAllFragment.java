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

public class SeeAllFragment extends Fragment {

    private static final String SEE_ALL_URL = Constants.ROOT_URL+"see_all.php";

    private ProfilenewsAdapter newsadapter, clanNewsAdapter;
    private PublicsTopicAdapter publicsnewsadapter;
    private List<Profilenews_Recycler> profilenewsRecyclerList, clanNewsRecyclerList;
    private List<PublicsTopic_Recycler> profilepublicsnewsRecyclerList;

    private RelativeLayout profileErrorScreen;
    private ProgressBar progressBar;
    private RecyclerView recyclerSeeAll;
    private ImageView backButton;
    private String queryID,thisUserID,thisUsername,method,queryIDextra;
    private Context mCtx;
    private TextView queryText, queryName, nothingToShow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View seeAllRootView = inflater.inflate(R.layout.fragment_see_all, null);

        backButton = seeAllRootView.findViewById(R.id.backButton);
        recyclerSeeAll = seeAllRootView.findViewById(R.id.recyclerSeeAll);
        progressBar = seeAllRootView.findViewById(R.id.progressBar);
        queryText = seeAllRootView.findViewById(R.id.queryText);
        queryName = seeAllRootView.findViewById(R.id.queryName);
        profileErrorScreen = seeAllRootView.findViewById(R.id.profileErrorScreen);
        nothingToShow = seeAllRootView.findViewById(R.id.nothingToShow);
        mCtx = getActivity();
        assert getArguments() != null;
        queryID = getArguments().getString("queryid");
        queryIDextra = getArguments().getString("queryidextra");
        method = getArguments().getString("method");
        thisUserID = SharedPrefManager.getInstance(mCtx).getUserID();
        thisUsername = SharedPrefManager.getInstance(mCtx).getUsername();
        profilenewsRecyclerList = new ArrayList<>();
        profilepublicsnewsRecyclerList = new ArrayList<>();
        clanNewsRecyclerList =  new ArrayList<>();
        recyclerSeeAll.setHasFixedSize(true);
        recyclerSeeAll.setLayoutManager(new LinearLayoutManager(mCtx));
        if (method.equals("publics")){
            queryText.setText(getString(R.string.publics_posts_text));
            loadPublics();
        }
        if (method.equals("posts")){
            queryText.setText(getString(R.string.profile_posts));
            loadPosts();
        }
        if (method.equals("clan_posts")){
            queryText.setText(getString(R.string.clan_posts));
            loadClanPosts();
        }
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());

        return seeAllRootView;
    }

    private void loadPublics(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, SEE_ALL_URL+"?queryid="+queryID+"&queryidextra="+queryIDextra+"&method="+method+"&userid="+thisUserID+"&deviceusername="+thisUsername, response -> {
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
                    String gamename = profilenewsObject.getString("gamename");
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
        }, error -> Toast.makeText(mCtx, "Network error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    private void loadPosts(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, SEE_ALL_URL+"?queryid="+queryID+"&queryidextra="+queryIDextra+"&method="+method+"&userid="+thisUserID+"&deviceusername="+thisUsername, response -> {
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
                if (profilenews.length()==0){
                    nothingToShow.setVisibility(View.VISIBLE);
                }
                newsadapter = new ProfilenewsAdapter(mCtx, profilenewsRecyclerList);
                recyclerSeeAll.setAdapter(newsadapter);
                progressBar.setVisibility(GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> profileErrorScreen.setVisibility(View.VISIBLE));
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    private void loadClanPosts(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, SEE_ALL_URL+"?queryid="+queryID+"&queryidextra="+queryIDextra+"&method="+method+"&userid="+thisUserID+"&deviceusername="+thisUsername, response -> {
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
                clanNewsAdapter = new ProfilenewsAdapter(mCtx, clanNewsRecyclerList);
                recyclerSeeAll.setAdapter(clanNewsAdapter);
                if (clannews.length()==0) {
                    nothingToShow.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mCtx, "Network error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

}